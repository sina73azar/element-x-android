package com.drp.shared_ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import io.element.android.x.R

@Composable
fun ActionItemCompose(
    modifier: Modifier = Modifier,
    title: String,
    icon: Int? = null,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.large_padding),
                    top = dimensionResource(id = R.dimen.small_padding),
                    bottom = dimensionResource(id = R.dimen.small_padding),
                    end = dimensionResource(id = R.dimen.small_padding)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = icon),
                    contentDescription = "action_icon",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.small_padding)))
            }

            Text(
                text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme
                    .colorScheme.onSecondaryContainer
            )
        }
    }

}
