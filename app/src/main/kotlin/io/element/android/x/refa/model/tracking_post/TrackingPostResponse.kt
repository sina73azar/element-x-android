package com.drp.data.model.tracking_post

data class TrackingPostResponse(
    val parameters: TrackingPostParameters?,
    val status: TrackingStatus
)

data class TrackingPostParameters(
    val destination: String?,
    val extraInfo: String?,
    val postCost: String?,
    val postPackageStatusDetail: List<PostPackageStatusDetail>?,
    val receiverName: String?,
    val source: String?
)

data class PostPackageStatusDetail(
    val dateTime: String,
    val eventNumber: String,
    val extraInfo: String,
    val province: String
)

data class TrackingStatus(
    val description: String
)
