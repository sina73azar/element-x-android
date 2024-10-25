package com.drp.card_facilities.presentation.transaction_history

import com.drp.data.database.entity.TransactionEntity
import com.drp.data.network.RequestState


data class TransactionHistoryScreenState(
    val searchQuery: String = "",
    val transactions: RequestState<List<TransactionEntity>> = RequestState.Idle
)
