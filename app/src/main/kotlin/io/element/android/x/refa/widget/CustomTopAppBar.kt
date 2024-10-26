package com.drp.shared_ui.widget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.element.android.x.R

/**
 * MR.C 1402/8/16
 * refreshingState should be change as state from outside to handle refresh icon rotation properly
 * height of toolbar hardcoded 60.dp
 * title later
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    backBtnVisible: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    headerTxt: String,
    leftIcon: Int? = null,
    leftIconClick: (() -> Unit)? = null,
    secondLeftIcon: Int? = null,
    secondLeftIconClick: (() -> Unit)? = null,
    haveRefreshButton: Boolean = false,
    refreshingState: Boolean = false,
    refreshClick: (() -> Unit)? = null,
    headerTextStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = "App Bar Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
        )
        TopAppBar(
            title = {

                Text(
                    style = headerTextStyle,
                    modifier = Modifier.offset(x = (-4).dp),
                    text = headerTxt,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onPrimary
                )

            },
            modifier = Modifier,
            navigationIcon = {
                if (backBtnVisible) {
                    IconButton(onClick = {
                        onBackClick?.invoke()
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_back_24),
                            contentDescription = "back_icon",
                            modifier = Modifier,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            },
            actions = {
                if (haveRefreshButton) {
                    val refreshTransition = rememberInfiniteTransition(label = "")
                    val refreshRotation by refreshTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        label = ""
                    )
                    IconButton(
                        modifier = Modifier.rotate(if (refreshingState) refreshRotation else 0f),
                        onClick = {
                            if (!refreshingState) {
                                refreshClick?.invoke()
                            }
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                if (leftIcon != null) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            leftIconClick?.invoke()
                        }) {
                        Icon(
                            painter = painterResource(id = leftIcon),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                if (secondLeftIcon != null) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            secondLeftIconClick?.invoke()
                        }) {
                        Icon(
                            painter = painterResource(id = secondLeftIcon),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

            },
            windowInsets = WindowInsets(left = 0.dp),
            colors = topAppBarColors(
                containerColor = Color.Transparent
            ),
            scrollBehavior = null
        )
    }
}

@Preview
@Composable
fun AppBarPreview() {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
//        CustomTopAppBar(
//            backBtnVisible = true, headerTxt = stringResource(
//                id = R.string
//                    .activation_account
//            ),
//            haveRefreshButton = true,
//            leftIcon = R.drawable.ic_arrow_left,
//            secondLeftIcon = R.drawable.ic_pen
//        )
    }
}
