package com.drp.card_facilities.presentation.card_to_card.refahi.inquiry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.database.entity.toAutoCompleteItem
import com.drp.data.enums.ContactType
import com.drp.data.network.CustomResponse
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.card_info.CardNumberRequest
import com.drp.refah.card_facilities.data.model.card_info.CardNumberResult
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardRequest
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardResult
import com.drp.refah.card_facilities.utility.SUPER_APP_CARD_TO_CARD_MOCK_RES
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.utils.extractDigits
import com.drp.utils.isValidCardPan
import com.drp.utils.logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RefahiCardInquiryViewModel @Inject constructor(
    private val userRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val dispatcher: CoroutineDispatcher
) : ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler {

    private val _uiState = MutableStateFlow(RefahiCardScreenState())
    val sharedViewModelUiState: StateFlow<RefahiCardScreenState> = _uiState
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

    private val _cardInquiryResult = MutableLiveData<CustomResponse<InquiryCardResult>>()
    val cardInquiryResult: LiveData<CustomResponse<InquiryCardResult>> = _cardInquiryResult


    private val _panContacts = MutableLiveData<CustomResponse<List<AutoCompleteItem>>>()
    val panContacts: LiveData<CustomResponse<List<AutoCompleteItem>>> = _panContacts


    init {
        logger(msg = "fetching card shot items")
//        getCardShotItems()
        sendSharedViewModelEvent(SharedViewModelEvents.GetCards)
        fetchRelatedContacts()
    }

    private fun fetchRelatedContacts() {
        _panContacts.postValue(CustomResponse.Loading())
        viewModelScope.launch {
            userRepository.queryContactsByType(ContactType.PAN).collect { entities ->
                _panContacts.postValue(CustomResponse.Success(entities.map { it.toAutoCompleteItem() }))
            }
        }
    }

    fun inquiry(fund: FundTransfer, trk2EquivData: Trk2EquivData) {
        _cardInquiryResult.postValue(CustomResponse.Loading())
//        val publicKeyStr = userRepository.getPublicKey()
//        if (!publicKeyStr.isNullOrEmpty()) {
//            val publicKey: PublicKey? = getPublic(publicKeyStr)
//        val pinByte: ByteArray? = encryptWithRsa(publicKey, trk2EquivData.pin.toByteArray())
//        val pinEncrypted: String = Base64.encodeToString(pinByte, Base64.NO_WRAP)
//            val cvv2Byte: ByteArray? = encryptWithRsa(publicKey, trk2EquivData.cvv2.toByteArray())
//            val cvv2Encrypted: String = Base64.encodeToString(cvv2Byte, Base64.NO_WRAP)
//        trk2EquivData.pin = pinEncrypted
//            trk2EquivData.cvv2 = cvv2Encrypted
        val headers: Map<String, String> =
            mapOf("X-Correlation-Id" to System.currentTimeMillis().toString())
        val request = InquiryCardRequest(fund, trk2EquivData)
        viewModelScope.launch {
            cardFacilitiesRepository.cardInquiry(request, headers, SUPER_APP_CARD_TO_CARD_MOCK_RES)
                .collect {
                    _cardInquiryResult.postValue(it)
                }
        }
    }


    fun checkPan(pan: String): Boolean {
        if (extractDigits(pan).length < 16) return false
        return isValidCardPan(pan)
    }

    /*private fun getCardShotItems() {
        _cardShotItems.postValue(CustomResponse.Loading())
        viewModelScope.launch {
            try {
                cardFacilitiesRepository.getCardShotItems().collect {
                    logger("cardShot res: $it")
                    _cardShotItems.postValue(CustomResponse.Success(it))
                }
            } catch (e: Exception) {

                logger("cardShot exception")
                _cardShotItems.postValue(CustomResponse.Fail())
            }
        }
    }*/

    suspend fun getCards(cardNumber: String): CardNumberResult? {
        val validCardNum = cardNumber.trim().filter { it.isDigit() }
        if (validCardNum.length == 6 || validCardNum.length == 16) {
            val request = CardNumberRequest(validCardNum)
            return withContext(Dispatchers.IO) {
                cardFacilitiesRepository.getCards(request)
                    .first().let {
                        if (it.status == CustomResponse.Status.SUCCESS) {
                            return@withContext it.data
                        } else return@withContext null
                    }
            }
        } else
            return null
    }


}