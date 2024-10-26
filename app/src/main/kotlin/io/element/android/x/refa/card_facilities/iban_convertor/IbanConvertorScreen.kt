package com.drp.card_facilities.presentation.iban_convertor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
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
import com.drp.card_facilities.presentation.iban_convertor.result.IbanConvertorResultDialog
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.enums.BanksForConvertToIban
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.Combo
import com.drp.shared_ui.widget.CustomSingleSelectionToggle
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PanEditText
import com.drp.shared_ui.widget.SearchSheet
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IbanConvertorScreen(
    modifier: Modifier = Modifier,
    viewModel: IbanConvertorViewModel? = null,
    navController: NavHostController = rememberNavController()
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: IbanConvertorScreenState()
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
            0, stringResource(id = R.string.card_to_iban_st)
        ),
        CustomToggleModel(
            1, stringResource(id = R.string.account_to_iban_st)
        ),
        CustomToggleModel(
            2, stringResource(id = R.string.iban_to_account_st)
        )
    )

    var bankSelectionSheet by remember {
        mutableStateOf(false)
    }

    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel?.setToggle(toggleModel[0])
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.convertState.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.dismissFailureDialog()
        }) {
            NetworkErrorDialogContent(closeAction = {
                viewModel?.dismissFailureDialog()
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
            headerTxt = stringResource(id = R.string.iban_convertor_title_st),
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
                CustomSingleSelectionToggle(
                    modifier = Modifier
                            .padding(
                                    horizontal = dimensionResource(id = R.dimen.large_padding)
                            )
                            .padding(top = dimensionResource(id = R.dimen.large_padding)),
                    data = toggleModel
                ) { toggleModel ->
                    viewModel?.setToggle(toggleModel)
                }
                if (uiState.selectedToggle?.id == 0)
                    PanEditText(
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.small_padding)),
                        value = uiState.cardNumber,
                        label = stringResource(id = R.string.card_toggle),
                        placeHolder = stringResource(id = R.string.card_toggle),
                        onValueChange = {
                            viewModel?.setCardNumber(it)
                        },
                        contactSheetList = uiState.cardNumberContactSheetList,
                        spinnerListContact = uiState.cardNumberContactSpinner,
                        onDismissSpinner = {
                            viewModel?.dismissCardContactSpinner()
                        },
                        onSpinnerDropDownClick = {
                            viewModel?.setCardNumber(it)
                        },
                        keyboardType = KeyboardType.Text,
                        errorMessage = uiState.cardNumberValidationMessage.asString(),
                        dumpErrorMessage = { viewModel?.dismissCardNumberValidationMessage() }
                    )
                if (uiState.selectedToggle?.id == 1) {
                    BankEditTextExposed(
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                        value = uiState.accountNumber,
                        onValueChange = { viewModel?.setAccountNumber(it) },
                        maxLength = 24,
                        label = stringResource(id = R.string.conversion_account),
                        placeHolder = stringResource(id = R.string.conversion_account),
                        keyboardType = KeyboardType.Number,
                        errorMessage = uiState.accountNumberValidationMessage.asString(),
                        dumpErrorMessage = { viewModel?.dismissAccountNumberValidationMessage() }
                    )
                    Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding))
                                .padding(
                                        top = dimensionResource(id = R.dimen.small_padding),
                                        bottom = 16.dp
                                )
                    ) {
                        Combo(
                            modifier = Modifier.weight(1f),
                            mValue = uiState.bankName,
                            onClick = { bankSelectionSheet = true },
                            onValueChange = {
                                viewModel?.setBankName(it)
                            },
                            isLoading = false,
                            label = stringResource(id = R.string.bank_name_st),
                            placeHolder = stringResource(id = R.string.bank_name_st),
                            isError = uiState.bankNameError,
                            dismissError = { viewModel?.dismissBankNameError() }
                        )
                    }
                }
                if (uiState.selectedToggle?.id == 2)
                    BankEditTextExposed(
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
                        value = uiState.iban,
                        onValueChange = { viewModel?.setIban(it) },
                        maxLength = 24,
                        label = stringResource(id = R.string.iban_toggle),
                        placeHolder = stringResource(id = R.string.iban_without_ir_st),
                        keyboardType = KeyboardType.Number,
                        errorMessage = uiState.ibanValidationMessage.asString(),
                        dumpErrorMessage = { viewModel?.dismissIbanValidationMessage() }
                    )
                LoadingButton(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                    top = 12.dp,
                                    bottom = dimensionResource(id = R.dimen.large_padding)
                            ),
                    loading = uiState.convertState.isLoading(),
                    paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
                    onClick = {
                        keyboardController?.hide()
                        viewModel?.convert()
                    },
                    btnLabel = stringResource(id = R.string.convert_st)
                )

                SearchSheet(
                    sheetVisible = bankSelectionSheet,
                    onDismiss = { bankSelectionSheet = false },
                    isGrid = false,
                    items = BanksForConvertToIban.entries.map { bankStr ->
                        SearchSheetItemModel(value = bankStr.bankNameInPersian)
                    },
                    onItemClick = { itemModel ->
                        viewModel?.setBankName(itemModel.value)
                        if (itemModel.value.isNotEmpty()) viewModel?.dismissBankNameError()
                        bankSelectionSheet = false
                    })

                uiState.convertState.let { convertState ->
                    if (convertState.isSuccess())
                        AnimatedVisibility(
                            visible = convertState.isSuccess()
                        ) {
                            val data = convertState.getSuccessData().parameters
                            BasicAlertDialog(onDismissRequest = {
                                viewModel?.dismissConvertState()
                            }) {
                                IbanConvertorResultDialog(
                                    bankShowName = data?.bankShowName,
                                    colorCode = data?.colorCode,
                                    imageUrl = data?.imageUrl,
                                    resultTitle = if (uiState.selectedToggle?.id == 0 || uiState.selectedToggle?.id == 1)
                                        stringResource(id = R.string.iban_toggle)
                                    else
                                        stringResource(id = R.string.conversion_account),
                                    result = if (uiState.selectedToggle?.id == 0 || uiState.selectedToggle?.id == 1)
                                        data?.shebaNumber!!
                                    else data?.accountNumber!!
                                ) {
                                    viewModel?.dismissConvertState()
                                }
                            }
                        }
                }

            }
        }
    }
}
