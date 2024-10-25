package com.drp.shared_ui.widget

import android.app.Activity
import android.content.Intent
import android.os.Build
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.refah.ui.data.model.ContactItem
import com.drp.shared_ui.R
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.utils.RESULT
import com.drp.utils.phoneNumberFromatter
import kotlinx.coroutines.launch


/**
 * @author Mr.C 1402/08/27
 * handle formatting and unformatting properly with lambda
 * @param simIconClick: pass null if you have no simCard icon , if you need simIcon then your
 * lambda should return phoneNumber as string if somehow return null pass snackbar to show right
 * message
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PhoneAutoEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    spinnerListContact: List<String> = emptyList(),
    onDismissSpinner: (() -> Unit)? = null,
    onSpinnerDropDownClick: ((String) -> Unit)? = null,
    label: String? = null,
    placeHolder: String = "",
    readOnly: Boolean = false,
    simIconClick: (() -> String?)? = null,
    contactSheetList: List<SearchSheetItemModel>? = null,
    phoneContactIntent: Intent = Intent(),
    snackbarHostState: SnackbarHostState? = null,
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {}
) {
    var localContactSheetVisibility by remember {
        mutableStateOf(false)
    }
    var selectionContactSheetVisibility by remember {
        mutableStateOf(false)
    }
    /*var textFieldValuePhone by remember {
        mutableStateOf(TextFieldValue(value))
    }*/
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            /*val formatted = phoneNumberFromatter(contactItem.mobileNo)
            textFieldValuePhone = textFieldValuePhone.copy(
                text = formatted,
                selection = TextRange(formatted.length)
            )*/
            dumpErrorMessage.invoke()

            onValueChange(contactItem.mobileNo)
        }
        // Handle the data accordingly
    }
    LaunchedEffect(key1 = true, block = {
        onValueChange(value)
        //save unformatted text value in viewModel state
        dumpErrorMessage()
        onDismissSpinner?.invoke()
    })

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded = true)
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                top = 0.dp,
                start = dimensionResource(id = R.dimen.large_padding),
                end = dimensionResource(id = R.dimen.large_padding),
                bottom = 0.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Start root
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (simIconClick != null && contactSheetList != null) 0.8f else 1f),
        ) {

            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                expanded = spinnerListContact.isNotEmpty(),
                onExpandedChange = {
                    onDismissSpinner?.invoke()
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = value,

                    onValueChange = { value ->
                        if ((value/*.text*/.length > 11)) {
                            return@OutlinedTextField
                        }
                        /*textFieldValuePhone = value.copy(
                            text = value.text,
                            selection = TextRange(value.text.length)
                        )*/
//                        var formatted = value.text
//                        if (value.text.isDigitsOnly() || value.text.contains("-")) {
//
////                            formatted = phoneNumberFromatter(value.text)
//                            textFieldValuePhone =
//                                value.copy(
//                                    text = phoneNumberFromatter(value.text),
//                                    selection = TextRange(formatted.length)
//                                )
//                        } else {
//                            textFieldValuePhone = value.copy(
//                                text = value.text
//                            )
//                        }
                        //save unformatted text value in viewModel state
                        dumpErrorMessage()
                        onValueChange.invoke(value/*.text*/)
                    },
                    readOnly = readOnly,
                    textStyle = MaterialTheme.typography.bodySmall,
                    label = {

                        Text(
                            text = label ?: stringResource(id = R.string.mobile_no),
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
                    visualTransformation = PhoneVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            keyboardController?.hide()
                        }
                    ),

                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (value.isNotEmpty())
                                IconButton(
                                    onClick = {
//                                        textFieldValuePhone = textFieldValuePhone.copy(text = "")
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
                            if (simIconClick != null) {

                                val msg = stringResource(id = R.string.infos_not_fetched_try_again)
                                IconButton(
                                    onClick = {
                                        val phone = simIconClick?.invoke()
                                        if (phone.isNullOrEmpty()) {
                                            scope.launch {
                                                snackbarHostState?.showSnackbar(msg)
                                            }
                                            return@IconButton
                                        }
                                        /*val formatted = phoneNumberFromatter(phone)
                                        textFieldValuePhone = textFieldValuePhone.copy(
                                            text = formatted,
                                            selection = TextRange(formatted.length)
                                        )*/
                                        onValueChange(phone)

                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_sim_card),
                                        contentDescription = "Sim Icon",
                                        tint = colorResource(
                                            id = R.color.colorAccent
                                        )
                                    )
                                }
                            }

//                            if (contactSheetList != null) {
                            IconButton(
                                onClick = {
                                    if (contactSheetList?.isNotEmpty() == true) {
                                        selectionContactSheetVisibility = true
                                    } else {
                                        launcher.launch(phoneContactIntent)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_contact_book),
                                    contentDescription = "Contact Icon",
                                    tint = colorResource(
                                        id = R.color.colorAccent
                                    )
                                )
//                                }
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


                if (Build.VERSION.SDK_INT >= 26)
                    ExposedDropdownMenu(
                        expanded = spinnerListContact.isNotEmpty(),
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
                        spinnerListContact.forEach {
                            DropdownMenuItem(
                                modifier = Modifier,
                                text = {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                onClick = {
                                    /*val formatted = phoneNumberFromatter(it)
                                    textFieldValuePhone = textFieldValuePhone.copy(
                                        text = formatted,
                                        selection = TextRange(formatted.length)
                                    )*/
                                    onValueChange.invoke(it)
                                    dumpErrorMessage()
//                                onSpinnerDropDownClick?.invoke(it)
                                    onDismissSpinner?.invoke()
                                },
                            )

                        }

                    }


            }

        }

    }


    /* Contact Sheet */

    if (!contactSheetList.isNullOrEmpty()) {
        SearchSheet(
            sheetVisible = localContactSheetVisibility,
            onDismiss = { localContactSheetVisibility = false },
            items = contactSheetList,
            isGrid = false,
            onItemClick = {
                localContactSheetVisibility = false
                /*val formatted = phoneNumberFromatter(it.value)
                textFieldValuePhone = textFieldValuePhone.copy(
                    text = formatted,
                    selection = TextRange(formatted.length)
                )*/
                onValueChange.invoke(it.value)
                dumpErrorMessage()
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
                                localContactSheetVisibility = true
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

class PhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        val formattedText =
            if (isNumber(originalText)) phoneNumberFromatter(text.text) else originalText

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
//                val dash = formattedText.count { it == '-' }
                return if (isNumber(originalText))
                    when {
                        offset <= 4 -> offset // No dash before the first 4 digits
                        offset <= 7 -> offset + 1 // One dash after 4th digit
                        offset <= 11 -> offset + 2 // Two dashes after 7th digit
                        else -> offset + 2 // Beyond 11 digits
                    }
                else
                    offset


            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (isNumber(originalText))
                    when {
                        offset <= 4 -> offset
                        offset <= 8 -> offset - 1 // First dash is between 4th and 5th digits
                        offset <= 12 -> offset - 2 // Second dash is between 7th and 8th digits
                        else -> offset - 2 // Beyond the second dash
                    }
                else
                    offset
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}

fun isNumber(input: String): Boolean {
    val numericPattern = "^[0-9]+$" // Example for integers
    return input.matches(numericPattern.toRegex())
}

@Preview
@Composable
fun PhoneAutoEditTextPreview() {
    var value by remember {
        mutableStateOf("")
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        PhoneAutoEditText(
            value = "",
            onValueChange = { curValue ->
                value = curValue
            }
        )
    }
}