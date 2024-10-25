package com.drp.data.repository

import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.installment.inquiry.FacilityResponse

import com.drp.shared_ui.model.CardShotItemInfo

import kotlinx.coroutines.flow.Flow

interface CardFacilitiesLoanRepository {
    fun facilityInquiry(
        facilityNumber: String
    ): Flow<CustomResponse<FacilityResponse>>

    fun facilityPayment(
        password: String,
        selectedCard: CardShotItemInfo? = null,
        trk2EquivData: Trk2EquivData,
        paymentId: String,
        amount: Long,
        facilityOwnerFirstName: String,
        facilityOwnerLastName: String
    ): Flow<CustomResponse<Any>>
}