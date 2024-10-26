package com.drp.refah.card_facilities.data.model.topup.payment

import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import java.io.Serializable

data class PaymentTopUpRequest(
    val chargeType: String,
    val sourceCardNo: String?,
    val trk2EquivData: PaymentTrk2EquivData,
//    var chargeTypeName: String?,
    var fundTransfer: TopUpPayment,
    var mobileNumber: String,
    var topupOperator: String
) : Serializable