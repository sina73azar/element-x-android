package com.drp.card_facilities.presentation.internet_package.compose.package_type

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.network.RequestState
import com.drp.refah.card_facilities.data.model.internet_package.inquiry.PackageFilterType

data class InternetPackageTypeScreenState(
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),
    var walletUiState: WalletUiState = WalletUiState(),
    val internetPackageTypeScreenModel: InternetPackageTypeScreenModel = InternetPackageTypeScreenModel(),
    val chips: ArrayList<PackageFilterType> = arrayListOf(),
    val selectedChip: PackageFilterType = PackageFilterType(title = "", packages = arrayListOf()),
    val selectedPackageItem: PackageItem? = null,
    val payment: RequestState<Any> = RequestState.Idle
)