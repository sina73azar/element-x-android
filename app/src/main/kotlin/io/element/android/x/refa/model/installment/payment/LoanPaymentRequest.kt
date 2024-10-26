package com.drp.refah.card_facilities.data.model.installment.payment

import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData

data class LoanPaymentRequest(
    val payment: LoanPaymentInfo,
    val facilityOwnerFirstName: String,
    val facilityOwnerLastName: String,
    val sourceCardNo: String?,
    val trk2EquivData: PaymentTrk2EquivData
)