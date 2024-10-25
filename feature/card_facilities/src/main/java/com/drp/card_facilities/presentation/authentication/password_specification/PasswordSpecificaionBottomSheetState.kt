package com.drp.card_facilities.presentation.authentication.password_specification

import com.drp.shared_ui.UiText


data class PasswordSpecificaionBottomSheetState(
    /** password bottom sheet parameters */
    var password: String = "",
    var passwordValidationMessage: UiText = UiText.DynamicString(""),
    var passwordRepeat: String = "",
    var passwordRepeatValidationMessage: UiText = UiText.DynamicString(""),

    var isPasswordSaveActionCompleted: Boolean = false
)
