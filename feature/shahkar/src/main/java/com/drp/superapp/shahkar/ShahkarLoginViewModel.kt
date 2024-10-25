package com.drp.superapp.shahkar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.network.CustomResponse
import com.drp.data.repository.ShahkarLoginRepository
import com.drp.utils.isValidMobileNo
import com.drp.utils.isValidNationalCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShahkarLoginViewModel @Inject constructor(
    private val shahkarLoginRepository: ShahkarLoginRepository
) : ViewModel() {

    private val _inquiryResult: MutableLiveData<CustomResponse<ShahkarInquiryResult>> =
        MutableLiveData()
    val inquiryResult: LiveData<CustomResponse<ShahkarInquiryResult>>
        get() = _inquiryResult

    fun checkNationalCode(nationalCode: String): Boolean {
        return isValidNationalCode(nationalCode)
    }

    fun checkMobileNo(mobileNo: String): Boolean {
        return isValidMobileNo(mobileNo)
    }

    fun inquiry(
        nationalCode: String,
        phoneNumber: String,
        birthDate: String,
        imei: String
    ) {
        _inquiryResult.postValue(CustomResponse.Loading())
        val request = ShahkarInquiryRequest(
            productID = "1001",
            nationalID = nationalCode,
            mobile = phoneNumber,
            imei = imei,
            birthDate = birthDate,
            secKey1 = "SecurityKey123154",
            secKey2 = ""
        )
//        val headers = mapOf("AccessParameter" to phoneNumber)
        viewModelScope.launch(Dispatchers.IO) {
            shahkarLoginRepository.shahkarInquiry(request/*, headers*/).collect {
                _inquiryResult.postValue(it)
            }
        }
    }
}


