package com.drp.refah.card_facilities.data.model.internet_package.payment

import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.google.gson.annotations.SerializedName

data class InternetPackagePaymentRequest(
    val productCode: String,
    val amount: Long,
    val amountWithTax: String,
    val amountWithoutTax: String,
    val sourceCardNo: String?,
    val trk2EquivData: PaymentTrk2EquivData,
    val mobileNumber: String,
    val topupOperator: String
)