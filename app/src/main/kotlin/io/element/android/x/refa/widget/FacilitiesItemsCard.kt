package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.drp.shared_ui.theme.ApplicationTheme
import io.element.android.x.R
import io.element.android.x.refa.model.FacilityServiceItem

@Composable
fun FacilitiesItemsCard(
    modifier: Modifier = Modifier,
    headerText: String,
    serviceList: Map<FacilityServiceItem, (FacilityServiceItem) -> Unit> = mapOf(),
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.medium_padding),
                vertical = dimensionResource(id = R.dimen.medium_padding)
            ),
            text = headerText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyVerticalGrid(
            modifier = Modifier.heightIn(min = 20.dp, max = 1000.dp),
            columns = GridCells.Fixed(4)
        ) {
            items(
                items = serviceList.keys.toList()
            ) {
                serviceList[it]?.let { onClick ->
                    FacilityItem(
                        facilityServiceItem = it,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun FacilityItem(
    modifier: Modifier = Modifier,
    facilityServiceItem: FacilityServiceItem,
    onClick: (FacilityServiceItem) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                    .padding(top = dimensionResource(id = R.dimen.small_padding))
                    .size(48.dp)
                    .background(
                            color = facilityServiceItem.backgroundColor,
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner))
                    ), onClick = { onClick.invoke(facilityServiceItem) }) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(id = facilityServiceItem.iconResource),
                contentDescription = facilityServiceItem.title,
                tint = Color.White
            )
        }
        Text(
            modifier = Modifier
                    .padding(
                            horizontal = dimensionResource(id = R.dimen.small_padding)
                    )
                    .padding(top = dimensionResource(id = R.dimen.small_padding))
                    .padding(bottom = dimensionResource(id = R.dimen.medium_padding))
                    .clickable {
                        onClick.invoke(facilityServiceItem)
                    },
            text = facilityServiceItem.title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FacilitiesItemCardPreview(modifier: Modifier = Modifier) {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            FacilitiesItemsCard(
                headerText = "خدمات پرکاربرد", serviceList = mapOf(
                generateFacilityServiceItem(1) to {},
                generateFacilityServiceItem(2) to {},
                generateFacilityServiceItem(3) to {},
                generateFacilityServiceItem(4) to {},
                generateFacilityServiceItem(5) to {},
            )
            )
        }
    }
}

fun generateFacilityServiceItem(
    id: Int,
    color: Color = Color(0xFF0B9411),
    title: String = "",
    icon: Int = R.drawable.ic_hamrah_card
): FacilityServiceItem =
    FacilityServiceItem(
        id = id,
        title = title,
        backgroundColor = color,
        iconResource = icon
    )

@Preview(showBackground = true)
@Composable
fun FacilityItemPreview(modifier: Modifier = Modifier) {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            FacilityItem(
                facilityServiceItem = FacilityServiceItem(
                    id = 1,
                    title = "کارت به کارت",
                    backgroundColor = Color.Green,
                    iconResource = R.drawable.ic_sim_card
                ),
                onClick = {}
            )
        }
    }
}
