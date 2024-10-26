package com.drp.card_facilities.presentation.installment.compose

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.data.model.installment.inquiry.FacilityResponse
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.UiText

data class InstallmentScreenState(
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    val facilityNumber: String = "",
    val facilityNumberValidationMessage: UiText = UiText.DynamicString(""),
    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),
    val facilityNumberContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val facilityNumberContactSpinner: List<String> = emptyList(),

    val inquiry: RequestState<FacilityResponse> = RequestState.Idle,
    val installmentPayment: RequestState<Any> = RequestState.Idle,
)
