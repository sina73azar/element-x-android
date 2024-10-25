package com.drp.card_facilities.presentation.wallet_minus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.wallet_minus.wirhdraw.WithdrawFromWalletBottomSheet
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CardFacilityAmountEditText
import com.drp.shared_ui.widget.CustomSingleSelectionToggle
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PanEditText
import com.drp.shared_ui.widget.SnackBarCompose
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletMinusScreen(
    modifier: Modifier = Modifier,
    viewModel: WalletMinusViewModel? = null,
    navController: NavHostController = rememberNavController()
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: WalletMinusScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val toggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(
            0, stringResource(id = R.string.iban_toggle)
        ), CustomToggleModel(
            1, stringResource(id = R.string.card_toggle)
        )
    )
    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel?.sendEvent(WalletMinusScreenEvents.ChangeCardOrIbanToggle(toggleModel[0]))
    }
    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.transferState.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(WalletMinusScreenEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(closeAction = {
                viewModel?.sendEvent(WalletMinusScreenEvents.DismissFailureDialog)
            })
        }
    }

    /** screen region */
    Scaffold(snackbarHost = {
        SnackBarCompose(
            snackbarHostState = snackBarHostState, snackBarType = currentSnackType
        )
    }, topBar = {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.decrease_balance_st),
            onBackClick = {
                navController.popBackStack()
            })
    }) { paddingValues ->
        Column(
            modifier = modifier.padding(top = paddingValues.calculateTopPadding())
        ) {
            ElevatedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding))
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(vertical = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = dimensionResource(id = com.drp.shared_ui.R.dimen.small_elevation)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                CustomSingleSelectionToggle(
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)
                        )
                        .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                    data = toggleModel
                ) { toggleModel ->
                    viewModel?.sendEvent(WalletMinusScreenEvents.ChangeCardOrIbanToggle(toggleModel))
                }
                if (uiState.selectedToggle == toggleModel[0])
                    BankEditTextExposed(
                        modifier = Modifier.padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
                        value = uiState.iban,
                        onValueChange = { viewModel?.sendEvent(WalletMinusScreenEvents.SetIban(it)) },
                        maxLength = 24,
                        label = stringResource(id = R.string.destination_iban_st),
                        placeHolder = stringResource(id = R.string.destination_iban_without_ir_st),
                        keyboardType = KeyboardType.Number,
                        errorMessage = uiState.ibanValidationMessage.asString(),
                        dumpErrorMessage = { viewModel?.sendEvent(WalletMinusScreenEvents.DumpIbanValidationMessage) }
                    )
                else {
                    PanEditText(
                        modifier = Modifier
                            .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
                        value = uiState.destinationCardNumber,
                        onValueChange = {
                            viewModel?.sendEvent(
                                WalletMinusScreenEvents.SetDestinationCardNumber(
                                    it
                                )
                            )
                        },
                        contactSheetList = uiState.destinationCardContactSheetList,
                        spinnerListContact = uiState.destinationCardContactSpinner,
                        onDismissSpinner = {
                            viewModel?.sendEvent(WalletMinusScreenEvents.DismissDestinationCardNumberContactSpinner)
                        },
                        onSpinnerDropDownClick = {
                            viewModel?.sendEvent(
                                WalletMinusScreenEvents.SetDestinationCardNumber(
                                    it
                                )
                            )
                        },
                        keyboardType = KeyboardType.Text,
                        errorMessage = uiState.destinationCardNumberValidationMessage.asString(),
                        dumpErrorMessage = { viewModel?.sendEvent(WalletMinusScreenEvents.DumpDestinationCardNumberValidationMessage) }
                    )
                }
                CardFacilityAmountEditText(
                    modifier = Modifier.padding(
                        top = dimensionResource(
                            id = com.drp.shared_ui.R.dimen.small_padding
                        )
                    ),
                    value = uiState.amount,
                    onValueChange = {
                        viewModel?.sendEvent(WalletMinusScreenEvents.SetAmount(it))
                    },
                    label = stringResource(id = R.string.amount),
                    placeHolder = stringResource(id = R.string.amount),
                    errorMessage = uiState.amountValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendEvent(WalletMinusScreenEvents.DumpAmountValidationMessage)
                    })
                BankEditTextExposed(
                    modifier = Modifier.padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
                    value = uiState.description,
                    onValueChange = { viewModel?.sendEvent(WalletMinusScreenEvents.SetDescription(it)) },
                    maxLength = 50,
                    label = stringResource(id = R.string.wallet_add_description_st),
                    placeHolder = stringResource(id = R.string.wallet_add_description_st)
                )
                LoadingButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            bottom = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)
                        ),
                    loading = false,
                    paddingHorizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding),
                    onClick = {
                        keyboardController?.hide()
                        viewModel?.sendEvent(WalletMinusScreenEvents.ValidateFields)
                    },
                    btnLabel = stringResource(id = R.string.submit_continue)
                )
            }

            /** get otp bottom sheet */
            WithdrawFromWalletBottomSheet(
                visibility = uiState.getOtpBottomSheetState,
                viewModel = viewModel,
                receiptItems = walletMinusReceipt(
                    walletId = uiState.walletUiState.shahkarUserData.walletId?.toString() ?: "",
                    destinationIban = if (uiState.selectedToggle?.id == 0) uiState.iban else "",
                    destinationCardNumber = if (uiState.selectedToggle?.id == 1) uiState.destinationCardNumber.filter { it.isDigit() } else "",
                    amount = uiState.amount,
                    description = uiState.description
                ),
                navigateToLanding = {
                    navController.popBackStack()
                },
                snackBarHost = {
                    SnackBarCompose(
                        snackbarHostState = snackBarHostState,
                        snackBarType = currentSnackType
                    )
                },
            ) {
                viewModel?.sendEvent(WalletMinusScreenEvents.DismissOtpBottomSheet)
                viewModel?.sendEvent(WalletMinusScreenEvents.BackToDefault)
            }
        }
    }
}