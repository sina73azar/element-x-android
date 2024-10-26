package com.drp.card_facilities.presentation.bill.transfer.separated

import CustomBillToggleModel
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillEvents
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillInquiryScreenState
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillViewModel
import com.drp.card_facilities.utility.ReceiptGenerator
import com.drp.data.enums.BillType
import com.drp.data.model.bill.inquiry.BillInquiryResult
import com.drp.refah.card_facilities.utility.Commons
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.refah.ui.theme.White
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.navigation.sheet.NavigationSheet
import com.drp.shared_ui.navigation.sheet.navigationToDestinationSheet
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.OTPPasswordEditTextCompose
import com.drp.shared_ui.widget.ReceiptItemCompose
import com.drp.shared_ui.widget.SubmitCancelRowCompose
import com.drp.utils.COMPOSE_RECEIPT
import com.drp.utils.ITEMS
import com.drp.utils.RECEIPTMODE
import com.drp.utils.currencyFormatter
import com.drp.utils.timeStampDateConvert
import com.drp.utils.timeStampToTimeConvert
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import toBillPaymentInfo
import java.util.Calendar
import io.element.android.x.R as UiRes

@Composable
fun SeparatedPhoneBillTransferBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    billType: BillType,
    viewModel: SeparatedBillViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    navigateToLanding: () -> Unit = {},
    dismiss: () -> Unit,
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: SeparatedBillInquiryScreenState()
    val successData =
        uiState.separatedUtilityBillInquiryState.getSuccessData().billInquiryResponseService
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val json = Json {
        ignoreUnknownKeys = true
    }
    val toggleData = listOf(
        CustomBillToggleModel(
            id = 0,
            amount = successData.amount,
            desc = stringResource(
                id = UiRes.string.mid_term
            ),
            billId = successData.billId,
            paymentId = successData.billPayment,
            billType = if (billType == BillType.FIXEDLINE) BillType.FIXEDLINE else BillType.MOBILE
        ),
        CustomBillToggleModel(
            id = 1,
            amount = successData.result?.let { json.decodeFromString<BillInquiryResult>(it).finalterm_amount }
                ?: 0L,
            desc = stringResource(
                id = UiRes.string.final_term
            ),
            billId = successData.billId,
            paymentId = successData.result?.let { json.decodeFromString<BillInquiryResult>(it).finalterm_pay_id }
                ?: "",
            billType = if (billType == BillType.FIXEDLINE) BillType.FIXEDLINEEXTENDED else BillType.MOBILE_EXTENDED
        )
    )
    var selectedToggle by remember {
        mutableStateOf(toggleData[0])
    }
    var receiptItems by remember { mutableStateOf<List<ReceiptItem>>(emptyList()) }

    LaunchedEffect(key1 = selectedToggle) {
        receiptItems = ReceiptGenerator.phoneBillReceipt(
            billType = selectedToggle.billType,
            billId = selectedToggle.billId,
            paymentId = selectedToggle.paymentId,
            phoneNumber = if (billType == BillType.FIXEDLINE) uiState.fixedPhoneNumber.trim()
                .filter { it.isDigit() } else uiState.mobileNumber.trim()
                .filter { it.isDigit() },
            amount = selectedToggle.amount,
            cardNumber = if (uiState.cardOrWalletToggle?.id == 0) uiState.sourceCardUiState.selectedCard?.pan
                ?: "" else "",
            walletId = if (uiState.cardOrWalletToggle?.id == 1) uiState.walletUiState.shahkarUserData.walletId?.toString()
                ?: "" else "",
        )
    }
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = UiRes.drawable.ic_bill,
        onDismiss = dismiss,
        title = stringResource(
            id = UiRes.string.bill_payment_title
        ),
        snackBarHost = snackBarHost
    ) {
        LazyColumn {
            item {
                CustomSingleSelectionToggle(
                    modifier = Modifier
                            .padding(horizontal = dimensionResource(id = UiRes.dimen.large_padding))
                            .padding(
                                    vertical = dimensionResource(
                                            id = UiRes.dimen.large_padding
                                    )
                            ),
                    selectedToggle = selectedToggle,
                    data = toggleData
                ) {
                    selectedToggle = it
                }
            }
            itemsIndexed(items = receiptItems) { index, receiptItem ->
                ReceiptItemCompose(
                    data = receiptItem,
                    isLastIndex = index == receiptItems.lastIndex
                )
            }
            item {
                OTPPasswordEditTextCompose(
                    passwordType = SecondAuthenticationMethod.SMS,
                    readOnly = false,
                    otpSmsRequest = {
                        viewModel?.sendEvent(
                            SeparatedBillEvents.SendOtp(
                                billPaymentInfo = selectedToggle.toBillPaymentInfo(),
                                billType = billType
                            )
                        )
                    },
                    smsState = uiState.walletUiState.walletSmsState,
                    textValue = uiState.walletUiState.walletOtpCode,
                    onValueChange = { otpCode ->
                        viewModel?.sendWalletEvent(
                            WalletEvents.SetOtpCode(otpCode = otpCode)
                        )
                    },
                    maxLength = 12,
                    errorMessage = uiState.walletUiState.walletOtpCodeValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendWalletEvent(
                            WalletEvents.DumpOtpCodeValidationMessage,
                        )
                    },
                    showError = {
                        viewModel?.sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(
                                it
                            )
                        )
                    }
                )
            }
            item {
                SubmitCancelRowCompose(
                    submitText = stringResource(id = UiRes.string.ok),
                    onSubmitClick = {
                        viewModel?.sendEvent(
                            SeparatedBillEvents.Payment(
                                /*BillPaymentInfo(
                                    billId = selectedToggle.billId,
                                    paymentId = selectedToggle.paymentId,
                                    amount = selectedToggle.amount
                                ),*/
                                billType
                            ),
                        )
                        keyboardController?.hide()
                    },
                    dismissText = stringResource(id = UiRes.string.cancel),
                    onSubmitLoading = uiState.billPaymentState.isLoading()
                ) {
                    viewModel?.sendEvent(SeparatedBillEvents.DismissInquiry)
                }
            }
        }

        /** payment bill receipt */
        LaunchedEffect(key1 = uiState.billPaymentState) {
            uiState.billPaymentState.let { paymentState ->
                if (paymentState.isSuccess()) {
                    val receiptItemsWithDateAndTime =
                        receiptItems as MutableList<ReceiptItem>
                    receiptItemsWithDateAndTime.add(
                        ReceiptItem(
                            0,
                            UiText.StringResource(UiRes.string.date).asString(context = context),
                            timeStampDateConvert(Calendar.getInstance().timeInMillis)
                        )
                    )
                    receiptItemsWithDateAndTime.add(
                        ReceiptItem(
                            0,
                            UiText.StringResource(UiRes.string.hour).asString(context = context),
                            timeStampToTimeConvert(Calendar.getInstance().timeInMillis)
                        )
                    )
                    receiptItemsWithDateAndTime.add(
                        ReceiptItem(
                            0,
                            UiText.StringResource(UiRes.string.cheque_followup_code)
                                .asString(context = context),
                            Commons.generateNumber()
                        )
                    )
                    val bundle = Bundle()
                    bundle.putBoolean(RECEIPTMODE, true)
                    bundle.putBoolean(COMPOSE_RECEIPT, true)
                    bundle.putParcelableArrayList(
                        ITEMS,
                        receiptItemsWithDateAndTime as ArrayList<out Parcelable>
                    )
                    coroutineScope.launch(Dispatchers.IO) {
                        coroutineScope {
                            viewModel?.saveTransaction(
                                receiptItems = receiptItemsWithDateAndTime,
                                amount = selectedToggle.amount,
                                billType = billType
                            )
                        }
                        withContext(Dispatchers.Main) {
                            navigateToLanding()
                            activity.navigationToDestinationSheet(
                                navigationPath = NavigationSheet.ReceiptFragment.path,
                                bundle = bundle
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSingleSelectionToggle(
    modifier: Modifier = Modifier,
    data: List<CustomBillToggleModel> = emptyList(),
    selectedToggle: CustomBillToggleModel,
    onSelect: (CustomBillToggleModel) -> Unit
) {
    if (data.isNotEmpty()) {
        LaunchedEffect(key1 = 0) {
            onSelect(data[0])
        }
        Row(
            modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensionResource(id = UiRes.dimen.medium_corner)))
                    .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(size = dimensionResource(id = UiRes.dimen.medium_corner))
                    )
        ) {
            if (data.size <= 2) {
                data.forEachIndexed { index, toggleModel ->
                    Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .background(
                                        color = if (selectedToggle.id == toggleModel.id) MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.1f
                                        ) else White
                                )
                                .border(
                                        width = 1.dp,
                                        color = if (selectedToggle.id == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = when (index) {
                                            0 -> RoundedCornerShape(
                                                    topStart = dimensionResource(
                                                            id = UiRes.dimen.medium_corner
                                                    ),
                                                    bottomStart = dimensionResource(
                                                            id = UiRes.dimen.medium_corner
                                                    )
                                            )

                                            data.lastIndex -> RoundedCornerShape(
                                                    topEnd = dimensionResource(
                                                            id = UiRes.dimen.medium_corner
                                                    ),
                                                    bottomEnd = dimensionResource(
                                                            id = UiRes.dimen.medium_corner
                                                    )
                                            )

                                            else -> RoundedCornerShape(0.dp)
                                        }
                                )
                                .padding(vertical = dimensionResource(id = UiRes.dimen.small_padding))
                                .clickable {
                                    if (toggleModel.amount != 0L)
                                        onSelect(toggleModel)
                                },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currencyFormatter(toggleModel.amount) + " " + stringResource(id = UiRes.string.irr_currency),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedToggle.id == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = toggleModel.desc,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = if (selectedToggle.id == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeparatedPhoneBillTransferBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SeparatedPhoneBillTransferBottomSheet(
                visibility = true,
                dismiss = {},
                billType = BillType.FIXEDLINE
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BillAmountRowPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        }
    }
}
