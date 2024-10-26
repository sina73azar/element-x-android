package com.drp.refah.card_facilities.data.model.bill.inquiry.separated


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SeparatedUtilityBillInquiryResponse(
    @SerializedName("amount")
    var amount: Long?,
    @SerializedName("billId")
    var billId: String?,
    @SerializedName("billPayment")
    var billPayment: String?,
    @SerializedName("result")
    var result: Result?
): Serializable