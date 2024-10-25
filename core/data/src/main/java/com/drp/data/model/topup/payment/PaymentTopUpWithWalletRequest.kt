package com.drp.data.model.topup.payment

//@Serializable
data class PaymentTopUpWithWalletRequest(
    val sourceAccountNo: String,
    val amount: Long,
    val mobileNumber: String,
    val topUpOperator: Int
)
