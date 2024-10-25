package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.drp.refah.ui.data.model.SnackBarType

@Composable
fun SnackBarCompose(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    snackBarType:SnackBarType?= SnackBarType.FAIL,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                modifier = modifier,
                content = {
                    Text(text = data.visuals.message, style = MaterialTheme.typography.bodySmall)
                },
                action = {
                    data.visuals.actionLabel?.let { actionLabel ->
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = actionLabel,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                    }
                },
                containerColor = when(snackBarType){
                    SnackBarType.FAIL->MaterialTheme.colorScheme.error
                    SnackBarType.SUCCESS->MaterialTheme.colorScheme.onTertiary
                    else->{
                        MaterialTheme.colorScheme.onTertiary
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )

}