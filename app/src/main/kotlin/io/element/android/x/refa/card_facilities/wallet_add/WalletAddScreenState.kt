package com.drp.card_facilities.presentation.wallet_add

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import io.element.android.x.refa.enums.UiText

data class WalletAddScreenState(
    /** screen user input parameters */
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),
    val walletUiState: WalletUiState = WalletUiState(),
    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),
    var description: String = "",
    var getOtpBottomSheetState: Boolean = false,
    var transferState: RequestState<Unit> = RequestState.Idle
)
