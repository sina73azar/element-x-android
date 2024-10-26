package com.drp.data.model.balance

data class CardBalanceFromWalletRequest(
    val cardExpiration: String,
    val cardNumber: String,
    val cvv2: String,
    val pin: String,
)
