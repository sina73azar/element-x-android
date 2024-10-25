package com.drp.data.repository

import com.drp.data.model.ShahkarUserData
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.network.CustomResponse
import com.drp.superapp.domain.model.remote.refresh_token.ShahkarRefreshTokenRequest
import kotlinx.coroutines.flow.Flow

interface ShahkarLoginRepository {
    fun shahkarInquiry(
        request: ShahkarInquiryRequest,
//        headers: Map<String, String>
    ): Flow<CustomResponse<ShahkarInquiryResult>>

    fun authValidate(
        request: ShahkarValidateRequest,
//        headers: Map<String, String>
    ): Flow<CustomResponse<ShahkarValidateResult>>

    fun refreshToken(
        request: ShahkarRefreshTokenRequest,
        headers: Map<String, String>
    ): Flow<CustomResponse<ShahkarValidateResult>>

    fun saveShahkarUserData(shahkarUserData: ShahkarUserData)

    fun loadShahkarUserData(): ShahkarUserData
    fun clearShahkarUserData()
}