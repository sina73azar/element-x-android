package com.drp.card_facilities.presentation.vehicle_violation

import com.drp.data.model.vehicle_violation.VehicleViolationResponse
import com.drp.data.network.RequestState
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.UiText

data class VehicleViolationScreenState(
    val leftNumber: String = "",
    val leftNumberError: Boolean = false,
    val alphabetic: String = "",
    val alphabeticError: Boolean = false,
    val midNumber: String = "",
    val midNumberError: Boolean = false,
    val rightNumber: String = "",
    val rightNumberError: Boolean = false,
    val mobileNumber: String = "",
    val mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val mobilePhoneContactSpinner: List<String> = emptyList(),
    val nationalId: String = "",
    val nationalIdValidationMessage: UiText = UiText.DynamicString(""),
    val inquiry: RequestState<VehicleViolationResponse> = RequestState.Idle
)
