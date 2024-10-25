package com.drp.card_facilities.presentation.internet_package.compose.package_type

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.presentation.app_shared_viewmodel.ComposeSharedViewModel
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelUiState
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardHandler
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardUiState
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletHandler
import com.drp.card_facilities.presentation.app_wallet_handler.WalletUiState
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.ContactType
import com.drp.data.enums.SimType
import com.drp.data.enums.TransactionType
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesTransactionRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.internet_package.inquiry.PackageFilterType
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.instacart.library.truetime.TrueTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class InternetPackageTypeViewModel @Inject constructor(
    private val cardFacilitiesRepository: CardFacilitiesRepository,
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
    private val transactionRepository: CardFacilitiesTransactionRepository,
    private val dispatcher: CoroutineDispatcher
) :
    ComposeSharedViewModel(cardFacilitiesRepository, dispatcher), SourceCardHandler, WalletHandler {
    private val _uiState = MutableStateFlow(InternetPackageTypeScreenState())
    val uiState: StateFlow<InternetPackageTypeScreenState>
        get() = _uiState
    override var sharedViewModelState: SharedViewModelUiState
        get() = _uiState.value.sharedViewModelUiState
        set(value) {
            _uiState.value = _uiState.value.copy(sharedViewModelUiState = value)
        }
    private lateinit var packageTypes: ArrayList<PackageFilterType>
    override var sourceCardState: SourceCardUiState
        get() = _uiState.value.internetPackageTypeScreenModel.sourceCardUiState
        set(value) {
            _uiState.value = _uiState.value.copy(
                internetPackageTypeScreenModel = _uiState.value.internetPackageTypeScreenModel.copy(
                    sourceCardUiState = value
                )
            )
        }

    override var walletUiState: WalletUiState
        get() = _uiState.value.walletUiState
        set(value) {
            _uiState.value = _uiState.value.copy(walletUiState = value)
        }

    init {
        sendWalletEvent(WalletEvents.SetShahkarUserData(cardFacilitiesUserRepository.getShahkarUserData()))
    }

    private fun setInternetPackageTypeScreenModel(internetPackageTypeScreenModel: InternetPackageTypeScreenModel) {
        _uiState.value =
            _uiState.value.copy(internetPackageTypeScreenModel = internetPackageTypeScreenModel)
        findPackageTypes()
    }

    private fun findPackageTypes() {
        val simType =
            if (_uiState.value.internetPackageTypeScreenModel.selectedToggle?.id == 0) SimType.PREPAID else SimType.POSTPAID
        val packagesList: List<PackageItem>? =
            _uiState.value.internetPackageTypeScreenModel.products/*?.filter {
                it.simType == simType.walletIndex || it.simType == SimType.ALL.walletIndex
            }*/
        val durationUnitList: List<String>? =
            packagesList?.distinctBy { it.duration }?.map { it.duration }
        packageTypes = arrayListOf()
        packagesList?.let {
            packageTypes.add(PackageFilterType(title = "همه", packages = it as ArrayList))
        }

        durationUnitList?.forEach { durationUnit ->
            createPackageFilerType(durationUnit, packagesList)
            /*when (durationUnit) {
                PackageTypeEnum.HOURLY.name -> {
                    createPackageFilerType(PackageTypeEnum.HOURLY, packagesList)
                }

                PackageTypeEnum.DAILY.name -> {
                    createPackageFilerType(PackageTypeEnum.DAILY, packagesList)
                }

                PackageTypeEnum.WEEKLY.name -> {
                    createPackageFilerType(PackageTypeEnum.WEEKLY, packagesList)
                }

                PackageTypeEnum.MONTHLY.name -> {
                    createPackageFilerType(PackageTypeEnum.MONTHLY, packagesList)
                }

                PackageTypeEnum.YEARLY.name -> {
                    createPackageFilerType(PackageTypeEnum.YEARLY, packagesList)
                }

                else -> {}
            }*/
        }
        _uiState.value = _uiState.value.copy(chips = packageTypes)
        _uiState.value = _uiState.value.copy(selectedChip = packageTypes[0])
    }

    private fun createPackageFilerType(
//        packageDurationUnit: PackageTypeEnum,
        durationUnit: String,
        packagesList: List<PackageItem>
    ) {
//        packageTypes.ad
        packageTypes.add(
                PackageFilterType(
                    title = durationUnit,
                    packages = packagesList.filter { it.duration == durationUnit } as ArrayList<PackageItem>))

    }

    private fun payment() {
        if (!validateWalletOtpCode())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(payment = RequestState.Loading)
            transactionRepository.paymentWithWallet(
                rqId = uiState.value.walletUiState.walletOtpRqId,
                pin = uiState.value.walletUiState.walletOtpCode,
                selectedCard = if (_uiState.value.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 1) uiState.value.internetPackageTypeScreenModel.sourceCardUiState.selectedCard else null,
                expireYear = if (_uiState.value.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 1) uiState.value.internetPackageTypeScreenModel.sourceCardUiState.year else null,
                expireMonth = if (_uiState.value.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 1) uiState.value.internetPackageTypeScreenModel.sourceCardUiState.month else null
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(response.getErrorMessage())
                            )
                        )
                    if (response.isSuccess())
                        coroutineScope {
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = ContactType.MOBILE_NO.name,
                                    title = ContactType.MOBILE_NO.savedContactTitle,
                                    value = _uiState.value.internetPackageTypeScreenModel.mobileNumber.trim()
                                        .filter { it.isDigit() }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(payment = response)
                }
            }
        }
        /*_uiState.value = _uiState.value.copy(payment = RequestState.Loading)
        viewModelScope.launch(dispatcher) {
            transactionRepository.internetPackagePayment(
                password = _uiState.value.internetPackageTypeScreenModel.sourceCardUiState.otpCode,
                selectedCard = _uiState.value.internetPackageTypeScreenModel.sourceCardUiState.selectedCard,
                trk2EquivData = Trk2EquivData(
                    expireDate = uiState.value.internetPackageTypeScreenModel.sourceCardUiState.year.substring(
                        2,
                        4
                    ) + uiState.value.internetPackageTypeScreenModel.sourceCardUiState.month,
                    cvv2 = uiState.value.internetPackageTypeScreenModel.sourceCardUiState.cvv2,
                    pin = uiState.value.internetPackageTypeScreenModel.sourceCardUiState.otpCode
                ),
                mobileNumber = _uiState.value.internetPackageTypeScreenModel.mobileNumber.filter { it.isDigit() },
                operator = _uiState.value.internetPackageTypeScreenModel.selectedMobileOperatorTab,
                packageItem = _uiState.value.selectedPackageItem
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError())
                        sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                UiText.DynamicString(response.getErrorMessage())
                            )
                        )
                    if (response.isSuccess())
                        coroutineScope {
                            cardFacilitiesUserRepository.upsertContact(
                                ContactEntity(
                                    contactType = ContactType.MOBILE_NO.name,
                                    title = ContactType.MOBILE_NO.savedContactTitle,
                                    value = _uiState.value.internetPackageTypeScreenModel.mobileNumber.trim()
                                        .filter { it.isDigit() }
                                )
                            )
                        }
                    _uiState.value = _uiState.value.copy(payment = response)
                }
            }
        }*/
    }

    private fun setSelectedChip(selectedChip: PackageFilterType) {
        _uiState.value = _uiState.value.copy(selectedChip = selectedChip)
    }

    private fun setSelectedPackageItem(packageItem: PackageItem?) {
        _uiState.value = _uiState.value.copy(selectedPackageItem = packageItem)
    }

    private fun dismissFailDialog() {
        if (_uiState.value.payment.isFail())
            _uiState.value = _uiState.value.copy(payment = RequestState.Idle)
    }

    suspend fun saveTransaction(receiptItems: List<ReceiptItem>) {
        var time = Calendar.getInstance().timeInMillis
        try {
            TrueTime.build().initialize()
            time = TrueTime.now().time
        } catch (ex: Exception) {
            Log.d("timeNotWork", ex.message.toString())
        }
        transactionRepository.insertTransaction(
            transaction = TransactionEntity(
                timeStamp = time,
                transactionType = TransactionType.NET_PACK.type,
                transactionValue = TransactionType.NET_PACK.type.plus(" ")
                    .plus(_uiState.value.internetPackageTypeScreenModel.selectedMobileOperatorTab.title),
                amount = _uiState.value.selectedPackageItem?.let { it.price + it.tax } ?: 0L,
                transactionStatus = "SUCCESS",
                sourceCardNo = _uiState.value.internetPackageTypeScreenModel.sourceCardUiState.selectedCard?.pan
                    ?: "",
                receiptItem = receiptItems
            )
        )
    }

    fun sendEvent(event: InternetPackageTypeEvents) {
        when (event) {
            is InternetPackageTypeEvents.SetSelectedPackageItem -> setSelectedPackageItem(event.packageItem)
            is InternetPackageTypeEvents.Payment -> payment()
            is InternetPackageTypeEvents.SendOtp -> sendWalletEvent(
                WalletEvents.SendInternetPackageWithWalletOtp(
                    viewModel = this,
                    cardFacilitiesUserRepository = cardFacilitiesUserRepository,
                    dispatcher = dispatcher,
                    isFromWallet = uiState.value.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 0,
                    sourceCardUiState = uiState.value.internetPackageTypeScreenModel.sourceCardUiState,
                    topUpOperatorTab = uiState.value.internetPackageTypeScreenModel.selectedMobileOperatorTab,
                    topUpPhoneNumber = uiState.value.internetPackageTypeScreenModel.mobileNumber.filter { it.isDigit() },
                    packageItem = uiState.value.selectedPackageItem!!
                )
            )

            is InternetPackageTypeEvents.DismissFailureDialog -> dismissFailDialog()
            is InternetPackageTypeEvents.SetInternetPackageTypeScreenModel -> setInternetPackageTypeScreenModel(
                event.internetPackageTypeScreenModel
            )

            is InternetPackageTypeEvents.SetSelectedChip -> setSelectedChip(event.selectedChip)
        }
    }
}

sealed class InternetPackageTypeEvents {
    data class SetInternetPackageTypeScreenModel(val internetPackageTypeScreenModel: InternetPackageTypeScreenModel) :
        InternetPackageTypeEvents()

    data object Payment : InternetPackageTypeEvents()
    data class SetSelectedChip(val selectedChip: PackageFilterType) : InternetPackageTypeEvents()
    data object DismissFailureDialog : InternetPackageTypeEvents()
    data object SendOtp : InternetPackageTypeEvents()
    data class SetSelectedPackageItem(val packageItem: PackageItem?) : InternetPackageTypeEvents()
}