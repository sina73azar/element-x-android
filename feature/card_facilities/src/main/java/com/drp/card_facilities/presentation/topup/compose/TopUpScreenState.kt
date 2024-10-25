package com.drp.card_facilities.presentation.topup.compose

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.data.model.topup.inquiry.CheckAmountResult
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.SearchSheetItemModel


data class TopUpScreenState(
    /** screen user input parameters */
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),
    var walletUiState: WalletUiState = WalletUiState(),

    var mobileNumber: String = "",
    var mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobilePhoneContactSpinner: List<String> = emptyList(),

    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),
    var wowAmount: String = "",
    var wowAmountError: Boolean = false,

    /** screen code parameters */
    val cardOrWalletToggle: CustomToggleModel? = null,
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    var selectedMobileOperatorTab: MobileOperatorTab = MobileOperatorTab.MTN,
    var selectedToggle: CustomToggleModel? = null,
    var operatorTabFinderCalled: Boolean = false,
    var walletOtpBottomSheetVisibility: Boolean = false,

    /** server requests */
    val inquiry: RequestState<CheckAmountResult> = RequestState.Idle,
    val topUpPayment: RequestState<Any> = RequestState.Idle,

    )
