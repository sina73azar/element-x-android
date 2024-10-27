/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package ui.main

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.drp.data.database.entity.ContactEntity
import com.drp.data.enums.BillType
import com.drp.data.enums.ContactType
import com.drp.refah.card_facilities.utility.BillUtils
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.refahland.ui.main.MainScreenState
import com.drp.refahland.ui.main.MainViewModel
import com.drp.shared_ui.naviagtion.Screens
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.AnimatedShimmer
import com.drp.shared_ui.widget.BankEditTextExposed
import com.drp.shared_ui.widget.CardShimmerItem
import com.drp.shared_ui.widget.CustomLine
import com.drp.shared_ui.widget.CustomSingleSelectionToggle
import com.drp.shared_ui.widget.CustomTopAppBar
import com.drp.shared_ui.widget.DynamicSheet
import com.drp.shared_ui.widget.EmptyOrFailContent
import com.drp.shared_ui.widget.LoadingButton
import com.drp.shared_ui.widget.ShimmerEffect
import com.drp.shared_ui.widget.findImageResources
import io.element.android.x.R
import io.element.android.x.refa.enums.UiText

@Composable
fun BillScreen(
    navController: NavController,
    viewModel: MainViewModel? = null
) {
    val uiState = viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: MainScreenState()

    var selectedContactEntityOptions by remember {
        mutableStateOf<ContactEntity?>(null)
    }
    var addBillBottomSheetVisibility by remember {
        mutableStateOf(false)
    }
    BackHandler {
        viewModel?.setSelectedBottomBarId(3)
        navController.popBackStack(
            route = Screens.HomeScreen.route,
            inclusive = false
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CustomTopAppBar(
            headerTxt = stringResource(id = R.string.bills_st),
            backBtnVisible = false
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
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
                                .padding(dimensionResource(id = R.dimen.large_padding)),
                            desc = stringResource(id = R.string.no_bill_st),
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
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = 96.dp, start = dimensionResource(
                        id = R.dimen.large_padding
                    )
                    ),
                onClick = {
                    addBillBottomSheetVisibility = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = dimensionResource(
                        id = R.dimen.medium_elevation
                    )
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
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

    AddBillBottomSheet(
        visibility = addBillBottomSheetVisibility,
        addContact = { contactType, billId ->
            viewModel?.addBillContact(contactType, billId)
        },
        billContactList = if (uiState.billContacts.isSuccess()) uiState.billContacts.getSuccessData() else emptyList(),
        dismiss = { addBillBottomSheetVisibility = false }
    )
}

@Composable
private fun AddBillBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    addContact: (contactType: ContactType, billId: String) -> Unit,
    billContactList: List<ContactEntity> = emptyList(),
    dismiss: () -> Unit,
) {
    val toggleModel: List<CustomToggleModel> = listOf(
        CustomToggleModel(
            0, stringResource(id = R.string.bill_water)
        ),
        CustomToggleModel(
            1, stringResource(id = R.string.bill_gas)
        ),
        CustomToggleModel(
            2, stringResource(id = R.string.bill_elec)
        )
    )
    var selectedToggle by remember {
        mutableStateOf(toggleModel[0])
    }
    var billId by remember {
        mutableStateOf("")
    }
    var billIdValidationMessage by remember {
        mutableStateOf<UiText>(UiText.DynamicString(""))
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        isVisible = visibility,
        onDismiss = {
            dismiss()
            billId = ""
            billIdValidationMessage = UiText.DynamicString("")
        },
        title = stringResource(id = R.string.add_bill_st),
        icon = R.drawable.ic_bill_barcode,
    ) {
        CustomSingleSelectionToggle(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.large_padding)
                )
                .padding(top = dimensionResource(id = R.dimen.large_padding)),
            data = toggleModel
        ) { toggleModel ->
            selectedToggle = toggleModel
        }
        BankEditTextExposed(
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.large_padding)),
            value = billId,
            onValueChange = {
                billId = it
            },
            label = stringResource(id = R.string.bill_id_title),
            placeHolder = stringResource(id = R.string.bill_id_title),
            errorMessage = billIdValidationMessage.asString(),
            dumpErrorMessage = {
                billIdValidationMessage = UiText.DynamicString("")
            },
            maxLength = 20
        )
        LoadingButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = dimensionResource(id = R.dimen.large_padding)
                ),
            loading = false,
            paddingHorizontal = dimensionResource(id = R.dimen.large_padding),
            onClick = {
                if (!isValidBillId(billId)) {
                    billIdValidationMessage =
                        UiText.StringResource(R.string.data_validation_bill_id)
                    return@LoadingButton
                }
                if (billContactList.find { it.value == billId } != null) {
                    billIdValidationMessage =
                        UiText.StringResource(R.string.bill_already_add_st)
                    return@LoadingButton
                }
                keyboardController?.hide()
                addContact(
                    when (selectedToggle.id) {
                        0 -> ContactType.WATER_BILL_ID
                        1 -> ContactType.GAS_BILL_ID
                        else -> ContactType.ELECTRIC_BILL_ID
                    },
                    billId
                )
                dismiss()
                billId = ""
                billIdValidationMessage = UiText.DynamicString("")
            },
            btnLabel = stringResource(id = R.string.add_bill_st)
        )
    }
}

private fun isValidBillId(billId: String): Boolean {
    val billUtils = BillUtils()
    return billUtils.validateBillId(billId)
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
            stringResource(id = R.string.bill_inquiry_st),
            stringResource(id = R.string.delete)
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
                            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                            .padding(
                                vertical = dimensionResource(
                                    id = R.dimen.medium_padding
                                )
                            ),
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (index != itemsList.lastIndex) CustomLine(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.medium_padding)
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
            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
            .padding(top = dimensionResource(id = R.dimen.large_padding)),
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.large_corner)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.medium_elevation))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.medium_padding)
                )
                .padding(
                    vertical = dimensionResource(id = R.dimen.medium_padding)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
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
                    .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
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
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
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
