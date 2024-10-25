package com.drp.refahland.ui.main

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.card_facilities.R
import com.drp.shared_ui.theme.ApplicationTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.drp.shared_ui.R as UIResources

@JvmOverloads
@Composable
fun QrCodeDialog(
    modifier: Modifier = Modifier,
    walletId: String,
    closeAction: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(dimensionResource(id = UIResources.dimen.large_corner))
            )
            .padding(horizontal = dimensionResource(id = UIResources.dimen.medium_padding))
            .padding(bottom = dimensionResource(id = UIResources.dimen.extra_large_padding))
            .padding(top = dimensionResource(id = UIResources.dimen.small_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = closeAction) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = UIResources.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_close_fill),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = dimensionResource(id = UIResources.dimen.small_padding)
                    )
                    .align(Alignment.Bottom),
                text = stringResource(id = R.string.your_wallet_qr_code),
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
        Image(
            bitmap = generateQRCode(walletId).asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier.size(300.dp)
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = UIResources.dimen.small_padding)
                ),
            text = stringResource(id = R.string.wallet_id_st),
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = UIResources.dimen.small_padding)
                )
                .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
            text = walletId,
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun generateQRCode(content: String): Bitmap {
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }
    return bitmap
}

@Preview(showBackground = true)
@Composable
fun QrCodeDialogPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            QrCodeDialog(walletId = "456123", closeAction = {})
        }
    }
}
