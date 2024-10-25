package com.drp.card_facilities.presentation.wallet_to_wallet.transfer

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletScreenEvents
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletScreenState
import com.drp.card_facilities.presentation.wallet_to_wallet.WalletToWalletViewModel
import com.drp.refah.card_facilities.utility.Commons
import com.drp.shared_ui.UiText
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.navigation.sheet.NavigationSheet
import com.drp.shared_ui.navigation.sheet.navigationToDestinationSheet
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.ReceiptItemCompose
import com.drp.shared_ui.widget.SubmitCancelRowCompose
import com.drp.utils.COMPOSE_RECEIPT
import com.drp.utils.ITEMS
import com.drp.utils.RECEIPTMODE
import com.drp.utils.timeStampDateConvert
import com.drp.utils.timeStampToTimeConvert
import java.util.Calendar

@Composable
fun WalletToWalletTransferBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: WalletToWalletViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    navigateToLanding: () -> Unit = {},
    receiptItems: List<ReceiptItem> = emptyList(),
    dismiss: () -> Unit,
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: WalletToWalletScreenState()
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_transfer_wallet,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.wallet_to_wallet_st
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
                SubmitCancelRowCompose(
                    submitText = stringResource(id = R.string.ok),
                    onSubmitClick = {
                        viewModel?.sendEvent(
                            WalletToWalletScreenEvents.Transfer
                        )
                        keyboardController?.hide()
                    },
                    dismissText = stringResource(id = R.string.cancel),
                    onSubmitLoading = uiState.transferState.isLoading()
                ) {
                    viewModel?.sendEvent(WalletToWalletScreenEvents.DismissOtpBottomSheet)
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState.transferState) {
        uiState.transferState.let {
            if (it.isSuccess()) {
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
                activity.navigationToDestinationSheet(
                    navigationPath = NavigationSheet.ReceiptFragment.path,
                    bundle = bundle
                )
                navigateToLanding()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransferToWalletBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            WalletToWalletTransferBottomSheet(
                visibility = true,
            ) {

            }
        }
    }
}