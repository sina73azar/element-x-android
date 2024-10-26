package com.drp.card_facilities.presentation.app_source_card_handler

import com.drp.refah.ui.data.model.SMSState
import com.drp.shared_ui.model.CardShotItemInfo
import io.element.android.x.refa.enums.UiText
import kotlinx.serialization.Serializable

@Serializable
data class SourceCardUiState(
    var selectedCard: CardShotItemInfo? = null,
    var cvv2: String = "",
    var cvv2ValidationMessage: UiText = UiText.DynamicString(""),
    var month: String = "",
    var monthError: Boolean = false,
    var year: String = "",
    var yearError: Boolean = false,
    var otpCode: String = "",
    var otpCodeValidationMessage: UiText = UiText.DynamicString(""),
    var smsState: SMSState = SMSState(),
    var cardOtpRqId: String = "",
)
