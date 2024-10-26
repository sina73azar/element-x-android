package com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry

import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.card_to_card.BankInfo
import com.drp.refah.card_facilities.data.model.card_to_card.DestinationPerson
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import java.io.Serializable

data class InquiryCardResult(
    val customerName: DestinationPerson, val card: BankInfo,
    var amount: Long, var fundTransfer: FundTransfer, var trk2EquivData: Trk2EquivData
) : Serializable