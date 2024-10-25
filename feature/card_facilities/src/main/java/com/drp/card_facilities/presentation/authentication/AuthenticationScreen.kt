package com.drp.card_facilities.presentation.authentication

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.R
import com.drp.data.model.FacilityServiceItem
import com.drp.card_facilities.presentation.authentication.password_check.PasswordCheckBottomSheet
import com.drp.card_facilities.presentation.authentication.password_specification.PasswordSpecificationBottomSheet
import com.drp.shared_ui.enums.AuthenticationType
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.UiText
import com.drp.shared_ui.widget.AuthenticateWithBiometricPrompt
import com.drp.shared_ui.widget.CustomLine
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.SnackBarCompose
import com.drp.shared_ui.widget.canAuthenticateWithBiometric

import com.drp.shared_ui.R as UiRes

@Composable
fun AuthenticationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AuthenticationScreenViewModel? = null,
) {
    val context = LocalContext.current

    val canAuthenticateWithBiometrics = canAuthenticateWithBiometric(context)

    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val curSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()
//    val landingScreenItems = generateLandingScreenItems()
//    LaunchedEffect(key1 = landingScreenItems.size) {
//        landingScreenItems.keys.forEach {
//            landingScreenItems[it]?.keys?.forEach { facilityServiceItem ->
//                viewModel?.sendEvent(AuthenticationScreenEvents.AddItems(facilityServiceItem))
//            }
//        }
//        viewModel?.sendEvent(AuthenticationScreenEvents.SetItemsAuthenticationEnabled)
//    }

    LaunchedEffect(key1 = true) {
        viewModel?.errors?.collect { message ->
            snackBarHostState.showSnackbar(message.asString(context))
        }
    }

    Scaffold(
        snackbarHost = {
            SnackBarCompose(snackbarHostState = snackBarHostState, snackBarType = curSnackType)
        },
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.authentication_st),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {
            item {
                SingleOrNoneSelectionAuthTypeWidget(
                    modifier = Modifier
                        .padding(top = dimensionResource(id = UiRes.dimen.large_padding))
                        .padding(
                            horizontal = dimensionResource(
                                id = UiRes.dimen.medium_padding
                            )
                        ),
                    headerText = stringResource(id = R.string.authentication_types_st),
                    selectedAuthType = uiState?.value?.selectedAuthType ?: AuthenticationType.NONE
                ) {
                    viewModel?.sendEvent(AuthenticationScreenEvents.ChangeAuthenticationType(it))
                }
            }
            item {
                AnimatedVisibility(visible = uiState?.value?.selectedAuthType != AuthenticationType.NONE) {
                    MultipleSelectionServicesWidget(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
                            .padding(top = dimensionResource(id = UiRes.dimen.large_padding)),
                        items = uiState?.value?.items ?: remember { mutableStateListOf() },
                        headerText = stringResource(id = R.string.auth_services_st),
                        onItemAddToAuth = {
                            viewModel?.sendEvent(
                                AuthenticationScreenEvents.AddServiceIdToAuthList(
                                    it
                                )
                            )
                        },
                        onItemAuthRemoved = {
                            viewModel?.sendEvent(
                                AuthenticationScreenEvents.RemoveServiceIdFromAuthList(
                                    it
                                )
                            )
                        }
                    )
                }
            }
        }

        uiState?.value?.passwordSpecificationBottomSheetVisibility?.let {
            PasswordSpecificationBottomSheet(
                visibility = it,
                viewModel = hiltViewModel(),
                onSuccess = {
                    if (uiState.value.actionType == ActionType.EnablePass ||
                        uiState.value.actionType == ActionType.DisableBio_EnablePass
                    )
                        viewModel.sendEvent(
                            AuthenticationScreenEvents.ChangeAuthTypeInLocal(
                                AuthenticationType.PASSWORD
                            )
                        )
                }) {
                viewModel.sendEvent(AuthenticationScreenEvents.DismissPasswordSpecificationBottomSheet)
            }
        }
        uiState?.value?.passwordCheckBottomSheetVisibility?.let {
            PasswordCheckBottomSheet(
                visibility = it,
                viewModel = hiltViewModel(),
                headerTitle = stringResource(
                    id = R.string.password_remove_st
                ),
                onPasswordMatch = {
                    if (uiState.value.actionType == ActionType.DisablePass)
                        viewModel.sendEvent(
                            AuthenticationScreenEvents.ChangeAuthTypeInLocal(
                                AuthenticationType.NONE
                            )
                        )
                    if (uiState.value.actionType == ActionType.DisablePass_EnableBio)
                        viewModel.sendEvent(
                            AuthenticationScreenEvents.ShowBiometricPrompt
                        )
                }
            ) {
                viewModel.sendEvent(AuthenticationScreenEvents.DismissPasswordCheckBottomSheet)
            }
        }
        uiState?.value?.biometricPromptVisibility?.let {
            if (it)
                if (canAuthenticateWithBiometrics) {
                    AuthenticateWithBiometricPrompt(
                        context = context,
                        onSuccess = {
                            if (
                                uiState.value.actionType == ActionType.EnableBio ||
                                uiState.value.actionType == ActionType.DisablePass_EnableBio
                            ) {
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.ChangeAuthTypeInLocal(
                                        AuthenticationType.BIOMETRIC
                                    )
                                )
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.DismissBiometricPrompt
                                )
                            }
                            if (uiState.value.actionType == ActionType.DisableBio) {
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.ChangeAuthTypeInLocal(
                                        AuthenticationType.NONE
                                    )
                                )
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.DismissBiometricPrompt
                                )
                            }
                            if (uiState.value.actionType == ActionType.DisableBio_EnablePass) {
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.DismissBiometricPrompt
                                )
                                viewModel.sendEvent(
                                    AuthenticationScreenEvents.ShowPasswordSpecificationBottomSheet
                                )
                            }
                        },
                        onError = {
                            viewModel.sendEvent(
                                AuthenticationScreenEvents.ShowError(
                                    UiText.StringResource(
                                        R.string.biometric_auth_error_st
                                    )
                                )
                            )
                            viewModel.sendEvent(
                                AuthenticationScreenEvents.DismissBiometricPrompt
                            )
                        })
                } else {
                    viewModel.sendEvent(
                        AuthenticationScreenEvents.ShowError(
                            UiText.StringResource(
                                R.string.finger_print_is_not_available
                            )
                        )
                    )
                }
        }
    }
}

@Composable
private fun MultipleSelectionServicesWidget(
    modifier: Modifier = Modifier,
    items: SnapshotStateList<FacilityServiceItem>,
    headerText: String = "",
    onItemAddToAuth: (Int) -> Unit,
    onItemAuthRemoved: (Int) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = UiRes.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        if (headerText.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = UiRes.dimen.medium_padding),
                        vertical = dimensionResource(id = UiRes.dimen.medium_padding)
                    )
                    .fillMaxWidth(),
                text = headerText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            CustomLine(modifier = Modifier.padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding)))
        }
        Column {
            items.forEach { facilityItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = UiRes.dimen.small_padding)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
                            .weight(1f),
                        text = facilityItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.W200
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        modifier = Modifier
                            .padding(end = dimensionResource(id = UiRes.dimen.large_padding)),
                        thumbContent = {
                            if (facilityItem.authenticationEnabled)
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                        },
                        checked = facilityItem.authenticationEnabled,
                        onCheckedChange = { checked ->
                            items.indexOf(facilityItem).let { index ->
                                items[index] = facilityItem.copy(authenticationEnabled = checked)
                            }
                            if (checked)
                                onItemAddToAuth(facilityItem.id)
                            else
                                onItemAuthRemoved(facilityItem.id)
                        })
                }
            }
        }
    }
}

@Composable
private fun SingleOrNoneSelectionAuthTypeWidget(
    modifier: Modifier = Modifier,
    headerText: String = "",
    selectedAuthType: AuthenticationType,
    onAuthTypeChange: (AuthenticationType) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = UiRes.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        if (headerText.isNotEmpty()) {

            Text(
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = UiRes.dimen.medium_padding),
                        vertical = dimensionResource(id = UiRes.dimen.medium_padding)
                    )
                    .fillMaxWidth(),
                text = headerText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            CustomLine(modifier = Modifier.padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding)))
        }
        AuthenticationType.entries.forEach { authType ->
            if (authType != AuthenticationType.NONE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(modifier = Modifier
                        .padding(
                            dimensionResource(id = UiRes.dimen.small_padding)
                        ), onClick = { }) {
                        Icon(
                            modifier = Modifier.size(dimensionResource(id = UiRes.dimen.icon_size)),
                            painter = painterResource(id = authType.icon),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = authType.title.asString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.W200
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        modifier = Modifier
                            .padding(end = dimensionResource(id = UiRes.dimen.large_padding)),
                        thumbContent = {
                            if (selectedAuthType == authType)
                                Icon(
                                    modifier = Modifier.size(16.dp),
                                    painter = painterResource(id = authType.icon),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                        },
                        checked = selectedAuthType == authType,
                        onCheckedChange = { checked ->
                            Log.i("TAG", "SingleOrNoneSelectionAuthTypeWidget: ")
                            if (checked) {
                                onAuthTypeChange(authType)
                            } else {
                                onAuthTypeChange(AuthenticationType.NONE)
                            }
                        })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthenticationScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AuthenticationScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SingleOrNoneSelectionAuthTypeWidgetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SingleOrNoneSelectionAuthTypeWidget(selectedAuthType = AuthenticationType.NONE) {

            }
        }
    }
}
//@Composable
//fun generateLandingScreenItems(
//    viewModel: LandingPageViewModel? = null,
//): Map<String, Map<FacilityServiceItem, (FacilityServiceItem) -> Unit>> {
//    return mapOf(
//        stringResource(id = R.string.card_services_title_st) to mapOf(
//            FacilityServiceItem(
//                id = Constants.CARD_TO_CARD_ID,
//                title = stringResource(id = R.string.card_to_card_st),
//                backgroundColor = Color(0xFFE24412),
//                iconResource = R.drawable.ic_card_to_card,
//                pageType = PageType.ACTIVITY,
//                route = NavigationActivity.SuperAppCardToCard.path
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//            FacilityServiceItem(
//                id = Constants.CARD_BALANCE_ID,
//                title = stringResource(id = R.string.card_balance_st),
//                backgroundColor = Color(0xFFE24412),
//                iconResource = R.drawable.ic_account,
//                route = Screens.BalanceScreen.route
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//            FacilityServiceItem(
//                id = Constants.CARD_STATEMENT_ID,
//                title = stringResource(id = R.string.card_statement_st),
//                backgroundColor = Color(0xFF0889C4),
//                iconResource = R.drawable.ic_list_account,
//                route = Screens.LastTenStatementScreen.route
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//        ),
//        stringResource(id = R.string.bill_services_title_st) to mapOf(
//            FacilityServiceItem(
//                id = Constants.CARD_BILL_PAYMENT_ID,
//                title = stringResource(id = R.string.bill_payment_st),
//                backgroundColor = Color(0xFF0B9411),
//                iconResource = R.drawable.ic_service_bill,
//                route = Screens.BillInquiryScreen.route
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//            FacilityServiceItem(
//                id = Constants.CARD_CHARGE_PAYMENT_ID,
//                title = stringResource(id = R.string.charge_payment_st),
//                backgroundColor = Color(0xFFE24412),
//                iconResource = com.drp.refah.ui.R.drawable.ic_hamrah_card,
//                route = Screens.TopUpScreen.route,
//                pageType = PageType.COMPOSE
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//            FacilityServiceItem(
//                id = Constants.CARD_INSURANCE_BILL_PAYMENT_ID,
//                title = stringResource(id = R.string.insurance_bill_payment_st),
//                backgroundColor = Color(0xFF0889C4),
//                iconResource = R.drawable.ic_payment_insurance,
//                route = NavigationActivity.InsuranceInquiryActivity.path,
//                pageType = PageType.ACTIVITY
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//            FacilityServiceItem(
//                id = Constants.CARD_INTERNET_PACKAGE_PAYMENT_ID,
//                title = stringResource(id = R.string.internet_package_title),
//                backgroundColor = Color(0xFF0889C4),
//                iconResource = R.drawable.ic_package_payment,
//                route = Screens.InternetPackageInquiryScreen.route,
//                pageType = PageType.COMPOSE
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//        ),
//        stringResource(id = R.string.loan_services_title_st) to mapOf(
//            FacilityServiceItem(
//                id = 1,
//                title = stringResource(id = R.string.loan_payment_st),
//                backgroundColor = Color(0xFF0B9411),
//                iconResource = com.drp.refah.ui.R.drawable.ic_online_loan,
//                route = Screens.InstallmentInquiryScreen.route,
//                pageType = PageType.COMPOSE
//            ) to {
//                viewModel?.sendEvent(LandingPageEvents.CheckAuthenticationEnabled(it))
//            },
//        )
//    )
//}
