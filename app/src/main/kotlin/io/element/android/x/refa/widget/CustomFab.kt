package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomFab(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(CornerSize(50)),
    onClick: () -> Unit,
    btnHeight: Dp? = null,
    content: @Composable () -> Unit,
) {

    var mModifier = modifier.wrapContentHeight()
    if (btnHeight != null) {
        mModifier = modifier.height(btnHeight)
    }
    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(elevation),
        modifier = mModifier
            .wrapContentWidth(),
        shape = shape,
        containerColor = containerColor,

    ) {
        content()
    }

}