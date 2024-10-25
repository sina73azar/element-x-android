package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.refah.ui.data.enums.TimePeriodType
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.refah.ui.theme.Grey40
import com.drp.shared_ui.R
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.ibanFormatterForEditText


@Composable
fun ReceiptItemCompose(
    modifier: Modifier = Modifier,
    data: ReceiptItem? = null,
    isLastIndex: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.small_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = dimensionResource(id = R.dimen.medium_padding)),
                text = data?.title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(end = dimensionResource(id = R.dimen.medium_padding))
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = data?.value?.let { value ->
                            checkValue(value)
                        } ?: "",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = data?.value?.let { value ->
                            if (value.contains("\\d".toRegex()) && value.contains("[آ-ی]".toRegex()))
                                MaterialTheme.typography.bodyMedium
                            else
                                MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr)
                        }
                            ?: MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Ltr)
                    )
                }
                // TODO implementing icon with coil
                /*Icon(
                    modifier = Modifier
                        .padding(end = dimensionResource(id = R.dimen.small_padding))
                        .size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_circle_info),
                    contentDescription = null
                )*/
                data?.type.let { type ->
                    if (type == ReceiptType.AMOUNT) {
                        Text(
                            modifier = Modifier
                                .padding(end = dimensionResource(id = R.dimen.small_padding))
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.large_corner))
                                )
                                .padding(
                                    horizontal = dimensionResource(id = R.dimen.medium_padding),
                                    vertical = 2.dp
                                ),
                            text = stringResource(id = R.string.irr_currency),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        if (!isLastIndex)
            CustomLine(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.small_padding)),
                dashed = true,
                color = Grey40
            )
    }
}

@Composable
private fun checkValue(value: String): String {
    return when (value) {
        TimePeriodType.DAILY.name -> stringResource(R.string.daily)
        TimePeriodType.WEEKLY.name -> stringResource(R.string.weekly)
        TimePeriodType.BI_WEEKLY.name -> stringResource(R.string.bi_weekly)
        TimePeriodType.TWICE_MONTHLY.name -> stringResource(R.string.twice_monthly)
        TimePeriodType.MONTHLY.name -> stringResource(R.string.monthly)
        TimePeriodType.END_OF_MONTH.name -> stringResource(R.string.end_of_month)
        TimePeriodType.FOUR_WEEKS.name -> stringResource(R.string.four_weeks)
        TimePeriodType.BI_MONTHLY.name -> stringResource(R.string.bi_monthly)
        TimePeriodType.QUARTERLY.name -> stringResource(R.string.quarterly)
        TimePeriodType.SEMI_ANNUALY.name -> stringResource(R.string.semi_annually)
        TimePeriodType.ANNUALLY.name -> stringResource(R.string.annually)
        else -> {
            if (value.contains(stringResource(R.string.ir))) {
                stringResource(R.string.ir) + ibanFormatterForEditText(value.filter { it.isDigit() })
            } else {
                value
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptItemPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ReceiptItemCompose(data = ReceiptItem(0, "عنوان", "IR45632463"))
        }
    }
}