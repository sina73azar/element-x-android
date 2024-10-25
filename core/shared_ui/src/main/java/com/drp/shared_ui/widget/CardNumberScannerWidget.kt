package com.drp.shared_ui.widget

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.drp.refah.ui.theme.Irancell_Yellow
import com.drp.shared_ui.R
import com.drp.shared_ui.theme.ApplicationTheme
import ir.arefdev.irdebitcardscanner.ScanActivity
import ir.arefdev.irdebitcardscanner.ScanActivityImpl

@Composable
fun CardNumberScanner(modifier: Modifier = Modifier, exposeScannedCardNumber: (String) -> Unit) {
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val scanResult = ScanActivity.debitCardFromResult(result.data)
            scanResult?.let { debitCard ->
                debitCard.number?.let { number ->
                    exposeScannedCardNumber(number)
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            activityResultLauncher.launch(Intent(activity, ScanActivityImpl::class.java))
        } else {
            ActivityResultContracts.RequestPermission()
        }
    }

    fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            activityResultLauncher.launch(Intent(activity, ScanActivityImpl::class.java))
        }
    }

    ElevatedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding)),
        onClick = {
            checkAndRequestPermission()
        },
        shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.medium_corner)),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Irancell_Yellow
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(36.dp)
                    .padding(
                        dimensionResource(id = R.dimen.small_padding)
                    ),
                painter = painterResource(id = R.drawable.ic_card_scan),
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = stringResource(id = R.string.destination_card_scan),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardNumberScannerPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CardNumberScanner {}
        }
    }
}