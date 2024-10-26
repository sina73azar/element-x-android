package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.element.android.x.R
import com.drp.shared_ui.theme.ApplicationTheme

@Composable
fun MotorLicencePlateWidget(
    modifier: Modifier = Modifier,
    rightValue: String,
    onRightValueChange: (String) -> Unit,
    rightError: Boolean,
    dumpRightError: () -> Unit,
    leftValue: String,
    onLeftValueChange: (String) -> Unit,
    leftError: Boolean,
    dumpLeftError: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(
                horizontal = dimensionResource(id = R.dimen.extra_large_padding),
            ),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(5f),
            value = rightValue,
            onValueChange = {
                if (it.length <= 5) {
                    dumpRightError()
                    onRightValueChange(it)
                    if (it.length == 5) {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                }
            },
            placeholder = {
                Text(
                    text = "- - - - -",
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.7f).fillMaxWidth()
                )
            },
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
                textDirection = TextDirection.Ltr
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            isError = rightError,
        )
        OutlinedTextField(
            modifier = Modifier
                .weight(3f)
                .padding(top = 4.dp, start = 4.dp),
            value = leftValue,
            onValueChange = {
                if (it.length <= 3) {
                    dumpLeftError()
                    onLeftValueChange(it)
                    if (it.length == 3) {
                        focusManager.moveFocus(FocusDirection.Previous)
                    }
                }
            },
            placeholder = {
                Text(
                    text = "- - -",
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.7f).fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(0.dp),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
                textDirection = TextDirection.Ltr
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            isError = leftError,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .padding(top = 4.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_iran_flag_dark),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LicencePlateWidgetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            MotorLicencePlateWidget(
                rightValue = "",
                onRightValueChange = {},
                rightError = false,
                dumpRightError = {},
                leftValue = "",
                onLeftValueChange = {},
                leftError = false,
                dumpLeftError = {}
            )
        }
    }
}
