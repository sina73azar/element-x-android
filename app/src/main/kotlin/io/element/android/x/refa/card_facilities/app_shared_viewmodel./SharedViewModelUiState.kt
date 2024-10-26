package com.drp.card_facilities.presentation.app_shared_viewmodel

import com.drp.shared_ui.model.CardShotItemInfo


data class SharedViewModelUiState(
    val cardsList: List<CardShotItemInfo> = emptyList()
)
