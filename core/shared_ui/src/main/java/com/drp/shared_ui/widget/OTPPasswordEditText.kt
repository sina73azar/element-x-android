package com.drp.shared_ui.widget

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.refah.ui.data.model.SMSState
import com.drp.shared_ui.R
import com.drp.shared_ui.UiText
import kotlinx.coroutines.delay
import java.util.StringTokenizer

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OTPPasswordEditTextCompose(
    modifier: Modifier = Modifier,
    textValue: String,
    onValueChange: (String) -> Unit,
    passwordType: SecondAuthenticationMethod,
    readOnly: Boolean,
    otpSmsRequest: (() -> Unit),
    smsState: SMSState,
    maxLength: Int,
    errorMessage: String = "",
    dumpErrorMessage: () -> Unit = {},
    timeDuration: Long? = 120000,
    btnLabel: String = stringResource(id = R.string.totp_title),
    showError: (UiText) -> Unit = {}
) {
    val keyBoardState = LocalSoftwareKeyboardController.current
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var loadingButtonClick by remember {
        mutableStateOf(false)
    }
    val showTimer = remember {
        mutableStateOf(false)
    }
    var failureDialogVisibility by remember {
        mutableStateOf(false)
    }

    GetSmsVerification(
        onValueChange = onValueChange,
        maxLength = maxLength,
        dumpErrorMessage = dumpErrorMessage
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary),

        ) {
        OutlinedTextField(
            modifier = modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(0.65f)
                .padding(
                    bottom = dimensionResource(
                        id = R.dimen.small_padding
                    ),
                    end = 4.dp,
                    start = 16.dp
                ),
            value = textValue,
            onValueChange = {
                if (it.length <= maxLength) {
                    dumpErrorMessage()
                    onValueChange(it)
                }
            },
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodySmall,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = {
                val lableText = when (passwordType) {
                    SecondAuthenticationMethod.STATIC_PASSWORD -> {
                        stringResource(id = R.string.static_pass)
                    }

                    SecondAuthenticationMethod.OTP -> {
                        stringResource(id = R.string.pass_creator)
                    }

                    SecondAuthenticationMethod.SMS -> {
                        stringResource(id = R.string.two_factor_sms_pass)
                    }

                    SecondAuthenticationMethod.DYNAMIC -> {
                        stringResource(id = R.string.dynamic_pass)
                    }

                    SecondAuthenticationMethod.LOCAL_SDK -> {
                        stringResource(id = R.string.dynamic_pass)
                    }

                    SecondAuthenticationMethod.HUB_OTP -> {

                        stringResource(id = R.string.two_factor_sms_pass)
                    }
                }
                Text(
                    text = lableText,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.alpha(0.7f)
                )
            },
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner)),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onAny = {
                    keyBoardState?.hide()
                }
            ),
            trailingIcon = {
                val icon = if (passwordVisible) {
                    R.drawable.ic_visibility
                } else
                    R.drawable.ic_invisibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = icon),
                        contentDescription = "ey icon",
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
            },
            singleLine = true,
            isError = errorMessage.isNotEmpty(),
            supportingText = {
                if (errorMessage.isNotEmpty())
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.9f)
                    )
            }
        )
        Box(
            modifier = modifier
                .weight(2f)
                .wrapContentWidth()
                .padding(top = dimensionResource(id =R.dimen.small_padding))
                .height(dimensionResource(id = R.dimen.app_bar_size)),
            contentAlignment = Alignment.Center
        ) {
            if (passwordType == SecondAuthenticationMethod.SMS) {
                if (showTimer.value) {
                    Log.d("timerrr", "No")
                    CounterDownTimer(timeDuration!!, showTimer)
                } else {
                    LoadingButton(
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .height(intrinsicSize = IntrinsicSize.Max)
                            .fillMaxWidth(0.75f),
                        onClick = {
                            otpSmsRequest()
//                            showTimer.value = true
                        },
                        btnLabel = btnLabel,
                        loading = loadingButtonClick,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)

                    )
                }
            }
        }
    }

    AnimatedVisibility(visible = failureDialogVisibility) {
        BasicAlertDialog(onDismissRequest = {
            failureDialogVisibility = false
        }) {
            NetworkErrorDialogContent(
                closeAction = {
                    failureDialogVisibility = false
                })
        }
    }
    LaunchedEffect(key1 = smsState) {
        if (smsState.SMSStateLoading) {
            loadingButtonClick = true
            showTimer.value = false
        }
        if (smsState.SMSStateInquiryFail) {
            loadingButtonClick = false
            showTimer.value = false
            failureDialogVisibility = true
        }
        if (smsState.SMSStateSuccess) {
            loadingButtonClick = false
            showTimer.value = true
        }

    }
    LaunchedEffect(key1 = smsState.SMSStateInquiryErrorMessage, block = {
        smsState.SMSStateInquiryErrorMessage?.let {
            if (smsState.SMSStateInquiryErrorMessage?.isNotEmpty() == true) {
                showError(UiText.DynamicString(it))
                loadingButtonClick = false
                showTimer.value = false
            }

        }
    })


}

@Composable
fun CounterDownTimer(
    time: Long,
    showTimer: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    var timer by remember { mutableLongStateOf(time) }
    LaunchedEffect(key1 = timer, block = {
        if (timer > 0) {
            delay(1000L)
            timer -= 1000L
        } else {
            showTimer.value = false
        }
    })
    val secMilSec: Long = 1000
    val minMilSec = 60 * secMilSec
    val hourMilSec = 60 * minMilSec
    val dayMilSec = 24 * hourMilSec
    val minutes = (timer % dayMilSec % hourMilSec / minMilSec).toInt()
    val seconds = (timer % dayMilSec % hourMilSec % minMilSec / secMilSec).toInt()
    Row(
        modifier = modifier
            .padding(start = dimensionResource(id = R.dimen.small_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = modifier
                .padding(end = 4.dp)
                .wrapContentWidth(),
            text = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Gray
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_clock),
            contentDescription = "clock icon",
            tint = Color.Gray
        )
    }

}

@Preview
@Composable
fun PasswordEditTextWidgetPreview() {
    val smsState by remember {
        mutableStateOf(SMSState())
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OTPPasswordEditTextCompose(
            passwordType = SecondAuthenticationMethod.SMS,
            readOnly = false,
            otpSmsRequest = {
            }, smsState = smsState,
            textValue = "",
            onValueChange = {},
            maxLength = 5
        )
    }
}

@Composable
private fun GetSmsVerification(
    onValueChange: (String) -> Unit,
    dumpErrorMessage: () -> Unit = {},
    maxLength: Int
) {
    val context = LocalContext.current
    lateinit var updateUIReceiver: BroadcastReceiver
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        DisposableEffect(key1 = context) {
            val filter = IntentFilter()
            filter.addAction("service.to.activity.transfer")
            updateUIReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    //UI update here
                    if (intent != null) {
                        val messageTxt = intent.getStringExtra("message").toString()
                        if (messageTxt.isNotEmpty()) {
                            val tokenizer = StringTokenizer(messageTxt, "\r\n")
                            while (tokenizer.hasMoreTokens()) {
                                val nextLine = tokenizer.nextToken()
                                if (nextLine!!.contains("رمز")) {
                                    nextLine.substringAfter("رمز").filter { it.isDigit() }.let {
                                        if (it.length <= maxLength) {
                                            dumpErrorMessage()
                                            onValueChange(it)
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null,
                    Context.RECEIVER_EXPORTED
                )
            } else
                context.registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null
                )

            onDispose {
                context.unregisterReceiver(updateUIReceiver)
            }
        }

    }
}