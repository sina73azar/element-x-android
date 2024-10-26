package com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry

import androidx.annotation.Keep


/**
 * hubStatus uses HubStatus enum class defined in the models
 * */

@Keep
data class HubCardHolderInquiryResponse(
    val trackingNumber: String,
    val transactionId: String,
    val registrationDate: String,
    val registrationDatePersian: String? = null,
    val stan: Long,
    val rrn: String,
    val transactionDate: String,
    val additionalResponseData: String? = null,
    val cardHolderName: String,
    val approvalCode: String? = null,
    val amount: Long,
    val securityFactor: String? = null,
)
