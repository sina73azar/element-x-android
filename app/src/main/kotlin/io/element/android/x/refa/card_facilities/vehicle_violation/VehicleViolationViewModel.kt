package com.drp.card_facilities.presentation.vehicle_violation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.utils.isValidMobileNo
import com.drp.utils.isValidNationalCode
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class VehicleViolationViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleViolationScreenState())
    val uiState: StateFlow<VehicleViolationScreenState> = _uiState
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
        if (uiState.value.leftNumber.length != 2) {
            _uiState.value =
                _uiState.value.copy(leftNumberError = true)
            return false
        }
        if (uiState.value.alphabetic.isEmpty()) {
            _uiState.value =
                _uiState.value.copy(alphabeticError = true)
            return false
        }
        if (uiState.value.midNumber.length != 3) {
            _uiState.value =
                _uiState.value.copy(midNumberError = true)
            return false
        }
        if (uiState.value.rightNumber.length != 2) {
            _uiState.value =
                _uiState.value.copy(rightNumberError = true)
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
            with(uiState.value) {

                cardFacilitiesRepository.vehicleViolationInquiry(
                    leftNumber = leftNumber,
                    alphabetic = alphabetic,
                    midNumber = midNumber,
                    rightNumber = rightNumber,
                    mobileNumber = mobileNumber,
                    nationalId = nationalId,
                    walletId = cardFacilitiesUserRepository.getShahkarUserData().walletId.toString()
                ).collectLatest {
                    it.toRequestState().let { response ->
                        if (response.isError()) {
                            showError(UiText.StringResource(R.string.vehicle_violation_inquiry_error_st))
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
    }

    fun setLeftNumber(leftNumber: String) {
        _uiState.value = _uiState.value.copy(leftNumber = leftNumber)
    }

    fun dismissLeftNumberError() {
        _uiState.value = _uiState.value.copy(leftNumberError = false)
    }

    fun setAlphabetic(alphabetic: String) {
        _uiState.value = _uiState.value.copy(alphabetic = alphabetic)
    }

    fun dismissAlphabeticError() {
        _uiState.value = _uiState.value.copy(alphabeticError = false)
    }

    fun setMidNumber(midNumber: String) {
        _uiState.value = _uiState.value.copy(midNumber = midNumber)
    }

    fun dismissMidNumberError() {
        _uiState.value = _uiState.value.copy(midNumberError = false)
    }

    fun setRightNumber(rightNumber: String) {
        _uiState.value = _uiState.value.copy(rightNumber = rightNumber)
    }

    fun dismissRightNumberError() {
        _uiState.value = _uiState.value.copy(rightNumberError = false)
    }

    fun setNationalId(nationalId: String) {
        _uiState.value = _uiState.value.copy(nationalId = nationalId)
    }

    fun dismissNationalIdValidationMessage() {
        _uiState.value = _uiState.value.copy(nationalIdValidationMessage = UiText.DynamicString(""))
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

    fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }

    fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    fun dismissInquiry() {
        _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    private fun setMobileContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(mobileContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }
}
