package com.drp.refah.ui.presentation.receipt.receipt_screen

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.refah.ui.theme.Green
import com.drp.refah.ui.theme.Orange
import com.drp.shared_ui.R
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
fun TransactionReceiptBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    receiptItems: List<ReceiptItem> = emptyList(),
    receiptTitle: String = stringResource(id = R.string.receipt_title),
    shareAction: () -> Unit,
    infoVisibility: Boolean = false,
    successTitle: String = stringResource(id = R.string.success_title)
) {
    DynamicSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        title = receiptTitle,
        icon = R.drawable.ic_info,
        partiallyExpanded = true
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
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = dimensionResource(id = R.dimen.small_padding)
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = dimensionResource(id = R.dimen.large_corner),
                            topEnd = dimensionResource(
                                id = R.dimen.large_corner
                            ),
                            bottomStart = if (receiptItems.isEmpty()) dimensionResource(id = R.dimen.large_corner) else 0.dp,
                            bottomEnd = if (receiptItems.isEmpty()) dimensionResource(id = R.dimen.large_corner) else 0.dp
                        )
                    )
                    .background(color = MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .aspectRatio(1f / 0.5f)
                        .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                    painter = painterResource(id = R.drawable.full_logo),
                    contentDescription = null
                )
                Row(
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.medium_padding),
                            bottom = if (receiptItems.isEmpty()) dimensionResource(id = R.dimen.large_padding) else 0.dp
                        )
                        .border(
                            width = 1.dp, color = Green, shape = RoundedCornerShape(
                                dimensionResource(id = R.dimen.medium_corner)
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(
                                dimensionResource(id = R.dimen.small_padding)
                            )
                            .size(dimensionResource(id = R.dimen.icon_size)),
                        painter = painterResource(id = R.drawable.ic_baseline_check_circle_24),
                        contentDescription = null,
                        tint = Green
                    )
                    Text(
                        modifier = Modifier.padding(end = dimensionResource(id = R.dimen.small_padding)),
                        text = successTitle,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Green
                    )
                }
                if (receiptItems.isNotEmpty())
                    Row(
                        modifier = Modifier
                            .padding(vertical = dimensionResource(id = R.dimen.large_padding)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.small_padding)),
                            text = stringResource(id = R.string.transfer_amount),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                        Text(
                            text = receiptItems.find { item -> item.type == ReceiptType.AMOUNT }?.value + " " + stringResource(
                                id = R.string.irr_currency
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
            }
            if (receiptItems.isNotEmpty()) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.small_padding),
                            )
                            .background(color = MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(bottom = dimensionResource(id = R.dimen.large_padding)),
                            painter = painterResource(id = R.drawable.success_watermark),
                            contentDescription = null
                        )
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            itemsIndexed(items = receiptItems) { index, receiptItem ->
                                ReceiptItemCompose(
                                    data = receiptItem,
                                    isLastIndex = index == receiptItems.lastIndex
                                )
                            }
                            if (infoVisibility)
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                all = dimensionResource(
                                                    id = R.dimen.small_padding
                                                )
                                            )
                                    ) {
                                        Icon(
                                            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.small_padding)),
                                            painter = painterResource(id = R.drawable.ic_circle_info),
                                            contentDescription = null,
                                            tint = Orange
                                        )
                                        Text(
                                            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.small_padding)),
                                            text = stringResource(id = R.string.receipt_transaction_ach),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            textAlign = TextAlign.Justify
                                        )
                                    }
                                }
                            item {
                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.tertiary.copy(
                                                alpha = 0.15f
                                            )
                                        )
                                        .padding(bottom = dimensionResource(id = R.dimen.large_padding)),
                                    painter = painterResource(id = R.drawable.receipt_bottom_line),
                                    contentDescription = null
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
                                        .background(color = MaterialTheme.colorScheme.primaryContainer),
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionReceiptBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TransactionReceiptBottomSheet(
                isVisible = true,
                onDismiss = {},
                receiptItems = emptyList(),
                shareAction = {}
            )
        }
    }
}