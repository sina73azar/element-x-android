package com.drp.data.model.wallet_add

import com.drp.data.model.Parameter

data class AddToWalletRequest(
    val parameters: List<Parameter>,
    val callType: String,
    val encoding: String,
    val requestId: String
)
