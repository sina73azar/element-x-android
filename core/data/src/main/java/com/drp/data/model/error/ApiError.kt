package com.adel.data.model.error

data class ApiError(
    val status: Int? = null,
    var title: String? = null,
    var error: String? = null,
    var detail: String? = null,
    var messageKey: String? = null,
    var code: Int? = null,
    var message: Any? = null
)