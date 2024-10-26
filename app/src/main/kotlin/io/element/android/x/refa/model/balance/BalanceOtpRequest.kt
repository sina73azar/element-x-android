package com.drp.data.model.balance

data class BalanceOtpRequest(
    val cardExpiration: String,
    val cardNumber: String,
    val cvv2: String,
    val pin: String,
    val amount: Long,
    val dynPinRequestType: Int,
    val additionalInformation: String
)
