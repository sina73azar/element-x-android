package com.drp.shared_ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

import com.drp.refah.ui.theme.Grey60
import com.drp.shared_ui.R

@Composable
fun EmptyItem(
    modifier: Modifier = Modifier,
    direction: LayoutDirection = LayoutDirection.Rtl,
    desc: String = "sa"
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.empty_state_account))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_corner)),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.medium_elevation)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.small_padding)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    modifier = Modifier.size(120.dp),
                    composition = composition,
                    progress = {
                        progress
                    })
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.large_padding)),
                    text = desc,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grey60
                )
            }
        }
    }
}

