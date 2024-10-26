package com.drp.refah.card_facilities.data.model.bill.payment

import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo

data class PaymentBillRequest(
    val payment: BillPaymentInfo,
    val sourceCardNo: String?,
    val trk2EquivData: PaymentTrk2EquivData
)