package com.drp.data.model.balance

import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    val ledgerBalance: Long?,
    val depositableAmount: Long,
    val blockedAmount: Long?,
    val currency: String?
)
