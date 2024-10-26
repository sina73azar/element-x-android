package com.drp.card_facilities.presentation.wallet_to_wallet

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.presentation.wallet_to_wallet.transfer.WalletToWalletTransferBottomSheet
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CardFacilityAmountEditText
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletToWalletScreen(
    modifier: Modifier = Modifier,
    viewModel: WalletToWalletViewModel? = null,
    navController: NavHostController = rememberNavController(),
    scannedWalletId: String,
    resetScannedWalletIdToDefault: () -> Unit
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: WalletToWalletScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    LaunchedEffect(key1 = scannedWalletId) {
        if (scannedWalletId.isNotEmpty()) {
            viewModel?.sendEvent(WalletToWalletScreenEvents.SetDestinationWalletId(scannedWalletId.filter { it.isDigit() }))
        }
    }

    BackHandler {
        navController.popBackStack()
        resetScannedWalletIdToDefault()
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            navController.navigate(Screens.CameraScreen.route)
        } else {
            ActivityResultContracts.RequestPermission()
        }
    }

    fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            navController.navigate(Screens.CameraScreen.route)
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
        visible = uiState.transferState.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.sendEvent(WalletToWalletScreenEvents.DismissFailureDialog)
        }) {
            NetworkErrorDialogContent(closeAction = {
                viewModel?.sendEvent(WalletToWalletScreenEvents.DismissFailureDialog)
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
            headerTxt = stringResource(id = R.string.wallet_to_wallet_st),
            onBackClick = {
                navController.popBackStack()
                resetScannedWalletIdToDefault()
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
                BankEditTextExposed(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)),
                    value = uiState.destinationWalletId,
                    onValueChange = {
                        viewModel?.sendEvent(
                            WalletToWalletScreenEvents.SetDestinationWalletId(it)
                        )
                    },
                    maxLength = 15,
                    label = stringResource(id = R.string.destination_wallet_id_st),
                    placeHolder = stringResource(id = R.string.destination_wallet_id_st),
                    keyboardType = KeyboardType.Number,
                    errorMessage = uiState.destinationWalletIdValidationMessage.asString(),
                    dumpErrorMessage = { viewModel?.sendEvent(WalletToWalletScreenEvents.DumpDestinationWalletIdValidationMessage) },
                    scannerIconVisibility = true,
                    scanIconClick = {
                        resetScannedWalletIdToDefault()
                        checkAndRequestPermission()
                    }
                )
                CardFacilityAmountEditText(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_padding)),
                    value = uiState.amount,
                    onValueChange = {
                        viewModel?.sendEvent(WalletToWalletScreenEvents.SetAmount(it))
                    },
                    label = stringResource(id = R.string.amount),
                    placeHolder = stringResource(id = R.string.amount),
                    errorMessage = uiState.amountValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.sendEvent(WalletToWalletScreenEvents.DumpAmountValidationMessage)
                    })
                BankEditTextExposed(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)),
                    value = uiState.description,
                    onValueChange = {
                        viewModel?.sendEvent(
                            WalletToWalletScreenEvents.SetDescription(
                                it
                            )
                        )
                    },
                    maxLength = 50,
                    label = stringResource(id = R.string.wallet_add_description_st),
                    placeHolder = stringResource(id = R.string.wallet_add_description_st)
                )
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
                        viewModel?.sendEvent(WalletToWalletScreenEvents.ValidateScreenParameters)
                    },
                    btnLabel = stringResource(id = R.string.submit_continue)
                )
            }

            /** get otp bottom sheet */
            WalletToWalletTransferBottomSheet(
                visibility = uiState.getOtpBottomSheetState,
                viewModel = viewModel,
                receiptItems = walletToWalletReceipt(
                    sourceWalletId = uiState.walletUiState.shahkarUserData.walletId?.toString()
                        ?: "",
                    destinationWalletId = uiState.destinationWalletId,
                    amount = uiState.amount,
                    description = uiState.description
                ),
                navigateToLanding = {
                    navController.popBackStack()
                    resetScannedWalletIdToDefault()
                },
                snackBarHost = {
                    SnackBarCompose(
                        snackbarHostState = snackBarHostState,
                        snackBarType = currentSnackType
                    )
                },
            ) {
                viewModel?.sendEvent(WalletToWalletScreenEvents.DismissOtpBottomSheet)
                viewModel?.sendEvent(WalletToWalletScreenEvents.BackToDefault)
            }
        }
    }
}
