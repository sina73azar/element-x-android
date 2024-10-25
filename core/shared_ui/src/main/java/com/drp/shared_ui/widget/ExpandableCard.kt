package com.drp.shared_ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drp.refah.ui.theme.Grey20
import com.drp.refah.ui.theme.Grey40
import com.drp.refah.ui.theme.White
import com.drp.shared_ui.R
import com.drp.shared_ui.model.receipt.ReceiptItem

enum class ExpandType {
    DefaultType,
    BottomSheetType,
}

@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    data: List<ReceiptItem>,
    shownItemCount: Int = 3,
    expanded: Boolean = false,
    expandType: ExpandType,
    actionIcon: Int? = null,
    actionClick: (() -> Unit)? = null,
    onExpandClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "",
        animationSpec = tween(durationMillis = 350, easing = EaseInOut)
    )
    AnimatedContent(targetState = expanded, label = "", transitionSpec = {
        fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = 0.5f
            ),
            initialAlpha = 0.9f
        ).togetherWith(
            fadeOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                targetAlpha = 0.1f
            )
        )/*.using(
            SizeTransform(
                sizeAnimationSpec = { _, _ ->
                    tween(100, easing = EaseInOut)
                }
            )
        )*/
    }) { target ->
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.small_corner)),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.medium_elevation)),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            if (data.size > shownItemCount) {
                data.take(shownItemCount).forEachIndexed { index, receiptItem ->
                    ReceiptItemCompose(
                        data = receiptItem,
                        isLastIndex = if (target) false else index == shownItemCount - 1
                    )
                }
                if (target)
                    data.takeLast(data.size - shownItemCount).forEachIndexed { index, receiptItem ->
                        ReceiptItemCompose(
                            data = receiptItem,
                            isLastIndex = index == data.size - shownItemCount - 1
                        )
                    }
            } else {
                data.forEachIndexed { index, receiptItem ->
                    ReceiptItemCompose(
                        data = receiptItem,
                        isLastIndex = index == data.lastIndex
                    )
                }
            }
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    when (expandType) {
                        ExpandType.DefaultType -> {
                            if (data.size > shownItemCount)
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(vertical = dimensionResource(id = R.dimen.small_padding))
                                        .background(
                                            color = Grey20.copy(alpha = 0.4f),
                                            shape = RoundedCornerShape(
                                                size = dimensionResource(
                                                    id = R.dimen.medium_corner
                                                )
                                            )
                                        )
                                        .size(36.dp),
                                    onClick = { onExpandClick?.invoke() }) {
                                    Icon(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .rotate(rotation),
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                }
                            actionIcon?.let { icon ->
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart),
                                    onClick = { actionClick?.invoke() }) {
                                    Icon(
                                        modifier = Modifier
                                            .size(dimensionResource(id = R.dimen.icon_size)),
                                        painter = painterResource(id = icon),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        ExpandType.BottomSheetType -> {
                            OutlinedButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = dimensionResource(
                                            id = R.dimen.medium_padding
                                        ),
                                        horizontal = dimensionResource(
                                            id = R.dimen.large_padding
                                        )
                                    ), onClick = { onExpandClick?.invoke() },
                                border = BorderStroke(
                                    width = (0.5).dp,
                                    MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = Grey40
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(
                                    defaultElevation = dimensionResource(
                                        id = R.dimen.small_elevation
                                    )
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.detail),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                }
                if (content != null) {
                    content()
                }
            }


        }
    }
}
