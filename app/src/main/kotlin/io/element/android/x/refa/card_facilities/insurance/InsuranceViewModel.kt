package com.drp.card_facilities.presentation.insurance

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.enums.TransactionType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesBillRepository
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesBillRepository: CardFacilitiesBillRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler {
    private val _uiState = MutableStateFlow(InsuranceScreenState())
    val uiState: StateFlow<InsuranceScreenState>
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

    private fun validateFields(): Boolean {
        if (_uiState.value.cardOrWalletToggle?.id == 1)
            if (!validateSourceCard()) {
                sendSharedViewModelEvent(
                    SharedViewModelEvents.ShowError(
                        UiText.StringResource(
                            R.string.empty_source_card_st
                        )
                    )
                )
                return false
            }

        if (uiState.value.paymentId.isEmpty()) {
            _uiState.value =
                _uiState.value.copy(paymentIdValidationMessage = UiText.StringResource(R.string.data_validation_pay_id))
            return false
        }

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return false
        }

        if (_uiState.value.cardOrWalletToggle?.id == 1)
            if (!validateSourceCardOtherFields()) {
                return false
            }

        return true
    }

    private fun inquiry() {
        if (!validateFields())
            return
        changePaymentBottomSheetVisibility(true)
    }

    private fun insurancePayment() {
        if (!validateOtpCode())
            return
        _uiState.value = _uiState.value.copy(insurancePayment = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesBillRepository.billPayment(
                password = _uiState.value.sourceCardUiState.otpCode,
                selectedCard = _uiState.value.sourceCardUiState.selectedCard,
                trk2EquivData = Trk2EquivData(
                    expireDate = uiState.value.sourceCardUiState.year.substring(
                        2,
                        4
                    ) + uiState.value.sourceCardUiState.month,
                    cvv2 = uiState.value.sourceCardUiState.cvv2,
                    pin = uiState.value.sourceCardUiState.otpCode
                ),
                payment = BillPaymentInfo(
                    billId = uiState.value.paymentId,
                    amount = uiState.value.amount
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
                    _uiState.value = _uiState.value.copy(insurancePayment = response)
                }
            }
        }
    }

    private fun changePaymentBottomSheetVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(paymentBottomSheetVisibility = visibility)
    }

    private fun changeCardOrWalletToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(cardOrWalletToggle = selectedToggle)
    }

    private fun setPaymentId(paymentId: String) {
        _uiState.value = _uiState.value.copy(paymentId = paymentId)
    }

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.insurancePayment.isFail())
            _uiState.value = _uiState.value.copy(insurancePayment = RequestState.Idle)
    }

    private fun dumpPaymentIdValidationMessage() {
        _uiState.value =
            _uiState.value.copy(paymentIdValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpAmountValidationMessage() {
        _uiState.value =
            _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    suspend fun saveTransaction(receiptItems: List<ReceiptItem>) {
        var time = Calendar.getInstance().timeInMillis
        try {
            TrueTime.build().initialize()
            time = TrueTime.now().time
        } catch (ex: Exception) {
        }
        transactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.INSURANCE.type,
                transactionValue = TransactionType.INSURANCE.type,
                amount = _uiState.value.amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    fun sendEvent(event: InsuranceEvents) {
        when (event) {
            is InsuranceEvents.ChangeCardOrWalletToggle -> changeCardOrWalletToggle(event.selectedToggle)
            is InsuranceEvents.Inquiry -> inquiry()
            is InsuranceEvents.Payment -> insurancePayment()
            is InsuranceEvents.SendOtp -> sendSourceCardEvent(
                SourceCardEvents.SendCardPasswordOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = _uiState.value.amount,
                    requestType = CardOtpRequestType.BILL_PAYMENT
                )
            )

            is InsuranceEvents.DismissPaymentBottomSheet -> changePaymentBottomSheetVisibility(false)
            is InsuranceEvents.DismissFailureDialog -> dismissFailureDialog()
            is InsuranceEvents.DumpPaymentIdValidationMessage -> dumpPaymentIdValidationMessage()
            is InsuranceEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()
            is InsuranceEvents.SetPaymentId -> setPaymentId(event.paymentId)
            is InsuranceEvents.SetAmount -> setAmount(event.amount)
        }
    }
}

sealed class InsuranceEvents {
    data class ChangeCardOrWalletToggle(val selectedToggle: CustomToggleModel) : InsuranceEvents()
    data object Inquiry : InsuranceEvents()
    data object Payment : InsuranceEvents()
    data object SendOtp : InsuranceEvents()
    data object DismissPaymentBottomSheet : InsuranceEvents()
    data object DismissFailureDialog : InsuranceEvents()
    data object DumpPaymentIdValidationMessage : InsuranceEvents()
    data object DumpAmountValidationMessage : InsuranceEvents()
    data class SetPaymentId(val paymentId: String) : InsuranceEvents()
    data class SetAmount(val amount: Long) : InsuranceEvents()
}
