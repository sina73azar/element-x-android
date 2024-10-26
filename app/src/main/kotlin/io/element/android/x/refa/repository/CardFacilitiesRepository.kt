package com.drp.data.repository


import com.drp.data.model.ShahkarUserData
import com.drp.data.model.balance.BalanceInquiryResult
import com.drp.data.model.balance.CardBalanceFromWalletResult
import com.drp.data.model.card_to_card.refahi.transfer.TransferCardResult
import com.drp.data.model.iban_convertor.IbanConvertorResponse
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.data.model.licence_negative_score.LicenceNegativeScoreInquiryResponse
import com.drp.data.model.motor_violation.MotorViolationResponse
import com.drp.data.model.tracking_post.TrackingPostResponse
import com.drp.data.model.vehicle_violation.VehicleViolationResponse
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.otp.TotpRequest
import com.drp.refah.card_facilities.data.model.bill.otp.TotpResult
import com.drp.refah.card_facilities.data.model.card_info.CardNumberRequest
import com.drp.refah.card_facilities.data.model.card_info.CardNumberResult
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry.HubCardHolderInquiryResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfo
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfoAddResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfoRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.enroll.HubEnrollmentRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.enroll.HubEnrollmentResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.otp.HubSendOtpRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.otp.HubSendOtpResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.public_key.KeyRetrievalRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.public_key.KeyRetrievalResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.reactivation.HubReActivationRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.reactivation.HubReActivationResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.transaction_logs.HubTransactionLogsRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.transaction_logs.HubTransactionLogsResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.transfer.HubCardTransferRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.transfer.HubCardTransferResponse
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardRequest
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardResult
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.transfer.TransferCardRequest
import com.drp.refah.card_facilities.data.model.topup.find_operator.DetectTopUpOperatorResponse
import com.drp.shared_ui.enums.IbanConvertType
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.flow.Flow

interface CardFacilitiesRepository {

    /**------------------------ wallet -------------------------*/
    fun addToWallet(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String
    ): Flow<CustomResponse<Unit>>

    fun transferWalletToWallet(
        userData: ShahkarUserData,
        amount: Long,
        destinationWalletId: String,
        description: String
    ): Flow<CustomResponse<Unit>>

    fun minusFromWallet(rqId: String, pin: String): Flow<CustomResponse<Unit>>

    fun cardToCardTransfer(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String
    ): Flow<CustomResponse<Unit>>

    fun cardBalanceInquiry(
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<BalanceInquiryResult>>

    fun lastTenStatementInquiry(
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<ArrayList<BankStatement>>>

    fun licenceNegativeScoreInquiry(
        licenceNumber: String,
        nationalId: String,
        mobileNumber: String
    ): Flow<CustomResponse<LicenceNegativeScoreInquiryResponse>>

    fun vehicleViolationInquiry(
        leftNumber: String,
        alphabetic: String,
        midNumber: String,
        rightNumber: String,
        mobileNumber: String,
        nationalId: String,
        walletId: String
    ): Flow<CustomResponse<VehicleViolationResponse>>

    fun motorViolationInquiry(
        leftNumber: String,
        rightNumber: String,
        mobileNumber: String,
        nationalId: String,
        walletId: String
    ): Flow<CustomResponse<MotorViolationResponse>>

    fun trackingPostInquiry(
        trackingNumber: String
    ): Flow<CustomResponse<TrackingPostResponse>>

    fun ibanConvertor(
        convertType: IbanConvertType,
        cardNumber: String = "",
        accountNumber: String = "",
        iban: String = "",
        bankNameForServiceCall: String = ""
    ): Flow<CustomResponse<IbanConvertorResponse>>

    fun findOperatorType(mobilePrefix: String): Flow<CustomResponse<DetectTopUpOperatorResponse>>

    /**card to card*/
    /**------------------------card to card-------------------------*/

    fun cardBalanceFromWalletInquiry(
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        pin: String
    ): Flow<CustomResponse<CardBalanceFromWalletResult>>

    fun cardInquiry(
        request: InquiryCardRequest,
        headers: Map<String, String>,
        mock: Boolean = false
    ): Flow<CustomResponse<InquiryCardResult>>


    fun cardFundTransfer(
        request: TransferCardRequest,
        headers: Map<String, String>,
        mock: Boolean = false
    ): Flow<CustomResponse<TransferCardResult>>


    fun cardPasswordInquiry(
        request: TotpRequest,
        headers: Map<String, String>
    ): Flow<CustomResponse<TotpResult>>


    /**----------------------Source cards -------------------------*/

    fun getCardShotItems(): Flow<List<CardShotItemInfo>>

    suspend fun upsertCardShotItem(cardShotItemInfo: CardShotItemInfo): Flow<Boolean>

    suspend fun removeCardShotItem(cardId: Int)
    suspend fun setToDefaultCardShotItem(cardId: Int)


    /**-----------------------------------Card info ------------------------*/

    fun getCards(request: CardNumberRequest): Flow<CustomResponse<CardNumberResult>>

    /**
     * Mr.C 1403/2/11
     * -------------------------------------HUB-----------------------------------------
     * */

    /**
     * return hub registration address
     * */
    suspend fun hubEnrollment(hubEnrollmentRequest: HubEnrollmentRequest): Flow<CustomResponse<HubEnrollmentResponse>>


    /**
     * HubcardInfo all registered cards
     * if cache is empty fetch channel db then reWrite cache onSuccess
     * */
    fun getAllHubCardInfo(): Flow<CustomResponse<List<HubCardInfo>>>

    /**
     * after registering new card in hub call this to kind of register it in channel manager
     * add HubCardInfo record entry after getting success of this api (insert method of hubCardInfoEntity not exposed from cardRepository)
     * in the impl class we receive HubCardInfoResponse there will handle hubError if exists and map it to HubCardInfoEntity
     * */
    suspend fun hubCardInfoAdd(hubCardInfoRequest: HubCardInfoRequest): Flow<CustomResponse<HubCardInfoAddResponse>>

    suspend fun deleteHubCardInfos()

    suspend fun getHubPublicKey(
        keyRetrievalRequest: KeyRetrievalRequest
    ): Flow<CustomResponse<KeyRetrievalResponse>>


    fun saveTempHubTransactionId(transactionId: String)

    fun getTempHubTransactionId(): String
    fun saveHubPublicKeyPref(keyData: String)

    fun getHubPublicKeyPref(): String

    /**
     * Mr.C 1403/1/20
     *
     * */
    suspend fun hubCardHolderInquiry(
        hubCardHolderInquiryRequest: com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry.HubCardHolderInquiryRequest
    ): Flow<CustomResponse<HubCardHolderInquiryResponse>>

    /**
     * Mr.C 1403/1/28
     * */

    suspend fun hubSendOtp(
        hubSendOtpRequest: HubSendOtpRequest,
    ): Flow<CustomResponse<HubSendOtpResponse>>

    suspend fun hubCardTransfer(
        hubCardTransferRequest: HubCardTransferRequest
    ): Flow<CustomResponse<HubCardTransferResponse>>


    /**
     * Mr.C 1403/2/1
     * */
    suspend fun hubReActivation(
        hubReActivationRequest: HubReActivationRequest
    ): Flow<CustomResponse<HubReActivationResponse>>


    /**
     * Mr.C 1403/2/12
     * */
    fun hubTransactionLogs(
        hubTransactionLogsRequest: HubTransactionLogsRequest
    ): Flow<CustomResponse<HubTransactionLogsResponse>>


}