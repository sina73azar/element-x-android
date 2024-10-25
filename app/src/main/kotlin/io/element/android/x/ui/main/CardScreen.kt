package com.drp.refahland.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.refahland.R
import com.drp.refahland.navigation.MainScreens
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CardNumberShotWidget
import com.drp.shared_ui.widget.CustomLine
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.EmptyOrFailContent
import com.drp.shared_ui.widget.findImageResources
import com.drp.utils.panFormatter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.drp.shared_ui.R as UiRes

@Composable
fun CardScreen(
    navController: NavController,
    viewModel: MainViewModel? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()
    var selectedCardShotOptions by remember { mutableStateOf<CardShotItemInfo?>(null) }
    var openSelectedCardForEdit by remember { mutableStateOf(false) }
    BackHandler {
        viewModel?.setSelectedBottomBarId(3)
        navController.popBackStack(
            route = MainScreens.HomeScreen.route,
            inclusive = false
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CustomTopAppBar(
            headerTxt = stringResource(id = com.drp.card_facilities.R.string.cards_st),
            backBtnVisible = false
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn {
                if (uiState.sharedViewModelUiState.cardsList.isEmpty())
                    item {
                        EmptyOrFailContent(
                            modifier = Modifier
                                .padding(dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                            desc = stringResource(id = R.string.no_card_st),
                        )
                    }
                else
                    items(items = uiState.sharedViewModelUiState.cardsList) { cardShotItem ->
                        CardItemWidget(
                            cardShotItemInfo = cardShotItem,
                            onCardItemOptionsClicked = { selectedCardShotOptions = cardShotItem }
                        )
                    }
                item {
                    Spacer(modifier = Modifier.height(170.dp))
                }
            }
            CardNumberShotWidget(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = 96.dp, start = dimensionResource(
                            id = UiRes.dimen.large_padding
                        )
                    ),
                selectedCard = null,
                isCardNumberInFabMode = true,
                cardsList = uiState.sharedViewModelUiState.cardsList,
                onCardAdd = {
                    viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.AddCard(it))
                },
                onCardEdited = {
                    viewModel?.sendSharedViewModelEvent(SharedViewModelEvents.EditCard(it))
                },
                openSelectedCardForEditBottomSheet = openSelectedCardForEdit,
                openSelectedCardForEditCardShotItemInfo = selectedCardShotOptions,
                actionsAfterSelectedCardForEditInFabMode = {
                    if (it) {
                        selectedCardShotOptions = null
                        openSelectedCardForEdit = false
                    } else
                        openSelectedCardForEdit = false
                }
            )
        }
    }

    CardOptionsBottomSheet(
        visibility = selectedCardShotOptions != null,
        selectedCard = selectedCardShotOptions,
        navController = navController,
        dismiss = { selectedCardShotOptions = null },
        deleteCard = {
            selectedCardShotOptions?.let {
                viewModel?.sendSharedViewModelEvent(
                    SharedViewModelEvents.RemoveCard(
                        it.id
                    )
                )
            }
        },
        editCard = {
            openSelectedCardForEdit = true
        }
    )
}

@Composable
fun CardItemWidget(
    modifier: Modifier = Modifier,
    cardShotItemInfo: CardShotItemInfo,
    onCardItemOptionsClicked: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = dimensionResource(id = UiRes.dimen.large_padding))
            .padding(top = dimensionResource(id = UiRes.dimen.large_padding)),
        shape = RoundedCornerShape(size = dimensionResource(id = UiRes.dimen.large_corner)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = UiRes.dimen.medium_elevation))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFac1f81),
                            Color(0xFF283f9c)
                        )
                    )
                )
        ) {
            Image(
                modifier = Modifier
                    .alpha(0.018f)
                    .align(Alignment.BottomStart),
                painter = painterResource(id = R.drawable.ic_two_circles_outline),
                contentDescription = null
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = dimensionResource(id = UiRes.dimen.medium_padding))
                    .padding(horizontal = dimensionResource(id = UiRes.dimen.large_padding)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = UiRes.dimen.icon_size)),
                    painter = painterResource(id = findImageResources(imageIcon = cardShotItemInfo.bankIcon)),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Text(
                    modifier = Modifier
                        .weight(1F)
                        .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding)),
                    text = cardShotItemInfo.bankName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_shetab),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8F)
                )
            }
            Text(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = UiRes.dimen.large_padding))
                    .align(Alignment.Center),
                text = panFormatter(cardShotItemInfo.pan, "  "),
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = TextDirection.Ltr,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onCardItemOptionsClicked() }) {
                    Icon(
                        modifier = Modifier
                            .size(dimensionResource(id = com.drp.shared_ui.R.dimen.icon_size)),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding)),
                    text = cardShotItemInfo.panExpiryYear?.let { expireYear ->
                        cardShotItemInfo.panExpiryMonth?.let { expireMonth ->
                            stringResource(id = R.string.expire_date).plus(" $expireMonth / $expireYear")
                        } ?: stringResource(id = R.string.expire_date).plus(" ** / ** ")
                    } ?: stringResource(id = R.string.expire_date).plus(" ** / ** "),
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    color = Color.White
                )
                Spacer(
                    modifier = Modifier
                        .size(48.dp)
                )
            }
        }
    }
}

@Composable
fun CardOptionsBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    selectedCard: CardShotItemInfo?,
    navController: NavController,
    deleteCard: () -> Unit,
    editCard: () -> Unit,
    dismiss: () -> Unit
) {
    val itemsList =
        arrayListOf(
            stringResource(id = com.drp.card_facilities.R.string.card_to_card_st),
            stringResource(id = com.drp.card_facilities.R.string.card_balance_st),
            stringResource(id = com.drp.card_facilities.R.string.card_statement_st),
            stringResource(id = com.drp.card_facilities.R.string.bill_payment_st),
            stringResource(id = com.drp.card_facilities.R.string.charge_payment_st),
            stringResource(id = com.drp.card_facilities.R.string.insurance_bill_payment_st),
            stringResource(id = com.drp.card_facilities.R.string.internet_package_title),
            stringResource(id = com.drp.shared_ui.R.string.card_modification_sheet_title),
            stringResource(id = com.drp.shared_ui.R.string.delete_card_st)
        )
    val context = LocalContext.current
    DynamicSheet(
        isVisible = visibility,
        onDismiss = dismiss,
        title = stringResource(id = com.drp.card_facilities.R.string.details),
        icon = com.drp.card_facilities.R.drawable.ic_card_list_outlined,
    ) {
        LazyColumn {
            itemsIndexed(items = itemsList) { index, title ->
                Card(shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {
                        when (index) {
                            0 -> {
                                /*val intent =
                                    Intent(context, SuperAppCardToCardActivity::class.java).apply {
                                        putExtra(
                                            SELECTED_CARD_KEY,
                                            Json.encodeToString(selectedCard)
                                        )
                                    }
                                context.startActivity(intent)
                                dismiss()*/
                                navController.navigate(
                                    route = "${Screens.CardToCardScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            1 -> {
                                navController.navigate(
                                    route = "${Screens.BalanceScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            2 -> {
                                navController.navigate(
                                    route = "${Screens.LastTenStatementScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            3 -> {
                                navController.navigate(
                                    route = "${Screens.BillInquiryScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            4 -> {
                                navController.navigate(
                                    route = "${Screens.TopUpScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            5 -> {
                                navController.navigate(
                                    route = "${Screens.InsuranceInquiryScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            6 -> {
                                navController.navigate(
                                    route = "${Screens.InternetPackageInquiryScreen.route}?selectedCard=${
                                        Json.encodeToString(
                                            selectedCard
                                        )
                                    }"
                                )
                            }

                            7 -> {
                                editCard()
//                                dismiss()
                            }

                            8 -> {
                                deleteCard()
                                dismiss()
                            }

                            else -> {

                            }
                        }
                    }) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
                            .padding(
                                vertical = dimensionResource(
                                    id = UiRes.dimen.medium_padding
                                )
                            ),
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (index != itemsList.lastIndex) CustomLine(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = UiRes.dimen.medium_padding)
                        )
                    )
                }
            }
        }
    }
}

private fun getMockCardShotItemInfos() = listOf(
    CardShotItemInfo(
        id = 1,
        pan = "5047061044402697",
        bankName = "بانک شهر",
        bankIcon = "ic_shahr",
        panExpiryYear = "1409",
        panExpiryMonth = "06",
        isRefahSource = false,
        default = false
    ),
    CardShotItemInfo(
        id = 2,
        pan = "6362141122457378",
        bankName = "بانک آینده",
        bankIcon = "ic_ayandeh",
        panExpiryYear = "1408",
        panExpiryMonth = "04",
        isRefahSource = false,
        default = true
    ),
    CardShotItemInfo(
        id = 3,
        pan = "6362141122457378",
        bankName = "بانک آینده",
        bankIcon = "ic_ayandeh",
        panExpiryYear = "1408",
        panExpiryMonth = "04",
        isRefahSource = false,
        default = true
    )
)

@Preview(showBackground = true)
@Composable
private fun CardScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CardScreen(navController = rememberNavController())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardShotItemPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CardItemWidget(cardShotItemInfo = getMockCardShotItemInfos()[0]) {}
        }
    }
}