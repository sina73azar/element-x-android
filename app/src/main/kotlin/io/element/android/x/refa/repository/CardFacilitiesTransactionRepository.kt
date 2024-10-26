package com.drp.data.repository

import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.SimType
import com.drp.data.model.history.HistoryResult
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryResponse
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryWithWalletResponse
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.topup.inquiry.CheckAmountResult
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.flow.Flow

interface CardFacilitiesTransactionRepository {
    fun topUpInquiry(
        transactionType: String,
        topUpOperator: String,
        amount: Long
    ): Flow<CustomResponse<CheckAmountResult>>

    fun topUpPayment(
        password: String,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData,
        chargeType: String,
        mobileNumber: String,
        topUpOperator: String,
        amount: Long
    ): Flow<CustomResponse<Any>>

    fun paymentWithWallet(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        expireYear: String?,
        expireMonth: String?
    ): Flow<CustomResponse<Unit>>

    fun internetPackageInquiry(
        operator: MobileOperatorTab
    ): Flow<CustomResponse<InternetPackageInquiryResponse>>

    fun internetPackageInquiryWithWallet(
        phoneNumber: String,
        operator: MobileOperatorTab,
        simType: SimType
    ): Flow<CustomResponse<InternetPackageInquiryWithWalletResponse>>

    fun internetPackagePayment(
        password: String,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData,
        mobileNumber: String,
        operator: MobileOperatorTab,
        packageItem: PackageItem?
    ): Flow<CustomResponse<Any>>

    suspend fun insertTransaction(transaction: TransactionEntity)

    suspend fun getTransactionsBySourceCardNo(cardNo: String): Flow<List<TransactionEntity>>
    fun getWalletTransactionHistory(walletId: String): Flow<CustomResponse<HistoryResult>>
    suspend fun deleteTransaction(timeStamp: Long)

}