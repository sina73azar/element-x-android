package com.drp.superapp.domain.model.remote.refresh_token

import com.google.gson.annotations.SerializedName

data class ShahkarRefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
