package com.drp.data.model.last_ten_statement

import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData

data class LastTenStatementInquiryRequest(
    val cardExpiration: String?,
    val cardNumber: String?,
    val cvv2: String?,
    val pin: String?,
)
