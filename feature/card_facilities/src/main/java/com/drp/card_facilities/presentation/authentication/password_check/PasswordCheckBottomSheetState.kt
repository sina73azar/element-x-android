package com.drp.card_facilities.presentation.authentication.password_check

import com.drp.shared_ui.UiText


data class PasswordCheckBottomSheetState(
    /** password bottom sheet parameters */
    var password: String = "",
    var passwordValidationMessage: UiText = UiText.DynamicString(""),

    var isPasswordCheckActionCompleted: Boolean = false
)
