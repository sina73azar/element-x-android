package com.drp.card_facilities.presentation.internet_package.compose.inquiry

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.enums.SimType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.utils.isValidMobileNo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InternetPackageInquiryViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository
) :
    ComposeSharedViewModel(cardFacilitiesRepository, dispatcher),
    SourceCardHandler {

    private val _uiState = MutableStateFlow(InternetPackageInquiryScreenState())
    val uiState: StateFlow<InternetPackageInquiryScreenState>
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

        if (_uiState.value.cardOrWalletToggle?.id == 1)
            if (!validateSourceCardOtherFields()) {
                return false
            }

        return true
    }

    private fun inquiry() {
        if (!validateFields())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
            cardFacilitiesTransactionRepository.internetPackageInquiryWithWallet(
                phoneNumber = _uiState.value.mobileNumber.trim().filter { it.isDigit() },
                operator = _uiState.value.selectedMobileOperatorTab,
                simType = if (uiState.value.selectedToggle?.id == 0) SimType.PREPAID else SimType.POSTPAID
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError()) {
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(
                                    response.getErrorMessage()
                                )
                            )
                        )
                    }
                    _uiState.value = _uiState.value.copy(inquiry = response)
                }
            }
        }
        /*viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.internetPackageInquiry(_uiState.value.selectedMobileOperatorTab)
                .collectLatest {
                    it.toRequestState().let { response ->
                        if (response.isError()) {
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.DynamicString(
                                        response.getErrorMessage()
                                    )
                                )
                            )
                        }
                        _uiState.value = _uiState.value.copy(inquiry = response)
                    }
                }
        }*/
    }

    private fun setMobileContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(mobileContactSheetList = contactList
                .map { it.toSearchSheetItemModel() })
    }

    private fun setMobileOperatorCurrentTab(currentTab: MobileOperatorTab) {
        _uiState.value = _uiState.value.copy(selectedMobileOperatorTab = currentTab)
    }

    private fun changeSelectedToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(selectedToggle = selectedToggle)
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

    private fun dumpMobilePhoneNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(mobileNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun dismissMobilePhoneSpinner() {
        _uiState.value = _uiState.value.copy(mobilePhoneContactSpinner = emptyList())
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    private fun dismissInquiry() {
        _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    private fun changeCardOrWalletToggle(selectedToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(cardOrWalletToggle = selectedToggle)
    }

    fun sendEvent(event: InternetPackageInquiryEvents) {
        when (event) {
            is InternetPackageInquiryEvents.ChangeMobileOperatorTab -> setMobileOperatorCurrentTab(
                event.currentTab
            )

            is InternetPackageInquiryEvents.ChangeSelectedToggle -> changeSelectedToggle(event.selectedToggle)

            is InternetPackageInquiryEvents.ChangeCardOrWalletToggle -> changeCardOrWalletToggle(
                event.selectedToggle
            )

            is InternetPackageInquiryEvents.Inquiry -> inquiry()

            is InternetPackageInquiryEvents.DismissFailureDialog -> dismissFailureDialog()

            is InternetPackageInquiryEvents.DismissMobilePhoneSpinner -> dismissMobilePhoneSpinner()

            is InternetPackageInquiryEvents.DismissInquiry -> dismissInquiry()

            is InternetPackageInquiryEvents.DumpMobilePhoneNumberValidationMessage -> dumpMobilePhoneNumberValidationMessage()

            is InternetPackageInquiryEvents.SetMobileNumber -> setMobilePhoneNumber(event.mobileNumber)
        }
    }
}

sealed class InternetPackageInquiryEvents {
    /** screen actions */
    data class ChangeMobileOperatorTab(val currentTab: MobileOperatorTab) :
        InternetPackageInquiryEvents()

    data class ChangeSelectedToggle(val selectedToggle: CustomToggleModel) :
        InternetPackageInquiryEvents()

    data class ChangeCardOrWalletToggle(val selectedToggle: CustomToggleModel) :
        InternetPackageInquiryEvents()

    data object Inquiry : InternetPackageInquiryEvents()

    data object DismissFailureDialog : InternetPackageInquiryEvents()
    data object DismissMobilePhoneSpinner : InternetPackageInquiryEvents()
    data object DismissInquiry : InternetPackageInquiryEvents()

    /** screen parameters error dumpers */
    data object DumpMobilePhoneNumberValidationMessage : InternetPackageInquiryEvents()

    /** screen parameters setters */
    data class SetMobileNumber(val mobileNumber: String) : InternetPackageInquiryEvents()

}