package com.drp.card_facilities.presentation.insurance

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.network.RequestState
import com.drp.refah.ui.data.model.CustomToggleModel
import io.element.android.x.refa.enums.UiText

data class InsuranceScreenState(
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    val paymentId: String = "",
    val paymentIdValidationMessage: UiText = UiText.DynamicString(""),
    var amount: Long = 0L,
    var amountValidationMessage: UiText = UiText.DynamicString(""),

    val cardOrWalletToggle: CustomToggleModel? = null,
    val paymentBottomSheetVisibility: Boolean = false,
    val insurancePayment: RequestState<Any> = RequestState.Idle,
)
