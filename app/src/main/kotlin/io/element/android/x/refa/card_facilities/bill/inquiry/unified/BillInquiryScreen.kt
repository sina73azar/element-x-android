package com.drp.card_facilities.presentation.bill.inquiry.unified

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.bill.transfer.unified.BillTransferBottomSheet
import com.drp.card_facilities.utility.ReceiptGenerator
import com.drp.data.enums.BillType
import com.drp.refah.card_facilities.widgets.compose.ServiceItemRow
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.refah.ui.theme.Delightful_purple
import com.drp.refah.ui.theme.Tab_indicator_color
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.model.ServiceItemModel
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CardNumberShotWidget
import com.drp.shared_ui.widget.Combo
import com.drp.shared_ui.widget.CustomSingleSelectionToggle
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PasswordEditText
import com.drp.shared_ui.widget.SearchSheet
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import io.element.android.x.refa.enums.BillTabs
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BillInquiryScreen(
    modifier: Modifier = Modifier,
    viewModel: BillViewModel? = null,
    navController: NavHostController = rememberNavController(),
    selectedCard: CardShotItemInfo? = null
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: BillInquiryScreenState()

    LaunchedEffect(key1 = Unit) {
        if (selectedCard != null)
            viewModel?.sendSourceCardEvent(SourceCardEvents.SetSelectedCard(selectedCard))
    }

    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }
    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }

    val cardOrWalletToggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(
            id = 0,
            stringResource(id = R.string.pay_with_wallet_st)
        ),
        CustomToggleModel(
            1,
            stringResource(id = R.string.pay_with_card_st)
        )
    )

    /**
     * Expire date fields
     * */

    val months =
        stringArrayResource(id = R.array.month).toList()
    val years =
        stringArrayResource(id = R.array.years).toList()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val selectedTab = uiState.selectedTab
    val billServices =
        mapOf(
            ServiceItemModel(
                id = 1,
                startIcon = R.drawable.mokhaberat,
                title = stringResource(
                    id = R.string.bill_phone
                ),
            ) to {
                navController.navigate(route = Screens.SeparatedBillInquiryScreen.route + "/${BillType.FIXEDLINE.name}")
            },
            ServiceItemModel(
                id = 2,
                startIcon = R.drawable.mobile_phone_ic,
                title = stringResource(
                    id = R.string.bill_mobile
                )
            ) to {
                navController.navigate(route = Screens.SeparatedBillInquiryScreen.route + "/${BillType.MOBILE.name}")
            },
            ServiceItemModel(
                id = 3,
                startIcon = R.drawable.ab,
                title = stringResource(
                    id = R.string.bill_water
                )
            ) to {
                navController.navigate(route = Screens.SeparatedBillInquiryScreen.route + "/${BillType.WATER.name}")
            },
            ServiceItemModel(
                id = 4,
                startIcon = R.drawable.gaz,
                title = stringResource(
                    id = R.string.bill_gas
                )
            ) to {
                navController.navigate(route = Screens.SeparatedBillInquiryScreen.route + "/${BillType.GAS.name}")
            },
            ServiceItemModel(
                id = 5,
                startIcon = R.drawable.bargh,
                title = stringResource(
                    id = R.string.bill_elec
                )
            ) to {
                navController.navigate(route = Screens.SeparatedBillInquiryScreen.route + "/${BillType.ELECTRICITY.name}")
            }
        )

    /** handling error messages */

    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(visible = uiState.billInquiryState.isFail() || uiState.billPaymentState.isFail()) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(BillEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(
                closeAction = {
                    viewModel?.sendEvent(BillEvents.DismissFailureDialog)
                })
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel?.sendEvent(BillEvents.ChangeCardOrWalletToggle(cardOrWalletToggleModel[0]))
    }
    /** screen region */
    Scaffold(
        snackbarHost = {
            SnackBarCompose(
                snackbarHostState = snackBarHostState,
                snackBarType = currentSnackType
            )
        },
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.bill_payment_st),
                onBackClick = { navController.popBackStack() }
            )
        }) { paddingValues ->

        val tabTitles = listOf(
            stringResource(id = R.string.bill_by_inquiry_title),
            stringResource(id = R.string.bill_by_followup_title)
        )
        Column(modifier = modifier.padding(top = paddingValues.calculateTopPadding())) {
            TabRow(
                modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.medium_padding))
                        .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                        .clip(
                                RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                        ),
                containerColor = Delightful_purple,
                selectedTabIndex = selectedTab.ordinal, indicator = { tabPositions ->
                val selectedTabPosition = tabPositions[selectedTab.ordinal]
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTabPosition),
                    color = Tab_indicator_color
                )
            }) {
                tabTitles.forEachIndexed { index, title ->
                    val curTabType = BillTabs.entries.toTypedArray()[index]
                    Tab(
                        selected = selectedTab.ordinal == index,
                        onClick = { viewModel?.sendEvent(BillEvents.ChangeTab(curTabType)) },
                        text = {
                            TabContent(
                                title, selectedTab.ordinal == index
                            )
                        },
                        modifier = Modifier.heightIn(min = dimensionResource(id = R.dimen.button_height)),
                    )
                }
            }
            when (selectedTab) {
                BillTabs.UNIFIED -> {
                    CustomSingleSelectionToggle(
                        modifier = Modifier
                                .padding(
                                        horizontal = dimensionResource(id = R.dimen.medium_padding)
                                )
                                .padding(top = dimensionResource(id = R.dimen.large_padding)),
                        data = cardOrWalletToggleModel
                    ) { toggleModel ->
                        viewModel?.sendEvent(BillEvents.ChangeCardOrWalletToggle(toggleModel))
                    }

                    if (uiState.cardOrWalletToggle?.id == 1)
                        CardNumberShotWidget(
//                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                            selectedCard = uiState.sourceCardUiState.selectedCard,
                            cardsList = uiState.sharedViewModelUiState.cardsList,
                            onSelectedCardChange = {
                                viewModel?.sendSourceCardEvent(SourceCardEvents.SetSelectedCard(it))
                                it?.panExpiryMonth?.let { expireMonth ->
                                    it.panExpiryYear?.let { expireYear ->
                                        years.filter { it.contains(expireYear) }.first()
                                            .let { fullExpireYear ->
                                                viewModel?.sendSourceCardEvent(
                                                    SourceCardEvents.SetMonth(expireMonth)
                                                )
                                                viewModel?.sendSourceCardEvent(
                                                    SourceCardEvents.SetYear(fullExpireYear)
                                                )
                                            }
                                    }
                                } ?: run {
                                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetMonth(""))
                                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetYear(""))
                                }
                            },
                            onCardRemoved = {
                                viewModel?.sendSharedViewModelEvent(
                                    SharedViewModelEvents.RemoveCard(
                                        it.id
                                    )
                                )
                            },
                            onCardSetToDefault = {
                                viewModel?.sendSharedViewModelEvent(
                                    SharedViewModelEvents.SetToDefaultCard(
                                        it.id
                                    )
                                )
                            },
                            onCardEdited = {
                                viewModel?.sendSharedViewModelEvent(
                                    SharedViewModelEvents.EditCard(
                                        it
                                    )
                                )
                            },
                            onCardAdd = {
                                viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.AddCard(it))
                            }
                        )
                    ElevatedCard(
                        modifier = modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = dimensionResource(id = R.dimen.medium_padding)),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        BankEditTextExposed(
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                            value = uiState.billId,
                            onValueChange = {
                                viewModel?.sendEvent(BillEvents.SetBillId(billId = it))
                                if (it.isEmpty())
                                    viewModel?.sendEvent(BillEvents.DismissBillIdSpinnerList)
                            },
                            contactList = uiState.billIdContactSheetList,
                            spinnerList = uiState.billIdSpinnerList,
                            onDismissSpinner = {
                                viewModel?.sendEvent(BillEvents.DismissBillIdSpinnerList)
                            },
                            onDropDownClick = {
                                viewModel?.sendEvent(BillEvents.SetBillId(it))
                            },
                            label = stringResource(id = R.string.bill_id_title),
                            placeHolder = stringResource(id = R.string.bill_id_title),
                            errorMessage = uiState.billIdValidationMessage.asString(),
                            dumpErrorMessage = { viewModel?.sendEvent(BillEvents.DumpBillIdValidationMessage) },
                            maxLength = 20
                        )
                        BankEditTextExposed(
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                            value = uiState.paymentId,
                            onValueChange = {
                                viewModel?.sendEvent(BillEvents.SetPaymentId(paymentId = it))
                            },
                            label = stringResource(id = R.string.pay_id_title),
                            placeHolder = stringResource(id = R.string.pay_id_title),
                            keyboardType = KeyboardType.Number,
                            errorMessage = uiState.paymentIdValidationMessage.asString(),
                            dumpErrorMessage = { viewModel?.sendEvent(BillEvents.DumpPaymentIdValidationMessage) },
                            maxLength = 20
                        )
                        if (uiState.cardOrWalletToggle?.id == 1) {
                            PasswordEditText(
                                modifier = Modifier
                                        .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                                        .padding(top = dimensionResource(id = R.dimen.small_padding)),
                                text = uiState.sourceCardUiState.cvv2,
                                label = stringResource(id = R.string.transfer_card_cvv2),
                                placeholder = stringResource(id = R.string.transfer_card_cvv2),
                                keyboardType = KeyboardType.Number,
                                maxLength = 4,
                                onTextChange = {
                                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetCvv2(it))
                                },
                                errorMessage = uiState.sourceCardUiState.cvv2ValidationMessage.asString(),
                                dumpErrorMessage = {
                                    viewModel?.sendSourceCardEvent(SourceCardEvents.DumpCvv2ValidationMessage)
                                }
                            )
                            Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding))
                                        .padding(top = dimensionResource(id = R.dimen.small_padding))
                            ) {
                                Combo(
                                    modifier = Modifier.weight(0.5f),
                                    mValue = uiState.sourceCardUiState.month,
                                    onClick = { monthSelectionSheet = true },
                                    onValueChange = {
                                        viewModel?.sendSourceCardEvent(SourceCardEvents.SetMonth(it))
                                    },
                                    isLoading = false,
                                    label = stringResource(id = R.string.month),
                                    placeHolder = stringResource(id = R.string.month),
                                    isError = uiState.sourceCardUiState.monthError,
                                    dismissError = { viewModel?.sendSourceCardEvent(SourceCardEvents.DumpMonthError) }
                                )
                                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.medium_padding)))
                                Combo(
                                    modifier = Modifier.weight(0.5f),
                                    mValue = uiState.sourceCardUiState.year,
                                    onClick = { yearSelectionSheet = true },
                                    onValueChange = {
                                        viewModel?.sendSourceCardEvent(SourceCardEvents.SetYear(it))
                                    },
                                    isLoading = false,
                                    label = stringResource(id = R.string.year),
                                    placeHolder = stringResource(id = R.string.year),
                                    isError = uiState.sourceCardUiState.yearError,
                                    dismissError = { viewModel?.sendSourceCardEvent(SourceCardEvents.DumpYearError) }
                                )
                            }
                        }
                        LoadingButton(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                            top = 24.dp,
                                            bottom = dimensionResource(id = R.dimen.large_padding)
                                    ),
                            loading = uiState.billInquiryState.isLoading(),
                            paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
                            onClick = {
                                keyboardController?.hide()
                                viewModel?.sendEvent(BillEvents.Inquiry)
                            },
                            btnLabel = stringResource(id = R.string.submit_continue)
                        )
                    }

                    /**
                     * bill transfer bottom sheet*/

                    uiState.billInquiryState.let { inquiryState ->
                        if (inquiryState.isSuccess())
                            BillTransferBottomSheet(
                                visibility = inquiryState.isSuccess(),
                                viewModel = viewModel,
                                receiptItems = ReceiptGenerator.billConfirmReceipt(
                                    result = inquiryState.getSuccessData(),
                                    billId = uiState.billId,
                                    paymentId = uiState.paymentId,
                                    cardNumber = if (uiState.cardOrWalletToggle?.id == 1) uiState.sourceCardUiState.selectedCard?.pan
                                        ?: "" else "",
                                    // TODO change wallet id
                                    walletId = if (uiState.cardOrWalletToggle?.id == 0) "14562" else "",
                                ),
                                snackBarHost = {
                                    SnackBarCompose(
                                        snackbarHostState = snackBarHostState,
                                        snackBarType = currentSnackType
                                    )
                                },
                                navigateToLanding = {
                                    navController.popBackStack()
                                },
                                dismiss = {
                                    viewModel?.sendEvent(BillEvents.DismissPaymentBottomSheet)
                                }
                            )

                    }

                    /**
                     * Expire date selection
                     * */
                    SearchSheet(sheetVisible = monthSelectionSheet,
                        onDismiss = { monthSelectionSheet = false },
                        items = months.map { monthStr ->
                            SearchSheetItemModel(value = monthStr)
                        },
                        onItemClick = { itemModel ->
                            viewModel?.sendSourceCardEvent(
                                SourceCardEvents.SetMonth(itemModel.value)
                            )
                            if (itemModel.value.isNotEmpty()) viewModel?.sendSourceCardEvent(
                                SourceCardEvents.DumpMonthError
                            )
                            monthSelectionSheet = false
                        })
                    SearchSheet(sheetVisible = yearSelectionSheet,
                        onDismiss = { yearSelectionSheet = false },
                        items = years.map { yearStr ->
                            SearchSheetItemModel(value = yearStr)
                        },
                        onItemClick = { itemModel ->
                            viewModel?.sendSourceCardEvent(SourceCardEvents.SetYear(itemModel.value))
                            if (itemModel.value.isNotEmpty()) viewModel?.sendSourceCardEvent(
                                SourceCardEvents.DumpYearError
                            )
                            yearSelectionSheet = false
                        })
                }

                BillTabs.SEPARATED -> {
                    LazyColumn(
                        modifier = Modifier
                                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                                .padding(
                                        top = dimensionResource(
                                                id = R.dimen.small_padding
                                        )
                                )
                                .fillMaxSize()
                    ) {
                        items(items = billServices.keys.toList()) {
                            ServiceItemRow(
                                modifier = Modifier.padding(
                                    top = dimensionResource(
                                        id = R.dimen.medium_padding
                                    )
                                ),
                                serviceItemModel = it.copy(
                                    endIcon = R.drawable.ic_arrow_left,
                                    endIconTint = MaterialTheme.colorScheme.onSurface
                                ),
                                onClick = billServices[it]!!
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabContent(
    title: String, isSelected: Boolean
) {
    Row(
        modifier = Modifier.alpha(
            if (!isSelected) 0.5f else 1f
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BillInquiryScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BillInquiryScreen()
        }
    }
}
