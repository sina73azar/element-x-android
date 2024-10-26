package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R
import com.drp.shared_ui.model.SearchSheetItemModel

@Composable
fun CardExpirationDateWidget(
    modifier: Modifier = Modifier,
    editButtonEnable: Boolean,
    onDismissEditButtonEnable: () -> Unit,
    monthValue: String = "",
    yearValue: String = "",
    onMonthChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    monthError: Boolean = false,
    yearError: Boolean = false,
    dismissMonthError: () -> Unit = {},
    dismissYearError: () -> Unit = {},
) {
    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }
    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }

    /**
     * Expire date fields
     * */
    val months =
        stringArrayResource(id = R.array.month).toList()
    val years =
        stringArrayResource(id = R.array.years).toList()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Combo2(
            modifier = Modifier.weight(0.35f),
            mValue = monthValue,
            iconRight = null,
            onClick = { monthSelectionSheet = true },
            enabled = !editButtonEnable,
            onValueChange = onMonthChange,
            isLoading = false,
            label = stringResource(id = R.string.month),
            isError = monthError,
            dismissError = dismissMonthError
        )
        Text(
            text = "/", style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .alpha(0.7f)
                .padding(horizontal = dimensionResource(id = R.dimen.small_padding))
        )
        Combo2(
            modifier = Modifier.weight(0.4f),
            mValue = yearValue,
            iconRight = null,
            onClick = { yearSelectionSheet = true },
            enabled = !editButtonEnable,
            onValueChange = onYearChange,
            isLoading = false,
            label = stringResource(id = R.string.year),
            isError = yearError,
            dismissError = dismissYearError
        )
        Button(modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.large_padding),
                top = dimensionResource(id = R.dimen.small_padding)
            ),
            enabled = editButtonEnable,
            onClick = { onDismissEditButtonEnable() }) {
            Text(
                text = "ویرایش تاریخ انقضا",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }
        /**
         * Expire date selection
         * */
        SearchSheet(sheetVisible = monthSelectionSheet,
            onDismiss = { monthSelectionSheet = false },
            items = months.map { monthStr ->
                SearchSheetItemModel(value = monthStr)
            },
            onItemClick = { itemModel ->
                onMonthChange(itemModel.value)
//                viewModel?.sendEvent(BillEvents.SetMonth(itemModel.value))
                monthSelectionSheet = false
            })
        SearchSheet(sheetVisible = yearSelectionSheet,
            onDismiss = { yearSelectionSheet = false },
            items = years.map { yearStr ->
                SearchSheetItemModel(value = yearStr)
            },
            onItemClick = { itemModel ->
                onYearChange(itemModel.value)
//                viewModel?.sendEvent(BillEvents.SetYear(itemModel.value))
                yearSelectionSheet = false
            })
        /*Combo(
            modifier = Modifier.weight(0.5f),
            mValue = uiState?.value?.year ?: "",
            onClick = { yearSelectionSheet = true },
            onValueChange = {
                viewModel?.sendEvent(BillEvents.SetYear(it))
            },
            isLoading = false,
            label = stringResource(id = com.drp.refah.card_facilities.R.string.year),
            isError = uiState?.value?.yearError == true,
            dismissError = { viewModel?.sendEvent(BillEvents.DumpYearError) }
        )*/
    }
}

@Preview(showBackground = true)
@Composable
private fun CardExpirationDateWidgetPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
//            CardExpirationDateWidget()
        }
    }
}
