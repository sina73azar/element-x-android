package com.drp.data.model.shahkar.validate

data class ShahkarValidateRequest(
    var rqid: String,
    var pin: String
    /*val username: String,
    @SerializedName("grant_type")
    var grantType: String = "ext_shk"*/
)
