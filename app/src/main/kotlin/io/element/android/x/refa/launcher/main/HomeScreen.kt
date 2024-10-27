/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package com.drp.refahland.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.drp.card_facilities.presentation.web_page.HomeItemWebUrlType
import com.drp.shared_ui.naviagtion.Screens
import com.drp.utils.currencyFormatter
import io.element.android.x.R

data class ItemData(
    val id: Int,
    val title: String,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)

data class BottomBarItemData(
    val id: Int,
    val title: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val onClick: () -> Unit = {}
)

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel? = null) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        if (uiState.balanceVisibility && !uiState.walletBalanceState.isSuccess())
            viewModel?.getWalletBalance()
    }
    val contentItems = listOf(
        ItemData(
            id = 1,
            title = stringResource(id = R.string.card_to_card_st),
            icon = R.drawable.ic_card_to_card2,
            onClick = {
                navController.navigate(Screens.CardToCardScreen.route)
            }
        ),
        ItemData(
            id = 2,
            title = stringResource(id = R.string.card_balance_st),
            icon = R.drawable.ic_balance,
            onClick = {
                navController.navigate(Screens.BalanceScreen.route)
            }
        ),
        ItemData(
            id = 3,
            title = stringResource(id = R.string.card_statement_st),
            icon = R.drawable.ic_last_transactions,
            onClick = {
                navController.navigate(Screens.LastTenStatementScreen.route)

            }
        ),
        ItemData(
            id = 4,
            title = stringResource(id = R.string.bill_payment_st),
            icon = R.drawable.ic_bill,
            onClick = {
                navController.navigate(Screens.BillInquiryScreen.route)
            }
        ),
        ItemData(
            id = 5,
            title = stringResource(id = R.string.charge_payment_st),
            icon = R.drawable.ic_top_up,
            onClick = {
                navController.navigate(Screens.TopUpScreen.route)
            }
        ),
        ItemData(
            id = 6,
            title = stringResource(id = R.string.insurance_bill_refah_land_title),
            icon = R.drawable.ic_insurance,
            onClick = {
                navController.navigate(Screens.InsuranceInquiryScreen.route)

            }
        ),
        ItemData(
            id = 7,
            title = stringResource(id = R.string.internet_package_st),
            icon = R.drawable.ic_package_payment,
            onClick = {
                navController.navigate(Screens.InternetPackageInquiryScreen.route)
            }
        ),
        ItemData(
            id = 8,
            title = stringResource(id = R.string.sayadi_cheque),
            icon = R.drawable.ic_cheque,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.SAYADI.name}")
            }
        ),
        ItemData(
            id = 9,
            title = stringResource(id = R.string.gardeshgari_st),
            icon = R.drawable.ic_tourism,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.GARDESHGARI.name}")
            }
        ),
        ItemData(
            id = 10,
            title = stringResource(id = R.string.kargozari_bours_st),
            icon = R.drawable.ic_broker,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.KAROZARI_BOURS.name}")
            }
        ),
        ItemData(
            id = 11,
            title = stringResource(id = R.string.sandogh_negin_st),
            icon = R.drawable.ic_box,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.SANDOGH_BIMEH.name}")
            }
        ),
        ItemData(
            id = 12,
            title = stringResource(id = R.string.bimeh_refah_st),
            icon = R.drawable.ic_bimeh,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.BIMEH_REFAH.name}")
            }
        ),
        ItemData(
            id = 13,
            title = stringResource(id = R.string.proxy_account_st),
            icon = R.drawable.ic_proxy_account,
            onClick = {
                navController.navigate(Screens.WebPageScreen.route + "/${HomeItemWebUrlType.PROXY_ACCOUNT.name}")
            }
        ),
        ItemData(
            id = 14,
            title = stringResource(id = R.string.licence_negative_score_st),
            icon = R.drawable.ic_driver_license,
            onClick = {
                navController.navigate(Screens.LicenceNegativeScoreInquiryScreen.route)
            }
        ),
        ItemData(
            id = 15,
            title = stringResource(id = R.string.tracking_title_st),
            icon = R.drawable.ic_tracking_post,
            onClick = {
                navController.navigate(Screens.TrackingPostScreen.route)
            }
        ),
        ItemData(
            id = 16,
            title = stringResource(id = R.string.iban_convertor_title_st),
            icon = R.drawable.ic_iban_convertor,
            onClick = {
                navController.navigate(Screens.IbanConvertorScreen.route)
            }
        ),
        ItemData(
            id = 17,
            title = stringResource(id = R.string.vehicle_violation_title_st),
            icon = R.drawable.ic_vehicle_violation,
            onClick = {
                navController.navigate(Screens.VehicleViolationScreen.route)
            }
        ),
        ItemData(
            id = 18,
            title = stringResource(id = R.string.motor_violation_title_st),
            icon = R.drawable.ic_motor,
            onClick = {
                navController.navigate(Screens.MotorViolationScreen.route)
            }
        ),
    )

    val headerItems = listOf(
        ItemData(
            id = 1,
            title = stringResource(id = R.string.increase_st),
            icon = R.drawable.ic_add_wallet,
            onClick = {
                navController.navigate(Screens.WalletAddScreen.route)
            }
        ),
        ItemData(
            id = 2,
            title = stringResource(id = R.string.transfer_st),
            icon = R.drawable.ic_transfer_wallet,
            onClick = {
                navController.navigate(Screens.WalletToWalletScreen.route)
            }
        ),
        ItemData(
            id = 3,
            title = stringResource(id = R.string.decrease_st),
            icon = R.drawable.ic_minus_wallet,
            onClick = {
                navController.navigate(Screens.WalletMinusScreen.route)
            }
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFac1f81),
                        Color(0xFF563492),
                        Color(0xFF563492),
                        Color(0xFF283f9c)
                    )
                )
            )
    ) {
        RefahLandHeader(
            viewModel = viewModel,
            headerItems = headerItems,
            balanceVisibility = uiState.balanceVisibility,
            balanceVisibilityChange = { visibility ->
                viewModel?.changeWalletBalanceVisibility(visibility)
                if (visibility)
                    viewModel?.getWalletBalance()
            },
            changeWalletBalanceVisibility = {
                viewModel?.changeWalletBalanceVisibility(it)
            },
            navigateToHistory = {
                navController.navigate(Screens.HistoryScreen.route)
            })
        RefahLandContent(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.medium_padding)),
            contentItems = contentItems,
            changeWalletBalanceVisibility = {
                viewModel?.changeWalletBalanceVisibility(it)
            }
        )
    }
}

@Composable
fun RefahLandContent(
    modifier: Modifier = Modifier,
    contentItems: List<ItemData>,
    changeWalletBalanceVisibility: (Boolean) -> Unit
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(all = dimensionResource(id = R.dimen.large_padding))
        ) {
            items(items = contentItems) { contentItem ->
                ContentItem(
                    contentItem = contentItem,
                    changeWalletBalanceVisibility = changeWalletBalanceVisibility
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefahLandHeader(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel?,
    headerItems: List<ItemData>,
    balanceVisibility: Boolean,
    balanceVisibilityChange: (Boolean) -> Unit,
    changeWalletBalanceVisibility: (Boolean) -> Unit,
    navigateToHistory: () -> Unit
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            .padding(top = dimensionResource(id = R.dimen.medium_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier
                .wrapContentWidth()
                .size(56.dp),
            onClick = {
                viewModel?.changeWalletQrCodeVisibility(true)
            }) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_qr_code),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(
            modifier = Modifier
                .wrapContentWidth()
                .size(36.dp),
            onClick = {
                navigateToHistory()
            }) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_history_outlined),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        if (uiState.walletBalanceState.isLoading())
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        if (uiState.walletBalanceState.isFail() || uiState.walletBalanceState.isError())
            Box(modifier = Modifier.weight(1f))
        if (uiState.walletBalanceState.isSuccess())
            Text(
                modifier = Modifier.weight(1f),
                text = if (balanceVisibility) currencyFormatter(uiState.walletBalanceState.getSuccessData().result.pTotalBalance).plus(
                    " "
                )
                    .plus(stringResource(id = R.string.irr_currency)) else "********",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.End),
                color = MaterialTheme.colorScheme.onPrimary
            )
        IconButton(
            modifier = Modifier.size(56.dp),
            onClick = { balanceVisibilityChange(!balanceVisibility) }) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = if (balanceVisibility) R.drawable.ic_eye_open else R.drawable.ic_eye_close),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .padding(vertical = dimensionResource(id = R.dimen.medium_padding)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        headerItems.forEach { headerItem ->
            HeaderItem(
                weight = 1f,
                headerItem = headerItem,
                changeWalletBalanceVisibility = changeWalletBalanceVisibility
            )
        }
    }
    AnimatedVisibility(
        visible = uiState.walletQrCodeVisibility
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.changeWalletQrCodeVisibility(false)
        }) {
            QrCodeDialog(
                walletId = uiState.walletId,
                closeAction = {
                    viewModel?.changeWalletQrCodeVisibility(false)
                })
        }
    }
}

@Composable
fun RowScope.HeaderItem(
    modifier: Modifier = Modifier,
    weight: Float,
    headerItem: ItemData,
    changeWalletBalanceVisibility: (Boolean) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .weight(weight)
            .aspectRatio(1f),
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.large_corner)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(
                alpha = 0.15f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    changeWalletBalanceVisibility(false)
                    headerItem.onClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(id = headerItem.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
                    .padding(top = dimensionResource(id = R.dimen.small_padding)),
                text = headerItem.title,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ContentItem(
    modifier: Modifier = Modifier,
    contentItem: ItemData,
    changeWalletBalanceVisibility: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .fillMaxSize()
            .aspectRatio(1f),
        shape = RoundedCornerShape(percent = 20),
        elevation =
        CardDefaults.elevatedCardElevation(defaultElevation = dimensionResource(id = R.dimen.small_elevation)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    changeWalletBalanceVisibility(false)
                    contentItem.onClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(27.dp),
                painter = painterResource(id = contentItem.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
                    .padding(top = dimensionResource(id = R.dimen.small_padding)),
                text = contentItem.title,
                style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
