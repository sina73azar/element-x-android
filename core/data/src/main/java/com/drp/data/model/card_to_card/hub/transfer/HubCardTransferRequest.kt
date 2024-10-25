package com.drp.refah.card_facilities.data.model.card_to_card.hub.transfer


/**
 * Mr.C 1403/1/28
 * securityControl follows defined enum class with same name
 * */
data class HubCardTransferRequest(
    val trackingNumber: String,
    val sourcePAN: String,
    val destinationPAN: String,
    val amount: Long,
    val pin: String? = null,
    val cvv2: String,
    val expiryDate: String,
    val securityControl: String,
    val referenceNumber: String? = null,
    val approvalCode: String? = null,
    val sourceAddress: String? = null,
)
