package com.drp.card_facilities.presentation.last_ten_statement.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.utils.currencyFormatter
import io.element.android.x.R

@Composable
fun LastTenStatementResultScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    inquiryResponse: ArrayList<BankStatement>? = null
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.card_statement_st),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {
            items(items = inquiryResponse?.toList()!!) {
                StatementItemWidget(bankStatement = it)
            }

        }
    }
}

@Composable
fun StatementItemWidget(modifier: Modifier = Modifier, bankStatement: BankStatement) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            .padding(top = dimensionResource(id = R.dimen.medium_padding)),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(id = R.dimen.small_padding)
                    ),
                onClick = {}) {
//                Icon(
//                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
//                    imageVector = ImageVector.vectorResource(
//                        if (bankStatement.transactionType == "-") R.drawable.ic_withdraw
//                        else R.drawable.ic_deposit
//                    ),
//                    contentDescription = "",
//                    tint = if (bankStatement.transactionType == "-") Red else Green
//                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        end = dimensionResource(id = R.dimen.medium_padding),
                        start = 4.dp
                    ),
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = bankStatement.description,
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
                            .padding(start = dimensionResource(id = R.dimen.medium_padding)),
                        text = currencyFormatter(
                            bankStatement.amount.toLong()
                        ) + " " + stringResource(
                            id = R.string.irr_currency
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Text(
//                        modifier = Modifier.weight(1f),
//                        text = stringResource(id = R.string.balance_title) + ": " +
//                                currencyFormatter(
//                                    bankStatement.balance.depositableAmount
//                                ) + " " +
//                                stringResource(
//                                    id = R.string.irr_currency
//                                ),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = dimensionResource(id = R.dimen.medium_padding)),
                        text = bankStatement.date,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LastTenStatementResultScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LastTenStatementResultScreen()
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun StatementItemWidgetPreview() {
//    ApplicationTheme {
//        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
//            StatementItemWidget(
//                bankStatement = BankStatement(
//                    id = null,
//                    dateTime = "14021241725136",
//                    transactionType = "-",
//                    amount = 12000.0,
//                    comment = "پرداخت قسط وام از شماره کارت 5423130",
//                    balance = Balance(
//                        ledgerBalance = null,
//                        depositableAmount = 100000L,
//                        blockedAmount = null,
//                        currency = null
//                    )
//                )
//            )
//        }
//    }
//}
