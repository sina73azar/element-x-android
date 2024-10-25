package com.drp.shared_ui.enums

import androidx.annotation.DrawableRes
import com.drp.shared_ui.R
import com.drp.shared_ui.UiText

enum class AuthenticationType(val title: UiText, @DrawableRes val icon: Int) {
    PASSWORD(
        title = UiText.StringResource(R.string.password_login_st),
        icon = R.drawable.ic_password
    ),
    BIOMETRIC(
        title = UiText.StringResource(R.string.biometric_login_st),
        icon = R.drawable.ic_finger_print
    ),
    NONE(title = UiText.DynamicString(""), icon = R.drawable.ic_finger_print)
}