package com.drp.card_facilities.presentation.shahkar

import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.network.RequestState
import com.drp.refah.ui.data.model.SMSState
import io.element.android.x.refa.enums.UiText

data class ShahkarScreenState(
    val mobileNumber: String = "",
    val mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val nationalId: String = "",
    val nationalIdValidationMessage: UiText = UiText.DynamicString(""),
    val inquiry: RequestState<ShahkarInquiryResult> = RequestState.Idle,
    val validate: RequestState<ShahkarValidateResult> = RequestState.Idle,
    var pRqId: String? = "",
    var otpCode: String = "",
    var otpCodeValidationMessage: UiText = UiText.DynamicString(""),
    var smsState: SMSState = SMSState(),
)
