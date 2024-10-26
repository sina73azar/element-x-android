package com.drp.card_facilities.presentation.app_wallet_handler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.data.enums.BillType
import com.drp.data.model.ShahkarUserData
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.network.CustomResponse
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.utility.Commons
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.SMSState
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

interface WalletHandler {
    var walletUiState: WalletUiState

    private fun setShahkarUserData(shahkarUserData: ShahkarUserData) {
        walletUiState = walletUiState.copy(shahkarUserData = shahkarUserData)
    }

    private fun setOtpCode(otpCode: String) {
        walletUiState = walletUiState.copy(walletOtpCode = otpCode)
    }

    private fun dumpOtpCodeValidationMessage() {
        walletUiState =
            walletUiState.copy(walletOtpCodeValidationMessage = UiText.DynamicString(""))
    }

    private fun backOtpCodeToDefault() {
        walletUiState = walletUiState.copy(
            walletOtpCode = "",
            walletOtpCodeValidationMessage = UiText.DynamicString(""),
            walletSmsState = SMSState(),
            walletOtpRqId = ""
        )
    }

    fun validateWalletOtpCode(): Boolean {
        if (!Commons.checkPin(walletUiState.walletOtpCode)) {
            walletUiState =
                walletUiState.copy(walletOtpCodeValidationMessage = UiText.StringResource(R.string.data_validation_pin))
            return false
        }
        return true
    }

    private fun sendAddToWalletOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        amount: Long,
        sourceCardUiState: SourceCardUiState,
        description: String
    ) {
        walletUiState =
            walletUiState.copy(walletSmsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.addToWalletOtp(
                amount = amount,
                sourceCardNumber = sourceCardUiState.selectedCard?.pan ?: "",
                sourceCardCvv2 = sourceCardUiState.cvv2,
                sourceCardExpireYear = sourceCardUiState.year,
                sourceCardExpireMonth = sourceCardUiState.month,
                description = description,
                userData = walletUiState.shahkarUserData,
            ).collectLatest {
                when (it.status) {
                    CustomResponse.Status.SUCCESS -> {
                        walletUiState =
                            walletUiState.copy(walletSmsState = SMSState(SMSStateSuccess = true))
                        walletUiState =
                            walletUiState.copy(walletOtpRqId = it.data?.result?.pRqId.toString())
                    }

                    CustomResponse.Status.Fail -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
                                    SMSStateInquiryFail = true
                                )
                            )
                    }

                    CustomResponse.Status.ERROR -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
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

    private fun sendMinusFromWalletOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        amount: Long,
        destinationIban: String,
        destinationCard: String,
        description: String
    ) {
        walletUiState =
            walletUiState.copy(walletSmsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.minusFromWalletOtp(
                amount = amount,
                description = description,
                destinationIban = destinationIban,
                destinationCard = destinationCard,
                userData = walletUiState.shahkarUserData,
            ).collectLatest {
                when (it.status) {
                    CustomResponse.Status.SUCCESS -> {
                        walletUiState =
                            walletUiState.copy(walletSmsState = SMSState(SMSStateSuccess = true))
                        walletUiState =
                            walletUiState.copy(walletOtpRqId = it.data?.result?.pRqId.toString())
                    }

                    CustomResponse.Status.Fail -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
                                    SMSStateInquiryFail = true
                                )
                            )
                    }

                    CustomResponse.Status.ERROR -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
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

    private fun sendTopUpWithWalletOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        isFromWallet: Boolean,
        amount: Long,
        sourceCardUiState: SourceCardUiState,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String
    ) {
        walletUiState =
            walletUiState.copy(walletSmsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.topUpWithWalletOtp(
                isFromWallet = isFromWallet,
                userData = walletUiState.shahkarUserData,
                amount = amount,
                sourceCardNumber = sourceCardUiState.selectedCard?.pan ?: "",
                sourceCardCvv2 = sourceCardUiState.cvv2,
                sourceCardExpireYear = sourceCardUiState.year,
                sourceCardExpireMonth = sourceCardUiState.month,
                topUpOperatorTab = topUpOperatorTab,
                topUpPhoneNumber = topUpPhoneNumber
            ).collectLatest {
                when (it.status) {
                    CustomResponse.Status.SUCCESS -> {
                        walletUiState =
                            walletUiState.copy(walletSmsState = SMSState(SMSStateSuccess = true))
                        walletUiState =
                            walletUiState.copy(walletOtpRqId = it.data?.result?.pRqId.toString())
                    }

                    CustomResponse.Status.Fail -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
                                    SMSStateInquiryFail = true
                                )
                            )
                    }

                    CustomResponse.Status.ERROR -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
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

    private fun sendInternetPackageWithWalletOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        isFromWallet: Boolean,
        sourceCardUiState: SourceCardUiState,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String,
        packageItem: PackageItem
    ) {
        walletUiState =
            walletUiState.copy(walletSmsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.internetPackageWithWalletOtp(
                isFromWallet = isFromWallet,
                userData = walletUiState.shahkarUserData,
                sourceCardNumber = sourceCardUiState.selectedCard?.pan ?: "",
                sourceCardCvv2 = sourceCardUiState.cvv2,
                sourceCardExpireYear = sourceCardUiState.year,
                sourceCardExpireMonth = sourceCardUiState.month,
                topUpOperatorTab = topUpOperatorTab,
                topUpPhoneNumber = topUpPhoneNumber,
                packageItem = packageItem
            ).collectLatest {
                when (it.status) {
                    CustomResponse.Status.SUCCESS -> {
                        walletUiState =
                            walletUiState.copy(walletSmsState = SMSState(SMSStateSuccess = true))
                        walletUiState =
                            walletUiState.copy(walletOtpRqId = it.data?.result?.pRqId.toString())
                    }

                    CustomResponse.Status.Fail -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
                                    SMSStateInquiryFail = true
                                )
                            )
                    }

                    CustomResponse.Status.ERROR -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
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

    private fun sendSeparatedBillWithWalletOtp(
        viewModel: ViewModel,
        cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        dispatcher: CoroutineDispatcher,
        isFromWallet: Boolean,
        sourceCardUiState: SourceCardUiState,
        billPayment: BillPaymentInfo,
        billType: BillType
    ) {
        walletUiState =
            walletUiState.copy(walletSmsState = SMSState(SMSStateLoading = true))
        viewModel.viewModelScope.launch(dispatcher) {
            cardFacilitiesUserRepository.separatedBillWithWalletOtp(
                isFromWallet = isFromWallet,
                userData = walletUiState.shahkarUserData,
                sourceCardNumber = sourceCardUiState.selectedCard?.pan ?: "",
                sourceCardCvv2 = sourceCardUiState.cvv2,
                sourceCardExpireYear = sourceCardUiState.year,
                sourceCardExpireMonth = sourceCardUiState.month,
                billType = billType,
                billPaymentInfo = billPayment
            ).collectLatest {
                when (it.status) {
                    CustomResponse.Status.SUCCESS -> {
                        walletUiState =
                            walletUiState.copy(walletSmsState = SMSState(SMSStateSuccess = true))
                        walletUiState =
                            walletUiState.copy(walletOtpRqId = it.data?.result?.pRqId.toString())
                    }

                    CustomResponse.Status.Fail -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
                                    SMSStateInquiryFail = true
                                )
                            )
                    }

                    CustomResponse.Status.ERROR -> {
                        walletUiState =
                            walletUiState.copy(
                                walletSmsState = SMSState(
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

    fun sendWalletEvent(event: WalletEvents) {
        when (event) {
            is WalletEvents.SetShahkarUserData -> setShahkarUserData(event.shahkarUserData)
            is WalletEvents.SetOtpCode -> setOtpCode(event.otpCode)
            is WalletEvents.DumpOtpCodeValidationMessage -> dumpOtpCodeValidationMessage()
            is WalletEvents.BackOtpCodeToDefault -> backOtpCodeToDefault()
            is WalletEvents.SendAddToWalletOtp -> sendAddToWalletOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                amount = event.amount,
                sourceCardUiState = event.sourceCardUiState,
                description = event.description
            )

            is WalletEvents.SendMinusFromWalletOtp -> sendMinusFromWalletOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                amount = event.amount,
                destinationIban = event.destinationIban,
                destinationCard = event.destinationCard,
                description = event.description
            )

            is WalletEvents.SendTopUpWithWalletOtp -> sendTopUpWithWalletOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                isFromWallet = event.isFromWallet,
                amount = event.amount,
                sourceCardUiState = event.sourceCardUiState,
                topUpOperatorTab = event.topUpOperatorTab,
                topUpPhoneNumber = event.topUpPhoneNumber
            )

            is WalletEvents.SendInternetPackageWithWalletOtp -> sendInternetPackageWithWalletOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                isFromWallet = event.isFromWallet,
                sourceCardUiState = event.sourceCardUiState,
                topUpOperatorTab = event.topUpOperatorTab,
                topUpPhoneNumber = event.topUpPhoneNumber,
                packageItem = event.packageItem
            )

            is WalletEvents.SendSeparatedBillWithWalletOtp -> sendSeparatedBillWithWalletOtp(
                viewModel = event.viewModel,
                cardFacilitiesUserRepository = event.cardFacilitiesUserRepository,
                dispatcher = event.dispatcher,
                isFromWallet = event.isFromWallet,
                sourceCardUiState = event.sourceCardUiState,
                billPayment = event.billPayment,
                billType = event.billType
            )
        }
    }
}

sealed class WalletEvents {
    data class SetOtpCode(val otpCode: String) : WalletEvents()
    data object DumpOtpCodeValidationMessage : WalletEvents()
    data object BackOtpCodeToDefault : WalletEvents()
    data class SetShahkarUserData(val shahkarUserData: ShahkarUserData) : WalletEvents()
    data class SendAddToWalletOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val amount: Long,
        val sourceCardUiState: SourceCardUiState,
        val description: String
    ) : WalletEvents()

    data class SendMinusFromWalletOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val amount: Long,
        val destinationIban: String,
        val destinationCard: String,
        val description: String
    ) : WalletEvents()

    data class SendTopUpWithWalletOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val isFromWallet: Boolean,
        val amount: Long,
        val sourceCardUiState: SourceCardUiState,
        val topUpOperatorTab: MobileOperatorTab,
        val topUpPhoneNumber: String
    ) : WalletEvents()

    data class SendInternetPackageWithWalletOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val isFromWallet: Boolean,
        val sourceCardUiState: SourceCardUiState,
        val topUpOperatorTab: MobileOperatorTab,
        val topUpPhoneNumber: String,
        val packageItem: PackageItem
    ) : WalletEvents()

    data class SendSeparatedBillWithWalletOtp(
        val viewModel: ViewModel,
        val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
        val dispatcher: CoroutineDispatcher,
        val isFromWallet: Boolean,
        val sourceCardUiState: SourceCardUiState,
        val billPayment: BillPaymentInfo,
        val billType: BillType
    ) : WalletEvents()
}
