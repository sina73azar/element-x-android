package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.refah.ui.data.model.CustomToggleModel
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R

@Composable
fun CustomSingleSelectionToggle(
    modifier: Modifier = Modifier,
    data: List<CustomToggleModel> = emptyList(),
    height: Dp = 48.dp,
    onSelect: (CustomToggleModel) -> Unit
) {
    if (data.isNotEmpty()) {
        var selected by remember {
            mutableIntStateOf(data[0].id)
        }
        LaunchedEffect(key1 = 0) {
            onSelect(data[0])
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner)))
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.medium_corner))
                )
        ) {
            if (data.size <= 3) {
                data.forEachIndexed { index, toggleModel ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .background(
                                color = if (selected == toggleModel.id) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ) else White
                            )
                            .border(
                                width = 1.dp,
                                color = if (selected == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = when (index) {
                                    0 -> RoundedCornerShape(
                                        topStart = dimensionResource(
                                            id = R.dimen.medium_corner
                                        ),
                                        bottomStart = dimensionResource(
                                            id = R.dimen.medium_corner
                                        )
                                    )

                                    data.lastIndex -> RoundedCornerShape(
                                        topEnd = dimensionResource(
                                            id = R.dimen.medium_corner
                                        ),
                                        bottomEnd = dimensionResource(
                                            id = R.dimen.medium_corner
                                        )
                                    )

                                    else -> RoundedCornerShape(0.dp)
                                }
                            )
                            .clickable {
                                selected = toggleModel.id
                                onSelect(toggleModel)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        toggleModel.icon?.let { iconRes ->
                            Icon(
                                modifier = Modifier
                                    .padding(end = dimensionResource(id = R.dimen.small_padding))
                                    .size(dimensionResource(id = R.dimen.icon_size)),
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = if (selected == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = toggleModel.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected == toggleModel.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTogglePreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CustomSingleSelectionToggle(
//                data = listOf(
//                    CustomToggleModel(0, "سو اثر شده", R.drawable.ic_three_dot),
//                    CustomToggleModel(1, "سو اثر نشده", R.drawable.ic_add)
//                )
            ) {

            }
        }
    }
}


