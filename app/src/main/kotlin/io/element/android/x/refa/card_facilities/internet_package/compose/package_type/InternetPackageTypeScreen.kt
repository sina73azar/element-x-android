package com.drp.card_facilities.presentation.internet_package.compose.package_type

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.presentation.app_source_card_handler.InternetPackageTypeScreenModel
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.app_wallet_handler.WalletEvents
import com.drp.card_facilities.presentation.internet_package.compose.payment.InternetPackagePaymentBottomSheet
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.refah.card_facilities.data.model.internet_package.inquiry.PackageFilterType
import com.drp.refah.card_facilities.presentation.internet_package.internetPackageReceipt
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.SnackBarCompose
import com.drp.utils.currencyFormatter
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternetPackageTypeScreen(
    modifier: Modifier = Modifier,
    viewModel: InternetPackageTypeViewModel? = null,
    navController: NavHostController,
    internetPackageTypeScreenModel: InternetPackageTypeScreenModel? = InternetPackageTypeScreenModel()
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: InternetPackageTypeScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        internetPackageTypeScreenModel?.let {
            viewModel?.sendEvent(InternetPackageTypeEvents.SetInternetPackageTypeScreenModel(it))
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.payment.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(InternetPackageTypeEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(closeAction = {
                viewModel?.sendEvent(InternetPackageTypeEvents.DismissFailureDialog)
            })
        }
    }

    Scaffold(snackbarHost = {
        SnackBarCompose(
            snackbarHostState = snackBarHostState, snackBarType = currentSnackType
        )
    }, topBar = {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.internet_package_title),
            onBackClick = {
                navController.popBackStack()
            })
    }) { paddingValues ->
        Column(
            modifier = modifier.padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(items = uiState.chips) {
                    Chip(
                        chip = it,
                        isSelected = uiState.selectedChip == it,
                        onSelectedChipChanged = {
                            viewModel?.sendEvent(InternetPackageTypeEvents.SetSelectedChip(it))
                        }
                    )
                }
            }
            LazyColumn {
                items(items = uiState.selectedChip.packages) { packageItem ->
                    PackageItemCard(packageItem = packageItem) {
                        viewModel?.sendEvent(InternetPackageTypeEvents.SetSelectedPackageItem(it))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.medium_padding)))
                }
            }
        }

        InternetPackagePaymentBottomSheet(
            visibility = uiState.selectedPackageItem != null,
            viewModel = viewModel,
            receiptItems = internetPackageReceipt(
                packageItem = uiState.selectedPackageItem,
                cardNumber = if (uiState.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 1) uiState.internetPackageTypeScreenModel.sourceCardUiState.selectedCard?.pan
                    ?: "" else "",
                walletId = if (uiState.internetPackageTypeScreenModel.cardOrWalletToggle?.id == 0) uiState.walletUiState.shahkarUserData.walletId.toString() else "",
                phoneNumber = uiState.internetPackageTypeScreenModel.mobileNumber,
                operator = uiState.internetPackageTypeScreenModel.selectedMobileOperatorTab,
            ),
            snackBarHost = {
                SnackBarCompose(
                    snackbarHostState = snackBarHostState,
                    snackBarType = currentSnackType
                )
            },
            navigateToLanding = {
                navController.popBackStack()
                navController.popBackStack()
            }
        ) {
            viewModel?.sendEvent(InternetPackageTypeEvents.SetSelectedPackageItem(null))
            viewModel?.sendSourceCardEvent(SourceCardEvents.BackOtpCodeToDefault)
            viewModel?.sendWalletEvent(WalletEvents.BackOtpCodeToDefault)
        }
    }
}

@Composable
fun PackageItemCard(
    modifier: Modifier = Modifier,
    packageItem: PackageItem,
    onPackageItemClicked: (PackageItem) -> Unit
) {
    ElevatedCard(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
        .padding(top = dimensionResource(id = R.dimen.medium_padding)),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ), onClick = { onPackageItemClicked(packageItem) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(start = dimensionResource(id = R.dimen.medium_padding))
                    .weight(1f),
                text = packageItem.title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = dimensionResource(id = R.dimen.small_padding)),
                text = currencyFormatter(packageItem.price),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                modifier = Modifier
                    .padding(end = dimensionResource(id = R.dimen.medium_padding))
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                    )
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.medium_padding),
                        vertical = 2.dp
                    )
                    .wrapContentWidth(),
                text = stringResource(id = R.string.irr_currency),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun Chip(
    isSelected: Boolean = false,
    chip: PackageFilterType,
    onSelectedChipChanged: (PackageFilterType) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(vertical = dimensionResource(id = R.dimen.small_padding))
            .padding(start = dimensionResource(id = R.dimen.small_padding)),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.extra_large_corner)),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectedChipChanged(chip)
                }
            )
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .padding(dimensionResource(id = R.dimen.small_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            /*Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                painter = painterResource(id = chip.icon ?: R.drawable.ic_day),
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
            )*/
            Text(
                text = chip.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding))
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
private fun PackageItemCardPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            PackageItemCard(
                packageItem = PackageItem(
                    duration = "30",
                    durationUnit = "DAILY",
                    amount = 282000L,
                    amountWithTax = 310200L,
                    simType = 2,
                    operatorTitle = "بسته ۱۰ روزه اینترنت همراه ایرانسل "
                )
            ) {

            }
        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun ChipPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Chip(
                isSelected = true,
                chip = PackageFilterType(
                    title = "روزانه",
                    packages = arrayListOf(),
//                    icon = R.drawable.ic_day
                )
            ) {

            }
        }
    }
}

@Preview
@Composable
private fun InternetPackageTypeScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            InternetPackageTypeScreen(navController = rememberNavController())
        }
    }
}
