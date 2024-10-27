package com.drp.card_facilities.presentation.shahkar

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PhoneAutoEditText
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import io.element.android.x.refa.card_facilities.shahkar.otp.ShahkarOtpBottomSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("HardwareIds")
@Composable
fun ShahkarLoginScreen(
    modifier: Modifier = Modifier,
    viewModel: ShahkarViewModel? = null,
    navController: NavHostController = rememberNavController(),
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: ShahkarScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val imei = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    val smsPermission = android.Manifest.permission.RECEIVE_SMS

    // Activity result launcher to request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, you can proceed with SMS operations
        } else {
            // Permission denied, show a message or handle accordingly
        }
    }

    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context, smsPermission
    ) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(Unit) {
        if (!isPermissionGranted) {
            permissionLauncher.launch(smsPermission)
        }
    }

    /*val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted)
            ActivityResultContracts.RequestPermission()
        *//*if (isGranted) {
            navController.navigate(Screens.CameraScreen.route)
        } else {

        }*//*
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permission
        launcher.launch(Manifest.permission.RECEIVE_SMS)
    }*/

    /** handling error messages */
    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collectLatest {
            snackBarHostState.showSnackbar(it.asString(context))
        }
    }

    /** handling failure dialog */
    AnimatedVisibility(
        visible = uiState.inquiry.isFail() || uiState.validate.isFail()
    ) {
        BasicAlertDialog(onDismissRequest = {
            viewModel?.dismissFailureDialog()
        }) {
            NetworkErrorDialogContent(
                closeAction = {
                    viewModel?.dismissFailureDialog()
                })
        }
    }

    Scaffold(
        snackbarHost = {
            SnackBarCompose(
                snackbarHostState = snackBarHostState,
                snackBarType = currentSnackType
            )
        }) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(top = paddingValues.calculateTopPadding()).background(Color.White)) {
            Image(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                painter = painterResource(id = R.drawable.full_logo),
                contentDescription = null
            )
            BankEditTextExposed(
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)),
                value = uiState.nationalId,
                onValueChange = { viewModel?.setNationalId(it) },
                maxLength = 10,
                label = stringResource(id = R.string.national_id_st),
                placeHolder = stringResource(id = R.string.national_id_st),
                keyboardType = KeyboardType.Number,
                errorMessage = uiState.nationalIdValidationMessage.asString(),
                dumpErrorMessage = { viewModel?.dismissNationalIdValidationMessage() }
            )
            PhoneAutoEditText(
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)),
                value = uiState.mobileNumber,
                onValueChange = {
                    viewModel?.setMobileNumber(it)
                },
                contactIconVisibility = false,
                keyboardType = KeyboardType.Number,
                label = stringResource(id = R.string.mobile_no),
                placeHolder = stringResource(id = R.string.mobile_no),
                errorMessage = uiState.mobileNumberValidationMessage.asString(),
                dumpErrorMessage = {
                    viewModel?.dismissMobileNumberValidationMessage()
                }
            )
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
                    viewModel?.inquiry(imei)
                },
                btnLabel = stringResource(id = R.string.login_st)
            )

            uiState.inquiry.let { inquiryState ->
                if (inquiryState.isSuccess())
                    ShahkarOtpBottomSheet(
                        visibility = inquiryState.isSuccess(),
                        viewModel = viewModel,
                        snackBarHost = {
                            SnackBarCompose(
                                snackbarHostState = snackBarHostState,
                                snackBarType = currentSnackType
                            )
                        },
                        navigateToHome = {
                            navController.navigate(route = Screens.HomeScreen.route){
                                popUpTo(route = Screens.ShahkarLoginScreen.route){
                                    inclusive = true
                                }
                            }
                        },
                        dismiss = {
                            viewModel?.backToDefault()
                        }
                    )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LicenceNegativeScoreInquiryScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ShahkarLoginScreen()
        }
    }
}
