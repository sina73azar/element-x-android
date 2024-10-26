package com.drp.refah.card_facilities.data.model.card_to_card

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Calendar

data class FundTransfer(
    val amount: Long,
    @SerializedName("sourceCardNumber") val source: String,
    @SerializedName("sourceAccountNumber") var sourceAccount: String? = null,
    @SerializedName("destinationCardNumber") var destination: String? = null,
    var processCode: String? = null,
    val date: Long? = Calendar.getInstance().timeInMillis,
    var personName: DestinationPerson? = null,
    var followupCode: String? = null
) : Serializable