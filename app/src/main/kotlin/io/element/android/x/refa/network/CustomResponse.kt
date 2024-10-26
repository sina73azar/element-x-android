package com.drp.data.network


sealed class CustomResponse<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null,
    val messageKey: String? = null
) {

    class Success<T>(data: T?) : CustomResponse<T>(Status.SUCCESS, data, null)
    class Error<T>(message: String?, messageKey: String? = null) :
        CustomResponse<T>(Status.ERROR, null, message, messageKey)

    class Fail<T> : CustomResponse<T>(Status.Fail)
    class Loading<T> : CustomResponse<T>(Status.LOADING)

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        Fail
    }
}