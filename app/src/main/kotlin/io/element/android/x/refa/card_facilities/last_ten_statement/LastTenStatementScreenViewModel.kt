package com.drp.card_facilities.presentation.last_ten_statement

import androidx.lifecycle.viewModelScope
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
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LastTenStatementScreenViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler {

    private val _uiState = MutableStateFlow(LastTenStatementScreenState())
    val uiState: StateFlow<LastTenStatementScreenState>
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

    private fun lastTenStatementInquiry() {
        if (!validateOtpCode())
            return
        _uiState.value =
            _uiState.value.copy(lastTenStatementInquiryInquiryState = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.lastTenStatementInquiry(
                selectedCard = _uiState.value.sourceCardUiState.selectedCard,
                trk2EquivData = Trk2EquivData(
                    expireDate = uiState.value.sourceCardUiState.year.substring(
                        2,
                        4
                    ) + uiState.value.sourceCardUiState.month,
                    cvv2 = uiState.value.sourceCardUiState.cvv2,
                    pin = _uiState.value.sourceCardUiState.otpCode
                )
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
                    _uiState.value =
                        _uiState.value.copy(lastTenStatementInquiryInquiryState = response)
                }
            }
        }
    }

    private fun changeOtpBottomSheetState(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(getOtpBottomSheetState = visibility)
    }

    private fun backToDefault() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.lastTenStatementInquiryInquiryState.isFail())
            _uiState.value =
                _uiState.value.copy(lastTenStatementInquiryInquiryState = RequestState.Idle)
    }

    private fun dismissInquiryDialog() {
        _uiState.value = _uiState.value.copy(
            lastTenStatementInquiryInquiryState = RequestState.Idle
        )
        changeOtpBottomSheetState(visibility = false)
    }

    fun sendEvent(event: LastTenStatementScreenEvents) {
        when (event) {

            /** functionalities */
            is LastTenStatementScreenEvents.ValidateScreenParameters -> validateScreenParameters()

            is LastTenStatementScreenEvents.SendOtp -> sendSourceCardEvent(
                SourceCardEvents.SendCardPasswordOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = 0L,
                    requestType = CardOtpRequestType.MINI_STATEMENT,
                )
            )

            is LastTenStatementScreenEvents.LastTenStatementInquiry -> lastTenStatementInquiry()

            /** screen actions */
            is LastTenStatementScreenEvents.DismissOtpBottomSheet -> changeOtpBottomSheetState(
                visibility = false
            )

            is LastTenStatementScreenEvents.DismissFailureDialog -> dismissFailureDialog()

            is LastTenStatementScreenEvents.DismissInquiryDialog -> dismissInquiryDialog()

            is LastTenStatementScreenEvents.BackToDefault -> {
                backToDefault()
            }
            else -> {}
        }
    }
}

sealed class LastTenStatementScreenEvents {
    data object ValidateScreenParameters : LastTenStatementScreenEvents()
    data object SendOtp : LastTenStatementScreenEvents()
    data object LastTenStatementInquiry : LastTenStatementScreenEvents()
    data object DismissOtpBottomSheet : LastTenStatementScreenEvents()
    data object DismissFailureDialog : LastTenStatementScreenEvents()
    data object DismissInquiryDialog : LastTenStatementScreenEvents()
    data object BackToDefault : LastTenStatementScreenEvents()
}
