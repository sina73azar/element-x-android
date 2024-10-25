package com.drp.card_facilities.presentation.app_source_card_handler

import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import kotlinx.serialization.Serializable

@Serializable
data class InternetPackageTypeScreenModel(
    var sourceCardUiState: SourceCardUiState = SourceCardUiState(),
    var mobileNumber: String = "",
    var selectedMobileOperatorTab: MobileOperatorTab = MobileOperatorTab.MTN,
    var selectedToggle: CustomToggleModel? = null,
    val cardOrWalletToggle: CustomToggleModel? = null,
    val products: List<PackageItem>? = emptyList()
)
