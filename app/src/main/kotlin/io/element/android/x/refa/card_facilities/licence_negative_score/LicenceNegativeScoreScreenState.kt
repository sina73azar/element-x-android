package com.drp.card_facilities.presentation.licence_negative_score

import com.drp.data.model.licence_negative_score.LicenceNegativeScoreInquiryResponse
import com.drp.data.network.RequestState
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.UiText

data class LicenceNegativeScoreScreenState(
    val licenceNumber: String = "",
    val licenceNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobileNumber: String = "",
    val mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val mobilePhoneContactSpinner: List<String> = emptyList(),
    val nationalId: String = "",
    val nationalIdValidationMessage: UiText = UiText.DynamicString(""),
    val inquiry: RequestState<LicenceNegativeScoreInquiryResponse> = RequestState.Idle
)
