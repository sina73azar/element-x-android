package com.drp.card_facilities.presentation.history

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.data.model.history.HistoryResult
import com.drp.data.network.RequestState


data class HistoryScreenState(
    val transactions: RequestState<HistoryResult> = RequestState.Idle,
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState()
)
