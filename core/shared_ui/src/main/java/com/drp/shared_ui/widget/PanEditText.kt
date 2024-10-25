package com.drp.shared_ui.widget

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.shared_ui.R
import com.drp.shared_ui.enums.Banks
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.utils.panFormatter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PanEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    spinnerListContact: List<String> = emptyList(),
    onDismissSpinner: (() -> Unit)? = null,
    onSpinnerDropDownClick: ((String) -> Unit)? = null,
    contactSheetList: List<SearchSheetItemModel>? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    label: String = stringResource(id = R.string.transfer_card_destination),
    placeHolder: String = stringResource(id = R.string.transfer_card_destination),
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {}
) {

    var localContactSheetVisibility by remember {
        mutableStateOf(false)
    }

    var bankIconName by remember {
        mutableStateOf("")
    }

    var bankName by remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        dumpErrorMessage()
        onValueChange.invoke(value)
        onDismissSpinner?.invoke()
    }

    LaunchedEffect(key1 = value) {
        bankIconName = if (value.length >= 6)
            Banks.entries.filter {
                it.cardNumberPrefix == value.substring(0, 6)
            }.let { enumList ->
                if (enumList.isNotEmpty()) {
                    enumList[0].bankIcon
                } else {
                    ""
                }
            }
        else
            ""
    }

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (contactSheetList != null) 0.8f else 1f),
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                expanded = spinnerListContact.isNotEmpty(),
                onExpandedChange = {
                    onDismissSpinner?.invoke()
                }
            ) {
                OutlinedTextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = value,
                    onValueChange = { value ->
                        if ((value.length > 16)) {
                            return@OutlinedTextField
                        }
                        dumpErrorMessage()
                        onValueChange.invoke(value)
                    },
                    textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.End),
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.alpha(0.7f)
                        )
                    },
                    placeholder = {
                        Text(
                            text = placeHolder,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.alpha(0.7f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(8.dp),

                    isError = errorMessage.isNotEmpty(),
                    supportingText = {
                        if (errorMessage.isNotEmpty())
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                modifier = Modifier.alpha(0.9f)
                            )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            keyboardController?.hide()
                        }
                    ),
                    leadingIcon = if (value.isNotEmpty()) {
                        @Composable {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (bankIconName.isNotEmpty() && value.length >= 6)
                                    IconButton(onClick = {}) {
                                        Icon(
                                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                            painter = painterResource(
                                                id = findImageResources(
                                                    bankIconName
                                                )
                                            ),
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                    }
                                if (value.isNotEmpty())
                                    IconButton(
                                        onClick = {
                                            dumpErrorMessage()
                                            onValueChange.invoke("")
                                            onDismissSpinner?.invoke()
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
                        }
                    } else null,
                    visualTransformation = PanVisualTransformation(),
                    maxLines = 1,
                    trailingIcon = if (contactSheetList?.isNotEmpty() == true) {
                        @Composable {
                            IconButton(onClick = {
                                localContactSheetVisibility = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_contact_book),
                                    contentDescription = "Contact Icon",
                                    tint = colorResource(
                                        id = R.color.colorAccent
                                    )
                                )
                            }
                        }
                    } else {
                        null
                    },
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
                            )
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
                                    onValueChange.invoke(it)
                                    dumpErrorMessage()
                                    onDismissSpinner?.invoke()
                                },
                            )

                        }

                    }

            }
        }
    }

    if (!contactSheetList.isNullOrEmpty()) {
        SearchSheet(
            sheetVisible = localContactSheetVisibility,
            onDismiss = { localContactSheetVisibility = false },
            items = contactSheetList,
            isGrid = false,
            onItemClick = {
                localContactSheetVisibility = false
                onValueChange.invoke(it.value)
                dumpErrorMessage()
                onDismissSpinner?.invoke()
            }
        )
    }

}

class PanVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        val formattedText =
            if (isNumber(originalText)) panFormatter(text.text) else originalText

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                return if (isNumber(originalText))
                    when {
                        offset <= 4 -> offset // No dash before the first 4 digits
                        offset <= 8 -> offset + 1 // One dash after 4th digit
                        offset <= 12 -> offset + 2 // Two dashes after 8th digit
                        offset <= 16 -> offset + 3 // Three dashes after 12th digit
                        else -> offset + 3 // Beyond 11 digits
                    }
                else
                    offset
            }

            override fun transformedToOriginal(offset: Int): Int {
                return if (isNumber(originalText))
                    when {
                        offset <= 4 -> offset
                        offset <= 9 -> offset - 1 // First dash is between 4th and 5th digits
                        offset <= 13 -> offset - 2 // Second dash is between 8th and 9th digits
                        offset <= 17 -> offset - 3 // Second dash is between 12th and 13th digits
                        else -> offset - 3 // Beyond the third dash
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