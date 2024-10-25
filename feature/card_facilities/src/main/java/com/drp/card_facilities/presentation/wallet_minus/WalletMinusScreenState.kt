package com.drp.card_facilities.presentation.wallet_minus

import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.SearchSheetItemModel

data class WalletMinusScreenState(
    val walletUiState: WalletUiState = WalletUiState(),
    var iban: String = "",
    var ibanValidationMessage: UiText = UiText.DynamicString(""),
    var destinationCardNumber: String = "",
    var destinationCardNumberValidationMessage: UiText = UiText.DynamicString(""),
    val destinationCardContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val destinationCardContactSpinner: List<String> = emptyList(),
    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),
    var description: String = "",

    var getOtpBottomSheetState: Boolean = false,

    var transferState: RequestState<Unit> = RequestState.Idle,

    var selectedToggle: CustomToggleModel? = null
)
