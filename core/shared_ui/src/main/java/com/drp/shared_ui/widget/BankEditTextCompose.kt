package com.drp.shared_ui.widget

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.refah.ui.data.model.ContactItem
import com.drp.shared_ui.R
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.utils.RESULT

@Preview
@Composable
fun BankEditTextPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        BankEditTextExposed(
            label = "sad",
            value = "",
            contactIcon = R.drawable.ic_contact,
            onValueChange = {},
            onDropDownClick = {},
            onDismissSpinner = {},
            maxLength = 5
        )
    }
}


/**
 * @author Mr.C 1402/08/21
 *
 * @param value hoisted outside of function
 * @param expanded state of search spinner will change with spinnerList empty or not state
 * */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BankEditTextExposed(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    spinnerList: List<String> = emptyList(),
    onDismissSpinner: (() -> Unit)? = null,
    readOnly: Boolean = false,
    label: String,
    placeHolder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    forceContactIconVisibilityToShowPhoneContactActivity: Boolean = false,
    phoneContactIntent: Intent = Intent(),
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {},
    contactList: List<SearchSheetItemModel>? = null,
    contactIcon: Int? = null,
    onDropDownClick: ((String) -> Unit)? = null,
    scannerIconVisibility: Boolean = false,
    scanIconClick: () -> Unit = {}
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var sheetVisible by remember {
        mutableStateOf(false)
    }
    var selectionContactSheetVisibility by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result of the activity here
        // For example, you can retrieve data from the activity result
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && result.data?.getSerializableExtra(
                RESULT
            ) is ContactItem
        ) {
            val contactItem: ContactItem =
                result.data?.getSerializableExtra(RESULT) as ContactItem
            if (contactItem.mobileNo.length <= maxLength) {
                dumpErrorMessage()
                onValueChange.invoke(contactItem.mobileNo)
            }
        }
        // Handle the data accordingly
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                top = 0.dp,
                start = dimensionResource(id = R.dimen.large_padding),
                end = dimensionResource(id = R.dimen.large_padding),
                bottom = 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (contactIcon != null) 0.9f else 1f),
        ) {
            /**
             *  Mr.C 1402/8/23
             *  padding for ExposedDropdownMenuBox is necessary for spinner match width with
             *  TextField
             */
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                expanded = spinnerList.isNotEmpty(),
                onExpandedChange = {
                    onDismissSpinner?.invoke()
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = value,
                    onValueChange = {
                        if (it.length <= maxLength) {
                            dumpErrorMessage()
                            onValueChange.invoke(it)
                        }
                    },
                    readOnly = readOnly,
                    textStyle = MaterialTheme.typography.bodySmall,
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.alpha(0.7f)
                        )
                    },
                    placeholder = {
                        Text(
                            text = placeHolder,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.alpha(0.7f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    ),

                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            keyboardController?.hide()
                        }
                    ),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (value.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        dumpErrorMessage()
                                        onValueChange.invoke("")
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_fill),
                                        contentDescription = "close Icon",
                                        tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            if (scannerIconVisibility)
                                IconButton(
                                    onClick = {
                                        scanIconClick()
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_qr_code),
                                        contentDescription = "qr code Icon",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            /**
                             * Contact icon
                             * */
                            if (contactList?.isNotEmpty() == true || forceContactIconVisibilityToShowPhoneContactActivity) {
                                IconButton(
                                    onClick = {
                                        if (forceContactIconVisibilityToShowPhoneContactActivity) {
                                            if (contactList?.isNotEmpty() == true) {
                                                selectionContactSheetVisibility = true
                                            } else {
                                                launcher.launch(phoneContactIntent)
                                            }
                                        } else {
                                            sheetVisible = true
                                        }
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                        painter = painterResource(id = R.drawable.ic_contact_book),
                                        contentDescription = "Contact Icon",
                                        tint = colorResource(
                                            id = R.color.colorAccent
                                        )
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true,
                    isError = errorMessage.isNotEmpty(),
                    supportingText = {
                        if (errorMessage.isNotEmpty())
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                modifier = Modifier.alpha(0.9f)
                            )
                    }
                )

                ExposedDropdownMenu(
                    expanded = spinnerList.isNotEmpty(),
                    onDismissRequest = {
                        onDismissSpinner?.invoke()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .exposedDropdownSize(true)
                        .background(
                            MaterialTheme.colorScheme.onPrimary
                        ),

                    ) {
                    spinnerList.forEach {
                        DropdownMenuItem(
                            modifier = Modifier,
                            text = { Text(text = it, style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                onDropDownClick?.invoke(it)
                                onDismissSpinner?.invoke()
                            },
                        )
                    }
                }
            }
        }
    }


    /* Contact Sheet */

    if (!contactList.isNullOrEmpty()) {
        SearchSheet(
            isGrid = false,
            sheetVisible = sheetVisible,
            onDismiss = { sheetVisible = false },
            items = contactList,
            onItemClick = {
                sheetVisible = false
                onValueChange.invoke(it.value)
                onDismissSpinner?.invoke()
            }
        )

    }

    val contactListType = listOf(
        stringResource(id = R.string.contact_phone_item),
        stringResource(id = R.string.contact_hamrah_card_item)
    )
    DynamicSheet(
        isVisible = selectionContactSheetVisibility,
        onDismiss = { selectionContactSheetVisibility = false },
        title = stringResource(id = R.string.type_contact_title),
        icon = R.drawable.ic_contact_book
    ) {
        contactListType.forEachIndexed { index, contactType ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (index == 0) {
                                launcher.launch(phoneContactIntent)
                            } else {
                                sheetVisible = true
                            }
                            selectionContactSheetVisibility = false
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(
                            vertical = dimensionResource(id = R.dimen.large_padding),
                            horizontal = dimensionResource(
                                id = R.dimen.medium_padding
                            )
                        ),
                        text = contactType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (index == 0)
                    CustomLine(
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.medium_padding)),
                        dashed = true,
                        color = MaterialTheme.colorScheme.primary
                    )
            }
        }
    }


}