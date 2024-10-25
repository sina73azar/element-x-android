package com.drp.refah.card_facilities.data.model

import com.google.gson.annotations.SerializedName

data class CardInfo(
    var active: Boolean? = null,
    var cardType: String? = null,
    @SerializedName("sourceAccountNumber") var sourceAccount: String? = null,
    @SerializedName("customerNumber") var sourceCard: String? = null,
    var sourceCardNumber: String? = null
)