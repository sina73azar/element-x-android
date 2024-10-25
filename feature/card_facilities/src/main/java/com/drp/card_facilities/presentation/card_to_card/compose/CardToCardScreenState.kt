package com.drp.card_facilities.presentation.card_to_card.compose

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.network.RequestState
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.SearchSheetItemModel

data class CardToCardScreenState(
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    val destinationCard: String = "",
    val destinationCardValidationMessage: UiText = UiText.DynamicString(""),
    val destinationCardContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val destinationCardContactSpinner: List<String> = emptyList(),
    val amount: Long = 0L,
    val amountValidationMessage: UiText = UiText.DynamicString(""),

    val inquiryBottomSheetVisibility: Boolean = false,
    val transferState: RequestState<Any> = RequestState.Idle,
)
