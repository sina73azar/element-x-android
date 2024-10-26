package com.drp.card_facilities.presentation.balance

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.model.balance.CardBalanceFromWalletResult
import com.drp.data.network.RequestState


data class BalanceScreenState(

    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    /** balance screen bottom sheet states */
    val getOtpBottomSheetState: Boolean = false,

    /** balance screen requests */
    val balanceInquiryState: RequestState<CardBalanceFromWalletResult> = RequestState.Idle,
)
