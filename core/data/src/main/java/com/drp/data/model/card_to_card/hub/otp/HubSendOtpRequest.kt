package com.drp.refah.card_facilities.data.model.card_to_card.hub.otp


/**
 * HubSecurityType
 * transactionType follows HubTransactionType enum
 * */
data class HubSendOtpRequest(

    val trackingNumber: String,
    val sourcePAN: String,
    val destinationPAN: String,
    val amount: Long,
    val transactionType: String? = null,
    val acceptorName: String? = null,
    val approvalCode: String? = null,
    val rrn: String? = null,
    val stan: Long? = null,
    val accessAddress: String? = null,
    val securityType: String? = null,
    val securityFactor: String? = null,
)
