package com.drp.data.model.internet_package.inquiry

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Tags(

    @field:SerializedName("MCI_OFFER_CODE")
    val mciOfferCode: String? = null,

    @field:SerializedName("RIGHTEL_ON_NET_MESSAGE_COUNT")
    val rightelOnNetMessageCount: String? = null,

    @field:SerializedName("RIGHTEL_OFF_NET_MESSAGE_COUNT")
    val rightelOffNetMessageCount: String? = null,

    @field:SerializedName("RIGHTEL_ON_NET_CALL_VOLUME")
    val rightelOnNetCallVolume: String? = null,

    @field:SerializedName("RIGHTEL_OFF_NET_CALL_VOLUME")
    val rightelOffNetCallVolume: String? = null,
)