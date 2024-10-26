package com.drp.data.model.balance

import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData

data class BalanceInquiryRequest(
    val sourceCardNo: String?,
    val trk2EquivData: PaymentTrk2EquivData
)
