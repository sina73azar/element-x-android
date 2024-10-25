package com.drp.data.repository

import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.toCardEntity
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.enums.SimType
import com.drp.data.model.Parameter
import com.drp.data.model.history.HistoryRequest
import com.drp.data.model.history.HistoryResult
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryRequest
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryResponse
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryWithWalletRequest
import com.drp.data.model.internet_package.inquiry.InternetPackageInquiryWithWalletResponse
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.model.wallet_add.AddToWalletRequest
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints
import com.drp.data.network.EndPoints.GET_WALLET_TRANS_END_POINT
import com.drp.data.network.EndPoints.INTERNET_PACKAGE_INQUIRY_END_POINT
import com.drp.data.network.EndPoints.INTERNET_PACKAGE_INQUIRY_WITH_WALLET_END_POINT
import com.drp.data.network.EndPoints.INTERNET_PACKAGE_PAYMENT_END_POINT
import com.drp.data.network.EndPoints.TOP_UP_PAYMENT_END_POINT
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.refah.card_facilities.data.model.ExpireDate
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.drp.refah.card_facilities.data.model.internet_package.payment.InternetPackagePaymentRequest
import com.drp.refah.card_facilities.data.model.topup.inquiry.CheckAmountResult
import com.drp.refah.card_facilities.data.model.topup.inquiry.TransactionAmount
import com.drp.refah.card_facilities.data.model.topup.payment.PaymentTopUpRequest
import com.drp.refah.card_facilities.data.model.topup.payment.TopUpPayment
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.utils.ASCII_ENCODING
import com.drp.utils.EXECUTER_CALL_TYPE
import com.drp.utils.P_PIN
import com.drp.utils.P_RQID
import com.drp.utils.REQUEST_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CardFacilitiesTransactionRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dataBaseHelper: DataBaseRequest
) :
    CardFacilitiesTransactionRepository {
    override fun topUpInquiry(
        transactionType: String,
        topUpOperator: String,
        amount: Long
    ): Flow<CustomResponse<CheckAmountResult>> {
        val request = TransactionAmount(
            transactionType = transactionType,
            topupOperator = topUpOperator,
            amount = amount
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.TOP_UP_INQUIRY_END_POINT,
            request = request,
            kClass = CheckAmountResult::class.java
        )
    }

    override fun topUpPayment(
        password: String,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData,
        chargeType: String,
        mobileNumber: String,
        topUpOperator: String,
        amount: Long
    ): Flow<CustomResponse<Any>> {
        val headers: Map<String, String> = mapOf("password" to password)
        val request = PaymentTopUpRequest(
            chargeType = chargeType,
            sourceCardNo = selectedCard?.pan,
            fundTransfer = TopUpPayment(amount = amount, account = ""),
            trk2EquivData = PaymentTrk2EquivData(
                expireDate = ExpireDate(
                    year = trk2EquivData.expireDate.substring(0, 2).toInt(),
                    month = trk2EquivData.expireDate.substring(2, 4).toInt()
                ),
                cvv2 = trk2EquivData.cvv2,
                pin = password
            ),
            mobileNumber = mobileNumber,
            topupOperator = topUpOperator,
//            chargeTypeName = null
        )
//        return flowOf(CustomResponse.Success(data = ""))
        return dynamicApiCall.dynamicPostCall(
            url = TOP_UP_PAYMENT_END_POINT,
            request = request,
            headers = headers,
            kClass = Any::class.java
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

    override fun paymentWithWallet(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        expireYear: String?,
        expireMonth: String?
    ): Flow<CustomResponse<Unit>> {
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_RQID,
                    value = rqId
                ),
                Parameter(
                    name = P_PIN,
                    value = pin
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = REQUEST_ID
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.PAYMENT_WALLET_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS) {
                selectedCard?.copy(
                    panExpiryYear = expireYear?.substring(
                        2, 4
                    ),
                    panExpiryMonth = expireMonth
                )?.toCardEntity()?.let {
                    dataBaseHelper.upsertCardEntity(
                        it
                    )
                }
            }
        }
    }

    override fun internetPackageInquiry(
        operator: MobileOperatorTab
    ): Flow<CustomResponse<InternetPackageInquiryResponse>> {
        val request =
            InternetPackageInquiryRequest(operator = operator.name, productType = "PACKAGE")
        return dynamicApiCall.dynamicPostCall(
            url = INTERNET_PACKAGE_INQUIRY_END_POINT,
            request = request,
            kClass = InternetPackageInquiryResponse::class.java
        )
    }

    override fun internetPackageInquiryWithWallet(
        phoneNumber: String,
        operator: MobileOperatorTab,
        simType: SimType
    ): Flow<CustomResponse<InternetPackageInquiryWithWalletResponse>> {
        val request =
            InternetPackageInquiryWithWalletRequest(
                operator = if (operator == MobileOperatorTab.MTN) "Irancell" else operator.name,
                simType = simType.name,
                /*accessParameter = phoneNumber,
                topUpOperator = operator.walletIndex*/
            )
        return dynamicApiCall.dynamicPostCall(
            url = INTERNET_PACKAGE_INQUIRY_WITH_WALLET_END_POINT,
            request = request,
            kClass = InternetPackageInquiryWithWalletResponse::class.java
        )
    }

    override fun internetPackagePayment(
        password: String,
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData,
        mobileNumber: String,
        operator: MobileOperatorTab,
        packageItem: PackageItem?
    ): Flow<CustomResponse<Any>> {
        val headers: Map<String, String> = mapOf("password" to password)
        val request = InternetPackagePaymentRequest(
            productCode = packageItem?.code!!,
            amountWithTax = (packageItem.price + packageItem.tax).toString(),
            amountWithoutTax = packageItem.price.toString(),
            sourceCardNo = selectedCard?.pan,
            trk2EquivData = PaymentTrk2EquivData(
                expireDate = ExpireDate(
                    year = trk2EquivData.expireDate.substring(0, 2).toInt(),
                    month = trk2EquivData.expireDate.substring(2, 4).toInt()
                ),
                cvv2 = trk2EquivData.cvv2,
                pin = password
            ),
            mobileNumber = mobileNumber.filter { it.isDigit() },
            topupOperator = operator.name,
            amount = packageItem.price
//            fundTransfer = FundTransfer(amount = amount, accountNo = selectedCard?.pan ?: ""),
        )
//        return flowOf(CustomResponse.Success(data = ""))
        return dynamicApiCall.dynamicPostCall(
            url = INTERNET_PACKAGE_PAYMENT_END_POINT,
            request = request,
            headers = headers,
            kClass = Any::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS) {
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

    override fun getWalletTransactionHistory(walletId: String): Flow<CustomResponse<HistoryResult>> {
        val request = HistoryRequest(
            walletId = walletId,
            fromDate = "14030701",
            toDate = "14031201"
        )
        return dynamicApiCall.dynamicPostCall(
            url = GET_WALLET_TRANS_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = HistoryResult::class.java
        )
    }

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        dataBaseHelper.insertTransaction(transaction)
    }

    override suspend fun getTransactionsBySourceCardNo(cardNo: String): Flow<List<TransactionEntity>> =
        dataBaseHelper.getTransactionsBySourceCardNo(cardNo)

    override suspend fun deleteTransaction(timeStamp: Long) {
        dataBaseHelper.deleteTransaction(timeStamp)
    }

}

