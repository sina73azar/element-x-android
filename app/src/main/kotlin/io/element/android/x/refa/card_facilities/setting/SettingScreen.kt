package com.drp.card_facilities.presentation.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.drp.shared_ui.model.ServiceItemModel
import com.drp.shared_ui.naviagtion.Screens
import com.drp.refah.card_facilities.widgets.compose.ServiceItemRow
import com.drp.refah.ui.data.model.SnackBarType
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.SnackBarCompose
import io.element.android.x.R

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingScreenViewModel? = null,
    navController: NavHostController = rememberNavController(),
    finishActivity: (() -> Unit)? = null
) {
    val settingServices =
        mapOf(
            ServiceItemModel(
                id = 1,
                startIcon = R.drawable.ic_shield,
                title = stringResource(id = R.string.authentication_st),
                endIcon = R.drawable.ic_arrow_left,
            ) to {
                navController.navigate(Screens.AuthenticationScreen.route)
            },
            ServiceItemModel(
                id = 2,
                startIcon = R.drawable.ic_history,
                title = stringResource(id = R.string.transaction_history_title),
                endIcon = R.drawable.ic_arrow_left,
            ) to {
                navController.navigate(Screens.TransactionHistoryScreen.route)
            }
        )
    val snackBarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
    val curSnackType by remember {
        mutableStateOf(SnackBarType.FAIL)
    }
    Scaffold(
        snackbarHost = {
            SnackBarCompose(snackbarHostState = snackBarHostState, snackBarType = curSnackType)
        },
        topBar = {
            CustomTopAppBar(
                headerTxt = stringResource(id = R.string.setting_header_title_st),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }) { paddingValues ->

        Box(
            modifier = modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                    .padding(
                        bottom = 72.dp
                    )
                    .fillMaxSize()
            ) {
                items(items = settingServices.keys.toList()) {
                    ServiceItemRow(
                        modifier = Modifier.padding(
                            top = dimensionResource(
                                id = R.dimen.medium_padding
                            )
                        ),
                        serviceItemModel = it.copy(
                            endIcon = R.drawable.ic_arrow_left,
                            startIconTint = MaterialTheme.colorScheme.primary
                        ),
                        onClick = settingServices[it]!!
                    )
                }
            }
            ExitRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = dimensionResource(
                            id = R.dimen.medium_padding
                        )
                    )
                    .padding(bottom = dimensionResource(id = R.dimen.large_padding))
            ) {
                viewModel?.logout()
                finishActivity?.invoke()
            }
        }
    }
}

@Composable
private fun ExitRow(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Red)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.app_bar_size)))
            Text(
                modifier = Modifier.weight(1f),
                text = "خروج",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.W200,
                    textAlign = TextAlign.Center
                ),
                color = Color.Red
            )
            IconButton(
                modifier = Modifier,
                onClick = onClick
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.logout_ic),
                    contentDescription = "",
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SettingScreen()
        }
    }
}

@Preview
@Composable
private fun ExitRowPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ExitRow {

            }
        }
    }
}

