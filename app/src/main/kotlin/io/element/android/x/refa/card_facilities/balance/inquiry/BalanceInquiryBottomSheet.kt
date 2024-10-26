package com.drp.card_facilities.presentation.balance.inquiry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.balance.BalanceScreenEvents
import com.drp.card_facilities.presentation.balance.BalanceScreenState
import com.drp.card_facilities.presentation.balance.BalanceScreenViewModel
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.OTPPasswordEditTextCompose
import com.drp.shared_ui.widget.SubmitCancelRowCompose
import io.element.android.x.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceInquiryBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: BalanceScreenViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    dismiss: () -> Unit,
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: BalanceScreenState()
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_balance,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.card_balance_st
        ),
        snackBarHost = snackBarHost
    ) {
        Column(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding))) {
            OTPPasswordEditTextCompose(
                passwordType = SecondAuthenticationMethod.SMS,
                readOnly = false,
                otpSmsRequest = {
                    viewModel?.sendEvent(
                        BalanceScreenEvents.SendOtp
                    )
                },
                smsState = uiState.sourceCardUiState.smsState,
                textValue = uiState.sourceCardUiState.otpCode,
                onValueChange = { otpCode ->
                    viewModel?.sendSourceCardEvent(SourceCardEvents.SetOtpCode(otpCode = otpCode))
                },
                maxLength = 12,
                errorMessage = uiState.sourceCardUiState.otpCodeValidationMessage.asString(),
                dumpErrorMessage = { viewModel?.sendSourceCardEvent(SourceCardEvents.DumpOtpCodeValidationMessage) },
                showError = { viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.ShowError(it)) }
            )
            SubmitCancelRowCompose(
                submitText = stringResource(id = R.string.ok),
                onSubmitClick = {
                    viewModel?.sendEvent(
                        BalanceScreenEvents.BalanceInquiry
                    )
                    keyboardController?.hide()
                },
                dismissText = stringResource(id = R.string.cancel),
                onSubmitLoading = uiState.balanceInquiryState.isLoading()
            ) {
                viewModel?.sendEvent(BalanceScreenEvents.DismissOtpBottomSheet)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BalanceScreenBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BalanceInquiryBottomSheet(
                visibility = true,
            ) {

            }
        }
    }
}
