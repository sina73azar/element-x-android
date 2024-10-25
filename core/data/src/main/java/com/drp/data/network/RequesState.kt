package com.drp.data.network

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Error(val message: String) : RequestState<Nothing>()
    data object Fail : RequestState<Nothing>()
    data class Success<T>(val value: T) : RequestState<T>()

    fun isSuccess() = this is Success
    fun getSuccessData() = (this as Success).value
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun getErrorMessage() = (this as Error).message
    fun isFail() = this is Fail
}

fun <T> CustomResponse<T>.toRequestState(): RequestState<T> {
    when (this.status) {
        CustomResponse.Status.LOADING -> {
            return RequestState.Loading
        }

        CustomResponse.Status.Fail -> {
            return RequestState.Fail
        }

        CustomResponse.Status.ERROR -> {
            return this.message?.let { RequestState.Error(it) }
                ?: RequestState.Error("خظای نامشخص")
        }

        CustomResponse.Status.SUCCESS -> {
            return this.data?.let { RequestState.Success(it) }
                ?: RequestState.Error("خطای نامشخص")
        }
    }
}