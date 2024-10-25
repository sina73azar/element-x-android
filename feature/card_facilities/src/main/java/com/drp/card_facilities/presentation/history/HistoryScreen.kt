package com.drp.card_facilities.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.transaction_history.TransactionHistoryScreen
import com.drp.data.model.history.HistoryItemResult
import com.drp.refah.ui.theme.HARD_GREEN
import com.drp.refah.ui.theme.Red
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.AnimatedShimmer
import com.drp.shared_ui.widget.CardShimmerItem
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.EmptyOrFailContent
import com.drp.shared_ui.widget.ShimmerEffect
import com.drp.utils.currencyFormatter

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HistoryViewModel? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: HistoryScreenState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val activity = context as FragmentActivity
    /*BackHandler {
        viewModel?.setSelectedBottomBarId(3)
        navController.popBackStack(
            route = MainScreens.HomeScreen.route,
            inclusive = false
        )
    }*/
    Column(modifier = Modifier.fillMaxSize()) {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.transaction_history_title),
            backBtnVisible = true,
            onBackClick = {
                navController.popBackStack()
            }
        )
        /*TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding))
                .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
            value = uiState.searchQuery,
            onValueChange = { newQuery ->
                viewModel?.setSearchQuery(newQuery)
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
                                dimensionResource(id = com.drp.shared_ui.R.dimen.icon_size)
                            ),
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = ""
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            viewModel?.setSearchQuery("")
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(
                                dimensionResource(id = com.drp.shared_ui.R.dimen.icon_size)
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
            shape = RoundedCornerShape(dimensionResource(id = com.drp.shared_ui.R.dimen.large_corner)),
            singleLine = true,
            maxLines = 1
        )*/
        LazyColumn {
            if (uiState.transactions.isLoading())
                item {
                    ShimmerEffect {
                        AnimatedShimmer { brush ->
                            CardShimmerItem(brush = brush)
                        }
                    }
                }
            if (uiState.transactions.isSuccess() && uiState.transactions.getSuccessData().result
                    .isEmpty()
            )
                item {
                    EmptyOrFailContent(
                        modifier = Modifier
                            .padding(dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                        desc = stringResource(id = R.string.empty_transaction_history),
                    )
                }
            if (uiState.transactions.isSuccess() && uiState.transactions.getSuccessData().result
                    .isNotEmpty()
            )
                items(
                    items = uiState.transactions.getSuccessData().result
                ) { historyItem ->
                    TransactionHistoryItem(
                        transaction = historyItem,
                        /*onTransactionRemoveClick = {
                            keyboardController?.hide()
//                            viewModel?.deleteTransaction(transaction.timeStamp)
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
                        }*/
                    )
                }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun TransactionHistoryItem(
    modifier: Modifier = Modifier,
    transaction: HistoryItemResult,
    /*onTransactionRemoveClick: () -> Unit,
    onTransactionDetailClick: () -> Unit,*/
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding))
            .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = com.drp.shared_ui.R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(com.drp.shared_ui.R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                text = stringResource(id = R.string.sources_card),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                text = panFormatter(
                    transaction.transactionDate, " "
                ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDirection = TextDirection.Ltr
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        CustomLine()*/
        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding),
                    end = dimensionResource(
                        id = com.drp.shared_ui.R.dimen.small_padding
                    )
                )
                .padding(
                    top = dimensionResource(
                        id = com.drp.shared_ui.R.dimen.large_padding
                    )
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(id = com.drp.shared_ui.R.dimen.large_corner)),
                text = transaction.description.replace(" - ", "\n"),
//                minLines = 3,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
                text = currencyFormatter(
                    if (transaction.credit.toLong() == 0L) transaction.debit.toLong() else transaction.credit.toLong()
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier
                    .padding(end = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding))
                    .background(
                        color = if (transaction.credit.toLong() == 0L) Red.copy(alpha = 0.15f) else HARD_GREEN.copy(
                            alpha = 0.15f
                        ),
                        shape = RoundedCornerShape(dimensionResource(id = com.drp.shared_ui.R.dimen.large_corner))
                    )
                    .padding(
                        horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding),
                        vertical = 2.dp
                    )
                    .wrapContentWidth(),
                text = stringResource(id = com.drp.shared_ui.R.string.irr_currency),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.extra_large_padding),
                )
                .padding(
                    vertical = dimensionResource(
                        id = com.drp.shared_ui.R.dimen.large_padding
                    )
                )
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                text = transaction.transactionDate.let {
                    val stringBuilder = StringBuilder(it)
                    stringBuilder.insert(4, "/")
                    stringBuilder.insert(7, "/")
                    stringBuilder.toString()
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                text = stringResource(id = R.string.time_st).plus(" ")
                    .plus(transaction.transactionTime.let {
                        val stringBuilder = StringBuilder(it)
                        stringBuilder.insert(2, ":")
                        stringBuilder.insert(5, ":")
                        stringBuilder.toString()
                    }),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
                color = MaterialTheme.colorScheme.primary
            )
        }

        /*Row(
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
        }*/

    }
}

/*@Preview(showBackground = true)
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
}*/

@Preview(showBackground = true)
@Composable
private fun TransactionHistoryPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TransactionHistoryScreen()
        }
    }
}