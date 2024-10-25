package com.drp.refah.ui.data.model


import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceItem(
    var type: ServicesType,
    var title: String,
    var color: Int,
    var showDelete: Boolean = false,
    var iconName: String? = "ic_chart",
    var checked: Boolean = false,
    var serviceId: Int,
    var path: String? = null,
    var serviceIntent: Intent? = null,
    var loginType: LoginType = LoginType.NONE,
    var isNewService: Boolean = false

    ) : Parcelable

enum class ServicesType {
    ACTIVITY,
    WEB,
    APK,
    INTENT
}
enum class LoginType {
    CHANNEL,
    SHAHKAR,
    SIGNATURE,
    NONE
}