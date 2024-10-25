package com.drp.card_facilities.presentation.licence_negative_score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.shared_ui.UiText
import com.drp.utils.isValidMobileNo
import com.drp.utils.isValidNationalCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenceNegativeScoreViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(LicenceNegativeScoreScreenState())
    val uiState: StateFlow<LicenceNegativeScoreScreenState> = _uiState
    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    init {
        getMobileNoContacts()
    }

    private fun getMobileNoContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.MOBILE_NO).collectLatest {
                setMobileContactSheetList(it)
            }
        }
    }

    private fun validateFields(): Boolean {
        if (uiState.value.licenceNumber.length < 10) {
            _uiState.value =
                _uiState.value.copy(
                    licenceNumberValidationMessage = UiText.StringResource(
                        R.string.licence_number_error_st
                    )
                )
            return false
        }
        if (!isValidNationalCode(uiState.value.nationalId)) {
            _uiState.value =
                _uiState.value.copy(
                    nationalIdValidationMessage = UiText.StringResource(
                        R.string.data_validation_national_code_st
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
        return true
    }

    fun inquiry() {
        if (!validateFields())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
            cardFacilitiesRepository.licenceNegativeScoreInquiry(
                licenceNumber = uiState.value.licenceNumber,
                nationalId = uiState.value.nationalId,
                mobileNumber = uiState.value.mobileNumber
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError()) {
                        showError(UiText.StringResource(R.string.licence_score_inquiry_error_st))
                    }
                    if (response.isSuccess()) {
                        if (response.getSuccessData().parameters == null) {
                            showError(UiText.DynamicString(response.getSuccessData().status.description))
                            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
                            return@collectLatest
                        }
                    }
                    _uiState.value = _uiState.value.copy(inquiry = response)
                }
            }
        }
    }

    private fun setMobileContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(mobileContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    fun setNationalId(nationalId: String) {
        _uiState.value = _uiState.value.copy(nationalId = nationalId)
    }

    fun dismissNationalIdValidationMessage() {
        _uiState.value = _uiState.value.copy(nationalIdValidationMessage = UiText.DynamicString(""))
    }

    fun setLicenceNumber(licenceNumber: String) {
        _uiState.value = _uiState.value.copy(licenceNumber = licenceNumber)
    }

    fun dismissLicenceNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(licenceNumberValidationMessage = UiText.DynamicString(""))
    }

    fun setMobileNumber(mobileNumber: String) {
        _uiState.value =
            _uiState.value.copy(mobilePhoneContactSpinner = _uiState.value.mobileContactSheetList.filter {
                it.value.contains(mobileNumber) || it.name?.contains(mobileNumber) == true
            }.map { it.value })
        _uiState.value = _uiState.value.copy(mobileNumber = mobileNumber)
    }

    fun dismissMobileNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(mobileNumberValidationMessage = UiText.DynamicString(""))
    }

    fun dismissMobilePhoneSpinner() {
        _uiState.value = _uiState.value.copy(mobilePhoneContactSpinner = emptyList())
    }


    fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }


}