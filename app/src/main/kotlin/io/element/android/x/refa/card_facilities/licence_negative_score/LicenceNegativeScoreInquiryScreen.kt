package com.drp.card_facilities.presentation.licence_negative_score

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.presentation.licence_negative_score.result.LicenceNegativeScoreResultDialog
import io.element.android.x.refa.card_facilities.phone_contact.SuperAppPhoneContactActivity
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.NetworkErrorDialogContent
import com.drp.shared_ui.widget.PhoneAutoEditText
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenceNegativeScoreInquiryScreen(
    modifier: Modifier = Modifier,
    viewModel: LicenceNegativeScoreViewModel? = null,
    navController: NavHostController = rememberNavController(),
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value
        ?: LicenceNegativeScoreScreenState()
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val currentSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

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
        },
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.licence_negative_score_title_st),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        Column(modifier = modifier.padding(top = paddingValues.calculateTopPadding())) {
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
                BankEditTextExposed(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_padding)),
                    value = uiState.licenceNumber,
                    onValueChange = { viewModel?.setLicenceNumber(it) },
                    maxLength = 10,
                    label = stringResource(id = R.string.licence_number_st),
                    placeHolder = stringResource(id = R.string.licence_number_st),
                    keyboardType = KeyboardType.Number,
                    errorMessage = uiState.licenceNumberValidationMessage.asString(),
                    dumpErrorMessage = { viewModel?.dismissLicenceNumberValidationMessage() }
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
                        if (it.isEmpty())
                            viewModel?.dismissMobilePhoneSpinner()
                    },
                    contactSheetList = uiState.mobileContactSheetList,
                    spinnerListContact = uiState.mobilePhoneContactSpinner,
                    onDismissSpinner = {
                        viewModel?.dismissMobilePhoneSpinner()
                    },
                    onSpinnerDropDownClick = {
                        viewModel?.setMobileNumber(it)
                    },
                    phoneContactIntent = Intent(
                        context, SuperAppPhoneContactActivity::class.java
                    ),
                    label = stringResource(id = R.string.mobile_no),
                    placeHolder = stringResource(id = R.string.mobile_no),
                    errorMessage = uiState.mobileNumberValidationMessage.asString(),
                    dumpErrorMessage = {
                        viewModel?.dismissMobileNumberValidationMessage()
                    })
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
                        viewModel?.inquiry()
                    },
                    btnLabel = stringResource(id = R.string.inquiry_st)
                )

                uiState.inquiry.let { inquiryState ->
                    if (inquiryState.isSuccess())
                        AnimatedVisibility(
                            visible = inquiryState.isSuccess()
                        ) {
                            BasicAlertDialog(onDismissRequest = {
                                navController.popBackStack()
                            }) {
                                LicenceNegativeScoreResultDialog(parameters = inquiryState.getSuccessData().parameters!!) {
                                    navController.popBackStack()
                                }
                            }
                        }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LicenceNegativeScoreInquiryScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LicenceNegativeScoreInquiryScreen()
        }
    }
}
