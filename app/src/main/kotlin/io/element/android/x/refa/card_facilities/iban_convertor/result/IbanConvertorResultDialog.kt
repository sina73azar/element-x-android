package com.drp.card_facilities.presentation.iban_convertor.result

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.request.ImageRequest
import coil3.compose.AsyncImage
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R

@JvmOverloads
@Composable
fun IbanConvertorResultDialog(
    modifier: Modifier = Modifier,
    bankShowName: String?,
    colorCode: String?,
    resultTitle: String,
    result: String,
    imageUrl: String?,
    closeAction: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val successCopyStr = stringResource(id = R.string.success_copy)
    Column(
        modifier = modifier
                .fillMaxWidth()
                .background(
                        color = Color.White,
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                )
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .padding(bottom = dimensionResource(id = R.dimen.extra_large_padding))
                .padding(top = dimensionResource(id = R.dimen.small_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = closeAction) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_close_fill),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            imageUrl?.let {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    error = painterResource(id = R.drawable.ic_iban_convertor),
                    placeholder = painterResource(id = R.drawable.ic_iban_convertor),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.large_padding))
                            .size(36.dp)
                )
            } ?: run {
                Icon(
                    modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.medium_padding))
                            .size(48.dp),
                    painter = painterResource(id = R.drawable.ic_iban_convertor),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.size(48.dp))
        }
        bankShowName?.let { bsn ->
            Text(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                                top = dimensionResource(id = R.dimen.medium_padding),
                        )
                        .padding(
                                horizontal = dimensionResource(id = R.dimen.small_padding)
                        ),
                text = bsn,
                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorCode?.let { hexToColor(it) } ?: Color.DarkGray
            )
        }
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                            top = dimensionResource(id = R.dimen.medium_padding),
                    )
                    .padding(
                            horizontal = dimensionResource(id = R.dimen.small_padding)
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                clipboardManager.setText(AnnotatedString((result)))
                Toast.makeText(context, successCopyStr, Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = resultTitle,
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    color = Color.DarkGray,
                    lineHeight = 25.sp
                )
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    color = Color.DarkGray,
                    lineHeight = 25.sp
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun BalanceDialogPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
//            BalanceResultDialog(balance = 2L, closeAction = {})
        }
    }
}

fun hexToColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}

/*@Preview(showBackground = true)
@Composable
fun Preview() {
    BalanceResultDialog(settingAction = {}, closeAction = {})
}*/
