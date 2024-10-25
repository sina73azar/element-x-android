package com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry

import androidx.annotation.Keep


/**
 * Mr.C 1403/1/20
 * for sourcePan we initially test with cardId
 * for sourceAddress initially send constant string then we change it to ip or sth
 * */
@Keep
data class HubCardHolderInquiryRequest(
    val sourcePAN: String,
    val destinationPAN: String,
    val amount: Long,
    val sourceAddress: String,
)
