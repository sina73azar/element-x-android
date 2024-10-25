package com.drp.refahland.ui.main

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.enums.ContactType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesTransactionRepository: CardFacilitiesTransactionRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher) {
    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState

    override var sharedViewModelState: SharedViewModelUiState
        get() = _uiState.value.sharedViewModelUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sharedViewModelUiState = value)
        }

    init {
        sendSharedViewModelEvent(SharedViewModelEvents.GetCards)
        getTransactionsBySourceCardNo(/*_uiState.value.searchQuery*/"")
        getBillContacts()
        getWalletIdForQrCodeGeneration()
//        getWalletBalance()
    }

    fun setSelectedBottomBarId(id: Int) {
        _uiState.value = _uiState.value.copy(selectedBottomBarItemId = id)
    }

    fun getTransactionsBySourceCardNo(sourceCardNo: String) {
//        _uiState.value = _uiState.value.copy(transactions = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.getTransactionsBySourceCardNo(sourceCardNo)
                .collect {
                    _uiState.value =
                        _uiState.value.copy(transactions = RequestState.Success(value = it))
                }
        }
    }

    fun getWalletBalance() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(walletBalanceState = RequestState.Loading)
            cardFacilitiesUserRepository.getWalletBalance().collect {
                it.toRequestState().let { response ->
                    _uiState.value = _uiState.value.copy(walletBalanceState = response)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value =
            _uiState.value.copy(searchQuery = query)
        getTransactionsBySourceCardNo(query)
    }

    fun deleteTransaction(timeStamp: Long) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesTransactionRepository.deleteTransaction(timeStamp)
        }
    }

    fun getBillContacts() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(billContacts = RequestState.Loading)
            cardFacilitiesUserRepository.getAllContacts().collectLatest {
                _uiState.value =
                    _uiState.value.copy(billContacts = RequestState.Success(it.filter {
                        it.contactType == ContactType.BILL_ID.name || it.contactType == ContactType.GAS_BILL_ID.name || it.contactType == ContactType.WATER_BILL_ID.name || it.contactType == ContactType.ELECTRIC_BILL_ID.name || it.contactType == ContactType.TELEPHONE_NO.name
                    }))
            }
        }
    }

    fun deleteContact(contactEntity: ContactEntity) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.deleteContact(contactEntity)
        }
    }

    fun getWalletIdForQrCodeGeneration() {
        cardFacilitiesUserRepository.getShahkarUserData().walletId?.toString()?.let {
            _uiState.value = _uiState.value.copy(walletId = it)
        }
    }

    fun changeWalletQrCodeVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(walletQrCodeVisibility = visibility)
    }

    fun changeWalletBalanceVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(balanceVisibility = visibility)
    }
}