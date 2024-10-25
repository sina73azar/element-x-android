package com.drp.data.model.history

data class HistoryRequest(
    val walletId: String,
    val fromDate: String,
    val toDate: String
)
