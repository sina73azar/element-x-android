package com.drp.refah.card_facilities.data.model.card_to_card.hub.transfer

import com.drp.refah.card_facilities.data.model.card_to_card.hub.SuccessStatus


/**
 * Mr.C 1403/1/29
 * transactionDate format is millis
 * */
data class HubCardTransferResponse(
    val status: com.drp.refah.card_facilities.data.model.card_to_card.hub.SuccessStatus? = null,
    val trackingNumber: String? = null,
    val transactionId: String? = null,
    val registrationDate: String? = null,
    val transactionDate: Long? = null,
    val registrationDatePersian: String? = null,
    val stan: Long? = null,
    val rrn: String? = null,
    val additionalResponseData: String? = null,
    val amount: Long? = null,
    val securityFactor: String? = null,
)
