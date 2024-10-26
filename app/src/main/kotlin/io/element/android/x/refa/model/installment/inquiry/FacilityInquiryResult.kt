package com.drp.refah.card_facilities.data.model.installment.inquiry

import java.io.Serializable

data class FacilityInquiryResult(
    val facilityOwnerFirstName: String,
    var facilityOwnerLastName: String,
    var amount: Long,
    var paymentId: String,
) : Serializable
