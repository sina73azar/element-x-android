package com.drp.data.network

import android.content.Context
import com.drp.data.Constants
import com.drp.data.model.ShahkarUserData
import com.drp.data.sharepref.DynamicPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class OAuthInterceptor(
    @ApplicationContext val context: Context,
    private val dynamicPreferences: DynamicPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = addHeader(chain)
        val response = chain.proceed(request)
        return response
    }

    private fun addHeader(chain: Interceptor.Chain): Request {
        var request = chain.request()
        val shahkarUserData: ShahkarUserData = Json.decodeFromString(
            dynamicPreferences.loadData(
                Constants.SHAHKAR_USER_DATA,
                Json.encodeToString(ShahkarUserData())
            ) as String
        )
        if (!shahkarUserData.accessToken.isNullOrEmpty() && shahkarUserData.walletId != null)
            request = request.newBuilder()
                .addHeader("AccessToken", shahkarUserData.accessToken.toString())
                .addHeader("WalletID", shahkarUserData.walletId.toString())
                .build()
        return request
    }
}