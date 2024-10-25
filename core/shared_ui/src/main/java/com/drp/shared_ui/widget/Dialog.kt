package com.drp.shared_ui.widget

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.shared_ui.R

@JvmOverloads
@Composable
fun NetworkErrorDialogContent(
    modifier: Modifier = Modifier, direction: LayoutDirection = LayoutDirection.Rtl,
    closeAction: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(all = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(40.dp),
                painter = painterResource(id = R.drawable.ic_network),
                contentDescription = "",
                tint = colorResource(id = R.color.colorPrimary)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.between_same_items),
                        start = 6.dp,
                        end = 6.dp
                    ),
                text = stringResource(id = R.string.network_dialog_title),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.between_same_items),
                        start = 6.dp,
                        end = 6.dp
                    ),
                text = stringResource(id = R.string.network_dialog_description),
                textAlign = TextAlign.Center,
                color = Color.DarkGray,
                lineHeight = 25.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.between_same_items),
                        start = 6.dp,
                        end = 6.dp
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                val context= LocalContext.current
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorPrimary)),
                    onClick = {  val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        context.startActivity(intent)
                        closeAction()}) {
                    Text(text = stringResource(id = R.string.network_setting))
                }
                Spacer(modifier = Modifier.width(30.dp))
                Button(
                    elevation = ButtonDefaults.elevatedButtonElevation(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.white)),
                    onClick = { closeAction() }) {
                    Text(text = stringResource(id = R.string.close), color = Color.DarkGray)
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    NetworkErrorDialogContent( closeAction = {})
}

/*
@Preview(showBackground = true)
@Composable
fun Preview() {
    NetworkErrorDialogContent(settingAction = {}, closeAction = {})
}*/
