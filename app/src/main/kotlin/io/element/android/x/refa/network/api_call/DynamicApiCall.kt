package com.drp.data.network.api_call

import com.drp.data.network.CustomResponse
import kotlinx.coroutines.flow.Flow

interface DynamicApiCall {
    fun <T, Y : Any> dynamicPostCall(
        url: String,
        request: T? = null,
        headers: Map<String, String>? = mapOf(),
        kClass: Class<Y>,
    ): Flow<CustomResponse<Y>>

    fun <Y : Any> dynamicGetCall(
        url: String,
        queryParams: Map<String, String>? = mapOf(),
        headers: Map<String, String>? = mapOf(),
        kClass: Class<Y>
    ): Flow<CustomResponse<Y>>
    fun <T, Y : Any> dynamicPostArrayCall(
        url: String,
        request: T? = null,
        headers: Map<String, String>? = mapOf(),
        kClass: Class<Y>,
    ): Flow<CustomResponse<ArrayList<Y>>>


    fun <Y : Any> dynamicArrayGetCall(
        url: String,
        queryParams: Map<String, String>?,
        headers: Map<String, String>?,
        kClass: Class<Y>
    ): Flow<CustomResponse<ArrayList<Y>>>
}