package com.drp.card_facilities.presentation.internet_package.compose.inquiry

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryWithWalletResponse
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.model.SearchSheetItemModel
import io.element.android.x.refa.enums.UiText

data class InternetPackageInquiryScreenState(
    /** screen user input parameters */
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),

    var mobileNumber: String = "",
    var mobileNumberValidationMessage: UiText = UiText.DynamicString(""),
    val mobilePhoneContactSpinner: List<String> = emptyList(),

    /** screen code parameters */
    val cardOrWalletToggle: CustomToggleModel? = null,
    val mobileContactSheetList: List<SearchSheetItemModel> = emptyList(),
    var selectedMobileOperatorTab: MobileOperatorTab = MobileOperatorTab.MTN,
    var selectedToggle: CustomToggleModel? = null,
    var operatorTabFinderCalled: Boolean = false,

    var inquiry: RequestState<InternetPackageInquiryWithWalletResponse> = RequestState.Idle,
)

fun InternetPackageInquiryScreenState.toInternetPackageTypeScreenModel() =
    InternetPackageTypeScreenModel(
        sourceCardUiState = this.sourceCardUiState,
        mobileNumber = this.mobileNumber,
        selectedMobileOperatorTab = this.selectedMobileOperatorTab,
        selectedToggle = this.selectedToggle,
        cardOrWalletToggle = this.cardOrWalletToggle,
        products = inquiry.getSuccessData().parameters?.products
    )

