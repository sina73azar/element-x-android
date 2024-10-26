package com.drp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ShahkarUserData(
    var nationalCode: String? = null,
    var phoneNumber: String? = null,
    var accessToken: String? = null,
    var walletId: Long? = null,
    var personId: Long? = null,
    var pImHomeServer: String? = null,
    var pImDeviceId: String? = null,
    var pImUserName: String? = null,
    var pImUserId: String? = null,
    var pImPassword: String? = null,
    var pImToken: String? = null,
    /*var refreshToken: String? = null,
    var expireDateInMillis: Long? = null*/
)
