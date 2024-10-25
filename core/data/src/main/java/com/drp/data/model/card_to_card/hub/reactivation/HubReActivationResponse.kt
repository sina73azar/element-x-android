package com.drp.refah.card_facilities.data.model.card_to_card.hub.reactivation

data class HubReActivationResponse(
    val transactionId: String,
    val trackingNumber: String,
    val reactivationAddress: String,
    val registrationDate: String? = null, //TimeStamp
    val registrationDatePersian: String? = null,
)
