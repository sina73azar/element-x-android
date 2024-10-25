package com.drp.card_facilities.presentation.wallet_to_wallet

import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import com.drp.shared_ui.UiText

data class WalletToWalletScreenState(
    val walletUiState: WalletUiState = WalletUiState(),

    var destinationWalletId: String = "",
    var destinationWalletIdValidationMessage: UiText = UiText.DynamicString(""),
    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),
    var description: String = "",

    var getOtpBottomSheetState: Boolean = false,
    var transferState: RequestState<Unit> = RequestState.Idle

)
