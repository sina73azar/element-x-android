package com.drp.shared_ui.widget

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.shared_ui.R


@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun PasswordEditText(
    text: String,
    onTextChange: (text: String) -> Unit,
    modifier: Modifier = Modifier,
    layoutDirection: LayoutDirection = LayoutDirection.Rtl,
    label: String = stringResource(id = R.string.password),
    placeholder: String = stringResource(id = R.string.password),
    fontSize: TextUnit = 14.sp,
    cornerSize: Dp = 8.dp,
    maxLength: Int = 25,
    readOnly: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    infoVisibility: Boolean = false,
    infoAction: (() -> Unit)? = null,
    timerVisibility: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Password,
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {},
) {

    var passwordVisibility by remember {
        mutableStateOf(false)
    }
    val passwordPainter =
        if (passwordVisibility) painterResource(id = R.drawable.ic_visibility) else painterResource(
            id = R.drawable.ic_invisibility
        )
    val focusRequest = remember {
        FocusRequester()
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            /**
             * Outlined Text Field starts here
             */
            OutlinedTextField(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.8f)
                    .focusRequester(focusRequest),
                value = text,
                onValueChange = {
                    if (it.length <= maxLength) {
                        dumpErrorMessage()
                        onTextChange(it)
                    }
                },
                label = {
                    Text(
                        text = label,
                        textAlign = textAlign,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.7f)
                    )
                },
                placeholder = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.7f),
                        text = placeholder,
                        textAlign = textAlign,
                        fontSize = fontSize,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = textAlign),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                shape = RoundedCornerShape(cornerSize),
                singleLine = true,
                readOnly = readOnly,
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = passwordPainter,
                            contentDescription = stringResource(id = R.string.password),
                            tint = colorResource(id = R.color.colorPrimary)
                        )
                    }
                },
                isError = errorMessage.isNotEmpty(),
                supportingText = {
                    if (errorMessage.isNotEmpty())
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            modifier = Modifier.alpha(0.9f)
                        )
                },
            )
            /**
             * info icon starts here
             */
            if (infoVisibility) {
                IconButton(
                    onClick = {
                        infoAction?.invoke()
                        focusRequest.requestFocus()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_info),
                        contentDescription = "info icon",
                        tint = colorResource(
                            id = R.color.orange
                        )
                    )
                }
            }
            /**
             * timer button starts here
             */

        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
private fun Preview() {
    Column(modifier = Modifier.fillMaxWidth()) {
        /*   PasswordEditText(
               modifier = Modifier
                   .fillMaxWidth()
                   .height(72.dp)
                   .padding(horizontal = 16.dp)
           )*/
    }
}