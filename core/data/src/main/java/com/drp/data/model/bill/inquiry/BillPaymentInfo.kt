package com.drp.refah.card_facilities.data.model.bill.inquiry

import java.io.Serializable

data class BillPaymentInfo(
    var billId: String? = null,
    val paymentId: String? = null,
    var amount: Long,

    var billerPersianName: String? = null,
) : Serializable