package com.drp.data.repository


import com.drp.data.database.entity.toCardEntity
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints.FACILITY_INQUIRY_END_POINT
import com.drp.data.network.EndPoints.FACILITY_PAYMENT_END_POINT
import com.drp.refah.card_facilities.data.model.ExpireDate
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.drp.refah.card_facilities.data.model.installment.inquiry.FacilityRequest
import com.drp.refah.card_facilities.data.model.installment.inquiry.FacilityResponse
import com.drp.refah.card_facilities.data.model.installment.payment.LoanPayment
import com.drp.refah.card_facilities.data.model.installment.payment.LoanPaymentInfo
import com.drp.refah.card_facilities.data.model.installment.payment.LoanPaymentRequest
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.shared_ui.model.CardShotItemInfo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CardFacilitiesLoanRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dataBaseHelper: DataBaseRequest
) : CardFacilitiesLoanRepository {
    override fun facilityInquiry(facilityNumber: String): Flow<CustomResponse<FacilityResponse>> {
        val request = FacilityRequest(facilityNumber = facilityNumber)
        /*return flow {
            delay(1000)
            emit(
                CustomResponse.Success(
                    FacilityResponse(
                        result = FacilityInquiryResult(
                            "میررضا",
                            "موسوی",
                            amount = 25000,
                            paymentId = "164.7460.13950745.1"
                        )
                    )
                )
            )
        }*/
        return dynamicApiCall.dynamicPostCall(
            url = FACILITY_INQUIRY_END_POINT,
            request = request,
            kClass = FacilityResponse::class.java
        )
    }

    override fun facilityPayment(
        password: String,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData,
        paymentId: String,
        amount: Long,
        facilityOwnerFirstName: String,
        facilityOwnerLastName: String
    ): Flow<CustomResponse<Any>> {
        val headers: Map<String, String> = mapOf("password" to password)
        val request = LoanPaymentRequest(
            payment = LoanPaymentInfo(
                paymentId = paymentId,
                amount = amount,
            ),
            sourceCardNo = selectedCard?.pan,
            trk2EquivData = PaymentTrk2EquivData(
                expireDate = ExpireDate(
                    year = trk2EquivData.expireDate.substring(0, 2).toInt(),
                    month = trk2EquivData.expireDate.substring(2, 4).toInt()
                ),
                cvv2 = trk2EquivData.cvv2,
                pin = password
            ),
            facilityOwnerFirstName = facilityOwnerFirstName,
            facilityOwnerLastName = facilityOwnerLastName
        )
//        return flowOf(CustomResponse.Success(""))
        return dynamicApiCall.dynamicPostCall(
            url = FACILITY_PAYMENT_END_POINT,
            request = request,
            headers = headers,
            kClass = LoanPayment::class.java
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