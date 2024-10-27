package com.drp.card_facilities.presentation.shahkar

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.model.ShahkarUserData
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.ui.data.model.SMSState
import com.drp.utils.isValidMobileNo
import com.drp.utils.isValidNationalCode
import io.element.android.appconfig.SharedData
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ShahkarViewModel(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShahkarScreenState())
    val uiState: StateFlow<ShahkarScreenState> = _uiState
    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    private fun validateFields(): Boolean {
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

    fun inquiry(imei: String) {
        if (!validateFields())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
            _uiState.value = _uiState.value.copy(smsState = SMSState(SMSStateLoading = true))
            val request = ShahkarInquiryRequest(
                productID = "1001",
                nationalID = uiState.value.nationalId,
                mobile = uiState.value.mobileNumber,
                imei = imei,
                birthDate = "13740414",
                secKey1 = "SecurityKey123154",
                secKey2 = ""
            )
            cardFacilitiesUserRepository.shahkarInquiry(request).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError()) {
                        showError(UiText.DynamicString(response.getErrorMessage()))
                        /*_uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateInquiryErrorMessage = response.getErrorMessage()))*/
                    }
                    /*if (response.isFail()) {
                        _uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateInquiryFail = true))
                    }*/
                    if (response.isSuccess()) {
                        _uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateSuccess = true))
                        _uiState.value.pRqId = response.getSuccessData().result.pRqId?.toString()
                    }
                    _uiState.value = _uiState.value.copy(inquiry = response)
                }
            }
        }
    }

    fun sendOtp(imei: String) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(smsState = SMSState(SMSStateLoading = true))
            val request = ShahkarInquiryRequest(
                productID = "1001",
                nationalID = uiState.value.nationalId,
                mobile = uiState.value.mobileNumber,
                imei = imei,
                birthDate = "13740414",
                secKey1 = "SecurityKey123154",
                secKey2 = ""
            )
            cardFacilitiesUserRepository.shahkarInquiry(request).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        _uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateInquiryErrorMessage = response.getErrorMessage()))
                    if (response.isFail())
                        _uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateInquiryFail = true))
                    if (response.isSuccess()) {
                        _uiState.value =
                            _uiState.value.copy(smsState = SMSState(SMSStateSuccess = true))
                        _uiState.value.pRqId = response.getSuccessData().result.pRqId?.toString()
                    }
                }
            }
        }
    }

    fun validate() {
        if (!isValidSMSToken(uiState.value.otpCode) || uiState.value.pRqId.isNullOrEmpty()) {
            _uiState.value =
                _uiState.value.copy(
                    otpCodeValidationMessage = UiText.DynamicString("مشتری گرامی لطفا پیامک فعالسازی را صحیح وارد نمایید.")/*StringResource(
                        R.string.otp_register_failure_st
                    )*/
                )
            return
        }
        uiState.value.pRqId?.let {
            viewModelScope.launch(dispatcher) {
                _uiState.value = _uiState.value.copy(validate = RequestState.Loading)
                val validateRequest = ShahkarValidateRequest(rqid = it, pin = uiState.value.otpCode)
                cardFacilitiesUserRepository.authValidate(validateRequest).collectLatest {
                    it.toRequestState().let { response ->
                        if (response.isError())
                            showError(UiText.DynamicString(response.getErrorMessage()))
                        if (response.isSuccess()) {
                            val data = response.getSuccessData().result
                            SharedData.userName = data.pImUserName
                            SharedData.pass = data.pImPassword
                            cardFacilitiesUserRepository.saveShahkarUserData(
                                ShahkarUserData(
                                    nationalCode = uiState.value.nationalId,
                                    phoneNumber = uiState.value.mobileNumber,
                                    accessToken = data.pAccessToken,
                                    walletId = data.pWalletId,
                                    personId = data.pPersonId,
                                    pImHomeServer = data.pImHomeServer,
                                    pImDeviceId = data.pImDeviceId,
                                    pImUserName = data.pImUserName,
                                    pImUserId = data.pImUserId,
                                    pImPassword = data.pImPassword,
                                    pImToken = data.pImToken
                                )
                            )
                        }
                        _uiState.value = _uiState.value.copy(validate = response)
                    }
                }
            }
        }
    }

    fun isValidSMSToken(token: String): Boolean {
        return isValidSMSTokenNo(token)
    }

    fun isValidSMSTokenNo(token: String): Boolean {
        if (token.isDigitsOnly() && token.length >= 4) return true
        return false
    }

    fun setNationalId(nationalId: String) {
        _uiState.value = _uiState.value.copy(nationalId = nationalId)
    }

    fun dismissNationalIdValidationMessage() {
        _uiState.value = _uiState.value.copy(nationalIdValidationMessage = UiText.DynamicString(""))
    }

    fun setMobileNumber(mobileNumber: String) {
        _uiState.value = _uiState.value.copy(mobileNumber = mobileNumber)
    }

    fun dismissMobileNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(mobileNumberValidationMessage = UiText.DynamicString(""))
    }

    fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
        if (_uiState.value.validate.isFail())
            _uiState.value = _uiState.value.copy(validate = RequestState.Idle)
    }

    fun setOtpCode(otpCode: String) {
        _uiState.value = _uiState.value.copy(otpCode = otpCode)
    }

    fun dismissOtpCodeValidationMessage() {
        _uiState.value = _uiState.value.copy(otpCodeValidationMessage = UiText.DynamicString(""))
    }

    fun backToDefault() {
        _uiState.value = _uiState.value.copy(
            inquiry = RequestState.Idle,
            otpCode = "",
            smsState = SMSState(),
            pRqId = "",
            validate = RequestState.Idle
        )
    }

    fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }
}
