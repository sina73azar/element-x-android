package com.drp.data.model.last_ten_statement

import com.drp.data.model.balance.Balance
import kotlinx.serialization.Serializable

@Serializable
data class BankStatement(
    val date: String,
    val description: String,
    val amount: Double,
    val paymentType: String,
)
