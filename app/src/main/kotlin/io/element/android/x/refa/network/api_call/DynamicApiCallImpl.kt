package com.drp.data.network.api_call

import com.drp.data.network.ApiService
import com.drp.data.network.getDynamicArrayResult
import com.drp.data.network.getDynamicResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import javax.inject.Inject


class DynamicApiCallImpl @Inject constructor(
    private val apiService: ApiService
) : DynamicApiCall {
    override fun <T, Y : Any> dynamicPostCall(
        url: String,
        request: T?,
        headers: Map<String, String>?,
        kClass: Class<Y>
    ) = flow {

            emit(getDynamicResult(kClass) {
                apiService.postCall(
                    url,
                    convertToJsonObject(request),
                    headers
                )
            })
    }.flowOn(Dispatchers.IO)

    override fun <Y : Any> dynamicGetCall(
        url: String,
        queryParams: Map<String, String>?,
        headers: Map<String, String>?,
        kClass: Class<Y>
    ) = flow {
        emit(
            getDynamicResult(kClass) { apiService.getCall(url, headers, queryParams) }
        )
    }.flowOn(Dispatchers.IO)

    private fun <T> convertToJsonObject(data: T?): RequestBody {
        val gson = Gson()
        val json = gson.toJson(data)
        // Convert JSON string to RequestBody
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        return RequestBody.create(JSON, json)

    }
    override fun <Y : Any> dynamicArrayGetCall(
        url: String,
        queryParams: Map<String, String>?,
        headers: Map<String, String>?,
        kClass: Class<Y>
    ) = flow {
        emit(
            getDynamicArrayResult(kClass) { apiService.getCall(url) }
        )
    }.flowOn(Dispatchers.IO)
    override fun <T, Y : Any> dynamicPostArrayCall(
        url: String,
        request: T?,
        headers: Map<String, String>?,
        kClass: Class<Y>
    ) = flow {
        emit(getDynamicArrayResult(kClass) {
            apiService.postCall(
                url,
                convertToJsonObject(request),
                headers
            )
        })
    }.flowOn(Dispatchers.IO)
}