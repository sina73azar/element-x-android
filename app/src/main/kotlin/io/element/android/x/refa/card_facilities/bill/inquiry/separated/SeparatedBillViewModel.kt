package com.drp.card_facilities.presentation.bill.inquiry.separated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.BillType
import com.drp.data.enums.ContactType
import com.drp.data.enums.TransactionType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesBillRepository
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.utility.BillUtils
import com.drp.refah.card_facilities.utility.Commons.isValidFixedTelephoneNo
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.utils.isValidMobileNo
import com.instacart.library.truetime.TrueTime
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class SeparatedBillViewModel(
    private val cardFacilitiesBillRepository: CardFacilitiesBillRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler, WalletHandler {

    private val billUtils: BillUtils = BillUtils()

    private val _uiState = MutableStateFlow(SeparatedBillInquiryScreenState())
    val uiState: StateFlow<SeparatedBillInquiryScreenState>
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
        getContacts()
    }

    private fun getContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.getAllContacts().collectLatest {
                setFixedPhoneContactSheetList(it)
                setMobileContactSheetList(it)
                setWaterBillIdContactSheetList(it)
                setGasBillIdContactSheetList(it)
                setElectricityBillIdContactSheetList(it)
            }
        }
    }

    private fun validateFieldsBasedOnBillType(billType: BillType): Boolean {
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

        when (billType) {
            BillType.FIXEDLINE -> {
                if (!isValidFixedTelephoneNo(_uiState.value.fixedPhoneNumber)) {
                    _uiState.value =
                        _uiState.value.copy(
                            fixedPhoneNumberValidationMessage = UiText.StringResource(
                                R.string.data_validation_telephoneNo
                            )
                        )
                    return false
                }
            }

            BillType.MOBILE -> {
                if (!isValidMobileNo(_uiState.value.mobileNumber.trim().filter { it.isDigit() })) {
                    _uiState.value =
                        _uiState.value.copy(
                            mobileNumberValidationMessage = UiText.StringResource(
                                R.string.data_validation_mobileNo
                            )
                        )
                    return false
                }
            }

            else -> {
                if (!isValidBillId(_uiState.value.billId)) {
                    _uiState.value =
                        _uiState.value.copy(billIdValidationMessage = UiText.StringResource(R.string.data_validation_bill_id))
                    return false
                }
            }
        }
        if (_uiState.value.cardOrWalletToggle?.id == 1)
            if (!validateSourceCardOtherFields()) {
                return false
            }
        return true
    }

    private fun inquiry(billType: BillType) {
        if (!validateFieldsBasedOnBillType(billType))
            return
        /*if (billType == BillType.FIXEDLINE || billType == BillType.MOBILE)
            _uiState.value =
                _uiState.value.copy(separatedPhoneBillInquiryState = RequestState.Loading)
        else
            _uiState.value =
                _uiState.value.copy(separatedUtilityBillInquiryState = RequestState.Loading)*/
        viewModelScope.launch(dispatcher) {
            _uiState.value =
                _uiState.value.copy(separatedUtilityBillInquiryState = RequestState.Loading)
            /* when (billType) {
                 BillType.FIXEDLINE, BillType.MOBILE -> {
                     cardFacilitiesBillRepository.separatedPhoneBillInquiry(
                         phoneNumber = if (billType == BillType.FIXEDLINE) _uiState.value.fixedPhoneNumber
                         else _uiState.value.mobileNumber.trim().filter { it.isDigit() },
                         billType = if (billType == BillType.FIXEDLINE) billType else {
                             when (_uiState.value.selectedMobileOperatorTab) {
                                 MobileOperatorTab.MTN -> BillType.MTNMOBILE
                                 MobileOperatorTab.MCI -> BillType.MCIMOBILE
                                 MobileOperatorTab.RIGHTEL -> BillType.RIGHTELMOBILE
                             }
                         }
                     ).collectLatest {
                         it.toRequestState().let { response ->
                             if (response.isError())
                                 sendSharedViewModelEvent(
                                     SharedViewModelEvents.ShowError(
                                         UiText.DynamicString(response.getErrorMessage())
                                     )
                                 )
                             if (response.isSuccess())
                                 if (response.getSuccessData().amount == 0L && response.getSuccessData().finalTermAmount == 0L) {
                                     sendSharedViewModelEvent(
                                         SharedViewModelEvents.ShowError(
                                             UiText.StringResource(R.string.paid_bill_error_st)
                                         )
                                     )
                                     _uiState.value =
                                         _uiState.value.copy(separatedPhoneBillInquiryState = RequestState.Idle)
                                     return@collectLatest
                                 }
                             _uiState.value =
                                 _uiState.value.copy(separatedPhoneBillInquiryState = response)
                         }
                     }
                 }

                 else -> {*/
            cardFacilitiesBillRepository.utilityBillInquiryWithWallet(
                billId = when (billType) {
                    BillType.FIXEDLINE -> _uiState.value.fixedPhoneNumber
                    BillType.MOBILE -> _uiState.value.mobileNumber.trim()
                        .filter { it.isDigit() }

                    else -> _uiState.value.billId
                },
                billType = if (billType == BillType.MOBILE) {
                    when (_uiState.value.selectedMobileOperatorTab) {
                        MobileOperatorTab.MTN -> BillType.MTNMOBILE
                        MobileOperatorTab.MCI -> BillType.MCIMOBILE
                        MobileOperatorTab.RIGHTEL -> BillType.RIGHTELMOBILE
                    }
                } else billType
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(response.getErrorMessage())
                            )
                        )
                    if (response.isSuccess()) {
                        if (response.getSuccessData().billInquiryResponseService.amount == 0L) {
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.StringResource(R.string.paid_bill_error_st)
                                )
                            )
                            _uiState.value =
                                _uiState.value.copy(separatedUtilityBillInquiryState = RequestState.Idle)
                            return@collectLatest
                        }
                        if (response.getSuccessData().billInquiryResponseService.billPayment.isEmpty()) {
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.StringResource(R.string.pay_id_bill_error_st)
                                )
                            )
                            _uiState.value =
                                _uiState.value.copy(separatedUtilityBillInquiryState = RequestState.Idle)
                            return@collectLatest
                        }
                    }
                    _uiState.value =
                        _uiState.value.copy(separatedUtilityBillInquiryState = response)
                }
//                    }
//                }
            }
        }
    }

    private fun billPayment(/*billPaymentInfo: BillPaymentInfo, */billType: BillType) {
        if (!validateWalletOtpCode())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(billPaymentState = RequestState.Loading)
            transactionRepository.paymentWithWallet(
                rqId = uiState.value.walletUiState.walletOtpRqId,
                pin = uiState.value.walletUiState.walletOtpCode,
                selectedCard = if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.selectedCard else null,
                expireYear = if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.year else null,
                expireMonth = if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.month else null
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
                            val contactType = when (billType) {
                                BillType.FIXEDLINE -> ContactType.TELEPHONE_NO
                                BillType.MOBILE -> ContactType.MOBILE_NO
                                BillType.WATER -> ContactType.WATER_BILL_ID
                                BillType.GAS -> ContactType.GAS_BILL_ID
                                BillType.ELECTRICITY -> ContactType.ELECTRIC_BILL_ID
                                else -> ContactType.BILL_ID
                            }
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = contactType.name,
                                    title = contactType.savedContactTitle,
                                    value = when (contactType) {
                                        ContactType.MOBILE_NO -> _uiState.value.mobileNumber.trim()
                                            .filter { it.isDigit() }

                                        ContactType.TELEPHONE_NO -> _uiState.value.fixedPhoneNumber
                                        else -> _uiState.value.billId
                                    }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(billPaymentState = response)
                }
            }
        }
        /*viewModelScope.launch(dispatcher) {
            cardFacilitiesBillRepository.billPayment(
                password = _uiState.value.sourceCardUiState.otpCode,
                payment = billPaymentInfo,
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
                            val contactType = when (billType) {
                                BillType.FIXEDLINE -> ContactType.TELEPHONE_NO
                                BillType.MOBILE -> ContactType.MOBILE_NO
                                BillType.WATER -> ContactType.WATER_BILL_ID
                                BillType.GAS -> ContactType.GAS_BILL_ID
                                BillType.ELECTRICITY -> ContactType.ELECTRIC_BILL_ID
                                else -> ContactType.BILL_ID
                            }
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = contactType.name,
                                    title = contactType.savedContactTitle,
                                    value = when (contactType) {
                                        ContactType.MOBILE_NO -> _uiState.value.mobileNumber.trim()
                                            .filter { it.isDigit() }

                                        ContactType.TELEPHONE_NO -> _uiState.value.fixedPhoneNumber
                                        else -> _uiState.value.billId
                                    }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(billPaymentState = response)
                }
            }
        }*/
    }

    private fun findOperatorType(mobilePrefix: String) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.findOperatorType(mobilePrefix)
                .collect {
                    it.toRequestState().let { response ->
                        if (response.isSuccess()) {
                            _uiState.value = _uiState.value.copy(
                                selectedMobileOperatorTab = response.getSuccessData().operatorType
                                    ?: MobileOperatorTab.MTN
                            )
                        }
                    }
                }
        }
    }

    suspend fun saveTransaction(receiptItems: List<ReceiptItem>, billType: BillType, amount: Long) {
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
                        billType.faName
                    ),
                amount = amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    private fun setMobileContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(mobileContactSheetList = contactList.filter { it.contactType == ContactType.MOBILE_NO.name }
                .map { it.toSearchSheetItemModel() })
    }

    private fun setFixedPhoneContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(fixedPhoneContactSheetList = contactList.filter { it.contactType == ContactType.TELEPHONE_NO.name }
                .map { it.toSearchSheetItemModel() })
    }

    private fun setWaterBillIdContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(waterBillIdContactSheetList = contactList.filter { it.contactType == ContactType.WATER_BILL_ID.name }
                .map { it.toSearchSheetItemModel() })
    }

    private fun setGasBillIdContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(gasBillIdContactSheetList = contactList.filter { it.contactType == ContactType.GAS_BILL_ID.name }
                .map { it.toSearchSheetItemModel() })
    }

    private fun setElectricityBillIdContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(electricityBillIdContactSheetList = contactList.filter { it.contactType == ContactType.ELECTRIC_BILL_ID.name }
                .map { it.toSearchSheetItemModel() })
    }

    private fun setBillId(billId: String, billType: BillType) {
        when (billType) {
            BillType.WATER -> {
                _uiState.value =
                    _uiState.value.copy(waterBillIdContactSpinner = _uiState.value.waterBillIdContactSheetList.filter {
                        it.value.contains(billId) || it.name?.contains(billId) == true
                    }.map { it.value })
            }

            BillType.GAS -> {
                _uiState.value =
                    _uiState.value.copy(gasBillIdContactSpinner = _uiState.value.gasBillIdContactSheetList.filter {
                        it.value.contains(billId) || it.name?.contains(billId) == true
                    }.map { it.value })
            }

            BillType.ELECTRICITY -> {
                _uiState.value =
                    _uiState.value.copy(electricityBillIdContactSpinner = _uiState.value.electricityBillIdContactSheetList.filter {
                        it.value.contains(billId) || it.name?.contains(billId) == true
                    }.map { it.value })
            }

            else -> {
                // Do Nothing
            }
        }
        _uiState.value = _uiState.value.copy(billId = billId)
    }

    private fun setFixedPhoneNumber(fixedPhoneNumber: String) {
        _uiState.value = _uiState.value.copy(fixedPhoneNumber = fixedPhoneNumber)
        _uiState.value =
            _uiState.value.copy(fixedPhoneContactSpinner = _uiState.value.fixedPhoneContactSheetList.filter {
                it.value.contains(fixedPhoneNumber) || it.name?.contains(fixedPhoneNumber) == true
            }.map { it.value })
    }

    private fun setMobilePhoneNumber(mobileNumber: String) {
        if (mobileNumber.length > 3 && !_uiState.value.operatorTabFinderCalled) {
            _uiState.value.operatorTabFinderCalled = true
            findOperatorType(mobileNumber.substring(0, 4))
        }
        if (mobileNumber.length < 4)
            _uiState.value.operatorTabFinderCalled = false
        _uiState.value =
            _uiState.value.copy(mobilePhoneContactSpinner = _uiState.value.mobileContactSheetList.filter {
                it.value.contains(mobileNumber.filter { it != '-' }) || it.name?.contains(
                    mobileNumber.filter { it != '-' }
                ) == true
            }.map { it.value })
        _uiState.value = _uiState.value.copy(mobileNumber = mobileNumber)
    }

    private fun setMobileOperatorCurrentTab(currentTab: MobileOperatorTab) {
        _uiState.value = _uiState.value.copy(selectedMobileOperatorTab = currentTab)
    }

    private fun changeCardOrWalletToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(cardOrWalletToggle = selectedToggle)
    }

    private fun dumpBillIdValidationMessage() {
        _uiState.value = _uiState.value.copy(billIdValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpFixedPhoneNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(fixedPhoneNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpMobilePhoneNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(mobileNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun dismissFailureDialog() {
        /*if (_uiState.value.separatedPhoneBillInquiryState.isFail())
            _uiState.value = _uiState.value.copy(separatedPhoneBillInquiryState = RequestState.Idle)*/
        if (_uiState.value.separatedUtilityBillInquiryState.isFail())
            _uiState.value =
                _uiState.value.copy(separatedUtilityBillInquiryState = RequestState.Idle)
        if (_uiState.value.billPaymentState.isFail())
            _uiState.value = _uiState.value.copy(billPaymentState = RequestState.Idle)
    }

    private fun dismissFixedPhoneSpinner() {
        _uiState.value = _uiState.value.copy(fixedPhoneContactSpinner = emptyList())
    }

    private fun dismissMobilePhoneSpinner() {
        _uiState.value = _uiState.value.copy(mobilePhoneContactSpinner = emptyList())
    }

    private fun dismissWaterBillIdSpinner() {
        _uiState.value = _uiState.value.copy(waterBillIdContactSpinner = emptyList())
    }

    private fun dismissGasBillIdSpinner() {
        _uiState.value = _uiState.value.copy(gasBillIdContactSpinner = emptyList())
    }

    private fun dismissElectricityBillIdSpinner() {
        _uiState.value = _uiState.value.copy(electricityBillIdContactSpinner = emptyList())
    }

    private fun dismissInquiry() {
//        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
        _uiState.value = _uiState.value.copy(
//            separatedPhoneBillInquiryState = RequestState.Idle,
            separatedUtilityBillInquiryState = RequestState.Idle
        )
    }

    private fun isValidBillId(billId: String): Boolean {
        return billUtils.validateBillId(billId)
    }

    fun sendEvent(event: SeparatedBillEvents) {
        when (event) {
            /** functionalities */
            is SeparatedBillEvents.Inquiry -> {
                inquiry(event.billType)
            }

            is SeparatedBillEvents.SendOtp -> {
                sendWalletEvent(
                    WalletEvents.SendSeparatedBillWithWalletOtp(
                        viewModel = this,
                        cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                        dispatcher = dispatcher,
                        isFromWallet = uiState.value.cardOrWalletToggle?.id == 0,
                        sourceCardUiState = uiState.value.sourceCardUiState,
                        billPayment = event.billPaymentInfo,
                        billType = if (event.billType == BillType.MOBILE) {
                            when (_uiState.value.selectedMobileOperatorTab) {
                                MobileOperatorTab.MTN -> BillType.MTNMOBILE
                                MobileOperatorTab.MCI -> BillType.MCIMOBILE
                                MobileOperatorTab.RIGHTEL -> BillType.RIGHTELMOBILE
                            }
                        } else event.billType
                    )
                )
            }

            is SeparatedBillEvents.Payment -> {
                billPayment(event.billType)
            }
            /** screen actions */
            is SeparatedBillEvents.ChangeCardOrWalletToggle -> changeCardOrWalletToggle(event.selectedToggle)
            is SeparatedBillEvents.ChangeMobileOperatorTab -> {
                setMobileOperatorCurrentTab(event.currentTab)
            }

            is SeparatedBillEvents.DismissInquiry -> {
                dismissInquiry()
            }

            is SeparatedBillEvents.DismissFailureDialog -> {
                dismissFailureDialog()
            }

            is SeparatedBillEvents.DismissFixedPhoneSpinner -> {
                dismissFixedPhoneSpinner()
            }

            is SeparatedBillEvents.DismissMobilePhoneSpinner -> {
                dismissMobilePhoneSpinner()
            }

            is SeparatedBillEvents.DismissWaterBillIdSpinner -> {
                dismissWaterBillIdSpinner()
            }

            is SeparatedBillEvents.DismissGasBillIdSpinner -> {
                dismissGasBillIdSpinner()
            }

            is SeparatedBillEvents.DismissElectricityBillIdSpinner -> {
                dismissElectricityBillIdSpinner()
            }

            is SeparatedBillEvents.DumpBillIdValidationMessage -> {
                dumpBillIdValidationMessage()
            }

            is SeparatedBillEvents.DumpFixedPhoneNumberValidationMessage -> {
                dumpFixedPhoneNumberValidationMessage()
            }

            is SeparatedBillEvents.DumpMobilePhoneNumberValidationMessage -> {
                dumpMobilePhoneNumberValidationMessage()
            }

            /** parameter setters */
            is SeparatedBillEvents.SetBillId -> {
                setBillId(event.billId, event.billType)
            }

            is SeparatedBillEvents.SetFixedPhoneNumber -> {
                setFixedPhoneNumber(event.fixedPhoneNumber)
            }

            is SeparatedBillEvents.SetMobileNumber -> {
                setMobilePhoneNumber(event.mobileNumber)
            }
            else -> {}
        }
    }
}

sealed class SeparatedBillEvents {
    data class Inquiry(val billType: BillType) : SeparatedBillEvents()
    data class SendOtp(val billPaymentInfo: BillPaymentInfo, val billType: BillType) :
        SeparatedBillEvents()

    data class Payment(val billType: BillType) :
        SeparatedBillEvents()

    data class ChangeCardOrWalletToggle(val selectedToggle: CustomToggleModel) :
        SeparatedBillEvents()

    data class ChangeMobileOperatorTab(val currentTab: MobileOperatorTab) : SeparatedBillEvents()
    data object DismissInquiry : SeparatedBillEvents()
    data object DismissFailureDialog : SeparatedBillEvents()
    data object DismissFixedPhoneSpinner : SeparatedBillEvents()
    data object DismissMobilePhoneSpinner : SeparatedBillEvents()
    data object DismissWaterBillIdSpinner : SeparatedBillEvents()
    data object DismissGasBillIdSpinner : SeparatedBillEvents()
    data object DismissElectricityBillIdSpinner : SeparatedBillEvents()
    data object DumpBillIdValidationMessage : SeparatedBillEvents()
    data object DumpFixedPhoneNumberValidationMessage : SeparatedBillEvents()
    data object DumpMobilePhoneNumberValidationMessage : SeparatedBillEvents()
    data class SetBillId(val billId: String, val billType: BillType) : SeparatedBillEvents()
    data class SetFixedPhoneNumber(val fixedPhoneNumber: String) : SeparatedBillEvents()
    data class SetMobileNumber(val mobileNumber: String) : SeparatedBillEvents()
}


class SeparatedBillViewModelFactory(
    private val billRepository: CardFacilitiesBillRepository,
    private val userRepository: CardFacilitiesUserRepository,
    private val cardFacilityRepository: CardFacilitiesRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeparatedBillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeparatedBillViewModel(
                billRepository,
                userRepository,
                cardFacilityRepository,
                transactionRepository,
                dispatcher
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
