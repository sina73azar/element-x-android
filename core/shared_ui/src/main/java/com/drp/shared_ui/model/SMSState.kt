package com.drp.refah.ui.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SMSState(
    var SMSStateLoading: Boolean = false,
    var SMSStateSuccess: Boolean = false,
    var SMSStateInquiryFail: Boolean = false,
    var SMSStateInquiryErrorMessage: String? = null,
)
