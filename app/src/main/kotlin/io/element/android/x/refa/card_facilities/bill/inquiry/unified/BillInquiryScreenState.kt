package com.drp.card_facilities.presentation.bill.inquiry.unified

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillResult
import com.drp.refah.card_facilities.data.model.bill.payment.PaymentBillResult
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.BillTabs
import io.element.android.x.refa.enums.UiText

data class BillInquiryScreenState(

    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    /** bill screen parameters */
    val cardOrWalletToggle: CustomToggleModel? = null,
    var billId: String = "",
    var billIdValidationMessage: UiText = UiText.DynamicString(""),
    var paymentId: String = "",
    var paymentIdValidationMessage: UiText = UiText.DynamicString(""),
    var selectedTab: BillTabs = BillTabs.SEPARATED,
    val billIdContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val billIdSpinnerList: List<String> = emptyList(),

    /** bill screen requests */
    val billInquiryState: RequestState<InquiryBillResult> = RequestState.Idle,
    val billPaymentState: RequestState<PaymentBillResult> = RequestState.Idle,
)
