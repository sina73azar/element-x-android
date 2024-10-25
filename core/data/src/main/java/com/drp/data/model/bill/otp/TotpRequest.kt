package com.drp.refah.card_facilities.data.model.bill.otp

import com.drp.refah.card_facilities.data.model.CardInfo
import com.drp.refah.card_facilities.data.model.Trk2EquivData

data class TotpRequest(
    var card: CardInfo,
    var additionalInformation: String,
    var trk2EquivData: Trk2EquivData,
    var amount: Long,
    var requestType: String = ""
)