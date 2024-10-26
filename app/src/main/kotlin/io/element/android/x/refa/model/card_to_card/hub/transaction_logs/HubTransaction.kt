package com.drp.refah.card_facilities.data.model.card_to_card.hub.transaction_logs

data class HubTransaction(
    val trackingNumber: String,
    val registrationDate: Long? = null,
    val transactionDate: Long,
    val stan: Long? = null,
    val rrn: String? = null,
    val amount: Long,
    val sourcePAN: String? = null,
    val destinationPAN: String? = null,
    val status: String
)
