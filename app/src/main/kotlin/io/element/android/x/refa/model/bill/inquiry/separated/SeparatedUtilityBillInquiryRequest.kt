package com.drp.refah.card_facilities.data.model.bill.inquiry.separated


data class SeparatedUtilityBillInquiryRequest(
    val billId: String,
    val billType: String,
    var cycle: String? = null,
    var traceNumber: String? = null
)
