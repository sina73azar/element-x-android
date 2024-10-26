package com.drp.card_facilities.presentation.vehicle_violation.result

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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drp.refah.ui.theme.Red
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.utils.currencyFormatter
import io.element.android.x.R

@JvmOverloads
@Composable
fun VehicleViolationResultDialog(
    modifier: Modifier = Modifier,
    amount: Long,
    closeAction: () -> Unit
) {
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
            Icon(
                modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.medium_padding))
                    .size(48.dp),
                painter = painterResource(id = R.drawable.ic_vehicle_violation),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.medium_padding),
                )
                .padding(
                    horizontal = dimensionResource(id = R.dimen.small_padding)
                ),
            text = stringResource(R.string.vehicle_penalty_amount_st),
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Red
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.medium_padding),
                )
                .padding(
                    horizontal = dimensionResource(id = R.dimen.small_padding)
                ),
            text = currencyFormatter(amount).plus(" ")
                .plus(stringResource(id = R.string.rial_st)),
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            color = Color.DarkGray,
            lineHeight = 25.sp
        )
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

/*@Preview(showBackground = true)
@Composable
fun Preview() {
    BalanceResultDialog(settingAction = {}, closeAction = {})
}*/
