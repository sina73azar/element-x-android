package com.drp.data.model.licence_negative_score

data class LicenceNegativeScoreInquiryRequest(
    val licenseNumber: String,
    val mobileNumber: String,
    val nationalID: String,
    val traceNumber: String,
    val walletIdentifier: String
)
