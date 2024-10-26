package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.element.android.x.R

@Composable
fun CardShimmerItem(
    modifier: Modifier = Modifier,
    brush: Brush,
    direction: LayoutDirection = LayoutDirection.Rtl
) {
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
        ) {
            Spacer(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .height(35.dp)
                    .clip(shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner)))
                    .background(brush)
            )
            Spacer(
                modifier = Modifier
                    .padding(start = 6.dp, end = 6.dp, bottom = 6.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner)))
                    .fillMaxHeight(fraction = 0.7f)
                    .background(brush)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardShimmerItemPrev() {
    CardShimmerItem(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f)
            )
        )
    )
}
