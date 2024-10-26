package com.drp.card_facilities.presentation.history

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val dispatcher: CoroutineDispatcher
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher) {

    private val _uiState = MutableStateFlow(HistoryScreenState())
    val uiState: StateFlow<HistoryScreenState>
        get() = _uiState

    override var sharedViewModelState: SharedViewModelUiState
        get() = _uiState.value.sharedViewModelUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sharedViewModelUiState = value)
        }

    init {
        getTransactionsBySourceCardNo()
    }

    private fun getTransactionsBySourceCardNo() {
        _uiState.value = _uiState.value.copy(transactions = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.getWalletTransactionHistory(
                cardFacilitiesUserRepository.getShahkarUserData().walletId.toString()
            )
                .collect {
                    it.toRequestState().let { response ->
                        if (response.isError())
                            sendSharedViewModelEvent(
                                SharedViewModelEvents.ShowError(
                                    UiText.DynamicString(
                                        response.getErrorMessage()
                                    )
                                )
                            )
                        _uiState.value = _uiState.value.copy(transactions = response)
                    }
                }
        }
    }

    /*private fun setSearchQuery(query: String) {
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
        }
    }*/
}

/*
sealed class TransactionHistoryEvents {
    data class SetSearchQuery(val query: String) : TransactionHistoryEvents()

    data class DeleteTransaction(val timeStamp: Long) : TransactionHistoryEvents()

}*/
