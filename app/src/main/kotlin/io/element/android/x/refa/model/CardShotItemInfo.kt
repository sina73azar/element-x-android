package com.drp.shared_ui.model

import kotlinx.serialization.Serializable

@Serializable
data class CardShotItemInfo(
    val id: Int = -1,
    var pan: String,
    var bankName: String,
    var bankIcon: String,
    var panExpiryMonth: String? = null,
    var panExpiryYear: String? = null,
    var maskedPan: String? = null,
    var isRefahSource: Boolean = false,
    val default: Boolean = false
)
