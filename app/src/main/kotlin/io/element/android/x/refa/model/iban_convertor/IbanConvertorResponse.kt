package com.drp.data.model.iban_convertor

data class IbanConvertorResponse(
    val parameters: IbanConvertorParameters?,
    val status: IbanConvertorStatus
)

data class IbanConvertorParameters(
    val accountNumber: String? = null,
    val bankShowName: String,
    val extraInfo: String,
    val colorCode: String,
    val shebaNumber: String? = null,
    val imageUrl: String,
    val whiteImageUrl: String
)

data class IbanConvertorStatus(
    val description: String
)