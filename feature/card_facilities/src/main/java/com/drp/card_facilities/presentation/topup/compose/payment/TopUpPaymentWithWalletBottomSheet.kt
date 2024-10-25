package com.drp.card_facilities.presentation.topup.compose.payment

import android.os.Bundle
import android.os.Parcelable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.topup.compose.TopUpEvents
import com.drp.card_facilities.presentation.topup.compose.TopUpScreenState
import com.drp.card_facilities.presentation.topup.compose.TopUpViewModel
import com.drp.refah.card_facilities.utility.Commons
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.shared_ui.UiText
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@Composable
fun TopUpPaymentWithWalletBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: TopUpViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    navigateToLanding: () -> Unit = {},
    receiptItems: List<ReceiptItem> = emptyList(),
    dismiss: () -> Unit,
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: TopUpScreenState()
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_top_up,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.charge_payment_st
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
                    passwordType = SecondAuthenticationMethod.SMS,
                    readOnly = false,
                    otpSmsRequest = {
                        viewModel?.sendEvent(
                            TopUpEvents.SendOtp
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
                    submitText = stringResource(id = R.string.ok),
                    onSubmitClick = {
                        viewModel?.sendEvent(
                            TopUpEvents.Payment
                        )
                        keyboardController?.hide()
                    },
                    dismissText = stringResource(id = R.string.cancel),
                    onSubmitLoading = uiState.topUpPayment.isLoading()
                ) {
                    viewModel?.sendEvent(TopUpEvents.DismissPaymentBottomSheet)
                }
            }
        }

        /** top up payment receipt */
        LaunchedEffect(key1 = uiState.topUpPayment) {
            uiState.topUpPayment.let { paymentState ->
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
                            viewModel?.saveTransaction(receiptItems = receiptItemsWithDateAndTime)
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
private fun TopUpPaymentBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TopUpPaymentBottomSheet(
                visibility = true,
                dismiss = {},
            )
        }
    }
}