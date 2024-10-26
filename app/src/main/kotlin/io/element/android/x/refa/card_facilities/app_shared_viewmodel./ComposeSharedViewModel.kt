package com.drp.card_facilities.presentation.app_shared_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.shared_ui.model.CardShotItemInfo
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class ComposeSharedViewModel(
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    abstract var sharedViewModelState: SharedViewModelUiState
    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    private fun getCardsList() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.getCardShotItems().collectLatest {
                setCardsList(it)
            }
        }
    }

    private fun removeCard(cardId: Int) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.removeCardShotItem(cardId)
        }
    }

    private fun setToDefaultCard(cardId: Int) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.setToDefaultCardShotItem(cardId)
        }
    }

    private fun updateCard(card: CardShotItemInfo) {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesRepository.upsertCardShotItem(card).collect()
        }
    }

    private fun setCardsList(cardList: List<CardShotItemInfo>) {
        sharedViewModelState = sharedViewModelState.copy(cardsList = cardList)
    }

    private fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }

    fun sendSharedViewModelEvent(event: SharedViewModelEvents) {
        when (event) {
            is SharedViewModelEvents.GetCards -> getCardsList()
            is SharedViewModelEvents.RemoveCard -> removeCard(event.cardId)
            is SharedViewModelEvents.AddCard -> updateCard(event.card)
            is SharedViewModelEvents.SetToDefaultCard -> setToDefaultCard(event.cardId)
            is SharedViewModelEvents.ShowError -> showError(event.message)
            is SharedViewModelEvents.EditCard -> updateCard(event.card)

            else -> {}
        }
    }
}

sealed class SharedViewModelEvents {
    data object GetCards : SharedViewModelEvents()
    data class RemoveCard(val cardId: Int) : SharedViewModelEvents()
    data class AddCard(val card: CardShotItemInfo) : SharedViewModelEvents()
    data class SetToDefaultCard(val cardId: Int) : SharedViewModelEvents()
    data class EditCard(val card: CardShotItemInfo) : SharedViewModelEvents()
    data class ShowError(val message: UiText) : SharedViewModelEvents()


}
