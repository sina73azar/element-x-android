package com.drp.shared_ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.x.R

@Composable
fun BottomMenu(
    modifier: Modifier = Modifier
) {


    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        IconTextColumn(
            icon = R.drawable.ic_call, text = stringResource(id = R.string.call),
            tint = MaterialTheme.colorScheme.primary
        ) {

        }
        Spacer(modifier = Modifier.width(24.dp))
        IconTextColumn(
            icon = R.drawable.ic_plus,
            text = stringResource(id = R.string.refah_plus),
            tint = colorResource(id = R.color.purple_dark)
        ) {

        }
        Spacer(modifier = Modifier.width(24.dp))
        IconTextColumn(
            icon = R.drawable.ic_more_horiz,
            text = stringResource(id = R.string.other_services),
            tint = MaterialTheme.colorScheme.primary
        ) {

        }
    }

}


@Composable
fun IconTextColumn(
    icon: Int,
    text: String,
    tint: Color,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomFab(
            onClick = onClick, shape = CircleShape,
            modifier = Modifier.size(44.dp),
            containerColor = Color.Transparent
        ) {
            Icon(
                modifier = Modifier.background(Color.Transparent),
                painter = painterResource(id = icon),
                contentDescription = "icon",
                tint = tint,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = text, color = tint, style = MaterialTheme.typography.bodySmall)

    }
}

@Preview
@Composable
fun MyPreview() {

    BottomMenu()
}
