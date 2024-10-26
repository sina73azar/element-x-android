package com.drp.card_facilities.presentation.bill.inquiry.unified

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.enums.ContactType
import com.drp.data.enums.TransactionType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesBillRepository
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.utility.BillUtils
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.R
import io.element.android.x.refa.enums.BillTabs
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BillViewModel @Inject constructor(
    private val cardFacilitiesBillRepository: CardFacilitiesBillRepository,
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler {

    private val billUtils: BillUtils = BillUtils()

    private val _uiState = MutableStateFlow(BillInquiryScreenState())
    val uiState: StateFlow<BillInquiryScreenState>
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
        getBillIdContacts()
    }

    private fun getBillIdContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.BILL_ID).collectLatest {
                setBillIdContactSheetList(it)
            }
        }
    }

    private fun validateInquiryScreenParameters(): Boolean {
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
        if (!isValidBillId(_uiState.value.billId)) {
            _uiState.value =
                _uiState.value.copy(billIdValidationMessage = UiText.StringResource(R.string.data_validation_bill_id))
            return false
        }
        if (!isValidPayId(_uiState.value.paymentId)) {
            _uiState.value =
                _uiState.value.copy(paymentIdValidationMessage = UiText.StringResource(R.string.data_validation_pay_id))
            return false
        }
        if (_uiState.value.cardOrWalletToggle?.id == 1)
            if (!validateSourceCardOtherFields()) {
                return false
            }
        return true
    }

    private fun inquiry() {
        if (!validateInquiryScreenParameters())
            return
        _uiState.value = _uiState.value.copy(billInquiryState = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesBillRepository.billInquiry(
                _uiState.value.billId,
                _uiState.value.paymentId
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(response.getErrorMessage())
                            )
                        )
                    _uiState.value = _uiState.value.copy(billInquiryState = response)
                }
            }
        }
    }

    private fun billPayment() {
        if (!validateOtpCode())
            return
        _uiState.value = _uiState.value.copy(billPaymentState = RequestState.Loading)
//        mockPaymentRes()
        viewModelScope.launch(dispatcher) {
            cardFacilitiesBillRepository.billPayment(
                password = _uiState.value.sourceCardUiState.otpCode,
                payment = uiState.value.billInquiryState.getSuccessData().payment,
                trk2EquivData = Trk2EquivData(
                    expireDate = uiState.value.sourceCardUiState.year.substring(
                        2,
                        4
                    ) + uiState.value.sourceCardUiState.month,
                    cvv2 = uiState.value.sourceCardUiState.cvv2,
                    pin = uiState.value.sourceCardUiState.otpCode
                )
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(response.getErrorMessage())
                            )
                        )
                    if (response.isSuccess())
                        coroutineScope {
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = ContactType.BILL_ID.name,
                                    title = ContactType.BILL_ID.savedContactTitle,
                                    value = _uiState.value.billId
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(billPaymentState = response)
                }
            }
        }
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
                transactionType = TransactionType.BILL.type,
                transactionValue = TransactionType.BILL.type.plus(" ")
                    .plus(
                        _uiState.value.billInquiryState.getSuccessData().payment.billerPersianName
                            ?: ""
                    ),
                amount = _uiState.value.billInquiryState.getSuccessData().payment.amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    private fun setBillIdContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(billIdContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    private fun setBillId(billId: String) {
        _uiState.value = _uiState.value.copy(billId = billId)
        _uiState.value =
            _uiState.value.copy(billIdSpinnerList = _uiState.value.billIdContactSheetList.filter {
                it.value.contains(billId) || it.name?.contains(billId) == true
            }.map { it.value })
    }

    private fun setPaymentId(paymentId: String) {
        _uiState.value = _uiState.value.copy(paymentId = paymentId)
    }

    private fun setCurrentTab(currentTab: BillTabs) {
        _uiState.value = _uiState.value.copy(selectedTab = currentTab)
    }

    private fun dumpBillIdValidationMessage() {
        _uiState.value = _uiState.value.copy(billIdValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpPaymentIdValidationMessage() {
        _uiState.value = _uiState.value.copy(paymentIdValidationMessage = UiText.DynamicString(""))
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.billInquiryState.isFail())
            _uiState.value = _uiState.value.copy(billInquiryState = RequestState.Idle)
        if (_uiState.value.billPaymentState.isFail())
            _uiState.value = _uiState.value.copy(billPaymentState = RequestState.Idle)
    }

    private fun dismissBillIdSpinnerList() {
        _uiState.value = _uiState.value.copy(billIdSpinnerList = emptyList())
    }

    private fun dismissPaymentBottomSheet() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        _uiState.value =
            _uiState.value.copy(billInquiryState = RequestState.Idle)
    }

    private fun isValidBillId(billId: String): Boolean {
        return billUtils.validateBillId(billId)
    }

    private fun isValidPayId(payId: String): Boolean {
        return billUtils.validatePayId(payId)
    }

    private fun changeCardOrWalletToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(cardOrWalletToggle = selectedToggle)
    }

    fun sendEvent(event: BillEvents) {
        when (event) {
            /** functionalities */
            is BillEvents.ChangeCardOrWalletToggle -> changeCardOrWalletToggle(event.selectedToggle)
            is BillEvents.Inquiry -> inquiry()
            is BillEvents.Payment -> billPayment()
            is BillEvents.SendOtp -> sendSourceCardEvent(
                SourceCardEvents.SendCardPasswordOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = _uiState.value.billInquiryState.getSuccessData().payment.amount,
                    requestType = CardOtpRequestType.BILL_PAYMENT,
                )
            )
            /** screen actions */
            is BillEvents.ChangeTab -> setCurrentTab(event.currentTab)

            is BillEvents.DismissPaymentBottomSheet -> dismissPaymentBottomSheet()

            is BillEvents.DismissFailureDialog -> dismissFailureDialog()

            is BillEvents.DismissBillIdSpinnerList -> dismissBillIdSpinnerList()

            is BillEvents.DumpBillIdValidationMessage -> dumpBillIdValidationMessage()

            is BillEvents.DumpPaymentIdValidationMessage -> dumpPaymentIdValidationMessage()
            /** parameter setters */
            is BillEvents.SetBillId -> setBillId(event.billId)

            is BillEvents.SetPaymentId -> setPaymentId(event.paymentId)
            else -> {}
        }
    }

}

sealed class BillEvents {
    data object Inquiry : BillEvents()
    data object Payment : BillEvents()
    data object SendOtp : BillEvents()
    data class ChangeTab(val currentTab: BillTabs) : BillEvents()
    data class ChangeCardOrWalletToggle(val selectedToggle: CustomToggleModel) : BillEvents()
    data object DismissPaymentBottomSheet : BillEvents()
    data object DismissFailureDialog : BillEvents()
    data object DismissBillIdSpinnerList : BillEvents()
    data object DumpBillIdValidationMessage : BillEvents()
    data object DumpPaymentIdValidationMessage : BillEvents()
    data class SetBillId(val billId: String) : BillEvents()
    data class SetPaymentId(val paymentId: String) : BillEvents()
}
