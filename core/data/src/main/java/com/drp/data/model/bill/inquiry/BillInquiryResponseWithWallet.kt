package com.drp.data.model.bill.inquiry

import kotlinx.serialization.Serializable

@Serializable
data class BillInquiryResponseWithWallet(
    val billInquiryResponseService: BillInquiryResponse
)

@Serializable
data class BillInquiryResponse(
    val amount: Long,
    val billId: String,
    val billPayment: String,
    val result: String?
)

@Serializable
data class BillInquiryResult(
    val amount: Long?,
    val bill_id: String?,
    val pay_id: String?,
    val finalterm_pay_id: String?,
    val finalterm_amount: Long?
)
