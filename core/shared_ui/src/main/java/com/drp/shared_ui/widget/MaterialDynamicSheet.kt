package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.R

@OptIn( ExperimentalMaterialApi::class)
@Composable
fun DynamicSheet2(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    icon: Int? = null,
    dragHandleEnabled: Boolean = false,
    partiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = partiallyExpanded)

    if (isVisible)
        ModalBottomSheetLayout(
            sheetContent = {
                content()
            },
            sheetBackgroundColor = MaterialTheme.colorScheme.onPrimary,
            sheetShape = RoundedCornerShape(
                topEnd = dimensionResource(id = R.dimen.medium_padding),
                topStart = dimensionResource(id = R.dimen.medium_padding)
            ),
            sheetGesturesEnabled = dragHandleEnabled,
            sheetState = modalBottomSheetState
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(id = R.dimen.medium_padding),
                            top = dimensionResource(id = R.dimen.medium_padding),
                            bottom = dimensionResource(id = R.dimen.medium_padding),
                        ),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "basic_logo",
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(
                                end = dimensionResource(
                                    id = R.dimen.medium_padding
                                )
                            )
                            .size(dimensionResource(id = R.dimen.icon_size)),
                        onClick = { onDismiss() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close_fill),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                CustomLine()
                content()
            }
        }

}