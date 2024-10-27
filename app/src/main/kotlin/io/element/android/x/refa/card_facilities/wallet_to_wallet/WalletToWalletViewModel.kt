package com.drp.card_facilities.presentation.wallet_to_wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WalletToWalletViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ViewModel(), WalletHandler {

    private val _uiState = MutableStateFlow(WalletToWalletScreenState())
    val uiState: StateFlow<WalletToWalletScreenState>
        get() = _uiState

    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    override var walletUiState: WalletUiState
        get() = _uiState.value.walletUiState
        set(value) {
            _uiState.value = _uiState.value.copy(walletUiState = value)
        }

    init {
        sendWalletEvent(WalletEvents.SetShahkarUserData(cardFacilitiesUserRepository.getShahkarUserData()))
    }

    private fun validateFields() {
        if (uiState.value.destinationWalletId.isEmpty()) {
            _uiState.value =
                _uiState.value.copy(destinationWalletIdValidationMessage = UiText.StringResource(R.string.wallet_id_validation_st))
            return
        }

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return
        }

        changeOtpBottomSheetState(visibility = true)
    }

    private fun changeOtpBottomSheetState(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(getOtpBottomSheetState = visibility)
    }

    private fun setDestinationWalletId(walletId: String) {
        _uiState.value = _uiState.value.copy(destinationWalletId = walletId)
    }

    private fun dumpDestinationWalletIdValidationMessage() {
        _uiState.value =
            _uiState.value.copy(destinationWalletIdValidationMessage = UiText.DynamicString(""))
    }

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun dumpAmountValidationMessage() {
        _uiState.value =
            _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    private fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    private fun backToDefault() {
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
//        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        _uiState.value = _uiState.value.copy(
            transferState = RequestState.Idle
        )
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.transferState.isFail())
            _uiState.value = _uiState.value.copy(transferState = RequestState.Idle)
    }

    private fun transfer() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(transferState = RequestState.Loading)
            cardFacilitiesRepository.transferWalletToWallet(
                userData = uiState.value.walletUiState.shahkarUserData,
                amount = uiState.value.amount,
                description = uiState.value.description,
                destinationWalletId = uiState.value.destinationWalletId
            ).collectLatest {
                it.toRequestState().let { response ->
                    _uiState.value = _uiState.value.copy(transferState = response)
                }
            }
        }
    }

    private fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }

    fun sendEvent(event: WalletToWalletScreenEvents) {
        when (event) {
            is WalletToWalletScreenEvents.SetDestinationWalletId -> setDestinationWalletId(event.walletId)
            is WalletToWalletScreenEvents.DumpDestinationWalletIdValidationMessage -> dumpDestinationWalletIdValidationMessage()
            is WalletToWalletScreenEvents.SetAmount -> setAmount(event.amount)
            is WalletToWalletScreenEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()
            is WalletToWalletScreenEvents.SetDescription -> setDescription(event.description)
            is WalletToWalletScreenEvents.ValidateScreenParameters -> validateFields()
            is WalletToWalletScreenEvents.SendOtp -> {
                // TODO send otp
            }

            is WalletToWalletScreenEvents.Transfer -> transfer()
            is WalletToWalletScreenEvents.DismissOtpBottomSheet -> changeOtpBottomSheetState(false)
            is WalletToWalletScreenEvents.DismissFailureDialog -> dismissFailureDialog()
            is WalletToWalletScreenEvents.BackToDefault -> backToDefault()
            else -> {}
        }
    }
}

sealed class WalletToWalletScreenEvents {
    data class SetDestinationWalletId(val walletId: String) : WalletToWalletScreenEvents()
    data object DumpDestinationWalletIdValidationMessage : WalletToWalletScreenEvents()
    data class SetAmount(val amount: Long) : WalletToWalletScreenEvents()
    data object DumpAmountValidationMessage : WalletToWalletScreenEvents()
    data class SetDescription(val description: String) : WalletToWalletScreenEvents()
    data object ValidateScreenParameters : WalletToWalletScreenEvents()
    data object SendOtp : WalletToWalletScreenEvents()
    data object Transfer : WalletToWalletScreenEvents()
    data object DismissOtpBottomSheet : WalletToWalletScreenEvents()
    data object DismissFailureDialog : WalletToWalletScreenEvents()
    data object BackToDefault : WalletToWalletScreenEvents()
}
