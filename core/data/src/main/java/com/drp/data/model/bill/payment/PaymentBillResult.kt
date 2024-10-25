package com.drp.refah.card_facilities.data.model.bill.payment

import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo

data class PaymentBillResult(val payment: BillPaymentInfo, val followupCode: String)