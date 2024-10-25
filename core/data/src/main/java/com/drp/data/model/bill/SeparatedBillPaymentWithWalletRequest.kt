package com.drp.data.model.bill

data class SeparatedBillPaymentWithWalletRequest(
    val billType: String,
    val billId: String,
    val paymentId: String,
    val sourceAccountNo: String,
    val amount: Long
)
