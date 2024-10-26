package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R

@Composable
fun SubmitCancelRowCompose(
    modifier: Modifier = Modifier,
    submitText: String,
    onSubmitClick: () -> Unit,
    onSubmitLoading: Boolean,
    dismissText: String,
    onDismissClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
    ) {
        LoadingButton(
            modifier = Modifier
                .padding(end = dimensionResource(id = R.dimen.medium_padding))
                .weight(1f),
            onClick = onSubmitClick,
            btnLabel = submitText,
            loading = onSubmitLoading
        )
        OutlinedButton(
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.medium_padding))
                .weight(1f),
            onClick = onDismissClick,
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.large_corner)),
            contentPadding = PaddingValues(
                dimensionResource(id = R.dimen.small_padding)
            )
        ) {
            Text(
                text = dismissText,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubmitCancelRowComposePreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            SubmitCancelRowCompose(
                submitText = "تایید",
                onSubmitClick = {},
                dismissText = "انصراف",
                onDismissClick = {},
                onSubmitLoading = false
            )
        }
    }
}
