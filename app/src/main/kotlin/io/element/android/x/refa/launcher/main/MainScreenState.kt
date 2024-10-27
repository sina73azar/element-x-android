/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package com.drp.refahland.ui.main

import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.model.wallet_add.WalletResponse
import com.drp.data.network.RequestState

data class MainScreenState(
    val selectedBottomBarItemId: Int = 3,
    val sharedViewModelUiState: SharedViewModelUiState = SharedViewModelUiState(),
    val searchQuery: String = "",
    val walletQrCodeVisibility: Boolean = false,
    val walletId: String = "",
    val balanceVisibility: Boolean = true,
    val transactions: RequestState<List<TransactionEntity>> = RequestState.Idle,
    val walletBalanceState: RequestState<WalletResponse> = RequestState.Idle,
    val billContacts: RequestState<List<ContactEntity>> = RequestState.Idle
)
