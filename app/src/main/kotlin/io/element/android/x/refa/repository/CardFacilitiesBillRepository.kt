package com.drp.data.repository


import com.drp.data.enums.BillType
import com.drp.data.model.bill.inquiry.BillInquiryResponseWithWallet
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedPhoneBillInquiryResult
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedUtilityBillInquiryResponse
import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillResult
import com.drp.refah.card_facilities.data.model.bill.payment.PaymentBillResult
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.flow.Flow

interface CardFacilitiesBillRepository {
    fun billInquiry(billId: String, paymentId: String): Flow<CustomResponse<InquiryBillResult>>

    fun separatedPhoneBillInquiry(
        phoneNumber: String,
        billType: BillType
    ): Flow<CustomResponse<SeparatedPhoneBillInquiryResult>>

    fun separatedUtilityBillInquiry(
        billId: String,
        billType: BillType
    ): Flow<CustomResponse<SeparatedUtilityBillInquiryResponse>>

    fun utilityBillInquiryWithWallet(
        billId: String,
        billType: BillType
    ): Flow<CustomResponse<BillInquiryResponseWithWallet>>

    fun billPayment(
        password: String,
        payment: BillPaymentInfo,
        selectedCard: CardShotItemInfo? = null,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<PaymentBillResult>>



}