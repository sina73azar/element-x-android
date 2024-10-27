package io.element.android.x.refa.card_facilities.shahkar.otp

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.presentation.shahkar.ShahkarScreenState
import com.drp.card_facilities.presentation.shahkar.ShahkarViewModel
import com.drp.card_facilities.presentation.topup.compose.payment.TopUpPaymentBottomSheet
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.OTPPasswordEditTextCompose
import com.drp.shared_ui.widget.SubmitCancelRowCompose
import io.element.android.x.R

@SuppressLint("HardwareIds")
@Composable
fun ShahkarOtpBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: ShahkarViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    navigateToHome: () -> Unit,
    dismiss: () -> Unit,
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: ShahkarScreenState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val imei = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_refah,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.login_to_account_st
        ),
        snackBarHost = snackBarHost
    ) {
        LazyColumn {

            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen.medium_padding))
                        .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding)),
                    text = stringResource(R.string.login_code_submitted).plus(" ")
                        .plus(uiState.mobileNumber).plus(" ").plus(stringResource(R.string.sent)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                OTPPasswordEditTextCompose(
                    passwordType = SecondAuthenticationMethod.SMS,
                    readOnly = false,
                    otpSmsRequest = {
                        viewModel?.sendOtp(imei)
                    },
                    smsState = uiState.smsState,
                    textValue = uiState.otpCode,
                    onValueChange = { otpCode ->
                        viewModel?.setOtpCode(otpCode)
                    },
                    maxLength = 12,
                    errorMessage = uiState.otpCodeValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.dismissOtpCodeValidationMessage()
                    },
                    showError = {
                        viewModel?.showError(
                            it
                        )
                    }

                )
            }
            item {
                SubmitCancelRowCompose(
                    submitText = stringResource(id = R.string.ok),
                    onSubmitClick = {
                        viewModel?.validate()
                        keyboardController?.hide()
                    },
                    dismissText = stringResource(id = R.string.cancel),
                    onSubmitLoading = uiState.validate.isLoading()
                ) {
                    viewModel?.backToDefault()
                }
            }
        }

        LaunchedEffect(key1 = uiState.validate) {
            uiState.validate.let { validateState ->
                if (validateState.isSuccess())
                    navigateToHome()
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TopUpPaymentBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            TopUpPaymentBottomSheet(
                visibility = true,
                dismiss = {},
            )
        }
    }
}
