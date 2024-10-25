package com.drp.card_facilities.presentation.iban_convertor

import com.drp.data.model.iban_convertor.IbanConvertorResponse
import com.drp.data.network.RequestState
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.SearchSheetItemModel

data class IbanConvertorScreenState(
    val accountNumber: String = "",
    val accountNumberValidationMessage: UiText = UiText.DynamicString(""),
    val bankName: String = "",
    val bankNameError: Boolean = false,
    var bankNameForServiceCall: String = "",

    val iban: String = "",
    val ibanValidationMessage: UiText = UiText.DynamicString(""),

    val cardNumber: String = "",
    val cardNumberValidationMessage: UiText = UiText.DynamicString(""),
    val cardNumberContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val cardNumberContactSpinner: List<String> = emptyList(),

    var selectedToggle: CustomToggleModel? = null,

    var convertState: RequestState<IbanConvertorResponse> = RequestState.Idle,
    /*var convertCardToIbanState: RequestState<Unit> = RequestState.Idle,
    var convertIbanToAccountState: RequestState<Unit> = RequestState.Idle*/
)
