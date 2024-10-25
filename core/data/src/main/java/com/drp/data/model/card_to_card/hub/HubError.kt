package com.drp.refah.card_facilities.data.model.card_to_card.hub

data class HubError(
    val errorCode: Long,
    val errorDescription: String,
    val extraData: String? = null,
    val originalValue: String? = null,
    val referenceName: String? = null,
    val cmCode: String? = null,
)
