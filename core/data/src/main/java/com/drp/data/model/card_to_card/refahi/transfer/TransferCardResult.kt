package com.drp.data.model.card_to_card.refahi.transfer

import com.drp.data.model.Amount
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import java.io.Serializable

data class TransferCardResult(
    var fundTransfer: FundTransfer,
    val balance: Amount,
    val destinationBankName: String,
    val processCode: String
) : Serializable