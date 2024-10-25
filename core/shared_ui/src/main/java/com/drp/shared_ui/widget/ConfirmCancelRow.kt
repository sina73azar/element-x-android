package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.drp.refah.ui.widgets.compose.CancelButton
import com.drp.shared_ui.R

@Composable
fun ConfirmCancelRow(
    modifier: Modifier = Modifier,
    confirmTitle: String = stringResource(id = R.string.confirm),
    cancelTitle: String = stringResource(id = R.string.cancel),
    confirmClick: () -> Unit,
    cancelClick: () -> Unit,
    confirmLoading: Boolean,
) {


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingButton(
            onClick = confirmClick,
            btnLabel = confirmTitle,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.48f),
            loading = confirmLoading,
        )

        Spacer(
            modifier = Modifier.weight(0.04f)
        )

        CancelButton(
            modifier = Modifier
                .fillMaxWidth(0.48f),
            title = cancelTitle,
            onClick = cancelClick,
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.small_padding)),
        )

    }
}