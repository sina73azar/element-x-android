package com.drp.card_facilities.presentation.last_ten_statement

import com.drp.data.model.last_ten_statement.LastTenStatementInquiryResponse
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.data.network.RequestState

data class LastTenStatementScreenState(

    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    /** balance screen bottom sheet states */
    val getOtpBottomSheetState: Boolean = false,

    /** balance screen requests */
    val lastTenStatementInquiryInquiryState: RequestState<ArrayList<BankStatement>> = RequestState.Idle,
)
