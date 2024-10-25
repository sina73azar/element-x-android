package com.drp.card_facilities.presentation.installment.compose

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesLoanRepository
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.utility.Commons
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.enums.ContactType
import com.drp.data.enums.TransactionType
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.UiText
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class InstallmentViewModel @Inject constructor(
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesLoanRepository: CardFacilitiesLoanRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) :
    ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler {

    private val _uiState = MutableStateFlow(InstallmentScreenState())
    val uiState: StateFlow<InstallmentScreenState>
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
        getInstallmentContacts()
    }

    private fun getInstallmentContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.INSTALLMENT_ID)
                .collectLatest {
                    setInstallmentContactSheetList(it)
                }
        }
    }

    private fun validateFields(): Boolean {
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

        if (!Commons.checkPaymentId(_uiState.value.facilityNumber)) {
            _uiState.value =
                _uiState.value.copy(facilityNumberValidationMessage = UiText.StringResource(R.string.failure_loan_payment_id))
            return false
        }

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return false
        }

        if (!validateSourceCardOtherFields()) {
            return false
        }

        return true
    }

    private fun inquiry() {
        if (!validateFields())
            return
        _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesLoanRepository.facilityInquiry(facilityNumber = _uiState.value.facilityNumber)
                .collectLatest {
                    it.toRequestState().let { response ->
                        if (response.isError())
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.DynamicString(
                                        response.getErrorMessage()
                                    )
                                )
                            )
                        _uiState.value = _uiState.value.copy(inquiry = response)
                    }
                }
        }
    }

    private fun installmentPayment() {
        if (!validateOtpCode())
            return
        _uiState.value = _uiState.value.copy(installmentPayment = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesLoanRepository.facilityPayment(
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
                paymentId = _uiState.value.facilityNumber,
                amount = _uiState.value.amount,
                facilityOwnerFirstName = _uiState.value.inquiry.getSuccessData().result.facilityOwnerFirstName,
                facilityOwnerLastName = _uiState.value.inquiry.getSuccessData().result.facilityOwnerLastName,
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
                    if (response.isSuccess())
                        coroutineScope {
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = ContactType.INSTALLMENT_ID.name,
                                    title = ContactType.INSTALLMENT_ID.savedContactTitle,
                                    value = _uiState.value.facilityNumber
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(installmentPayment = response)
                }
            }
        }

    }

    private fun setInstallmentContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(facilityNumberContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    private fun setFacilityNumber(facilityNumber: String) {
        _uiState.value = _uiState.value.copy(facilityNumber = facilityNumber)
        _uiState.value =
            _uiState.value.copy(facilityNumberContactSpinner = _uiState.value.facilityNumberContactSheetList.filter {
                it.value.contains(facilityNumber) || it.name?.contains(facilityNumber) == true
            }.map { it.value })
    }

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun dismissPaymentBottomSheet() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        _uiState.value =
            _uiState.value.copy(inquiry = RequestState.Idle)
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
        if (_uiState.value.installmentPayment.isFail())
            _uiState.value = _uiState.value.copy(installmentPayment = RequestState.Idle)
    }

    private fun dismissFacilityNumberSpinner() {
        _uiState.value = _uiState.value.copy(facilityNumberContactSpinner = emptyList())
    }

    private fun dumpFacilityNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(facilityNumberValidationMessage = UiText.DynamicString(""))
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
            Log.d("timeNotWork", ex.message.toString())
        }
        transactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.LOAN.type,
                transactionValue = TransactionType.LOAN.type,
                amount = _uiState.value.amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    fun sendEvent(event: InstallmentEvents) {
        when (event) {
            is InstallmentEvents.Inquiry -> inquiry()
            is InstallmentEvents.Payment -> installmentPayment()
            is InstallmentEvents.SendOtp -> sendSourceCardEvent(
                SourceCardEvents.SendCardPasswordOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = _uiState.value.amount,
                    requestType = CardOtpRequestType.AUTHENTICATION_LOAN_PAYMENT
                )
            )

            is InstallmentEvents.DismissPaymentBottomSheet -> dismissPaymentBottomSheet()
            is InstallmentEvents.DismissFailureDialog -> dismissFailureDialog()
            is InstallmentEvents.DismissFacilityNumberSpinner -> dismissFacilityNumberSpinner()
            is InstallmentEvents.DumpFacilityNumberValidationMessage -> dumpFacilityNumberValidationMessage()
            is InstallmentEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()
            is InstallmentEvents.SetFacilityNumber -> setFacilityNumber(event.facilityNumber)
            is InstallmentEvents.SetAmount -> setAmount(event.amount)
        }
    }
}

sealed class InstallmentEvents {
    data object Inquiry : InstallmentEvents()
    data object Payment : InstallmentEvents()
    data object SendOtp : InstallmentEvents()
    data object DismissPaymentBottomSheet : InstallmentEvents()
    data object DismissFailureDialog : InstallmentEvents()
    data object DismissFacilityNumberSpinner : InstallmentEvents()
    data object DumpFacilityNumberValidationMessage : InstallmentEvents()
    data object DumpAmountValidationMessage : InstallmentEvents()
    data class SetFacilityNumber(val facilityNumber: String) : InstallmentEvents()
    data class SetAmount(val amount: Long) : InstallmentEvents()
}

