package com.drp.data.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getCall(
        @Url utl: String,
        @HeaderMap headers: Map<String, String>? = mapOf(),
        @QueryMap queryParams: Map<String, String>? = mapOf()
    ): Response<ResponseBody>

    @POST
    suspend fun postCall(
        @Url url: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>? = mapOf()
    ): Response<ResponseBody>
}