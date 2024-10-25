@file:OptIn(ExperimentalMaterial3Api::class)

package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.shared_ui.R
import com.drp.utils.currencyFormatter
import ir.yamin.digits.Digits
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CardFacilityAmountEditText(
    modifier: Modifier = Modifier,
    value: Long,
    infoIcVisibility: Boolean = false,
    onValueChange: ((Long) -> Unit),
    label: String? = null,
    placeHolder: String = "",
    maxLength: Int = 25,
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(if (value == 0L) "" else currencyFormatter(value)))
    }

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) {
                    keyboardController?.show()
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded = true)
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                top = 0.dp,
                start = dimensionResource(id = R.dimen.extra_large_padding),
                end = dimensionResource(id = R.dimen.extra_large_padding),
                bottom = 0.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = textFieldValue,
                singleLine = true,
                onValueChange = { value ->
                    if (value.text.length <= maxLength) {
                        if (value.text.isNotEmpty()) {
                            try {
                                val formatted =
                                    currencyFormatter(value.text.filter { it.isDigit() }
                                        .toLong())
                                textFieldValue =
                                    textFieldValue.copy(
                                        text = formatted,
                                        selection = TextRange(formatted.length)
                                    )
                                onValueChange.invoke(value.text.filter { it.isDigit() }
                                    .toLong())
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }
                        } else {
                            textFieldValue = value
                            onValueChange.invoke(0L)
                        }
                    }
                    dumpErrorMessage()
                },
                textStyle = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.Ltr),
                leadingIcon = if (textFieldValue.text.isNotEmpty()) {
                    @Composable {
                        IconButton(
                            onClick = {
                                textFieldValue = TextFieldValue("")
                                dumpErrorMessage()
                                onValueChange.invoke(0L)
                            },
                        ) {
                            Icon(
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_fill),
                                contentDescription = "close Icon",
                                tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                            )
                        }
                    }
                } else null,
                label = {

                    Text(
                        text = label ?: stringResource(id = R.string.amount),
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    Text(
                        text = stringResource(id = R.string.irr_currency),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = dimensionResource(
                                id = R.dimen.ui_text_small
                            ).value.sp
                        ),
                        modifier = Modifier.alpha(0.8f)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                interactionSource = interactionSource,
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
            /*if (infoIcVisibility) {
                IconButton(modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp), onClick = {
                    Toast.makeText(context, "icon clicked", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_circle_info),
                        contentDescription = "sda",
                        tint = colorResource(id = R.color.orange)
                    )
                }
            }*/

        }
        if (textFieldValue.text.isNotEmpty())
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
                text = if (textFieldValue.text.isNotEmpty()) toNumeric(txtVal = textFieldValue.text) else "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall.copy(fontSize = dimensionResource(id = R.dimen.ui_text_nano).value.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
    }

}

@Composable
private fun toNumeric(txtVal: String): String {
    val amount = txtVal.filter { it.isDigit() }.toLong()
    var price = ""
    if (amount / 10 > 0)
        price = Digits().spellToFarsi(amount / 10).plus(" ")
            .plus(stringResource(R.string.toman_currency)).plus(" ")
    if (amount % 10 > 0) {
        if (price.isNotEmpty())
            price += "و".plus(" ")
        price += Digits().spellToFarsi((amount % 10)).plus(" ")
            .plus(stringResource(id = R.string.irr_currency))
    }
    return price
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AmountEditTextPrev() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        CardFacilityAmountEditText(value = 0L, onValueChange = {})
    }
}