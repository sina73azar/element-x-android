package com.drp.refah.card_facilities.data.model.bill

import com.drp.refah.card_facilities.data.model.ExpireDate
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PaymentTrk2EquivData(
    @SerializedName("cardExpirationYearMonth") var expireDate: ExpireDate,
    var cvv2: String, var pin: String
) : Serializable