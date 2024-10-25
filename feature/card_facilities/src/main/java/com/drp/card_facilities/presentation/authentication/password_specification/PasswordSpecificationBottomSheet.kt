package com.drp.card_facilities.presentation.authentication.password_specification

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.R
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.PasswordEditText
import com.drp.shared_ui.R as UiRes

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PasswordSpecificationBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: PasswordBottomSheetViewModel? = null,
    onSuccess: () -> Unit,
    snackBarHost: @Composable () -> Unit = {},
    dismiss: () -> Unit,
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()

    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = UiRes.drawable.ic_password,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.set_password_st
        ),
        snackBarHost = snackBarHost
    ) {
        PasswordEditText(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
                .padding(top = dimensionResource(id = UiRes.dimen.small_padding)),
            text = uiState?.value?.password ?: "",
            onTextChange = {
                viewModel?.sendEvent(
                    PasswordSpecificationBottomSheetEvents.SetPasswordSpecification(
                        it
                    )
                )
            },
            errorMessage = uiState?.value?.passwordValidationMessage?.asString() ?: "",
            dumpErrorMessage = {
                viewModel?.sendEvent(PasswordSpecificationBottomSheetEvents.DumpPasswordValidationMessageSpecification)
            }
        )
        PasswordEditText(
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding)),
            label = stringResource(id = R.string.password_repeat_label),
            text = uiState?.value?.passwordRepeat ?: "",
            onTextChange = { viewModel?.sendEvent(
                PasswordSpecificationBottomSheetEvents.SetPasswordRepeatSpecification(
                    it
                )
            ) },
            errorMessage = uiState?.value?.passwordRepeatValidationMessage?.asString() ?: "",
            dumpErrorMessage = {
                viewModel?.sendEvent(PasswordSpecificationBottomSheetEvents.DumpPasswordRepeatValidationMessageSpecification)
            }
        )
        LoadingButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
                .padding(top = dimensionResource(id = UiRes.dimen.medium_padding)),
            onClick = { viewModel?.sendEvent(PasswordSpecificationBottomSheetEvents.SavePasswordSpecification) },
            btnLabel = stringResource(id = R.string.ok)
        )
    }

    uiState?.value?.isPasswordSaveActionCompleted?.let {
        if (it) {
            onSuccess()
            dismiss()
            viewModel.sendEvent(PasswordSpecificationBottomSheetEvents.BackToDefault)
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun PasswordBottomSheetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            PasswordSpecificationBottomSheet(
                visibility = true,
                dismiss = {},
                viewModel = hiltViewModel(),
                onSuccess = {}
            )
        }
    }
}