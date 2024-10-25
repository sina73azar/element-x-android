package com.drp.card_facilities.presentation.balance

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.shared_ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceScreenViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler {

    private val _uiState = MutableStateFlow(BalanceScreenState())
    val uiState: StateFlow<BalanceScreenState>
        get() = _uiState

    override var sourceCardState: SourceCardUiState
        get() = _uiState.value.sourceCardUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sourceCardUiState = value)
        }

    override var sharedViewModelState: SharedViewModelUiState
        get() = _uiState.value.sharedViewModelUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sharedViewModelUiState = value)
        }

    init {
        sendSharedViewModelEvent(SharedViewModelEvents.GetCards)
    }

    private fun validateScreenParameters() {
        if (!validateSourceCard()) {
            sendSharedViewModelEvent(
                SharedViewModelEvents.ShowError(
                    UiText.StringResource(
                        R.string.empty_source_card_st
                    )
                )
            )
            return
        }

        if (!validateSourceCardOtherFields()) {
            return
        }
        changeOtpBottomSheetState(visibility = true)
    }

    private fun balanceInquiry() {
        if (!validateOtpCode())
            return
        _uiState.value = _uiState.value.copy(balanceInquiryState = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.cardBalanceFromWalletInquiry(
                selectedCard = _uiState.value.sourceCardUiState.selectedCard,
                cardYear = _uiState.value.sourceCardUiState.year,
                cardMonth = _uiState.value.sourceCardUiState.month,
                cvv2 = _uiState.value.sourceCardUiState.cvv2,
                pin = _uiState.value.sourceCardUiState.otpCode
                /*selectedCard = _uiState.value.sourceCardUiState.selectedCard,
                trk2EquivData = Trk2EquivData(
                    expireDate = _uiState.value.sourceCardUiState.year.substring(
                        2,
                        4
                    ) + _uiState.value.sourceCardUiState.month,
                    cvv2 = _uiState.value.sourceCardUiState.cvv2,
                    pin = _uiState.value.sourceCardUiState.otpCode
                )*/
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(
                                    response.getErrorMessage()
                                )
                            )
                        )
                    _uiState.value = _uiState.value.copy(balanceInquiryState = response)
                }
            }
        }
    }


    private fun changeOtpBottomSheetState(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(getOtpBottomSheetState = visibility)
    }


    private fun backToDefault() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        _uiState.value = _uiState.value.copy(
            balanceInquiryState = RequestState.Idle
        )
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.balanceInquiryState.isFail())
            _uiState.value = _uiState.value.copy(balanceInquiryState = RequestState.Idle)
    }

    fun sendEvent(event: BalanceScreenEvents) {
        when (event) {
            /** functionalities */
            is BalanceScreenEvents.ValidateScreenParameters -> validateScreenParameters()

            is BalanceScreenEvents.SendOtp -> sendSourceCardEvent(
                SourceCardEvents.SendCardPasswordOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = 0L,
                    requestType = CardOtpRequestType.GET_BALANCE,
                )
            )

            is BalanceScreenEvents.BalanceInquiry -> balanceInquiry()

            /** screen actions */
            is BalanceScreenEvents.DismissOtpBottomSheet -> changeOtpBottomSheetState(visibility = false)

            is BalanceScreenEvents.DismissFailureDialog -> dismissFailureDialog()

            is BalanceScreenEvents.BackToDefault -> backToDefault()
        }
    }
}

sealed class BalanceScreenEvents {
    data object ValidateScreenParameters : BalanceScreenEvents()
    data object SendOtp : BalanceScreenEvents()
    data object BalanceInquiry : BalanceScreenEvents()
    data object DismissOtpBottomSheet : BalanceScreenEvents()
    data object DismissFailureDialog : BalanceScreenEvents()
    data object BackToDefault : BalanceScreenEvents()
}