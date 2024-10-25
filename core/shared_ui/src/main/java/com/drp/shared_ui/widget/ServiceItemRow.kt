package com.drp.refah.card_facilities.widgets.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.R
import com.drp.shared_ui.model.ServiceItemModel

@Composable
fun ServiceItemRow(
    modifier: Modifier = Modifier,
    serviceItemModel: ServiceItemModel,
    textAlign: TextAlign = TextAlign.Start,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            serviceItemModel.startIcon?.let { startIcon ->
                IconButton(
                    modifier = Modifier
                        .padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ),
                    onClick = onClick
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                        painter = painterResource(id = startIcon),
                        contentDescription = "",
                        tint = serviceItemModel.startIconTint
                    )
                }
            }
            Text(
                modifier = Modifier.weight(1f),
                text = serviceItemModel.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.W200,
                    textAlign = textAlign
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            serviceItemModel.endIcon?.let { endIcon ->
                IconButton(
                    modifier = Modifier
                        .padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ),
                    onClick = onClick
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = endIcon),
                        contentDescription = "",
                        tint = serviceItemModel.endIconTint
                    )
                }
            }
        }
    }
}