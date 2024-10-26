package com.drp.data.model.shahkar.inquiry

data class ShahkarInquiryRequest(
    var productID: String,
    var nationalID: String,
    var mobile: String,
    var imei: String,
    var birthDate: String,
    var secKey1: String,
    var secKey2: String,
    /*var username: String,
    @SerializedName("grant_type")
    var grantType: String = "ext_shk"*/
)
