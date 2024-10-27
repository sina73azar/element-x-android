package com.drp.card_facilities.presentation.wallet_minus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.toSearchSheetItemModel
import com.drp.data.enums.ContactType
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.utils.extractDigits
import com.drp.utils.isValidCardPan
import dagger.hilt.android.lifecycle.HiltViewModel
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.iban4j.IbanUtil
import javax.inject.Inject

@HiltViewModel
class WalletMinusViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository
) : ViewModel(), WalletHandler {
    private val _uiState = MutableStateFlow(WalletMinusScreenState())
    val uiState: StateFlow<WalletMinusScreenState>
        get() = _uiState

    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    override var walletUiState: WalletUiState
        get() = _uiState.value.walletUiState
        set(value) {
            _uiState.value = _uiState.value.copy(walletUiState = value)
        }

    init {
        sendWalletEvent(WalletEvents.SetShahkarUserData(cardFacilitiesUserRepository.getShahkarUserData()))
        getDestinationCardContacts()
    }

    private fun getDestinationCardContacts() {
        viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.queryContactsByType(ContactType.PAN).collectLatest {
                setDestinationCardContactSheetList(it)
            }
        }
    }

    private fun validateFields() {
        if (uiState.value.selectedToggle?.id == 0)
            if (!checkIban("IR".plus(uiState.value.iban))) {
                _uiState.value =
                    _uiState.value.copy(ibanValidationMessage = UiText.StringResource(R.string.data_validation_iban))
                return
            }

        if (uiState.value.selectedToggle?.id == 1)
            if (!checkPan(uiState.value.destinationCardNumber.filter { it.isDigit() })) {
                _uiState.value = _uiState.value.copy(
                    destinationCardNumberValidationMessage = UiText.StringResource(R.string.data_validation_pan_destination)
                )
                return
            }

        if (_uiState.value.amount < 1) {
            _uiState.value =
                _uiState.value.copy(amountValidationMessage = UiText.StringResource(R.string.data_validation_amount))
            return
        }

        changeOtpBottomSheetState(visibility = true)
    }

    private fun setAmount(amount: Long) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun dumpAmountValidationMessage() {
        _uiState.value = _uiState.value.copy(amountValidationMessage = UiText.DynamicString(""))
    }

    private fun setIban(iban: String) {
        _uiState.value = _uiState.value.copy(iban = iban)
    }

    private fun setDestinationCardNumber(cardNumber: String) {
        _uiState.value =
            _uiState.value.copy(destinationCardContactSpinner = _uiState.value.destinationCardContactSheetList.filter {
                it.value.contains(cardNumber) || it.name?.contains(cardNumber) == true
            }.map { it.value })
        _uiState.value = _uiState.value.copy(destinationCardNumber = cardNumber)
    }

    private fun setDestinationCardContactSheetList(contactList: List<ContactEntity>) {
        _uiState.value =
            _uiState.value.copy(destinationCardContactSheetList = contactList.map { it.toSearchSheetItemModel() })
    }

    private fun dismissDestinationCardContactSpinner() {
        _uiState.value = _uiState.value.copy(destinationCardContactSpinner = emptyList())
    }

    private fun dumpDestinationCardNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(destinationCardNumberValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpIbanValidationMessage() {
        _uiState.value = _uiState.value.copy(ibanValidationMessage = UiText.DynamicString(""))
    }

    private fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    private fun transfer() {
        if (!validateWalletOtpCode())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(transferState = RequestState.Loading)
            cardFacilitiesRepository.minusFromWallet(
                rqId = uiState.value.walletUiState.walletOtpRqId,
                pin = uiState.value.walletUiState.walletOtpCode
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        showError(
                            UiText.DynamicString(
                                response.getErrorMessage()
                            )
                        )
                    if (uiState.value.selectedToggle?.id == 1)
                        if (response.isSuccess()) {
                            coroutineScope {
                                cardFacilitiesUserRepository.upsertContact(
                                    ContactEntity(
                                        contactType = ContactType.PAN.name,
                                        title = ContactType.PAN.savedContactTitle,
                                        value = _uiState.value.destinationCardNumber
                                    )
                                )
                            }
                        }
                    _uiState.value = _uiState.value.copy(transferState = response)
                }
            }
        }
    }

    private fun dismissFailureDialog() {
        if (_uiState.value.transferState.isFail())
            _uiState.value = _uiState.value.copy(transferState = RequestState.Idle)
    }

    private fun backToDefault() {
        sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
//        sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
        _uiState.value = _uiState.value.copy(
            transferState = RequestState.Idle
        )
    }

    private fun changeOtpBottomSheetState(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(getOtpBottomSheetState = visibility)
    }

    private fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
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

    private fun setIbanOrCardTab(currentTab: CustomToggleModel) {
        _uiState.value = _uiState.value.copy(selectedToggle = currentTab)
    }

    fun sendEvent(event: WalletMinusScreenEvents) {
        when (event) {
            is WalletMinusScreenEvents.ChangeCardOrIbanToggle -> setIbanOrCardTab(event.selectedToggle)
            is WalletMinusScreenEvents.SetAmount -> setAmount(event.amount)
            is WalletMinusScreenEvents.DumpAmountValidationMessage -> dumpAmountValidationMessage()
            is WalletMinusScreenEvents.SetIban -> setIban(event.iban)
            is WalletMinusScreenEvents.DumpIbanValidationMessage -> dumpIbanValidationMessage()
            is WalletMinusScreenEvents.SetDestinationCardNumber -> setDestinationCardNumber(event.destinationCardNumber)
            is WalletMinusScreenEvents.DumpDestinationCardNumberValidationMessage -> dumpDestinationCardNumberValidationMessage()
            is WalletMinusScreenEvents.DismissDestinationCardNumberContactSpinner -> dismissDestinationCardContactSpinner()
            is WalletMinusScreenEvents.SetDescription -> setDescription(event.description)
            is WalletMinusScreenEvents.ValidateFields -> validateFields()
            is WalletMinusScreenEvents.SendOtp -> sendWalletEvent(
                WalletEvents.SendMinusFromWalletOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    amount = uiState.value.amount,
                    destinationIban = if (uiState.value.selectedToggle?.id == 0) uiState.value.iban else "",
                    destinationCard = if (uiState.value.selectedToggle?.id == 1) uiState.value.destinationCardNumber.filter { it.isDigit() } else "",
                    description = uiState.value.description
                )
            )

            is WalletMinusScreenEvents.Transfer -> transfer()
            is WalletMinusScreenEvents.DismissOtpBottomSheet -> changeOtpBottomSheetState(false)
            is WalletMinusScreenEvents.DismissFailureDialog -> dismissFailureDialog()
            is WalletMinusScreenEvents.BackToDefault -> backToDefault()
            is WalletMinusScreenEvents.ShowError -> showError(event.message)
            else -> {}
        }
    }
}

sealed class WalletMinusScreenEvents {
    data class ChangeCardOrIbanToggle(val selectedToggle: CustomToggleModel) :
        WalletMinusScreenEvents()

    data class SetAmount(val amount: Long) : WalletMinusScreenEvents()
    data object DumpAmountValidationMessage : WalletMinusScreenEvents()
    data class SetIban(val iban: String) : WalletMinusScreenEvents()
    data class SetDestinationCardNumber(val destinationCardNumber: String) :
        WalletMinusScreenEvents()

    data object DumpIbanValidationMessage : WalletMinusScreenEvents()
    data object DumpDestinationCardNumberValidationMessage : WalletMinusScreenEvents()
    data object DismissDestinationCardNumberContactSpinner : WalletMinusScreenEvents()
    data class SetDescription(val description: String) : WalletMinusScreenEvents()
    data object ValidateFields : WalletMinusScreenEvents()
    data object SendOtp : WalletMinusScreenEvents()
    data object Transfer : WalletMinusScreenEvents()
    data object DismissOtpBottomSheet : WalletMinusScreenEvents()
    data object DismissFailureDialog : WalletMinusScreenEvents()
    data object BackToDefault : WalletMinusScreenEvents()
    data class ShowError(val message: UiText) : WalletMinusScreenEvents()
}
