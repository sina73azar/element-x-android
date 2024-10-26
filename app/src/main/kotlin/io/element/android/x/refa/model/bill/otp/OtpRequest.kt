package com.drp.refah.card_facilities.data.model.bill.otp

data class OtpRequest(
    val username: String,
    var reason: String? = null,
    var receiver: String? = null,
    var amount: Long? = null,
)