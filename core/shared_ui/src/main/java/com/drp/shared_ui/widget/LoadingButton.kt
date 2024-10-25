package com.drp.shared_ui.widget

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.R


@Composable
fun LoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ),
    btnLabel: String,
    paddingHorizontal: Dp = 0.dp,
    textStyle: TextStyle? = MaterialTheme.typography.titleSmall,
    elevation: Dp = 2.dp
) {
    val contentAlpha by animateFloatAsState(targetValue = if (loading) 0f else 1f, label = "")
    val loadingAlpha by animateFloatAsState(targetValue = if (loading) 1f else 0f, label = "")
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = paddingHorizontal),
        enabled = enabled,
        colors = colors,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.large_corner)),
        contentPadding = PaddingValues(
            dimensionResource(id = R.dimen.small_padding)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation)
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(
                animating = loading,
                modifier = Modifier.graphicsLayer { alpha = loadingAlpha },
                color = MaterialTheme.colorScheme.onPrimary,
                indicatorSpacing = 5.dp,
            )
            Box(
                modifier = Modifier.graphicsLayer { alpha = contentAlpha }
            ) {
                Text(
                    text = btnLabel,
                    style = textStyle!!,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    animating: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    indicatorSpacing: Dp = 8.dp,
) {
    val animatedValues = List(3) { index ->
        // 2
        var animatedValue by remember(key1 = animating) { mutableStateOf(0f) }
        // 3
        LaunchedEffect(key1 = animating) {
            if (animating) {
                // 4
                animate(
                    initialValue = 10 / 2f,
                    targetValue = -10 / 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 300),
                        repeatMode = RepeatMode.Reverse,
                        initialStartOffset = StartOffset(100 * index),
                    ),
                ) { value, _ -> animatedValue = value }
            }
        }
        animatedValue
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        animatedValues.forEach { animatedValue ->
            LoadingDot(
                modifier = Modifier
                    .padding(horizontal = indicatorSpacing)
                    .width(6.dp)
                    .aspectRatio(1f)
                    .then(
                        Modifier.offset(y = animatedValue.dp)
                    ),
                color = color,
            )
        }
    }
}

@Composable
private fun LoadingDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .background(color = color)
    )
}


@Preview
@Composable
fun LoadingButtonPreview() {
    var loading by remember {
        mutableStateOf<Boolean>(false)
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LoadingButton(
            onClick = { loading = !loading },
            loading = loading,
            btnLabel = "asdsfdgfdghgfhfhgfhad"
        )
    }
}