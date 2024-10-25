package com.drp.card_facilities.presentation.wallet_add

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
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
class WalletAddViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler, WalletHandler {
    private val _uiState = MutableStateFlow(WalletAddScreenState())
    val uiState: StateFlow<WalletAddScreenState>
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

    override var walletUiState: WalletUiState
        get() = _uiState.value.walletUiState
        set(value) {
            _uiState.value = _uiState.value.copy(walletUiState = value)
        }

    init {
        sendSharedViewModelEvent(SharedViewModelEvents.GetCards)
        sendWalletEvent(WalletEvents.SetShahkarUserData(cardFacilitiesUserRepository.getShahkarUserData()))
    }

    private fun validateFields() {
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

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return
        }

        if (!validateSourceCardOtherFields()) {
            return
        }
        changeOtpBottomSheetState(visibility = true)
    }

    private fun transfer() {
        if (!validateWalletOtpCode())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(transferState = RequestState.Loading)
            cardFacilitiesRepository.addToWallet(
                rqId = uiState.value.walletUiState.walletOtpRqId,
                pin = uiState.value.walletUiState.walletOtpCode,
                selectedCard = uiState.value.sourceCardUiState.selectedCard,
                cardYear = uiState.value.sourceCardUiState.year,
                cardMonth = uiState.value.sourceCardUiState.month
            ).collectLatest {
                _uiState.value = _uiState.value.copy(transferState = it.toRequestState())
            }
        }
    }

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    private fun dumpAmountValidationMessage() {
        _uiState.value =
            _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    private fun backToDefault() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
        _uiState.value = _uiState.value.copy(
            transferState = RequestState.Idle
        )
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.transferState.isFail())
            _uiState.value = _uiState.value.copy(transferState = RequestState.Idle)
    }

    /*suspend fun saveTransaction(receiptItems: List<ReceiptItem>) {
        var time = Calendar.getInstance().timeInMillis
        try {
            TrueTime.build().initialize()
            time = TrueTime.now().time
        } catch (ex: Exception) {
            Log.d("timeNotWork", ex.message.toString())
        }
        cardFacilitiesTransactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.CHARGE_WALLET.type,
                transactionValue = TransactionType.CHARGE_WALLET.type.plus(" "),
                amount = uiState.value.amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan ?: "",
                receiptItem = receiptItems
            )
        )
    }*/

    private fun changeOtpBottomSheetState(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(getOtpBottomSheetState = visibility)
    }

    fun sendEvent(event: WalletAddScreenEvents) {
        when (event) {
            is WalletAddScreenEvents.SetAmount -> setAmount(event.amount)
            is WalletAddScreenEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()
            is WalletAddScreenEvents.SetDescription -> setDescription(event.description)
            is WalletAddScreenEvents.ValidateScreenParameters -> validateFields()
            is WalletAddScreenEvents.SendOtp -> sendWalletEvent(
                WalletEvents.SendAddToWalletOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    amount = _uiState.value.amount,
                    description = _uiState.value.description,
                    dispatcher = dispatcher,
                    sourceCardUiState = uiState.value.sourceCardUiState
                )
            )

            is WalletAddScreenEvents.Transfer -> transfer()

            is WalletAddScreenEvents.DismissOtpBottomSheet -> changeOtpBottomSheetState(false)
            is WalletAddScreenEvents.DismissFailureDialog -> dismissFailureDialog()
            is WalletAddScreenEvents.BackToDefault -> backToDefault()
        }
    }

}

sealed class WalletAddScreenEvents {
    data class SetAmount(val amount: Long) : WalletAddScreenEvents()
    data object DumpAmountValidationMessage : WalletAddScreenEvents()
    data class SetDescription(val description: String) : WalletAddScreenEvents()
    data object ValidateScreenParameters : WalletAddScreenEvents()
    data object SendOtp : WalletAddScreenEvents()
    data object Transfer : WalletAddScreenEvents()
    data object DismissOtpBottomSheet : WalletAddScreenEvents()
    data object DismissFailureDialog : WalletAddScreenEvents()
    data object BackToDefault : WalletAddScreenEvents()
}