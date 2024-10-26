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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R

@Composable
fun ProfileCardItem(
    modifier: Modifier = Modifier,
    phoneNumber: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .padding(top = dimensionResource(id = R.dimen.medium_padding))
                .clickable {
                    onClick.invoke()
                },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                modifier = Modifier
                    .padding(
                        dimensionResource(id = R.dimen.small_padding)
                    ),
                onClick = onClick
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.app_bar_size)),
                    imageVector = ImageVector.vectorResource(id = R.drawable.avatar),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = phoneNumber,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                modifier = Modifier
                    .padding(
                        dimensionResource(id = R.dimen.small_padding)
                    ),
                onClick = onClick
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.settings_ic),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileCardItemPreview(modifier: Modifier = Modifier) {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ProfileCardItem(phoneNumber = "09147937991") {

            }
        }
    }
}
