package com.drp.refah.card_facilities.data.model.topup.inquiry

data class TransactionAmount(
    val transactionType: String,
    val topupOperator: String? = null,
    val amount: Long
)
