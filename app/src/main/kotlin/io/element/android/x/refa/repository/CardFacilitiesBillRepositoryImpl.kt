package com.drp.data.repository

import com.drp.data.database.entity.toCardEntity
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.enums.BillType
import com.drp.data.model.bill.inquiry.BillInquiryResponseWithWallet
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints
import com.drp.data.network.EndPoints.BILL_INQUIRY_WITH_WALLET_END_POINT
import com.drp.data.network.EndPoints.CARD_BILL_PAYMENT_END_POINT
import com.drp.data.network.EndPoints.PHONE_BILL_INQUIRY_END_POINT
import com.drp.data.network.EndPoints.UTILITY_BILL_INQUIRY_END_POINT
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.refah.card_facilities.data.model.ExpireDate
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedInquiryBillRequest
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedPhoneBillInquiryResult
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedUtilityBillInquiryRequest
import com.drp.refah.card_facilities.data.model.bill.inquiry.separated.SeparatedUtilityBillInquiryResponse
import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillRequest
import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillResult
import com.drp.refah.card_facilities.data.model.bill.payment.PaymentBillRequest
import com.drp.refah.card_facilities.data.model.bill.payment.PaymentBillResult
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CardFacilitiesBillRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dataBaseHelper: DataBaseRequest
) : CardFacilitiesBillRepository {
    override fun billInquiry(
        billId: String,
        paymentId: String
    ): Flow<CustomResponse<InquiryBillResult>> {
        val request = InquiryBillRequest(billId, paymentId)
        /*return flowOf(
            CustomResponse.Success(
                data = InquiryBillResult(
                    payment = BillPaymentInfo(
                        billId = "2068989604610",
                        paymentId = "0000116180220",
                        amount = 116000
                    )
                )
            )
        )*/
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.BILL_INQUIRY_END_POINT,
            request = request,
            kClass = InquiryBillResult::class.java
        )
    }

    override fun separatedPhoneBillInquiry(
        phoneNumber: String,
        billType: BillType
    ): Flow<CustomResponse<SeparatedPhoneBillInquiryResult>> {
        val request = SeparatedInquiryBillRequest(billId = phoneNumber, billType = billType.name)
        return dynamicApiCall.dynamicPostCall(
            url = PHONE_BILL_INQUIRY_END_POINT,
            request = request,
            kClass = SeparatedPhoneBillInquiryResult::class.java
        )
    }

    override fun separatedUtilityBillInquiry(
        billId: String,
        billType: BillType
    ): Flow<CustomResponse<SeparatedUtilityBillInquiryResponse>> {
        val request = SeparatedUtilityBillInquiryRequest(billId = billId, billType = billType.name)
        return dynamicApiCall.dynamicPostCall(
            url = UTILITY_BILL_INQUIRY_END_POINT,
            request = request,
            kClass = SeparatedUtilityBillInquiryResponse::class.java
        )
    }

    override fun utilityBillInquiryWithWallet(
        billId: String,
        billType: BillType
    ): Flow<CustomResponse<BillInquiryResponseWithWallet>> {
        val request =
            SeparatedUtilityBillInquiryRequest(billId = billId, billType = billType.walletIndex)
        return dynamicApiCall.dynamicPostCall(
            url = BILL_INQUIRY_WITH_WALLET_END_POINT,
            request = request,
            kClass = BillInquiryResponseWithWallet::class.java
        )
    }

    override fun billPayment(
        password: String,
        payment: BillPaymentInfo,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<PaymentBillResult>> {
        val headers: Map<String, String> = mapOf("password" to password)
        val request = PaymentBillRequest(
            payment,
            sourceCardNo = selectedCard?.pan,
            trk2EquivData = PaymentTrk2EquivData(
                expireDate = ExpireDate(
                    year = trk2EquivData.expireDate.substring(0, 2).toInt(),
                    month = trk2EquivData.expireDate.substring(2, 4).toInt()
                ),
                cvv2 = trk2EquivData.cvv2,
                pin = password
            )
        )
//        return flowOf(CustomResponse.Error(message = "error"))
        return dynamicApiCall.dynamicPostCall(
            url = CARD_BILL_PAYMENT_END_POINT,
            request = request,
            headers = headers,
            kClass = PaymentBillResult::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS)
                selectedCard?.copy(
                    panExpiryYear = trk2EquivData.expireDate.substring(
                        0, 2
                    ),
                    panExpiryMonth = trk2EquivData.expireDate.substring(
                        2, 4
                    )
                )?.toCardEntity()?.let {
                    dataBaseHelper.upsertCardEntity(
                        it
                    )
                }
        }
    }
}