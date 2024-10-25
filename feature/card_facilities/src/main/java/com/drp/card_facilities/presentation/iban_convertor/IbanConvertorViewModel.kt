package com.drp.card_facilities.presentation.iban_convertor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.UiText
import com.drp.shared_ui.enums.BanksForConvertToIban
import com.drp.shared_ui.enums.IbanConvertType
import com.drp.utils.extractDigits
import com.drp.utils.isValidCardPan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.iban4j.IbanUtil
import javax.inject.Inject

@HiltViewModel
class IbanConvertorViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(IbanConvertorScreenState())
    val uiState: StateFlow<IbanConvertorScreenState> = _uiState

    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    init {
        getCardContacts()
    }

    private fun getCardContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.PAN).collectLatest {
                setCardContactSheetList(it)
            }
        }
    }

    fun setIban(iban: String) {
        _uiState.value = _uiState.value.copy(iban = iban)
    }

    fun dismissIbanValidationMessage() {
        _uiState.value = _uiState.value.copy(ibanValidationMessage = UiText.DynamicString(""))
    }

    fun setAccountNumber(accountNumber: String) {
        _uiState.value = _uiState.value.copy(accountNumber = accountNumber)
    }

    fun dismissAccountNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(accountNumberValidationMessage = UiText.DynamicString(""))
    }

    fun setCardNumber(cardNumber: String) {
        _uiState.value =
            _uiState.value.copy(cardNumberContactSpinner = _uiState.value.cardNumberContactSheetList.filter {
                it.value.contains(cardNumber) || it.name?.contains(cardNumber) == true
            }.map { it.value })
        _uiState.value = _uiState.value.copy(cardNumber = cardNumber)
    }

    fun dismissCardNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(cardNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun setCardContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(cardNumberContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    fun dismissCardContactSpinner() {
        _uiState.value = _uiState.value.copy(cardNumberContactSpinner = emptyList())
    }

    fun setToggle(currentToggle: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(selectedToggle = currentToggle)
    }

    fun setBankName(bankName: String) {
        _uiState.value = _uiState.value.copy(bankName = bankName)
        _uiState.value.bankNameForServiceCall =
            BanksForConvertToIban.entries.find { it.bankNameInPersian == bankName }?.bankNameForServiceCall
                ?: ""
    }

    fun dismissBankNameError() {
        _uiState.value = _uiState.value.copy(bankNameError = false)
    }

    fun dismissFailureDialog() {
        if (_uiState.value.convertState.isFail())
            _uiState.value = _uiState.value.copy(convertState = RequestState.Idle)

    }

    private fun checkIban(iban: String): Boolean {
        return try {
            IbanUtil.validate(iban)
            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun checkPan(pan: String): Boolean {
        if (extractDigits(pan).length < 16) return false
        return isValidCardPan(pan)
    }

    private fun validateFields(): Boolean {
        if (uiState.value.selectedToggle?.id == 0)
            if (!checkPan(uiState.value.cardNumber.filter { it.isDigit() })) {
                _uiState.value = _uiState.value.copy(
                    cardNumberValidationMessage = UiText.StringResource(R.string.data_validation_pan_st)
                )
                return false
            }
        if (uiState.value.selectedToggle?.id == 1) {
            if (_uiState.value.accountNumber.isEmpty()) {
                _uiState.value =
                    _uiState.value.copy(accountNumberValidationMessage = UiText.StringResource(R.string.data_validation_account_st))
                return false
            }
            if (uiState.value.bankName.isEmpty()) {
                _uiState.value =
                    _uiState.value.copy(bankNameError = true)
                return false
            }
        }
        if (uiState.value.selectedToggle?.id == 2)
            if (!checkIban("IR".plus(uiState.value.iban))) {
                _uiState.value =
                    _uiState.value.copy(ibanValidationMessage = UiText.StringResource(R.string.data_validation_iban))
                return false
            }
        return true

    }

    fun convert() {
        if (!validateFields())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(convertState = RequestState.Loading)
            val convertType = when (uiState.value.selectedToggle?.id) {
                0 -> IbanConvertType.CardToIban
                1 -> IbanConvertType.AccountToIban
                else -> IbanConvertType.IbanToAccount
            }
            cardFacilitiesRepository.ibanConvertor(
                convertType = convertType,
                cardNumber = uiState.value.cardNumber,
                accountNumber = uiState.value.accountNumber,
                iban = "IR".plus(uiState.value.iban),
                bankNameForServiceCall = uiState.value.bankNameForServiceCall
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError()) {
                        showError(UiText.StringResource(R.string.iban_convertor_error_st))
                    }
                    if (response.isSuccess()) {
                        if (response.getSuccessData().parameters == null) {
                            showError(UiText.DynamicString(response.getSuccessData().status.description))
                            _uiState.value = _uiState.value.copy(convertState = RequestState.Idle)
                            return@collectLatest
                        }
                    }
                    _uiState.value = _uiState.value.copy(convertState = response)
                }
            }
        }
    }

    fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }
}