package com.drp.card_facilities.presentation.motor_violation

import com.drp.data.model.motor_violation.MotorViolationResponse
import com.drp.data.network.RequestState
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.UiText

data class MotorViolationScreenState(
    val leftNumber: String = "",
    val leftNumberError: Boolean = false,
    val rightNumber: String = "",
    val rightNumberError: Boolean = false,
    val mobileNumber: String = "",
    val mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val mobilePhoneContactSpinner: List<String> = emptyList(),
    val nationalId: String = "",
    val nationalIdValidationMessage: UiText = UiText.DynamicString(""),
    val inquiry: RequestState<MotorViolationResponse> = RequestState.Idle
)
