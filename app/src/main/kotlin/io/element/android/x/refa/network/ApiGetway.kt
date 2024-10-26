package com.drp.data.network

import android.util.Log
import com.adel.data.model.error.ApiError
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

suspend fun <T : Any> getDynamicResult(
    kClass: Class<T>,
    call: suspend () -> Response<ResponseBody>,
): CustomResponse<T> {

    try {
        val response = call()
        if (response.code() == 200 || response.code() == 201) {
            val body = response.body()
            val gson = Gson()
            val result =
                gson.fromJson(body?.charStream(), kClass)
            return CustomResponse.Success<T>(result)
        } else {
            val gson = Gson()
            val apiError: ApiError? =
                gson.fromJson(response.errorBody()!!.charStream(), ApiError::class.java)
            val url = response.raw().request.url.toString()
            /**
             * handle messageKey as the enum error for custom error handling
             * just push the messageKey to the UI
             * */

            if (apiError?.code != null && apiError.code!! >= 500) {
                if (url.contains("Payment", ignoreCase = true) ||
                    url.contains("FundTransfer", ignoreCase = true) ||
                    url.contains("topup", ignoreCase = true) ||
                    url.contains("loanPayment", ignoreCase = true)
                ) {
                    return if (!url.contains(
                            "payment/ChannelPaymentTypeInquiry",
                            ignoreCase = true
                        )
                    )
                        CustomResponse.Fail()
                    else
                        CustomResponse.Error(
                            "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                            apiError.messageKey
                        )
                } else {
                    return if (!apiError.detail.isNullOrEmpty())
                        (
                                CustomResponse.Error(
                                    apiError.detail.toString(),
                                    apiError.messageKey
                                )
                                )
                    else {
                        CustomResponse.Error(
                            "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                            apiError.messageKey
                        )
                    }
                }

            } else {
                return if (!apiError?.detail.isNullOrEmpty())
                    CustomResponse.Error(
                        apiError?.detail.toString(),
                        apiError?.messageKey
                    )
                else {
                    CustomResponse.Error(
                        "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                        apiError?.messageKey
                    )
                }
            }


        }
    } catch (e: java.lang.Exception) {
        Log.d("retrofit exception", e.message.toString())
        return when (e) {
            is UnknownHostException -> {
                CustomResponse.Fail()
            }

            is ConnectException -> {
                CustomResponse.Fail()
            }

            is JsonSyntaxException -> {
                Log.d("ExceptionCatcher", "getResult: JsonSyntaxException")
                CustomResponse.Fail()
            }

            else ->
                CustomResponse.Error("خطا در دسترسی به سامانه،دقایقی دیگر مجدد تلاش کنید.")
        }
    }
}

suspend fun <T : Any> getDynamicArrayResult(
    kClass: Class<T>,
    call: suspend () -> Response<ResponseBody>,
): CustomResponse<ArrayList<T>> {

    try {
        val response = call()
        if (response.code() == 200 || response.code() == 201) {
            val body = response.body()
            val gson = Gson()
            val type = TypeToken.getParameterized(ArrayList::class.java, kClass).type
            val result =
                gson.fromJson<ArrayList<T>>(body?.charStream(), type)
            return CustomResponse.Success<ArrayList<T>>(result)
        } else {
            val gson = Gson()
            val apiError: ApiError? =
                gson.fromJson(response.errorBody()!!.charStream(), ApiError::class.java)
            val url = response.raw().request.url.toString()
            /**
             * handle messageKey as the enum error for custom error handling
             * just push the messageKey to the UI
             * */

            if (apiError?.code != null && apiError.code!! >= 500) {
                if (url.contains("Payment", ignoreCase = true) ||
                    url.contains("FundTransfer", ignoreCase = true) ||
                    url.contains("topup", ignoreCase = true) ||
                    url.contains("loanPayment", ignoreCase = true)
                ) {
                    return if (!url.contains(
                            "payment/ChannelPaymentTypeInquiry",
                            ignoreCase = true
                        )
                    )
                        CustomResponse.Fail()
                    else
                        CustomResponse.Error(
                            "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                            apiError.messageKey
                        )
                } else {
                    return if (!apiError.detail.isNullOrEmpty())
                        (
                                CustomResponse.Error(
                                    apiError.detail.toString(),
                                    apiError.messageKey
                                )
                                )
                    else {
                        CustomResponse.Error(
                            "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                            apiError.messageKey
                        )
                    }
                }

            } else {
                return if (!apiError?.detail.isNullOrEmpty())
                    CustomResponse.Error(
                        apiError?.detail.toString(),
                        apiError?.messageKey
                    )
                else {
                    CustomResponse.Error(
                        "خطا در دسترسی به سامانه، مجدد تلاش کنید.",
                        apiError?.messageKey
                    )
                }
            }


        }
    } catch (e: java.lang.Exception) {
        Log.d("retrofit exception", e.message.toString())
        return when (e) {
            is UnknownHostException -> {
                CustomResponse.Fail()
            }

            is ConnectException -> {
                CustomResponse.Fail()
            }

            is JsonSyntaxException -> {
                Log.d("ExceptionCatcher", "getResult: JsonSyntaxException")
                CustomResponse.Fail()
            }

            else ->
                CustomResponse.Error("خطا در دسترسی به سامانه،دقایقی دیگر مجدد تلاش کنید.")
        }
    }
}
