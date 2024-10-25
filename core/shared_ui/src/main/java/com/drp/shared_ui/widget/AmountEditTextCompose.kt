@file:OptIn(ExperimentalMaterial3Api::class)

package com.drp.shared_ui.widget

import android.widget.Toast
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.colorResource
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
fun AmountEditText(
    modifier: Modifier = Modifier,
    infoIcVisibility: Boolean = false,
    onValueChange: ((Long?) -> Unit),
    maxLength: Int = 25,
    isError: Boolean = false
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var txtValue by remember {
        mutableStateOf(TextFieldValue(""))
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
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = modifier
                    .heightIn(max = dimensionResource(id = com.intuit.sdp.R.dimen._90sdp))
                    .weight(6f),
                value = txtValue,
                singleLine = true,
                isError = isError,
                onValueChange = { changeText ->
                    if (changeText.text.length <= maxLength)
                        if (changeText.text.isNotEmpty()) {
                            try {
                                val formatted =
                                    currencyFormatter(changeText.text.filter { it.isDigit() }
                                        .toLong())
                                txtValue =
                                    changeText.copy(
                                        text = formatted,
                                        selection = TextRange(formatted.length)
                                    )
                                onValueChange.invoke(changeText.text.filter { it.isDigit() }
                                    .toLong())
                            } catch (e: NumberFormatException) {
                                e.printStackTrace()
                            }

                        } else {
                            txtValue = changeText
                            onValueChange.invoke(null)

                        }
                },
                textStyle = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.Ltr),

                prefix = {
                    if (txtValue.text.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.icon_size))
                                .padding(0.dp),
                            onClick = {
                                txtValue = TextFieldValue("")
                                onValueChange?.invoke(null)
                            },
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_fill),
                                contentDescription = "close Icon",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(
                                    dimensionResource(id = R.dimen.icon_size)
                                )
                            )
                        }
                    } else {

                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    /*platformImeOptions = PlatformImeOptions(
                        imeAction = ImeAction.Done
                    )*/
                ),
                keyboardActions = KeyboardActions {
                    keyboardController?.hide()
                }, trailingIcon = {
                    Text(
                        text = stringResource(id = R.string.irr_currency),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = dimensionResource(
                                id = R.dimen.ui_text_small
                            ).value.sp
                        )
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.amount),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = dimensionResource(
                                id = R.dimen.ui_text_small
                            ).value.sp
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.alpha(0.7f)
                    )
                },
                shape = RoundedCornerShape(8.dp),
                interactionSource = interactionSource
            )
            if (infoIcVisibility) {
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
            }

        }
        if (txtValue.text.isNotEmpty())
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                text = if (txtValue.text.isNotEmpty()) toNumeric(txtVal = txtValue.text) else "",
                color = colorResource(id = R.color.colorPrimary),
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

//        AmountEditText()
    }
}