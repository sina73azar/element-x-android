package com.drp.refah.card_facilities.data.model.topup.payment

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TopUpPayment(
    val amount: Long,
    val vat: Long? = null,
    val date: Long? = null,
    @SerializedName("sourceAccountNo") var account: String
) : Serializable