package com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry

import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer


data class InquiryCardRequest(var fundTransfer: FundTransfer, var trk2EquivData: Trk2EquivData)