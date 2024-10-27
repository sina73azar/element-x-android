package com.drp.card_facilities.presentation.transaction_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.network.RequestState
import com.drp.data.repository.CardFacilitiesTransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryScreenState())
    val uiState: StateFlow<TransactionHistoryScreenState>
        get() = _uiState

    init {
        getTransactionsBySourceCardNo(_uiState.value.searchQuery)
    }

    private fun getTransactionsBySourceCardNo(sourceCardNo: String) {
//        _uiState.value = _uiState.value.copy(transactions = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.getTransactionsBySourceCardNo(sourceCardNo)
                .collect {
                    _uiState.value =
                        _uiState.value.copy(transactions = RequestState.Success(value = it))
                }
        }
    }

    private fun setSearchQuery(query: String) {
        _uiState.value =
            _uiState.value.copy(searchQuery = query)
        getTransactionsBySourceCardNo(query)
    }

    private fun deleteTransaction(timeStamp: Long) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.deleteTransaction(timeStamp)
        }
    }

    fun sendEvent(event: TransactionHistoryEvents) {
        when (event) {
            is TransactionHistoryEvents.SetSearchQuery -> setSearchQuery(event.query)

            is TransactionHistoryEvents.DeleteTransaction -> deleteTransaction(event.timeStamp)
            else -> {}
        }
    }
}

sealed class TransactionHistoryEvents {
    data class SetSearchQuery(val query: String) : TransactionHistoryEvents()

    data class DeleteTransaction(val timeStamp: Long) : TransactionHistoryEvents()

}
