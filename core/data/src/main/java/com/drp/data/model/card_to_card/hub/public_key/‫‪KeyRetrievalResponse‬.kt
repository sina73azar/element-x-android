package com.drp.refah.card_facilities.data.model.card_to_card.hub.public_key

data class KeyRetrievalResponse(
    val transactionId:String?=null,
    val keySpec:Int?=null,
    val keyData:String?=null,
    val status:Int?=null,
    val errors:Any
)
