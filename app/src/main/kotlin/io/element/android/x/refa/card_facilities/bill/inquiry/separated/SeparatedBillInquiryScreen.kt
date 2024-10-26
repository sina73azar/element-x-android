package com.drp.card_facilities.presentation.bill.inquiry.separated

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.drp.card_facilities.presentation.bill.transfer.separated.SeparatedPhoneBillTransferBottomSheet
import com.drp.card_facilities.presentation.bill.transfer.separated.SeparatedUtilityBillTransferBottomSheet
import com.drp.card_facilities.presentation.phone_contact.SuperAppPhoneContactActivity
import com.drp.data.enums.BillType
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.refah.ui.theme.Delightful_purple
import com.drp.refah.ui.theme.Irancell_Yellow
import com.drp.refah.ui.theme.Rightel_Purple
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CardNumberShotWidget
import com.drp.shared_ui.widget.Combo
import com.drp.shared_ui.widget.CustomSingleSelectionToggle
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PasswordEditText
import com.drp.shared_ui.widget.PhoneAutoEditText
import com.drp.shared_ui.widget.SearchSheet
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SeparatedBillInquiryScreen(
    modifier: Modifier = Modifier,
    billType: BillType = BillType.WATER,
    viewModel: SeparatedBillViewModel? = null,
    navController: NavHostController = rememberNavController(),
    billId: String? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: SeparatedBillInquiryScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val selectedTab = uiState.selectedMobileOperatorTab

    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }
    var pageTitle by remember {
        mutableStateOf<String>("")
    }
    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }

    val cardOrWalletToggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(0, stringResource(id = R.string.pay_with_wallet_st)),
        CustomToggleModel(1, stringResource(id = R.string.pay_with_card_st))
    )

    /**
     * Expire date fields
     * */

    val months =
        stringArrayResource(id = R.array.month).toList()
    val years =
        stringArrayResource(id = R.array.years).toList()

    LaunchedEffect(key1 = Unit) {
        if (billId != null) {
            when (billType) {
                BillType.WATER, BillType.GAS, BillType.ELECTRICITY -> {
                    viewModel?.sendEvent(
                        SeparatedBillEvents.SetBillId(billId, billType)
                    )
                    viewModel?.sendEvent(SeparatedBillEvents.DismissWaterBillIdSpinner)
                    viewModel?.sendEvent(SeparatedBillEvents.DismissGasBillIdSpinner)
                    viewModel?.sendEvent(SeparatedBillEvents.DismissElectricityBillIdSpinner)
                }

                BillType.FIXEDLINE -> {
                    viewModel?.sendEvent(
                        SeparatedBillEvents.SetFixedPhoneNumber(billId)
                    )
                    viewModel?.sendEvent(SeparatedBillEvents.DismissFixedPhoneSpinner)
                }

                else -> {

                }
            }
        }
    }

    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = /*uiState.separatedPhoneBillInquiryState.isFail()
                ||*/ uiState.separatedUtilityBillInquiryState.isFail()
                || uiState.billPaymentState.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(SeparatedBillEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(
                closeAction = {
                    viewModel?.sendEvent(SeparatedBillEvents.DismissFailureDialog)
                })
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel?.sendEvent(SeparatedBillEvents.ChangeCardOrWalletToggle(cardOrWalletToggleModel[0]))
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
                headerTxt = pageTitle,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        Column(modifier = modifier.padding(top = paddingValues.calculateTopPadding())) {

            CustomSingleSelectionToggle(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.medium_padding)
                    )
                    .padding(top = dimensionResource(id = R.dimen.large_padding)),
                data = cardOrWalletToggleModel
            ) { toggleModel ->
                viewModel?.sendEvent(SeparatedBillEvents.ChangeCardOrWalletToggle(toggleModel))
            }
            if (uiState.cardOrWalletToggle?.id == 1)
                CardNumberShotWidget(
//                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
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
                        viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.RemoveCard(it.id))
                    },
                    onCardSetToDefault = {
                        viewModel?.sendSharedViewModelEvent(
                            SharedViewModelEvents.SetToDefaultCard(
                                it.id
                            )
                        )
                    },
                    onCardEdited = {
                        viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.EditCard(it))
                    },
                    onCardAdd = {
                        viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.AddCard(it))
                    }
                )
            ElevatedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(vertical = dimensionResource(id = R.dimen.medium_padding)),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = dimensionResource(id = R.dimen.small_elevation)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                when (billType) {
                    BillType.FIXEDLINE -> {
                        BankEditTextExposed(
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                            value = uiState.fixedPhoneNumber,
                            onValueChange = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.SetFixedPhoneNumber(
                                        fixedPhoneNumber = it
                                    )
                                )
                                if (it.isEmpty())
                                    viewModel?.sendEvent(
                                        SeparatedBillEvents.DismissFixedPhoneSpinner
                                    )
                            },
                            contactList = uiState.fixedPhoneContactSheetList,
                            forceContactIconVisibilityToShowPhoneContactActivity = true,
                            phoneContactIntent = Intent(
                                context,
                                SuperAppPhoneContactActivity::class.java
                            ),
                            spinnerList = uiState.fixedPhoneContactSpinner,
                            onDismissSpinner = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.DismissFixedPhoneSpinner
                                )
                            },
                            onDropDownClick = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.SetFixedPhoneNumber(
                                        fixedPhoneNumber = it
                                    )
                                )
                            },
                            label = stringResource(id = R.string.telephone_with_city_code),
                            placeHolder = stringResource(id = R.string.telephone_with_city_code),
                            errorMessage = uiState.fixedPhoneNumberValidationMessage.asString(),
                            dumpErrorMessage = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.DumpFixedPhoneNumberValidationMessage
                                )
                            },
                            maxLength = 11
                        )
                        pageTitle = stringResource(id = R.string.bill_payment_st).plus(" ").plus(
                            stringResource(id = R.string.bill_phone)
                        )
                    }

                    BillType.MOBILE -> {
                        pageTitle = stringResource(id = R.string.bill_payment_st).plus(" ").plus(
                            stringResource(id = R.string.bill_mobile)
                        )
                        val tabTitles = listOf(
                            stringResource(id = R.string.top_up_mtn_title),
                            stringResource(id = R.string.top_up_mci_title),
                            stringResource(id = R.string.top_up_rightel_title)
                        )
                        TabRow(
                            modifier = Modifier
                                .padding(top = dimensionResource(id = R.dimen.large_padding))
                                .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                                .clip(
                                    RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                                ),
                            containerColor = Delightful_purple,
                            selectedTabIndex = selectedTab.ordinal, indicator = { tabPositions ->
                                val selectedTabPosition = tabPositions[selectedTab.ordinal]
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(selectedTabPosition),
                                    color = when (uiState.selectedMobileOperatorTab) {
                                        MobileOperatorTab.MTN -> Irancell_Yellow
                                        MobileOperatorTab.MCI -> MaterialTheme.colorScheme.primary
                                        MobileOperatorTab.RIGHTEL -> Rightel_Purple
                                    }
                                )
                            }) {
                            tabTitles.forEachIndexed { index, title ->
                                val curTabType = MobileOperatorTab.entries.toTypedArray()[index]
                                Tab(
                                    selected = selectedTab.ordinal == index,
                                    onClick = {
                                        viewModel?.sendEvent(
                                            SeparatedBillEvents.ChangeMobileOperatorTab(
                                                curTabType
                                            )
                                        )
                                    },
                                    text = {
                                        TabContent(
                                            title, selectedTab.ordinal == index
                                        )
                                    },
                                    modifier = Modifier.heightIn(min = dimensionResource(id = R.dimen.button_height)),
                                )
                            }
                        }

                        PhoneAutoEditText(
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                            value = uiState.mobileNumber,
                            onValueChange = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.SetMobileNumber(
                                        mobileNumber = it
                                    )
                                )
                                if (it.isEmpty())
                                    viewModel?.sendEvent(
                                        SeparatedBillEvents.DismissMobilePhoneSpinner
                                    )
                            },
                            contactSheetList = uiState.mobileContactSheetList,
                            spinnerListContact = uiState.mobilePhoneContactSpinner,
                            onDismissSpinner = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.DismissMobilePhoneSpinner
                                )
                            },
                            onSpinnerDropDownClick = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.SetMobileNumber(it)
                                )
                            },
                            phoneContactIntent = Intent(
                                context,
                                SuperAppPhoneContactActivity::class.java
                            ),
                            label = stringResource(id = R.string.mobile_no),
                            placeHolder = stringResource(id = R.string.mobile_no),
                            errorMessage = uiState.mobileNumberValidationMessage.asString(),
                            dumpErrorMessage = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.DumpMobilePhoneNumberValidationMessage
                                )
                            }
                        )
                    }

                    else -> {
                        BankEditTextExposed(
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                            value = uiState.billId,
                            onValueChange = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.SetBillId(billId = it, billType = billType)
                                )
                                if (it.isEmpty())
                                    when (billType) {
                                        BillType.WATER -> {
                                            viewModel?.sendEvent(
                                                SeparatedBillEvents.DismissWaterBillIdSpinner
                                            )
                                        }

                                        BillType.ELECTRICITY -> {
                                            viewModel?.sendEvent(
                                                SeparatedBillEvents.DismissElectricityBillIdSpinner
                                            )
                                        }

                                        BillType.GAS -> {
                                            viewModel?.sendEvent(
                                                SeparatedBillEvents.DismissGasBillIdSpinner
                                            )
                                        }

                                        else -> {
                                            // Do Nothing
                                        }
                                    }
                            },
                            contactList = when (billType) {
                                BillType.WATER -> {
                                    uiState.waterBillIdContactSheetList
                                }

                                BillType.ELECTRICITY -> {
                                    uiState.electricityBillIdContactSheetList
                                }

                                BillType.GAS -> {
                                    uiState.gasBillIdContactSheetList
                                }

                                else -> {
                                    emptyList()
                                }
                            },
                            spinnerList = when (billType) {
                                BillType.WATER -> {
                                    uiState.waterBillIdContactSpinner
                                }

                                BillType.ELECTRICITY -> {
                                    uiState.electricityBillIdContactSpinner
                                }

                                BillType.GAS -> {
                                    uiState.gasBillIdContactSpinner
                                }

                                else -> {
                                    emptyList()
                                }
                            },
                            onDismissSpinner = {
                                when (billType) {
                                    BillType.WATER -> {
                                        viewModel?.sendEvent(
                                            SeparatedBillEvents.DismissWaterBillIdSpinner
                                        )
                                    }

                                    BillType.ELECTRICITY -> {
                                        viewModel?.sendEvent(
                                            SeparatedBillEvents.DismissElectricityBillIdSpinner
                                        )
                                    }

                                    BillType.GAS -> {
                                        viewModel?.sendEvent(
                                            SeparatedBillEvents.DismissGasBillIdSpinner
                                        )
                                    }

                                    else -> {
                                        // Do Nothing
                                    }
                                }
                            },
                            onDropDownClick = {
                                viewModel?.sendEvent(SeparatedBillEvents.SetBillId(it, billType))
                            },
                            label = stringResource(id = R.string.bill_id_title),
                            placeHolder = stringResource(id = R.string.bill_id_title),
                            errorMessage = uiState.billIdValidationMessage.asString(),
                            dumpErrorMessage = {
                                viewModel?.sendEvent(
                                    SeparatedBillEvents.DumpBillIdValidationMessage,
                                )
                            },
                            maxLength = 20
                        )
                        pageTitle = stringResource(id = R.string.bill_payment_st).plus(" ").plus(
                            when (billType) {
                                BillType.WATER -> {
                                    stringResource(id = R.string.bill_water)
                                }

                                BillType.ELECTRICITY -> {
                                    stringResource(id = R.string.bill_elec)
                                }

                                BillType.GAS -> {
                                    stringResource(id = R.string.bill_gas)
                                }

                                else -> {
                                    stringResource(id = R.string.bill_water)
                                }

                            }
                        )

                    }
                }
                if (uiState.cardOrWalletToggle?.id == 1) {
                    PasswordEditText(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                            .padding(top = dimensionResource(id = R.dimen.small_padding)),
                        text = uiState.sourceCardUiState.cvv2,
                        label = stringResource(id = R.string.transfer_card_cvv2),
                        placeholder = stringResource(id = R.string.transfer_card_cvv2),
                        maxLength = 4,
                        keyboardType = KeyboardType.NumberPassword,
                        onTextChange = {
                            viewModel?.sendSourceCardEvent(SourceCardEvents.SetCvv2(cvv2 = it))
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
                            dismissError = {
                                viewModel?.sendSourceCardEvent(SourceCardEvents.DumpMonthError)
                            }
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
                            dismissError = {
                                viewModel?.sendSourceCardEvent(SourceCardEvents.DumpYearError)
                            }
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
                    loading = /*if (billType == BillType.FIXEDLINE || billType == BillType.MOBILE)
                        uiState.separatedPhoneBillInquiryState.isLoading()
                    else*/
                    uiState.separatedUtilityBillInquiryState.isLoading(),
                    paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
                    onClick = {
                        keyboardController?.hide()
                        viewModel?.sendEvent(
                            SeparatedBillEvents.Inquiry(billType = billType)
                        )
                    },
                    btnLabel = stringResource(id = R.string.submit_continue)
                )

                /** phone bill transfer bottom sheet */

                uiState.separatedUtilityBillInquiryState.let { inquiryState ->
                    if (inquiryState.isSuccess() && (billType == BillType.MOBILE || billType == BillType.FIXEDLINE))
                        SeparatedPhoneBillTransferBottomSheet(
                            visibility = inquiryState.isSuccess(),
                            viewModel = viewModel,
                            snackBarHost = {
                                SnackBarCompose(
                                    snackbarHostState = snackBarHostState,
                                    snackBarType = currentSnackType
                                )
                            },
                            billType = billType,
                            navigateToLanding = {
                                navController.popBackStack()
                            }
                        ) {
                            viewModel?.sendEvent(SeparatedBillEvents.DismissInquiry)
                        }
                }

                /** utility bill transfer bottom sheet */

                uiState.separatedUtilityBillInquiryState.let { inquiryState ->
                    if (inquiryState.isSuccess() && (billType == BillType.ELECTRICITY || billType == BillType.GAS || billType == BillType.WATER))
                        SeparatedUtilityBillTransferBottomSheet(
                            visibility = inquiryState.isSuccess(),
                            viewModel = viewModel,
                            snackBarHost = {
                                SnackBarCompose(
                                    snackbarHostState = snackBarHostState,
                                    snackBarType = currentSnackType
                                )
                            },
                            billType = billType,
                            navigateToLanding = {
                                navController.popBackStack()
                            }
                        ) {
                            viewModel?.sendEvent(SeparatedBillEvents.DismissInquiry)
                        }
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
        }
    }
}

@Composable
fun TabContent(
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
private fun SeparatedBillInquiryScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SeparatedBillInquiryScreen()
        }
    }
}
