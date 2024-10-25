package com.drp.card_facilities.presentation.tracking_post.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drp.card_facilities.R
import com.drp.card_facilities.presentation.tracking_post.TrackingPostScreenState
import com.drp.card_facilities.presentation.tracking_post.TrackingPostViewModel
import com.drp.data.model.tracking_post.PostPackageStatusDetail
import com.drp.shared_ui.widget.DynamicSheet

@Composable
fun TrackingPostResultBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    viewModel: TrackingPostViewModel? = null,
    snackBarHost: @Composable () -> Unit = {},
    navigateToLanding: () -> Unit = {},
    dismiss: () -> Unit,
) {
    val uiState =
        viewModel?.uiState?.collectAsStateWithLifecycle()?.value ?: TrackingPostScreenState()
    val context = LocalContext.current
    DynamicSheet(
        modifier = modifier,
        isVisible = visibility,
        icon = R.drawable.ic_tracking_post,
        onDismiss = dismiss,
        title = stringResource(
            id = R.string.tracking_post_title_st
        ),
        snackBarHost = snackBarHost
    ) {
        val data = uiState.inquiry.getSuccessData().parameters
        LazyColumn(modifier = Modifier.padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding))) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
                    text = stringResource(id = R.string.receiver_name_st).plus(" ")
                        .plus(data?.receiverName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
                    text = stringResource(id = R.string.source_st).plus(" ")
                        .plus(data?.source),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
                    text = stringResource(id = R.string.destination_st).plus(" ")
                        .plus(data?.destination),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(items = data?.postPackageStatusDetail ?: emptyList()) {
                PostPackageStatusWidget(data = it)
            }

            item {
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

@Composable
fun PostPackageStatusWidget(modifier: Modifier = Modifier, data: PostPackageStatusDetail) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.large_padding))
                .background(MaterialTheme.colorScheme.primary)
                .padding(dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding)),
            text = data.dateTime,
            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
            color = Color.White
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding))
                .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
            text = stringResource(id = R.string.location_st).plus(" ")
                .plus(data.province),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = com.drp.shared_ui.R.dimen.small_padding))
                .padding(horizontal = dimensionResource(id = com.drp.shared_ui.R.dimen.medium_padding)),
            text = data.extraInfo.replace("\"", "").replace("{", "").replace("}", "")
                .replace(":", ": "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        /*CustomLine(
            modifier = Modifier
                .fillMaxWidth()
        )*/
    }
}