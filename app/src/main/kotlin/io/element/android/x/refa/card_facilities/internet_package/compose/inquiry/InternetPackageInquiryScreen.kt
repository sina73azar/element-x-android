package com.drp.card_facilities.presentation.internet_package.compose.inquiry

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.drp.card_facilities.presentation.bill.inquiry.separated.TabContent
import com.drp.card_facilities.presentation.phone_contact.SuperAppPhoneContactActivity
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.refah.ui.theme.Delightful_purple
import com.drp.refah.ui.theme.Irancell_Yellow
import com.drp.refah.ui.theme.Rightel_Purple
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun InternetPackageInquiryScreen(
    modifier: Modifier = Modifier,
    viewModel: InternetPackageInquiryViewModel? = null,
    navController: NavHostController = rememberNavController(),
    selectedCard: CardShotItemInfo? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: InternetPackageInquiryScreenState()

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
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val selectedTab = uiState.selectedMobileOperatorTab

    val cardOrWalletToggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(0, stringResource(id = R.string.pay_with_wallet_st)),
        CustomToggleModel(1, stringResource(id = R.string.pay_with_card_st))
    )

    val toggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(
            0, stringResource(id = R.string.prepaid_simcard_st)
        ), CustomToggleModel(
            1, stringResource(id = R.string.postpaid_simcard_st)
        )
    )

    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }

    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }

    /**
     * Expire date fields
     * */

    val months = stringArrayResource(id = R.array.month).toList()
    val years = stringArrayResource(id = R.array.years).toList()

    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.inquiry.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(InternetPackageInquiryEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(closeAction = {
                viewModel?.sendEvent(InternetPackageInquiryEvents.DismissFailureDialog)
            })
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel?.sendEvent(
            InternetPackageInquiryEvents.ChangeCardOrWalletToggle(
                uiState.cardOrWalletToggle ?: cardOrWalletToggleModel[0]
            )
        )
    }

    /** screen region */
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
            CustomSingleSelectionToggle(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.medium_padding)
                    )
                    .padding(top = dimensionResource(id = R.dimen.large_padding)),
                data = cardOrWalletToggleModel
            ) { toggleModel ->
                viewModel?.sendEvent(
                    InternetPackageInquiryEvents.ChangeCardOrWalletToggle(
                        toggleModel
                    )
                )
            }
            if (uiState.cardOrWalletToggle?.id == 1)
                CardNumberShotWidget(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                    isJustRefahSourceCardEnabled = false,
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
                val tabTitles = listOf(
                    stringResource(id = R.string.top_up_mtn_title),
                    stringResource(id = R.string.top_up_mci_title),
                    stringResource(id = R.string.top_up_rightel_title)
                )
                TabRow(modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.large_padding))
                    .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                    .clip(
                        RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                    ),
                    containerColor = Delightful_purple,
                    selectedTabIndex = selectedTab.ordinal,
                    indicator = { tabPositions ->
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
                                    InternetPackageInquiryEvents.ChangeMobileOperatorTab(
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

                CustomSingleSelectionToggle(
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(id = R.dimen.extra_large_padding)
                        )
                        .padding(top = dimensionResource(id = R.dimen.extra_large_padding)),
                    data = toggleModel
                ) { toggleModel ->
                    viewModel?.sendEvent(
                        InternetPackageInquiryEvents.ChangeSelectedToggle(
                            toggleModel
                        )
                    )
                }

                PhoneAutoEditText(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                    value = uiState.mobileNumber,
                    onValueChange = {
                        viewModel?.sendEvent(
                            InternetPackageInquiryEvents.SetMobileNumber(
                                mobileNumber = it
                            )
                        )
                        if (it.isEmpty()) viewModel?.sendEvent(
                            InternetPackageInquiryEvents.DismissMobilePhoneSpinner
                        )
                    },
                    contactSheetList = uiState.mobileContactSheetList,
                    spinnerListContact = uiState.mobilePhoneContactSpinner,
                    onDismissSpinner = {
                        viewModel?.sendEvent(
                            InternetPackageInquiryEvents.DismissMobilePhoneSpinner
                        )
                    },
                    onSpinnerDropDownClick = {
                        viewModel?.sendEvent(
                            InternetPackageInquiryEvents.SetMobileNumber(it)
                        )
                    },
                    phoneContactIntent = Intent(
                        context, SuperAppPhoneContactActivity::class.java
                    ),
                    label = stringResource(id = R.string.mobile_no),
                    placeHolder = stringResource(id = R.string.mobile_no),
                    errorMessage = uiState.mobileNumberValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendEvent(
                            InternetPackageInquiryEvents.DumpMobilePhoneNumberValidationMessage
                        )
                    })

                if (uiState.cardOrWalletToggle?.id == 1) {
                    PasswordEditText(modifier = Modifier
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
                        })

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding))
                            .padding(top = dimensionResource(id = R.dimen.small_padding))
                    ) {
                        Combo(modifier = Modifier.weight(0.5f),
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
                            })
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.medium_padding)))
                        Combo(modifier = Modifier.weight(0.5f),
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
                            })
                    }
                }

                LoadingButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            bottom = dimensionResource(id = R.dimen.large_padding)
                        ),
                    loading = uiState.inquiry.isLoading(),
                    paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
                    onClick = {
                        keyboardController?.hide()
                        viewModel?.sendEvent(
                            InternetPackageInquiryEvents.Inquiry
                        )
                    },
                    btnLabel = stringResource(id = R.string.submit_continue)
                )

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

                /** top up inquiry bottom sheet */

                LaunchedEffect(key1 = uiState.inquiry) {
                    uiState.inquiry.let { inquiryState ->
                        if (inquiryState.isSuccess()) {
                            navController.navigate(
                                "${Screens.InternetPackageTypeScreen.route}?netPackTypeScreenModel=${
                                    Json.encodeToString(
                                        uiState.toInternetPackageTypeScreenModel()
                                    )
                                }"
                            )
                            viewModel?.sendEvent(InternetPackageInquiryEvents.DismissInquiry)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun InternetPackageInquiryScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            InternetPackageInquiryScreen()
        }
    }
}
