package com.drp.data.model.balance

data class CardBalanceFromWalletResult(
    val availableBalance: Long,
    val ledgerBalance: Long,
    val doTime: String
)
