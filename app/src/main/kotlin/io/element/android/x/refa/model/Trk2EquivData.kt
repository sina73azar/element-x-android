package com.drp.refah.card_facilities.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Trk2EquivData(
    @SerializedName("cardExpirationYearMonth") var expireDate: String,
    var cvv2: String, var pin: String
) : Serializable