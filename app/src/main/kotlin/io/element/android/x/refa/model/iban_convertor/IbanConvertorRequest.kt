package com.drp.data.model.iban_convertor

data class IbanConvertorRequest(
    val accountTypeName: String,
    val cardNumber: String? = null,
    val accountNumber: String? = null,
    val bankName: String? = null,
    val shebaNumber: String? = null,
    val traceNumber: String
)
