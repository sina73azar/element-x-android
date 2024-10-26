package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.element.android.x.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DynamicSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    icon: Int? = null,
    dragHandleEnabled: Boolean = true,
    partiallyExpanded: Boolean = true,
    closable: Boolean = true,
    snackBarHost: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val modalBottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = partiallyExpanded)
    if (isVisible)
        ModalBottomSheet(
            modifier = modifier.fillMaxWidth(),
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheetState,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            dragHandle = { if (dragHandleEnabled) CustomLine() },
            shape = RoundedCornerShape(
                topEnd = dimensionResource(id = R.dimen.medium_padding),
                topStart = dimensionResource(id = R.dimen.medium_padding)
            ),
//            windowInsets = WindowInsets.ime/*WindowInsets(bottom = 48.dp)*/
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .imePadding()
//                    .windowInsetsPadding(WindowInsets.ime)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(id = R.dimen.medium_padding),
                            top = dimensionResource(id = R.dimen.medium_padding),
                            bottom = dimensionResource(id = R.dimen.large_padding),
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

                    if (closable) {
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
                }
                CustomLine()
                content()
                snackBarHost()
                Spacer(modifier = Modifier.height(if (!imeVisible) 48.dp else 16.dp))

            }
        }
}
