package com.drp.card_facilities.presentation.app_wallet_handler

import com.drp.data.model.ShahkarUserData
import com.drp.refah.ui.data.model.SMSState
import io.element.android.x.refa.enums.UiText

data class WalletUiState(
    var shahkarUserData: ShahkarUserData = ShahkarUserData(),
    var walletOtpCode: String = "",
    var walletOtpCodeValidationMessage: UiText = UiText.DynamicString(""),
    var walletSmsState: SMSState = SMSState(),
    var walletOtpRqId: String = ""
)
