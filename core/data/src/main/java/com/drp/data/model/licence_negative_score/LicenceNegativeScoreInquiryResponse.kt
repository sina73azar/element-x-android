package com.drp.data.model.licence_negative_score

data class LicenceNegativeScoreInquiryResponse(
    val parameters: Parameters?,
    val status: LicenceStatus
)

data class Parameters(
    val allowedToDrive: Boolean,
    val point: String,
    val rule: String
)

data class LicenceStatus(
    val description: String
)