package com.drp.superapp.shahkar.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.network.CustomResponse
import com.drp.data.repository.ShahkarLoginRepository
import com.drp.superapp.util.Commons.isValidSMSTokenNo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShahkarOtpSheetViewModel @Inject constructor(private val shahkarLoginRepository: ShahkarLoginRepository) :
    ViewModel() {

    private val _register = MutableLiveData<CustomResponse<ShahkarInquiryResult>>()
    val register: LiveData<CustomResponse<ShahkarInquiryResult>> = _register
    private val _validate = MutableLiveData<CustomResponse<ShahkarValidateResult>>()
    val validate: LiveData<CustomResponse<ShahkarValidateResult>> = _validate

    fun authRegister(
        nationalCode: String,
        phoneNumber: String,
        imei: String,
        birthDate: String
    ) {
        _register.postValue(CustomResponse.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val registerRequest = ShahkarInquiryRequest(
                productID = "1001",
                nationalID = nationalCode,
                mobile = phoneNumber,
                imei = imei,
                birthDate = birthDate,
                secKey1 = "SecurityKey123154",
                secKey2 = ""
            )
//            val headers = mapOf("AccessParameter" to phoneNumber)
//            _register.postValue(CustomResponse.Success(""))
            shahkarLoginRepository.shahkarInquiry(registerRequest/*, headers*/)
                .collect { result -> _register.postValue(result) }
        }
    }

    fun authValidate(phoneNumber: String, token: String, requestId: String) {
        val validateRequest = ShahkarValidateRequest(rqid = requestId, pin = token)
        /*val headers = mapOf(
            "AccessParameter" to phoneNumber,
            "x-otp-code" to token
        )*/
        _validate.postValue(CustomResponse.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            /*_validate.postValue(
                CustomResponse.Success(
                    ShahkarValidateResult(
                        accessToken = "",
                        refreshToken = "",
                        expiresIn = 10L,
                        scope = "",
                        iat = 123L,
                        firstName = "",
                        lastName = ""
                    )
                )
            )*/
            shahkarLoginRepository.authValidate(validateRequest/*, headers*/)
                .collect { result ->
                    _validate.postValue(result)
                }
        }
    }

    fun isValidSMSToken(token: String): Boolean {
        return isValidSMSTokenNo(token)
    }
}