package com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey


data class HubCardInfo(
    val transactionId: String,
    val cardId: Long,
    val maskedPan: String,
    val default: Boolean,
    val referenceExpiryDate: String? = null,
    val referenceExpiryDatePersian: String? = null,
    val panExpiryDatePersian: String? = null,
    val pan: String? = null,
    val panExpiryMonth: String? = null,
    val panExpiryYear: String? = null,
    val assuranceLevel: String? = null,
    val expired: Boolean = false,
)
