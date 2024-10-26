package com.drp.card_facilities.presentation.bill.transfer.separated

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillEvents
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillInquiryScreenState
import com.drp.card_facilities.presentation.bill.inquiry.separated.SeparatedBillViewModel
import com.drp.card_facilities.utility.ReceiptGenerator
import com.drp.data.enums.BillType
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillResult
import com.drp.refah.card_facilities.utility.Commons
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
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
import com.drp.utils.timeStampDateConvert
import com.drp.utils.timeStampToTimeConvert
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@Composable
fun SeparatedUtilityBillTransferBottomSheet(
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
    val paymentInfo = BillPaymentInfo(
        billId = successData.billId,
        paymentId = successData.billPayment,
        amount = successData.amount,
        billerPersianName = billType.faName
    )
    val receiptItems = ReceiptGenerator.billConfirmReceipt(
        result = InquiryBillResult(
            payment = paymentInfo
        ),
        billId = successData.billId,
        paymentId = successData.billPayment,
        cardNumber = if (uiState.cardOrWalletToggle?.id == 0) uiState.sourceCardUiState.selectedCard?.pan
            ?: "" else "",
        walletId = if (uiState.cardOrWalletToggle?.id == 1) uiState.walletUiState.shahkarUserData.walletId?.toString()
            ?: "" else "",
    )
    DynamicSheet(
        isVisible = visibility,
        icon = R.drawable.ic_bill,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.bill_payment_title
        ),
        snackBarHost = snackBarHost
    ) {
        LazyColumn {
            itemsIndexed(items = receiptItems) { index, receiptItem ->
                ReceiptItemCompose(
                    data = receiptItem,
                    isLastIndex = index == receiptItems.lastIndex
                )
            }
            item {
                OTPPasswordEditTextCompose(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.small_padding)),
                    passwordType = SecondAuthenticationMethod.SMS,
                    readOnly = false,
                    otpSmsRequest = {
                        successData.amount.let { amount ->
                            viewModel?.sendEvent(
                                SeparatedBillEvents.SendOtp(billPaymentInfo = paymentInfo, billType = billType)
                            )
                        }
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
                    submitText = stringResource(id = R.string.ok),
                    onSubmitClick = {
                        viewModel?.sendEvent(
                            SeparatedBillEvents.Payment(
//                                billPaymentInfo = paymentInfo,
                                billType = billType
                            )
                        )
                        keyboardController?.hide()
                    },
                    dismissText = stringResource(id = R.string.cancel),
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
                            UiText.StringResource(R.string.date).asString(context = context),
                            timeStampDateConvert(Calendar.getInstance().timeInMillis)
                        )
                    )
                    receiptItemsWithDateAndTime.add(
                        ReceiptItem(
                            0,
                            UiText.StringResource(R.string.hour).asString(context = context),
                            timeStampToTimeConvert(Calendar.getInstance().timeInMillis)
                        )
                    )
                    receiptItemsWithDateAndTime.add(
                        ReceiptItem(
                            0,
                            UiText.StringResource(R.string.cheque_followup_code)
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
                                billType = billType,
                                amount = paymentInfo.amount
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

@Preview(showBackground = true)
@Composable
private fun BillTransferBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SeparatedUtilityBillTransferBottomSheet(
                visibility = true,
                dismiss = {},
                billType = BillType.GAS
            )
        }
    }
}
