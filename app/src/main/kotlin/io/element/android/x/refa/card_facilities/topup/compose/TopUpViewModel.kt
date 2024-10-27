package com.drp.card_facilities.presentation.topup.compose

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.enums.TransactionType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.utils.isValidMobileNo
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.R
import io.element.android.x.refa.card_facilities.topup.compose.TopUpScreenState
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
class TopUpViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository
) :
    ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler, WalletHandler {
    private val _uiState = MutableStateFlow(TopUpScreenState())
    val uiState: StateFlow<TopUpScreenState>
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
        getMobileNoContacts()
    }

    private fun getMobileNoContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.MOBILE_NO).collectLatest {
                setMobileContactSheetList(it)
            }
        }
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

        if (!isValidMobileNo(_uiState.value.mobileNumber.trim().filter { it.isDigit() })) {
            _uiState.value =
                _uiState.value.copy(
                    mobileNumberValidationMessage = UiText.StringResource(
                        R.string.data_validation_mobileNo
                    )
                )
            return false
        }

        if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1) {
            if (_uiState.value.wowAmount.isEmpty()) {
                _uiState.value =
                    _uiState.value.copy(wowAmountError = true)
                return false
            }
        } else
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
        val amount =
            if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1)
                _uiState.value.wowAmount.filter { it.isDigit() }.toLong() else _uiState.value.amount
        /*if (uiState.value.cardOrWalletToggle?.id == 0)
            viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
                cardFacilitiesTransactionRepository.topUpInquiry(
                    transactionType = "TOPUP",
                    topUpOperator = _uiState.value.selectedMobileOperatorTab.name,
                    amount = amount
                ).collectLatest {
                    it.toRequestState().let { response ->
                        if (response.isError())
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.DynamicString(response.getErrorMessage())
                                )
                            )
                        if (response.isSuccess()) {
                            response.getSuccessData().let { checkAmountResult ->
                                checkAmountResult.max?.let { max ->
                                    checkAmountResult.min?.let { min ->
                                        if (amount !in min..max) {
                                            sendSharedViewModelEvent(
                                                SharedViewModelEvents.ShowError(
                                                    UiText.StringResource(
                                                        R.string.data_validation_topup_amount,
                                                        currencyFormatter(min),
                                                        currencyFormatter(max)
                                                    )
                                                )
                                            )
                                            _uiState.value =
                                                _uiState.value.copy(inquiry = RequestState.Idle)
                                            return@collectLatest
                                        }
                                    }
                                }
                            }
                        }
                        _uiState.value = _uiState.value.copy(inquiry = response)
                    }
                }
            }
        else {*/
        changeWalletOtpBottomSheetVisibility(true)
//        }
    }

    private fun topUpPayment() {
        if (!validateWalletOtpCode())
            return
        _uiState.value = _uiState.value.copy(topUpPayment = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.paymentWithWallet(
                rqId = uiState.value.walletUiState.walletOtpRqId,
                pin = uiState.value.walletUiState.walletOtpCode,
                selectedCard = if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.selectedCard else null,
                expireYear = if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.year else null,
                expireMonth =  if (_uiState.value.cardOrWalletToggle?.id == 1) uiState.value.sourceCardUiState.month else null
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
                                    contactType = ContactType.MOBILE_NO.name,
                                    title = ContactType.MOBILE_NO.savedContactTitle,
                                    value = _uiState.value.mobileNumber.trim()
                                        .filter { it.isDigit() }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(topUpPayment = response)
                }
            }
        }
        /*viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.topUpPayment(
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
                chargeType = if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1)
                    ChargeType.WOW.name else ChargeType.NORMAL.name,
                mobileNumber = _uiState.value.mobileNumber.trim().filter { it.isDigit() },
                amount = amount,
                topUpOperator = _uiState.value.selectedMobileOperatorTab.name
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
                                    contactType = ContactType.MOBILE_NO.name,
                                    title = ContactType.MOBILE_NO.savedContactTitle,
                                    value = _uiState.value.mobileNumber.trim()
                                        .filter { it.isDigit() }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(topUpPayment = response)
                }
            }
        }*/
    }

    private fun setMobileContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(mobileContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    private fun setMobileOperatorCurrentTab(currentTab: MobileOperatorTab) {
        _uiState.value = _uiState.value.copy(selectedMobileOperatorTab = currentTab)
    }

    private fun changeSelectedToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(selectedToggle = selectedToggle)
    }

    private fun changeCardOrWalletToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(cardOrWalletToggle = selectedToggle)
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

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun setWowAmount(amount: String) {
        _uiState.value = _uiState.value.copy(wowAmount = amount)
    }

    private fun dismissMobilePhoneSpinner() {
        _uiState.value = _uiState.value.copy(mobilePhoneContactSpinner = emptyList())
    }

    private fun dismissPaymentBottomSheet() {
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
        changeWalletOtpBottomSheetVisibility(false)
        /*_uiState.value =
            _uiState.value.copy(inquiry = RequestState.Idle)*/
    }

    private fun dismissPaymentWithWalletBottomSheet() {
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
        changeWalletOtpBottomSheetVisibility(false)
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
        if (_uiState.value.topUpPayment.isFail())
            _uiState.value = _uiState.value.copy(topUpPayment = RequestState.Idle)
    }

    private fun dumpMobilePhoneNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(mobileNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpAmountValidationMessage() {
        _uiState.value =
            _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpWowAmountError() {
        _uiState.value =
            _uiState.value.copy(wowAmountError = false)
    }

    private fun changeWalletOtpBottomSheetVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(walletOtpBottomSheetVisibility = visibility)
    }

    suspend fun saveTransaction(receiptItems: List<ReceiptItem>) {
        val amount =
            if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1) _uiState.value.wowAmount.filter { it.isDigit() }
                .toLong() else _uiState.value.amount
        var time = Calendar.getInstance().timeInMillis
        try {
            TrueTime.build().initialize()
            time = TrueTime.now().time
        } catch (ex: Exception) {
        }
        cardFacilitiesTransactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.TOP_UP.type,
                transactionValue = TransactionType.TOP_UP.type.plus(" ")
                    .plus(_uiState.value.selectedMobileOperatorTab.title),
                amount = amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan ?: "",
                receiptItem = receiptItems
            )
        )
    }

    fun sendEvent(event: TopUpEvents) {
        when (event) {
            is TopUpEvents.ChangeMobileOperatorTab -> setMobileOperatorCurrentTab(event.currentTab)

            is TopUpEvents.ChangeSelectedToggle -> changeSelectedToggle(event.selectedToggle)

            is TopUpEvents.ChangeCardOrWalletToggle -> changeCardOrWalletToggle(event.selectedToggle)

            is TopUpEvents.Inquiry -> inquiry()

            is TopUpEvents.Payment -> topUpPayment()

            /*is TopUpEvents.SendOtp -> {
                val amount =
                    if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1) _uiState.value.wowAmount.filter { it.isDigit() }
                        .toLong() else _uiState.value.amount
                sendSourceCardEvent(
                    SourceCardEvents.SendCardPasswordOtp(
                        viewModel = this,
                        cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                        dispatcher = dispatcher,
                        amount = amount,
                        requestType = CardOtpRequestType.AUTHENTICATION_TOPUP_PAYMENT,
                    )
                )
            }*/
            is TopUpEvents.SendOtp -> {
                val amount =
                    if (_uiState.value.selectedMobileOperatorTab == MobileOperatorTab.MTN && _uiState.value.selectedToggle?.id == 1) _uiState.value.wowAmount.filter { it.isDigit() }
                        .toLong() else _uiState.value.amount
                sendWalletEvent(
                    WalletEvents.SendTopUpWithWalletOtp(
                        viewModel = this,
                        cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                        dispatcher = dispatcher,
                        isFromWallet = uiState.value.cardOrWalletToggle?.id == 0,
                        amount = amount,
                        sourceCardUiState = uiState.value.sourceCardUiState,
                        topUpOperatorTab = uiState.value.selectedMobileOperatorTab,
                        topUpPhoneNumber = uiState.value.mobileNumber.trim().filter { it.isDigit() }
                    )
                )
            }

            is TopUpEvents.DismissPaymentBottomSheet -> dismissPaymentBottomSheet()

            is TopUpEvents.DismissFailureDialog -> dismissFailureDialog()

            is TopUpEvents.DismissMobilePhoneSpinner -> dismissMobilePhoneSpinner()

            is TopUpEvents.DumpMobilePhoneNumberValidationMessage -> dumpMobilePhoneNumberValidationMessage()

            is TopUpEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()

            is TopUpEvents.DumpWowAmountError -> dumpWowAmountError()

            is TopUpEvents.SetMobileNumber -> setMobilePhoneNumber(event.mobileNumber)

            is TopUpEvents.SetAmount -> setAmount(event.amount)

            is TopUpEvents.SetWowAmount -> setWowAmount(event.amount)
            else -> {}
        }
    }
}

sealed class TopUpEvents {
    /** screen actions */
    data class ChangeMobileOperatorTab(val currentTab: MobileOperatorTab) : TopUpEvents()
    data class ChangeSelectedToggle(val selectedToggle: CustomToggleModel) : TopUpEvents()
    data class ChangeCardOrWalletToggle(val selectedToggle: CustomToggleModel) : TopUpEvents()
    data object Inquiry : TopUpEvents()
    data object Payment : TopUpEvents()
    data object SendOtp : TopUpEvents()
    data object DismissPaymentBottomSheet : TopUpEvents()
    data object DismissFailureDialog : TopUpEvents()
    data object DismissMobilePhoneSpinner : TopUpEvents()

    /** screen parameters error dumpers */
    data object DumpMobilePhoneNumberValidationMessage : TopUpEvents()
    data object DumpAmountValidationMessage : TopUpEvents()
    data object DumpWowAmountError : TopUpEvents()

    /** screen parameters setters */
    data class SetMobileNumber(val mobileNumber: String) : TopUpEvents()
    data class SetAmount(val amount: Long) : TopUpEvents()
    data class SetWowAmount(val amount: String) : TopUpEvents()
}
