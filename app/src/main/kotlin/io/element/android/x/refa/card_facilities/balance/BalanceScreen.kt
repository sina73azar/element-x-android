package com.drp.card_facilities.presentation.balance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.drp.card_facilities.presentation.balance.inquiry.BalanceInquiryBottomSheet
import com.drp.card_facilities.presentation.balance.result.BalanceResultDialog
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CardNumberShotWidget
import com.drp.shared_ui.widget.Combo
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PasswordEditText
import com.drp.shared_ui.widget.SearchSheet
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BalanceScreen(
    modifier: Modifier = Modifier,
    viewModel: BalanceScreenViewModel? = null,
    navController: NavHostController = rememberNavController(),
    selectedCard: CardShotItemInfo? = null,
) {

    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: BalanceScreenState()

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

    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }
    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }

    /**
     * Expire date fields
     * */

    val months =
        stringArrayResource(id = R.array.month).toList()
    val years =
        stringArrayResource(id = R.array.years).toList()
    /*var editButtonEnabled by remember {
        mutableStateOf(false)
    }*/

    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.balanceInquiryState.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(BalanceScreenEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(
                closeAction = {
                    viewModel?.sendEvent(BalanceScreenEvents.DismissFailureDialog)
                })
        }
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
                headerTxt = stringResource(id = R.string.card_balance_st),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        Column(modifier = modifier.padding(top = paddingValues.calculateTopPadding())) {
            CardNumberShotWidget(
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                selectedCard = uiState.sourceCardUiState.selectedCard,
                cardsList = uiState.sharedViewModelUiState.cardsList,
                isJustRefahSourceCardEnabled = true,
                onSelectedCardChange = {
                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetSelectedCard(it))
                    it?.panExpiryMonth?.let { expireMonth ->
                        it.panExpiryYear?.let { expireYear ->
                            years.filter { it.contains(expireYear) }.first().let { fullExpireYear ->
                                viewModel?.sendSourceCardEvent(SourceCardEvents.SetMonth(expireMonth))
                                viewModel?.sendSourceCardEvent(
                                    SourceCardEvents.SetYear(
                                        fullExpireYear
                                    )
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
                    viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.SetToDefaultCard(it.id))
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
                        .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = dimensionResource(id = R.dimen.small_elevation)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                PasswordEditText(
                    modifier = Modifier
                            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                            .padding(top = dimensionResource(id = R.dimen.small_padding)),
                    text = uiState.sourceCardUiState.cvv2,
                    label = stringResource(id = R.string.transfer_card_cvv2),
                    maxLength = 4,
                    keyboardType = KeyboardType.NumberPassword,
                    placeholder = stringResource(id = R.string.transfer_card_cvv2),
                    onTextChange = {
                        viewModel?.sendSourceCardEvent(SourceCardEvents.SetCvv2(cvv2 = it))
                    },
                    errorMessage = uiState.sourceCardUiState.cvv2ValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendSourceCardEvent(SourceCardEvents.DumpCvv2ValidationMessage)
                    }
                )
                /*CardExpirationDateWidget(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                    monthValue = uiState?.value?.month ?: "",
                    yearValue = uiState?.value?.year ?: "",
                    onMonthChange = {
                        viewModel?.sendEvent(BalanceScreenEvents.SetMonth(it))
                    },
                    onYearChange = {
                        viewModel?.sendEvent(BalanceScreenEvents.SetYear(it))
                    },
                    monthError = uiState?.value?.monthError == true,
                    yearError = uiState?.value?.yearError == true,
                    dismissMonthError = { viewModel?.sendEvent(BalanceScreenEvents.DumpMonthError) },
                    dismissYearError = { viewModel?.sendEvent(BalanceScreenEvents.DumpYearError) },
                    editButtonEnable = editButtonEnabled,
                    onDismissEditButtonEnable = { editButtonEnabled = false }
                )*/
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
                LoadingButton(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                    top = 24.dp,
                                    bottom = dimensionResource(id = R.dimen.large_padding)
                            ),
                    loading = false,
                    paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
                    onClick = {
                        keyboardController?.hide()
                        viewModel?.sendEvent(BalanceScreenEvents.ValidateScreenParameters)
                    },
                    btnLabel = stringResource(id = R.string.submit_continue)
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
                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetMonth(itemModel.value))
                    if (itemModel.value.isNotEmpty())
                        viewModel?.sendSourceCardEvent(SourceCardEvents.DumpMonthError)
                    monthSelectionSheet = false
                })
            SearchSheet(sheetVisible = yearSelectionSheet,
                onDismiss = { yearSelectionSheet = false },
                items = years.map { yearStr ->
                    SearchSheetItemModel(value = yearStr)
                },
                onItemClick = { itemModel ->
                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetYear(itemModel.value))
                    if (itemModel.value.isNotEmpty())
                        viewModel?.sendSourceCardEvent(SourceCardEvents.DumpYearError)
                    yearSelectionSheet = false
                })

            /** get otp bottom sheet */
            BalanceInquiryBottomSheet(
                visibility = uiState.getOtpBottomSheetState,
                viewModel = viewModel,
                snackBarHost = {
                    SnackBarCompose(
                        snackbarHostState = snackBarHostState,
                        snackBarType = currentSnackType
                    )
                },
            ) {
                viewModel?.sendEvent(BalanceScreenEvents.DismissOtpBottomSheet)
                viewModel?.sendEvent(BalanceScreenEvents.BackToDefault)
            }

            uiState.balanceInquiryState.let { inquiryState ->
                if (inquiryState.isSuccess())
                    AnimatedVisibility(
                        visible = inquiryState.isSuccess()
                    ) {
                        BasicAlertDialog(onDismissRequest = {
                            navController.popBackStack()
                        }) {
                            BalanceResultDialog(
//                                balance = uiState.balanceInquiryState.getSuccessData().balance.depositableAmount,
                                cardBalance = inquiryState.getSuccessData(),
                                closeAction = {
                                    navController.popBackStack()
                                })
                        }
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BalanceScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BalanceScreen()
        }
    }
}
