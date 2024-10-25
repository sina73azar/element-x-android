package com.drp.card_facilities.presentation.app_source_card_handler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.network.CustomResponse
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.utility.Commons
import com.drp.refah.card_facilities.utility.Commons.isValidCvv2
import com.drp.refah.ui.data.model.SMSState
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface SourceCardHandler {
    var sourceCardState: SourceCardUiState

    fun validateSourceCard(): Boolean = sourceCardState.selectedCard != null
    fun validateSourceCardOtherFields(): Boolean {
        if (!isValidCvv2(sourceCardState.cvv2)) {
            sourceCardState =
                sourceCardState.copy(cvv2ValidationMessage = UiText.StringResource(R.string.data_validation_cvv2))
            return false
        }
        if (sourceCardState.month.isEmpty()) {
            sourceCardState =
                sourceCardState.copy(monthError = true)
            return false
        }
        if (sourceCardState.year.isEmpty()) {
            sourceCardState =
                sourceCardState.copy(yearError = true)
            return false
        }
        return true
    }

    private fun setSelectedCard(selectedCard: CardShotItemInfo?) {
        sourceCardState = sourceCardState.copy(selectedCard = selectedCard)
    }

    private fun setCvv2(cvv2: String) {
        sourceCardState = sourceCardState.copy(cvv2 = cvv2)
    }

    private fun setMonth(month: String) {
        sourceCardState = sourceCardState.copy(month = month)
    }

    private fun setYear(year: String) {
        sourceCardState = sourceCardState.copy(year = year)
    }

    private fun dumpCvv2ValidationMessage() {
        sourceCardState =
            sourceCardState.copy(cvv2ValidationMessage = UiText.DynamicString(""))
    }

    private fun dumpMonthError() {
        sourceCardState = sourceCardState.copy(monthError = false)
    }

    private fun dumpYearError() {
        sourceCardState = sourceCardState.copy(yearError = false)
    }

    private fun setOtpCode(otpCode: String) {
        sourceCardState = sourceCardState.copy(otpCode = otpCode)
    }

    private fun dumpOtpCodeValidationMessage() {
        sourceCardState =
            sourceCardState.copy(otpCodeValidationMessage = UiText.DynamicString(""))
    }

    private fun backOtpCodeToDefault() {
        sourceCardState = sourceCardState.copy(
            otpCode = "",
            otpCodeValidationMessage = UiText.DynamicString(""),
            smsState = SMSState(),
            cardOtpRqId = ""
        )
    }

    fun validateOtpCode(): Boolean {
        if (!Commons.checkPin(sourceCardState.otpCode)) {
            sourceCardState =
                sourceCardState.copy(otpCodeValidationMessage = UiText.StringResource(R.string.data_validation_pin))
            return false
        }
        return true
    }

    private fun sendCardPasswordOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        amount: Long,
        requestType: CardOtpRequestType,
    ) {
        sourceCardState =
            sourceCardState.copy(smsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.cardOtp(
                selectedCard = sourceCardState.selectedCard,
                cardYear = sourceCardState.year,
                cardMonth = sourceCardState.month,
                cvv2 = sourceCardState.cvv2,
                requestType = requestType
                /*amount = amount,
                requestType = requestType,
                selectedCard = sourceCardState.selectedCard,
                trk2EquivData = Trk2EquivData(
                    expireDate = sourceCardState.year.substring(
                        2,
                        4
                    ) + sourceCardState.month,
                    cvv2 = sourceCardState.cvv2,
                    pin = ""
                )*/
            )
                .collectLatest {
                    when (it.status) {
                        CustomResponse.Status.SUCCESS -> {
                            sourceCardState =
                                sourceCardState.copy(smsState = SMSState(SMSStateSuccess = true))
                        }

                        CustomResponse.Status.Fail -> {
                            sourceCardState =
                                sourceCardState.copy(
                                    smsState = SMSState(
                                        SMSStateInquiryFail = true
                                    )
                                )
                        }

                        CustomResponse.Status.ERROR -> {
                            sourceCardState =
                                sourceCardState.copy(
                                    smsState = SMSState(
                                        SMSStateInquiryErrorMessage = it.message
                                    )
                                )
                        }

                        else -> {

                        }
                    }
                }
        }
    }

    private fun sendCardToCardOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        destinationCard: String,
        amount: Long
    ) {
        sourceCardState =
            sourceCardState.copy(smsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.cardToCardOtp(
                selectedCard = sourceCardState.selectedCard,
                cardYear = sourceCardState.year,
                cardMonth = sourceCardState.month,
                cvv2 = sourceCardState.cvv2,
                destinationCardNumber = destinationCard,
                amount = amount
            )
                .collectLatest {
                    when (it.status) {
                        CustomResponse.Status.SUCCESS -> {
                            sourceCardState =
                                sourceCardState.copy(smsState = SMSState(SMSStateSuccess = true))
                            sourceCardState =
                                sourceCardState.copy(cardOtpRqId = it.data?.result?.pRqId.toString())
                        }

                        CustomResponse.Status.Fail -> {
                            sourceCardState =
                                sourceCardState.copy(
                                    smsState = SMSState(
                                        SMSStateInquiryFail = true
                                    )
                                )
                        }

                        CustomResponse.Status.ERROR -> {
                            sourceCardState =
                                sourceCardState.copy(
                                    smsState = SMSState(
                                        SMSStateInquiryErrorMessage = it.message
                                    )
                                )
                        }

                        else -> {

                        }
                    }
                }
        }
    }

    fun sendSourceCardEvent(event: SourceCardEvents) {
        when (event) {
            is SourceCardEvents.SetSelectedCard -> setSelectedCard(event.selectedCard)

            is SourceCardEvents.SetCvv2 -> setCvv2(event.cvv2)

            is SourceCardEvents.SetMonth -> setMonth(event.month)

            is SourceCardEvents.SetYear -> setYear(event.year)

            is SourceCardEvents.SetOtpCode -> setOtpCode(event.otpCode)

            is SourceCardEvents.DumpCvv2ValidationMessage -> dumpCvv2ValidationMessage()

            is SourceCardEvents.DumpMonthError -> dumpMonthError()

            is SourceCardEvents.DumpYearError -> dumpYearError()

            is SourceCardEvents.DumpOtpCodeValidationMessage -> dumpOtpCodeValidationMessage()

            is SourceCardEvents.SendCardPasswordOtp -> sendCardPasswordOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                amount = event.amount,
                requestType = event.requestType
            )

            is SourceCardEvents.SendCardToCardOtp -> sendCardToCardOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                destinationCard = event.destinationCard,
                amount = event.amount
            )

            is SourceCardEvents.BackOtpCodeToDefault -> backOtpCodeToDefault()
        }
    }

}

sealed class SourceCardEvents {
    data class SetSelectedCard(val selectedCard: CardShotItemInfo?) : SourceCardEvents()
    data class SetCvv2(val cvv2: String) : SourceCardEvents()
    data class SetMonth(val month: String) : SourceCardEvents()
    data class SetYear(val year: String) : SourceCardEvents()
    data class SetOtpCode(val otpCode: String) : SourceCardEvents()
    data object DumpCvv2ValidationMessage : SourceCardEvents()
    data object DumpMonthError : SourceCardEvents()
    data object DumpYearError : SourceCardEvents()
    data object DumpOtpCodeValidationMessage : SourceCardEvents()

    data class SendCardPasswordOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val amount: Long,
        val requestType: CardOtpRequestType,
    ) : SourceCardEvents()

    data class SendCardToCardOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val destinationCard: String,
        val amount: Long,
    ) : SourceCardEvents()

    data object BackOtpCodeToDefault : SourceCardEvents()
}