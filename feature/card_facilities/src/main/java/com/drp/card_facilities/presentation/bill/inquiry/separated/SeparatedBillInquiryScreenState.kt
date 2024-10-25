package com.drp.card_facilities.presentation.bill.inquiry.separated

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.model.bill.inquiry.BillInquiryResponseWithWallet
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.SearchSheetItemModel

data class SeparatedBillInquiryScreenState(
    /** bill screen parameters */
    val cardOrWalletToggle: CustomToggleModel? = null,
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    var walletUiState: WalletUiState = WalletUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    var billId: String = "",
    var billIdValidationMessage: UiText = UiText.DynamicString(""),

    var fixedPhoneNumber: String = "",
    var fixedPhoneNumberValidationMessage: UiText = UiText.DynamicString(""),

    var mobileNumber: String = "",
    var mobileNumberValidationMessage: UiText = UiText.DynamicString(""),

    var selectedMobileOperatorTab: MobileOperatorTab = MobileOperatorTab.MTN,
    var operatorTabFinderCalled: Boolean = false,


    val fixedPhoneContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val waterBillIdContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val gasBillIdContactSheetList: List<SearchSheetItemModel> = emptyList(),
    val electricityBillIdContactSheetList: List<SearchSheetItemModel> = emptyList(),

    val fixedPhoneContactSpinner: List<String> = emptyList(),
    val mobilePhoneContactSpinner: List<String> = emptyList(),
    val waterBillIdContactSpinner: List<String> = emptyList(),
    val gasBillIdContactSpinner: List<String> = emptyList(),
    val electricityBillIdContactSpinner: List<String> = emptyList(),

    /** bill screen requests */
//    val separatedPhoneBillInquiryState: RequestState<SeparatedPhoneBillInquiryResult> = RequestState.Idle,
    val separatedUtilityBillInquiryState: RequestState<BillInquiryResponseWithWallet> = RequestState.Idle,
    val billPaymentState: RequestState<Unit> = RequestState.Idle,
)