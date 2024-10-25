package com.drp.shared_ui.widget

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.R
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Combo(
    modifier: Modifier = Modifier,
    mValue: String,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    iconRight: ImageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_down),
    iconLeftOutside: ImageVector? = null,
    iconLeftAction: ((String) -> Unit)? = null,
    isLoading: Boolean,
    label: String,
    placeHolder: String = "",
    isError: Boolean = false,
    dismissError: () -> Unit = {}
) {

    val interactionSource = remember {
        object : MutableInteractionSource {
            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                if (interaction is PressInteraction.Release) {
                    onClick()
                }

                interactions.emit(interaction)
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
    }


    Row(
        modifier = modifier
    )
    {
        OutlinedTextField(
            value = mValue,
            textStyle = MaterialTheme.typography.bodySmall,
            onValueChange = {
                onValueChange(it)
                dismissError()
            },
            isError = isError,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.7f)
                )
            },
            placeholder = {
                Text(
                    text = placeHolder,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            leadingIcon = { Icon(imageVector = iconRight, contentDescription = "right_icon") },
            trailingIcon =
            if (isLoading) {
                @Composable {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(26.dp),
                        color = colorResource(id = R.color.colorPrimary),
                        strokeWidth = 2.dp
                    )
                }
            } else null,
            singleLine = true,
        )
        iconLeftOutside?.let {
            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    imageVector = iconLeftOutside,
                    contentDescription = null
                )
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun ComboPreview() {
//    Combo(onValueChange = {}, isLoading = false, label = "label", onClick = {})
}



