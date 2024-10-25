package com.drp.card_facilities.presentation.transaction_history

import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.R
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.TransactionType
import com.drp.shared_ui.navigation.sheet.NavigationSheet
import com.drp.shared_ui.navigation.sheet.navigationToDestinationSheet
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.AnimatedShimmer
import com.drp.shared_ui.widget.CardShimmerItem
import com.drp.shared_ui.widget.CustomLine
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.EmptyOrFailContent
import com.drp.shared_ui.widget.ListActionButton
import com.drp.shared_ui.widget.ShimmerEffect
import com.drp.utils.COMPOSE_RECEIPT
import com.drp.utils.ITEMS
import com.drp.utils.RECEIPTMODE
import com.drp.utils.currencyFormatter
import com.drp.utils.diffTwoTime
import com.drp.utils.panFormatter
import java.util.Calendar
import com.drp.shared_ui.R as UIRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionHistoryViewModel? = null,
    navController: NavHostController = rememberNavController()
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: TransactionHistoryScreenState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val activity = context as FragmentActivity

    /** screen region */
    Scaffold(
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.transaction_history_title),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        Column(modifier = modifier.padding(top = paddingValues.calculateTopPadding())) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = UIRes.dimen.medium_padding))
                    .padding(top = dimensionResource(id = UIRes.dimen.small_padding)),
                value = uiState.searchQuery,
                onValueChange = { newQuery ->
                    viewModel?.sendEvent(TransactionHistoryEvents.SetSearchQuery(newQuery))
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIcon = {
                    if (uiState.searchQuery.isEmpty()) {
                        IconButton(onClick = { }) {
                            Icon(
                                modifier = Modifier.size(
                                    dimensionResource(id = UIRes.dimen.icon_size)
                                ),
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = ""
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                viewModel?.sendEvent(TransactionHistoryEvents.SetSearchQuery(""))
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(
                                    dimensionResource(id = UIRes.dimen.icon_size)
                                ),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_fill),
                                contentDescription = "close Icon",
                                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_by_card_number),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.7f)
                    )
                },
                shape = RoundedCornerShape(dimensionResource(id = UIRes.dimen.large_corner)),
                singleLine = true,
                maxLines = 1
            )
            LazyColumn {
                if (uiState.transactions.isLoading())
                    item {
                        ShimmerEffect {
                            AnimatedShimmer { brush ->
                                CardShimmerItem(brush = brush)
                            }
                        }
                    }
                if (uiState.transactions.isSuccess() && uiState.transactions.getSuccessData()
                        .isEmpty()
                )
                    item {
                        EmptyOrFailContent(
                            modifier = Modifier
                                .padding(dimensionResource(id = UIRes.dimen.small_padding)),
                            desc = stringResource(id = R.string.empty_transaction_history),
                        )
                    }
                if (uiState.transactions.isSuccess() && uiState.transactions.getSuccessData()
                        .isNotEmpty()
                )
                    items(
                        items = uiState.transactions.getSuccessData().reversed(),
                        key = { it.timeStamp }) { transaction ->
                        TransactionHistoryItem(
                            transaction = transaction,
                            onTransactionRemoveClick = {
                                keyboardController?.hide()
                                viewModel?.sendEvent(
                                    TransactionHistoryEvents.DeleteTransaction(
                                        transaction.timeStamp
                                    )
                                )
                            },
                            onTransactionDetailClick = {
                                keyboardController?.hide()
                                val bundle = Bundle()
                                bundle.putBoolean(RECEIPTMODE, true)
                                bundle.putBoolean(COMPOSE_RECEIPT, true)
                                bundle.putParcelableArrayList(
                                    ITEMS,
                                    transaction.receiptItem as ArrayList<out Parcelable>
                                )
                                activity.navigationToDestinationSheet(
                                    navigationPath = NavigationSheet.ReceiptFragment.path,
                                    bundle = bundle
                                )
                            })
                    }
                item {
                    Spacer(modifier = Modifier.height(170.dp))
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(
    modifier: Modifier = Modifier,
    transaction: TransactionEntity,
    onTransactionRemoveClick: () -> Unit,
    onTransactionDetailClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = UIRes.dimen.medium_padding))
            .padding(top = dimensionResource(id = UIRes.dimen.medium_padding)),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = UIRes.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(UIRes.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(id = UIRes.dimen.large_padding)),
                text = stringResource(id = R.string.sources_card),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(id = UIRes.dimen.large_padding)),
                text = panFormatter(
                    transaction.sourceCardNo, " "
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = TextDirection.Ltr
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        CustomLine()
        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = UIRes.dimen.large_padding),
                    end = dimensionResource(
                        id = UIRes.dimen.small_padding
                    )
                )
                .padding(
                    top = dimensionResource(
                        id = UIRes.dimen.large_padding
                    )
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = transaction.transactionValue,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = dimensionResource(id = UIRes.dimen.small_padding)),
                text = currencyFormatter(
                    transaction.amount
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier
                    .padding(end = dimensionResource(id = UIRes.dimen.small_padding))
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(dimensionResource(id = UIRes.dimen.large_corner))
                    )
                    .padding(
                        horizontal = dimensionResource(id = UIRes.dimen.medium_padding),
                        vertical = 2.dp
                    )
                    .wrapContentWidth(),
                text = stringResource(id = UIRes.string.irr_currency),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            modifier = Modifier
                .padding(
                    end = dimensionResource(id = UIRes.dimen.extra_large_padding),
                )
                .padding(
                    vertical = dimensionResource(
                        id = UIRes.dimen.large_padding
                    )
                )
                .align(Alignment.End),
            text = diffTwoTime(Calendar.getInstance().timeInMillis, transaction.timeStamp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            ListActionButton(
                modifier = Modifier
                    .weight(0.5f),
                title = stringResource(id = R.string.remove),
                icon = R.drawable.ic_remove_transaction,
                onClick = onTransactionRemoveClick
            )
            Spacer(
                modifier = Modifier
                    .width(0.5.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
            ListActionButton(
                modifier = Modifier
                    .weight(0.5f),
                title = stringResource(id = R.string.details),
                icon = R.drawable.ic_detail,
                onClick = onTransactionDetailClick
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionHistoryItemPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TransactionHistoryItem(
                transaction = TransactionEntity(
                    timeStamp = 123456789,
                    transactionType = TransactionType.NET_PACK.type,
                    transactionValue = TransactionType.NET_PACK.type,
                    amount = 1250000,
                    transactionStatus = "Success",
                    sourceCardNo = "5894631884724436",
                    receiptItem = emptyList(),
                ), onTransactionRemoveClick = {}) {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionHistoryPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TransactionHistoryScreen()
        }
    }
}