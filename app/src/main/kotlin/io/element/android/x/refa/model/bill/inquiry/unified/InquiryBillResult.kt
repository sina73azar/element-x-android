package com.drp.refah.card_facilities.data.model.bill.inquiry.unified

import com.drp.refah.card_facilities.data.model.CardInfo
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import java.io.Serializable

data class InquiryBillResult(
    val payment: BillPaymentInfo,
    var trk2EquivData: Trk2EquivData? = null,
    var cardInfo: CardInfo? = null,
    val billerCode: String? = null
) :
    Serializable