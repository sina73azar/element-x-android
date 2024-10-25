package com.drp.refah.card_facilities.data.model.installment.payment

import kotlinx.serialization.Serializable


@Serializable
data class LoanPaymentInfo(
    var paymentId: String,
    var amount: Long
)