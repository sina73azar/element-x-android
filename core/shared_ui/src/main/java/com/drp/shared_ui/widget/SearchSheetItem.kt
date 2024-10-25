package com.drp.shared_ui.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drp.refah.ui.data.model.ContactItem
import com.drp.shared_ui.R
import com.drp.shared_ui.model.SearchSheetItemModel


fun SearchSheetItemModel.toContactItem() = ContactItem(
    name = this.name ?: "",
    mobileNo = this.value,
    avatar = null
)

@Composable
fun SearchSheetItem(
    model: SearchSheetItemModel,
    onItemClick: (SearchSheetItemModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onItemClick.invoke(model) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (model.img == null) 1f else 0.9f)
                .padding(vertical = dimensionResource(id = R.dimen.small_padding)),
            verticalArrangement = Arrangement.Center
        ) {
            model.name?.let {
                Text(
                    text = model.name!!,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.small_padding)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = model.value,
                modifier = Modifier,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        model.img?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = it), contentDescription = "avatar"
            )
        }
    }


}

@Composable
fun SearchGridSheetItem(
    modifier: Modifier = Modifier,
    model: SearchSheetItemModel,
    onItemClick: (SearchSheetItemModel) -> Unit,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .border(
                width = 1.dp, color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner))
            )
            .clickable {
                onItemClick.invoke(model)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = model.value,
            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )
    }

}


