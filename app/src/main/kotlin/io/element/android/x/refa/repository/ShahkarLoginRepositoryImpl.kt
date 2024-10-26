package com.drp.data.repository

import com.drp.data.model.ShahkarUserData
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints.SHAHKAR_INQUIRY_ENDPOINT
import com.drp.data.network.EndPoints.SHAHKAR_REFRESH_TOKEN_ENDPOINT
import com.drp.data.network.EndPoints.SHAHKAR_VALIDATE_ENDPOINT
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.data.repository.CardFacilitiesRepositoryImpl.Companion.SHAHKAR_USER_DATA
import com.drp.data.sharepref.DynamicPreferences
import com.drp.superapp.domain.model.remote.refresh_token.ShahkarRefreshTokenRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ShahkarLoginRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dynamicPreferences: DynamicPreferences
) :
    ShahkarLoginRepository {
    override fun shahkarInquiry(request: ShahkarInquiryRequest/*, headers: Map<String, String>*/) =
        dynamicApiCall.dynamicPostCall(
            url = SHAHKAR_INQUIRY_ENDPOINT,
            request = request,
//            headers = headers,
            kClass = ShahkarInquiryResult::class.java
        )

    override fun authValidate(request: ShahkarValidateRequest/*, headers: Map<String, String>*/) =
        dynamicApiCall.dynamicPostCall(
            url = SHAHKAR_VALIDATE_ENDPOINT,
            request = request,
//            headers = headers,
            kClass = ShahkarValidateResult::class.java
        )

    override fun refreshToken(
        request: ShahkarRefreshTokenRequest,
        headers: Map<String, String>
    ): Flow<CustomResponse<ShahkarValidateResult>> =
        dynamicApiCall.dynamicPostCall(
            url = SHAHKAR_REFRESH_TOKEN_ENDPOINT,
            request = request,
            headers = headers,
            kClass = ShahkarValidateResult::class.java
        )

    override fun saveShahkarUserData(shahkarUserData: ShahkarUserData) {
        val shahkarUserDataJson = Json.encodeToString(shahkarUserData)
        dynamicPreferences.saveData(SHAHKAR_USER_DATA, shahkarUserDataJson)
    }

    override fun loadShahkarUserData(): ShahkarUserData {
        return Json.decodeFromString(
            dynamicPreferences.loadData(
                SHAHKAR_USER_DATA,
                Json.encodeToString(ShahkarUserData())
            ) as String
        )
    }

    override fun clearShahkarUserData() {
        dynamicPreferences.removeData(SHAHKAR_USER_DATA)
    }

}