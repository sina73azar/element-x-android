package com.drp.card_facilities.presentation.tracking_post

import com.drp.data.model.tracking_post.TrackingPostResponse
import com.drp.data.network.RequestState
import io.element.android.x.refa.enums.UiText

data class TrackingPostScreenState(
    val trackingNumber: String = "",
    val trackingNumberValidationMessage: UiText = UiText.DynamicString(""),
    val inquiry: RequestState<TrackingPostResponse> = RequestState.Idle
)
