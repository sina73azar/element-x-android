package com.drp.refah.ui.widgets.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.x.R

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    elevation: Dp = 2.dp,
    shape: Shape = RoundedCornerShape(CornerSize(50)),
    title:String = stringResource(id = R.string.cancel),
    onClick: () -> Unit,
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.padding(vertical = 0.dp),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
        ),
        contentPadding = PaddingValues(
            top = dimensionResource(id = com.intuit.sdp.R.dimen._7sdp),
            bottom = dimensionResource(id = com.intuit.sdp.R.dimen._7sdp),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation
        ),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }

}
