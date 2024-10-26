package com.drp.shared_ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.drp.refah.ui.theme.Grey20

@Composable
fun CustomLine(
    modifier: Modifier = Modifier,
    color: Color = Grey20,
    thickness: Dp = 1.dp,
    dashed: Boolean = false,
    phase: Float = 10f,
    intervals: FloatArray = floatArrayOf(10f, 10f)
) {
    if (dashed) {
        Canvas(modifier = modifier
            .fillMaxWidth()
            .height(thickness)) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(
                    intervals,
                    phase
                )
            )
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(thickness)
                .background(color)
        ) {

        }
    }
}

@Preview
@Composable
fun CustomLinePreview() {
    CustomLine()
}


