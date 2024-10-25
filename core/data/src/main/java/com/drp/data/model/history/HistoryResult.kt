package com.drp.data.model.history

import com.google.gson.annotations.SerializedName

data class HistoryResult(
    val result: List<HistoryItemResult>
)

data class HistoryItemResult(
    @SerializedName("TRNDATE")
    val transactionDate: String,
    @SerializedName("TRNTIME")
    val transactionTime: String,
    @SerializedName("CREDIT")
    val credit: Double,
    @SerializedName("DEBIT")
    val debit: Double,
    @SerializedName("BALANCE")
    val balance: Double,
    @SerializedName("DESCRIPTION")
    val description: String
)
