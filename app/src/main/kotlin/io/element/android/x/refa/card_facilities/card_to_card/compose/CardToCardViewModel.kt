package com.drp.card_facilities.presentation.card_to_card.compose

import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.enums.TransactionType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.utils.extractDigits
import com.drp.utils.isValidCardPan
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CardToCardViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository
) :
    ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler {

    private val _uiState = MutableStateFlow(CardToCardScreenState())
    val uiState: StateFlow<CardToCardScreenState>
        get() = _uiState
    override var sourceCardState: SourceCardUiState
        get() = _uiState.value.sourceCardUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sourceCardUiState = value)
        }
    override var sharedViewModelState: SharedViewModelUiState
        get() = _uiState.value.sharedViewModelUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sharedViewModelUiState = value)
        }

    init {
        sendSharedViewModelEvent(SharedViewModelEvents.GetCards)
        getDestinationCardContacts()
    }

    private fun getDestinationCardContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.PAN).collectLatest {
                setDestinationCardContactSheetList(it)
            }
        }
    }

    private fun validateFields(): Boolean {
        if (!validateSourceCard()) {
            sendSharedViewModelEvent(
                SharedViewModelEvents.ShowError(
                    UiText.StringResource(
                        R.string.empty_source_card_st
                    )
                )
            )
            return false
        }

        if (!checkPan(uiState.value.destinationCard.filter { it.isDigit() })) {
            _uiState.value =
                _uiState.value.copy(destinationCardValidationMessage = UiText.StringResource(R.string.invalid_dest_card_st))
            return false
        }

        if (_uiState.value.destinationCard.filter { it.isDigit() } == _uiState.value.sourceCardUiState.selectedCard?.pan?.filter { it.isDigit() }) {
            _uiState.value =
                _uiState.value.copy(destinationCardValidationMessage = UiText.StringResource(R.string.same_source_dest_card))
            return false
        }

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return false
        }

        if (_uiState.value.amount > 100000000L) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.card_to_card_max_value_error))
            return false
        }

        if (!validateSourceCardOtherFields()) {
            return false
        }

        return true
    }

    fun inquiry() {
        if (!validateFields())
            return
        changeInquiryBottomSheetVisibility(true)
    }

    fun transfer() {
        if (!validateOtpCode())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(transferState = RequestState.Loading)
            cardFacilitiesRepository.cardToCardTransfer(
                rqId = uiState.value.sourceCardUiState.cardOtpRqId,
                pin = uiState.value.sourceCardUiState.otpCode,
                selectedCard = uiState.value.sourceCardUiState.selectedCard,
                cardYear = uiState.value.sourceCardUiState.year,
                cardMonth = uiState.value.sourceCardUiState.month
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(
                                    response.getErrorMessage()
                                )
                            )
                        )
                    if (response.isSuccess())
                        coroutineScope {
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = ContactType.PAN.name,
                                    title = ContactType.PAN.savedContactTitle,
                                    value = _uiState.value.destinationCard
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(transferState = response)
                }
            }
        }
    }

    fun sendOtp() {
        sendSourceCardEvent(
            SourceCardEvents.SendCardToCardOtp(
                viewModel = this,
                cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                dispatcher = dispatcher,
                destinationCard = uiState.value.destinationCard.filter { it.isDigit() },
                amount = uiState.value.amount
            )
        )
    }

    suspend fun saveTransaction(receiptItems: List<ReceiptItem>) {
        var time = Calendar.getInstance().timeInMillis
        try {
            TrueTime.build().initialize()
            time = TrueTime.now().time
        } catch (ex: Exception) {

        }
        transactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.CARD_TO_CARD.type,
                transactionValue = TransactionType.CARD_TO_CARD.type,
                amount = _uiState.value.amount,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    fun dismissInquiryBottomSheet() {
        changeInquiryBottomSheetVisibility(false)
        _uiState.value = _uiState.value.copy(transferState = RequestState.Idle)
        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
    }

    private fun changeInquiryBottomSheetVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(inquiryBottomSheetVisibility = visibility)
    }

    fun setDestinationCard(destinationCard: String) {
        _uiState.value =
            _uiState.value.copy(destinationCardContactSpinner = _uiState.value.destinationCardContactSheetList.filter {
                it.value.contains(destinationCard) || it.name?.contains(destinationCard) == true
            }.map { it.value })
        _uiState.value = _uiState.value.copy(destinationCard = destinationCard)
    }

    fun setDestinationCardContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(destinationCardContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    fun dismissDestinationCardContactSpinner() {
        _uiState.value = _uiState.value.copy(destinationCardContactSpinner = emptyList())
    }

    fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun dismissFailureDialog() {
        if (_uiState.value.transferState.isFail())
            _uiState.value = _uiState.value.copy(transferState = RequestState.Idle)
    }

    fun dumpDestinationCardValidationMessage() {
        _uiState.value =
            _uiState.value.copy(destinationCardValidationMessage = UiText.DynamicString(""))
    }

    fun dumpAmountValidationMessage() {
        _uiState.value =
            _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    fun checkPan(pan: String): Boolean {
        if (extractDigits(pan).length < 16) return false
        return isValidCardPan(pan)
    }
}
