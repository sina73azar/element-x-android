package com.drp.card_facilities.presentation.insurance.payment

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.insurance.InsuranceEvents
import com.drp.card_facilities.presentation.insurance.InsuranceScreenState
import com.drp.card_facilities.presentation.insurance.InsuranceViewModel
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
fun InsurancePaymentBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: InsuranceViewModel? = null,
    @SuppressLint("ComposableLambdaParameterPosition") snackBarHost: @Composable () -> Unit = {},
    navigateToLanding: () -> Unit = {},
    receiptItems: List<ReceiptItem> = emptyList(),
    dismiss: () -> Unit,
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: InsuranceScreenState()
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val coroutineScope = rememberCoroutineScope()
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_insurance,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.insurance_bill_inquiry_title
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
                        viewModel?.sendEvent(InsuranceEvents.SendOtp)
                    },
                    smsState = uiState.sourceCardUiState.smsState,
                    textValue = uiState.sourceCardUiState.otpCode,
                    onValueChange = { otpCode ->
                        viewModel?.sendSourceCardEvent(
                            SourceCardEvents.SetOtpCode(otpCode = otpCode)
                        )
                    },
                    maxLength = 12,
                    errorMessage = uiState.sourceCardUiState.otpCodeValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendSourceCardEvent(
                            SourceCardEvents.DumpOtpCodeValidationMessage,
                        )
                    },
                    showError = {
                        viewModel?.sendSharedViewModelEvent(
                            SharedViewModelEvents.ShowError(it)
                        )
                    }
                )
            }
            item {
                SubmitCancelRowCompose(
                    submitText = stringResource(id = R.string.ok),
                    onSubmitClick = {
                        viewModel?.sendEvent(InsuranceEvents.Payment)
                    },
                    dismissText = stringResource(id = R.string.cancel),
                    onSubmitLoading = uiState.insurancePayment.isLoading()
                ) {
                    viewModel?.sendEvent(InsuranceEvents.DismissPaymentBottomSheet)
                }
            }
        }

        /** top up payment receipt */
        LaunchedEffect(key1 = uiState.insurancePayment) {
            uiState.insurancePayment.let { paymentState ->
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
private fun InsurancePaymentBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            InsurancePaymentBottomSheet(
                visibility = true,
                dismiss = {},
            )
        }
    }
}
