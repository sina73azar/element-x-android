package io.element.android.x.Receipt.receipt_screen

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.refah.ui.theme.Green
import io.element.android.x.R
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.ReceiptItemCompose
import com.drp.utils.accountMaskFormatter
import com.drp.utils.panMaskFormatter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReceiptBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    receiptItems: List<ReceiptItem>,
    receiptTitle: String = stringResource(id = R.string.receipt_title),
    shareAction: () -> Unit
) {
    /**
     * preview mode is not available with calling permission request
     */
    val permissionsRequired = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val permissionsState =
        rememberMultiplePermissionsState(permissions = permissionsRequired)
    LaunchedEffect(key1 = permissionsState.allPermissionsGranted) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
    DynamicSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        title = receiptTitle,
        icon = R.drawable.ic_info,
        partiallyExpanded = true
    ) {
        if (receiptItems.any { it.title == stringResource(R.string.receipt_fragment_source_account_title) }) {
            val value =
                receiptItems.findLast { it.title == stringResource(R.string.receipt_fragment_source_account_title) }?.value
            receiptItems.findLast { it.title == stringResource(R.string.receipt_fragment_source_account_title) }?.value =
                accountMaskFormatter(value)
        }
        receiptItems.onEach { receiptItem ->
            if (receiptItem.type == ReceiptType.CARD) {
                val value = receiptItem.value?.filter { it.isDigit() }
                receiptItem.value = panMaskFormatter(value)
            }
        }
        LazyColumn {
            itemsIndexed(items = receiptItems) { index, receiptItem ->
                ReceiptItemCompose(
                    data = receiptItem,
                    isLastIndex = index == receiptItems.lastIndex
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .clickable {
                            shareAction()
                        }
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.button_height))
                        .background(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        tint = Green
                    )
                    Text(
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.small_padding)),
                        text = stringResource(id = R.string.receipt_share),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Green
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ReceiptBottomSheet(
                isVisible = true,
                onDismiss = {},
                receiptItems = listOf(
                    ReceiptItem(order = 0, "asd", value = "sda"),
                    ReceiptItem(order = 0, "asd", value = "sda"),
                    ReceiptItem(order = 0, "asd", value = "sda"),
                    ReceiptItem(order = 0, "asd", value = "sda"),
                    ReceiptItem(order = 0, "asd", value = "sda"),
                    ReceiptItem(order = 0, "asd", value = "sda"),
                ),
                shareAction = {}
            )
        }
    }
}
