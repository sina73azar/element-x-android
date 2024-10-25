package com.drp.refahland.ui.main

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.drp.card_facilities.R
import com.drp.data.database.entity.ContactEntity
import com.drp.data.enums.BillType
import com.drp.data.enums.ContactType
import com.drp.refahland.navigation.MainScreens
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.AnimatedShimmer
import com.drp.shared_ui.widget.CardShimmerItem
import com.drp.shared_ui.widget.CustomLine
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.EmptyOrFailContent
import com.drp.shared_ui.widget.ShimmerEffect
import com.drp.shared_ui.widget.findImageResources
import com.drp.shared_ui.R as UiRes


@Composable
fun BillScreen(
    navController: NavController,
    viewModel: MainViewModel? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()
    /*val mockBillList = listOf(
        ContactEntity(
            contactType = ContactType.BILL_ID.name,
            title = ContactType.BILL_ID.savedContactTitle,
            value = "45612345678"
        ),
        ContactEntity(
            contactType = ContactType.MOBILE_NO.name,
            title = ContactType.MOBILE_NO.savedContactTitle,
            value = "09144617450"
        ),
        ContactEntity(
            contactType = ContactType.TELEPHONE_NO.name,
            title = ContactType.TELEPHONE_NO.savedContactTitle,
            value = "02155420528"
        ),
        ContactEntity(
            contactType = ContactType.GAS_BILL_ID.name,
            title = ContactType.GAS_BILL_ID.savedContactTitle,
            value = "2359655"
        ),
        ContactEntity(
            contactType = ContactType.WATER_BILL_ID.name,
            title = ContactType.WATER_BILL_ID.savedContactTitle,
            value = "789651"
        ),
        ContactEntity(
            contactType = ContactType.ELECTRIC_BILL_ID.name,
            title = ContactType.ELECTRIC_BILL_ID.savedContactTitle,
            value = "523174532"
        )
    )*/

    var selectedContactEntityOptions by remember {
        mutableStateOf<ContactEntity?>(null)
    }
    BackHandler {
        viewModel?.setSelectedBottomBarId(3)
        navController.popBackStack(
            route = MainScreens.HomeScreen.route,
            inclusive = false
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.bills_st),
            backBtnVisible = false
        )
        LazyColumn {
            if (uiState.billContacts.isLoading())
                item {
                    ShimmerEffect {
                        AnimatedShimmer { brush ->
                            CardShimmerItem(brush = brush)
                        }
                    }
                }
            if (uiState.billContacts.isSuccess() && uiState.billContacts.getSuccessData()
                    .isEmpty()
            )
                item {
                    EmptyOrFailContent(
                        modifier = Modifier
                            .padding(dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
                        desc = stringResource(id = com.drp.refahland.R.string.no_bill_st),
                    )
                }
            if (uiState.billContacts.isSuccess() && uiState.billContacts.getSuccessData()
                    .isNotEmpty()
            )
                items(items = uiState.billContacts.getSuccessData()) { contactEntity ->
                    BillItemWidget(contactEntity = contactEntity) {
                        selectedContactEntityOptions = contactEntity
                    }
                }
            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

    BillOptionsBottomSheet(
        visibility = selectedContactEntityOptions != null,
        dismiss = { selectedContactEntityOptions = null },
        navigateToSeparatedBillScreen = {
            selectedContactEntityOptions?.let { selectedContact ->
                when (selectedContact.contactType) {
                    ContactType.TELEPHONE_NO.name -> navController.navigate(Screens.SeparatedBillInquiryScreen.route + "/${BillType.FIXEDLINE.name}?billId=${selectedContact.value}")
                    ContactType.GAS_BILL_ID.name -> navController.navigate(Screens.SeparatedBillInquiryScreen.route + "/${BillType.GAS.name}?billId=${selectedContact.value}")
                    ContactType.WATER_BILL_ID.name -> navController.navigate(Screens.SeparatedBillInquiryScreen.route + "/${BillType.WATER.name}?billId=${selectedContact.value}")
                    ContactType.ELECTRIC_BILL_ID.name -> navController.navigate(Screens.SeparatedBillInquiryScreen.route + "/${BillType.ELECTRICITY.name}?billId=${selectedContact.value}")
                }
            }
        },
        deleteContact = {
            selectedContactEntityOptions?.let { selectedContact ->
                viewModel?.deleteContact(selectedContact)
            }
        }
    )
}

@Composable
private fun BillOptionsBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    dismiss: () -> Unit,
    navigateToSeparatedBillScreen: () -> Unit,
    deleteContact: () -> Unit
) {
    val itemsList =
        arrayListOf(
            stringResource(id = com.drp.refahland.R.string.bill_inquiry_st),
            stringResource(id = com.drp.shared_ui.R.string.delete)
        )
    DynamicSheet(
        isVisible = visibility,
        onDismiss = dismiss,
        title = stringResource(id = R.string.details),
        icon = R.drawable.ic_bill_barcode,
    ) {
        LazyColumn {
            itemsIndexed(items = itemsList) { index, title ->
                Card(shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {
                        when (index) {
                            0 -> {
                                navigateToSeparatedBillScreen()
                            }

                            1 -> {
                                deleteContact()
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

@Composable
fun BillItemWidget(
    modifier: Modifier = Modifier,
    contactEntity: ContactEntity,
    onBillItemOptionsClicked: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding))
            .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding)),
        shape = RoundedCornerShape(size = dimensionResource(id = com.drp.shared_ui.R.dimen.large_corner)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_elevation))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = UiRes.dimen.medium_padding)
                )
                .padding(
                    vertical = dimensionResource(id = UiRes.dimen.medium_padding)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = UiRes.dimen.icon_size)),
                painter = painterResource(
                    id = findImageResources(
                        imageIcon = ContactType.entries.find { it.name == contactEntity.contactType }?.iconName
                    )
                ),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(id = UiRes.dimen.medium_padding))
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = contactEntity.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = stringResource(id = R.string.bill_id_title).plus(": ")
                        .plus(contactEntity.value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = {
                onBillItemOptionsClicked()
            }) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = com.drp.shared_ui.R.dimen.icon_size)),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BillScreenPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BillScreen(navController = rememberNavController())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BillItemWidgetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BillItemWidget(
                contactEntity = ContactEntity(
                    contactType = ContactType.BILL_ID.name,
                    title = ContactType.BILL_ID.savedContactTitle,
                    value = "45612345678"
                )
            ) {}
        }
    }
}