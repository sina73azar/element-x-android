package com.drp.refah.card_facilities.data.model.bill.inquiry.separated

import java.io.Serializable


data class SeparatedPhoneBillInquiryResult(
    val billId: String,
    var amount: Long,
    val billPayment: String,
    var finalTermAmount: Long,
    var finalTermBillPayId: String,
) : Serializable
