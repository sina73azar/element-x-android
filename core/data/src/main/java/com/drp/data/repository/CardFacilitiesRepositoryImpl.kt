package com.drp.data.repository

import com.drp.data.database.entity.toCardEntity
import com.drp.data.database.entity.toCardShotItemInfo
import com.drp.data.database.entity.toHubCardInfo
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.model.Amount
import com.drp.data.model.Parameter
import com.drp.data.model.ShahkarUserData
import com.drp.data.model.balance.BalanceInquiryRequest
import com.drp.data.model.balance.BalanceInquiryResult
import com.drp.data.model.balance.CardBalanceFromWalletRequest
import com.drp.data.model.balance.CardBalanceFromWalletResult
import com.drp.data.model.card_to_card.refahi.transfer.TransferCardResult
import com.drp.data.model.iban_convertor.IbanConvertorRequest
import com.drp.data.model.iban_convertor.IbanConvertorResponse
import com.drp.data.model.last_ten_statement.BankStatement
import com.drp.data.model.last_ten_statement.LastTenStatementInquiryRequest
import com.drp.data.model.licence_negative_score.LicenceNegativeScoreInquiryRequest
import com.drp.data.model.licence_negative_score.LicenceNegativeScoreInquiryResponse
import com.drp.data.model.topup.find_operator.DetectTopUpOperatorRequest
import com.drp.data.model.tracking_post.TrackingPostRequest
import com.drp.data.model.tracking_post.TrackingPostResponse
import com.drp.data.model.wallet_add.AddToWalletRequest
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints
import com.drp.data.network.EndPoints.ACCOUNT_TO_IBAN_END_POINT
import com.drp.data.network.EndPoints.ALL_HUB_CARD_INFO
import com.drp.data.network.EndPoints.CARD_FUND_TRANSFER
import com.drp.data.network.EndPoints.CARD_INQUIRY
import com.drp.data.network.EndPoints.CARD_PASSWORD_INQUIRY
import com.drp.data.network.EndPoints.CARD_TO_CARD_TRANSFER_END_POINT
import com.drp.data.network.EndPoints.CARD_TO_IBAN_END_POINT
import com.drp.data.network.EndPoints.GET_CARDS
import com.drp.data.network.EndPoints.HUB_CARD_HOLDER_INQUIRY
import com.drp.data.network.EndPoints.HUB_CARD_INFO_ADD
import com.drp.data.network.EndPoints.HUB_CARD_TRANSFER
import com.drp.data.network.EndPoints.HUB_ENROLLMENT
import com.drp.data.network.EndPoints.HUB_REACTIVATION
import com.drp.data.network.EndPoints.HUB_SEND_OTP
import com.drp.data.network.EndPoints.HUB_TRANSACTION_LOGS
import com.drp.data.network.EndPoints.IBAN_TO_ACCOUNT_END_POINT
import com.drp.data.network.EndPoints.TRACKING_POST_INQUIRY_END_POINT
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.data.sharepref.DynamicPreferences
import com.drp.refah.card_facilities.data.model.ExpireDate
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.PaymentTrk2EquivData
import com.drp.refah.card_facilities.data.model.bill.otp.TotpRequest
import com.drp.refah.card_facilities.data.model.bill.otp.TotpResult
import com.drp.refah.card_facilities.data.model.card_info.CardNumberRequest
import com.drp.refah.card_facilities.data.model.card_info.CardNumberResult
import com.drp.refah.card_facilities.data.model.card_to_card.BankInfo
import com.drp.refah.card_facilities.data.model.card_to_card.DestinationPerson
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfo
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfoAddResponse
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfoRequest
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardsResponse
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
import com.drp.utils.ASCII_ENCODING
import com.drp.utils.EXECUTER_CALL_TYPE
import com.drp.utils.P_ACTORPERSONID
import com.drp.utils.P_AMOUNT
import com.drp.utils.P_BABATID
import com.drp.utils.P_CREDIT_DESCRIPTION
import com.drp.utils.P_CREDIT_FEEAMOUNT
import com.drp.utils.P_DEBIT_DESCRIPTION
import com.drp.utils.P_DEBIT_FEEAMOUNT
import com.drp.utils.P_FROMMOBILE
import com.drp.utils.P_FROMWALLET
import com.drp.utils.P_PIN
import com.drp.utils.P_REFCODE
import com.drp.utils.P_RQID
import com.drp.utils.P_TOMOBILE
import com.drp.utils.P_TOWALLET
import com.drp.utils.REQUEST_ID
import com.drp.utils.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CardFacilitiesRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dataBaseHelper: DataBaseRequest,
    private val dynamicPreferences: DynamicPreferences,
) :
    CardFacilitiesRepository {

    override fun addToWallet(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String
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
            url = EndPoints.ADD_TO_WALLET_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS) {
                selectedCard?.copy(
                    panExpiryYear = cardYear.substring(
                        2, 4
                    ),
                    panExpiryMonth = cardMonth
                )?.toCardEntity()?.let {
                    dataBaseHelper.upsertCardEntity(
                        it
                    )
                }
            }
        }
    }

    override fun transferWalletToWallet(
        userData: ShahkarUserData,
        amount: Long,
        destinationWalletId: String,
        description: String
    ): Flow<CustomResponse<Unit>> {
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_FROMWALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_TOWALLET,
                    value = destinationWalletId
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = amount.toString()
                ),
                Parameter(
                    name = P_DEBIT_DESCRIPTION,
                    value = description
                ),
                Parameter(
                    name = P_DEBIT_FEEAMOUNT,
                    value = "1"
                ),
                Parameter(
                    name = P_CREDIT_DESCRIPTION,
                    value = "1"
                ),
                Parameter(
                    name = P_CREDIT_FEEAMOUNT,
                    value = "1"
                ),

                Parameter(
                    name = P_BABATID,
                    value = "1"
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId?.toString() ?: ""
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_FROMMOBILE,
                    value = ""
                ),
                Parameter(
                    name = P_TOMOBILE,
                    value = ""
                ),
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = REQUEST_ID
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.WALLET_TO_WALLET_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        )
    }

    override fun minusFromWallet(rqId: String, pin: String): Flow<CustomResponse<Unit>> {
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
            url = EndPoints.MINUS_FROM_WALLET_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        )
    }

    override fun cardToCardTransfer(
        rqId: String,
        pin: String,
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String
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
            url = CARD_TO_CARD_TRANSFER_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS) {
                selectedCard?.copy(
                    panExpiryYear = cardYear.substring(
                        2, 4
                    ),
                    panExpiryMonth = cardMonth
                )?.toCardEntity()?.let {
                    dataBaseHelper.upsertCardEntity(
                        it
                    )
                }
            }
        }
    }

    override fun cardBalanceFromWalletInquiry(
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        pin: String
    ): Flow<CustomResponse<CardBalanceFromWalletResult>> {
        val request = CardBalanceFromWalletRequest(
            cardExpiration = cardYear.substring(2, 4).plus("/").plus(cardMonth),
            cardNumber = selectedCard?.pan ?: "",
            cvv2 = cvv2,
            pin = pin,
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.GET_BALANCE_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = CardBalanceFromWalletResult::class.java
        ).onEach { response ->
            if (response.status == CustomResponse.Status.SUCCESS) {
                selectedCard?.copy(
                    panExpiryYear = cardYear.substring(
                        2, 4
                    ),
                    panExpiryMonth = cardMonth
                )?.toCardEntity()?.let {
                    dataBaseHelper.upsertCardEntity(
                        it
                    )
                }
            }
        }
    }

    override fun cardBalanceInquiry(
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<BalanceInquiryResult>> {
        val headers: Map<String, String> = mapOf("password" to trk2EquivData.pin)
        val request = BalanceInquiryRequest(
            sourceCardNo = selectedCard?.pan,
            trk2EquivData = PaymentTrk2EquivData(
                expireDate = ExpireDate(
                    year = trk2EquivData.expireDate.substring(0, 2).toInt(),
                    month = trk2EquivData.expireDate.substring(2, 4).toInt()
                ),
                cvv2 = trk2EquivData.cvv2,
                pin = trk2EquivData.pin
            )
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.CARD_BALANCE_INQUIRY_END_POINT,
            request = request,
            headers = headers,
            kClass = BalanceInquiryResult::class.java
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

    override fun lastTenStatementInquiry(
        selectedCard: CardShotItemInfo?,
        trk2EquivData: Trk2EquivData
    ): Flow<CustomResponse<ArrayList<BankStatement>>> {
        val headers: Map<String, String> = mapOf("password" to trk2EquivData.pin)
        val request = LastTenStatementInquiryRequest(
            cardNumber = selectedCard?.pan,
            cardExpiration = "${
                trk2EquivData.expireDate.substring(
                    0,
                    2
                )
            }/${trk2EquivData.expireDate.substring(2, 4)}",
            cvv2 = trk2EquivData.cvv2,
            pin = trk2EquivData.pin
        )
        return dynamicApiCall.dynamicPostArrayCall(
            url = EndPoints.LAST_TEN_STATEMENT_INQUIRY_END_POINT,
            request = request,
            headers = headers,
            kClass = BankStatement::class.java
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

    override fun licenceNegativeScoreInquiry(
        licenceNumber: String,
        nationalId: String,
        mobileNumber: String
    ): Flow<CustomResponse<LicenceNegativeScoreInquiryResponse>> {
        val request = LicenceNegativeScoreInquiryRequest(
            licenseNumber = licenceNumber,
            mobileNumber = mobileNumber,
            nationalID = nationalId,
            traceNumber = "",
            walletIdentifier = mobileNumber
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.LICENCE_NEGATIVE_SCORE_INQUIRY_END_POINT,
            request = request,
            kClass = LicenceNegativeScoreInquiryResponse::class.java
        )
    }

    override fun trackingPostInquiry(trackingNumber: String): Flow<CustomResponse<TrackingPostResponse>> {
        val request = TrackingPostRequest(
            packageNumber = trackingNumber,
            traceNumber = ""
        )
        return dynamicApiCall.dynamicPostCall(
            url = TRACKING_POST_INQUIRY_END_POINT,
            request = request,
            kClass = TrackingPostResponse::class.java
        )
    }

    override fun ibanConvertor(
        convertType: IbanConvertType,
        cardNumber: String,
        accountNumber: String,
        iban: String,
        bankNameForServiceCall: String
    ): Flow<CustomResponse<IbanConvertorResponse>> {
        val request = when (convertType) {
            IbanConvertType.CardToIban -> IbanConvertorRequest(
                accountTypeName = "Deposit",
                cardNumber = cardNumber,
                traceNumber = ""
            )

            IbanConvertType.AccountToIban -> IbanConvertorRequest(
                accountTypeName = "Deposit",
                accountNumber = accountNumber,
                bankName = bankNameForServiceCall,
                traceNumber = ""
            )

            IbanConvertType.IbanToAccount -> IbanConvertorRequest(
                accountTypeName = "Deposit",
                shebaNumber = iban,
                traceNumber = ""
            )
        }
        return dynamicApiCall.dynamicPostCall(
            url = when (convertType) {
                IbanConvertType.CardToIban -> CARD_TO_IBAN_END_POINT
                IbanConvertType.AccountToIban -> ACCOUNT_TO_IBAN_END_POINT
                IbanConvertType.IbanToAccount -> IBAN_TO_ACCOUNT_END_POINT
            },
            request = request,
            kClass = IbanConvertorResponse::class.java
        )
    }

    override fun findOperatorType(mobilePrefix: String): Flow<CustomResponse<DetectTopUpOperatorResponse>> {
        val request = DetectTopUpOperatorRequest(mobilePrefix)
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.DETECT_TOP_UP_OPERATOR_END_POINT,
            request = request,
            kClass = DetectTopUpOperatorResponse::class.java
        )
    }

    /**card to card*/
    override fun cardInquiry(
        request: InquiryCardRequest,
        headers: Map<String, String>,
        mock: Boolean
    ): Flow<CustomResponse<InquiryCardResult>> {
        if (mock) {
            return flow {
                emit(
                    CustomResponse.Success(
                        InquiryCardResult(
                            customerName = DestinationPerson(
                                firstName = "adel",
                                lastName = "dadras"
                            ),
                            card = BankInfo(
                                destinationBankName = "refah",
                                imageUrl = ""
                            ),
                            amount = 11L,
                            fundTransfer = FundTransfer(
                                amount = 44,
                                source = "",

                                ),
                            trk2EquivData = Trk2EquivData("", "", "")
                        )
                    )
                )
            }
        }
        return dynamicApiCall.dynamicPostCall(
            url = CARD_INQUIRY,
            request = request,
            headers = headers,
            kClass = InquiryCardResult::class.java
        )
    }

    override fun cardFundTransfer(
        request: TransferCardRequest,
        headers: Map<String, String>,
        mock: Boolean
    ): Flow<CustomResponse<TransferCardResult>> =
        flow {
            if (mock) {
                emit(
                    CustomResponse.Success(
                        TransferCardResult(
                            fundTransfer = FundTransfer(
                                amount = 123123123,
                                source = "",
                                sourceAccount = null,
                                destination = request.fundTransfer.destination,
                                personName = DestinationPerson(
                                    firstName = "adellll",
                                    lastName = "anzh"
                                )
                            ),
                            balance = Amount(1212121212, 121212, ""),
                            destinationBankName = "",
                            processCode = ""
                        )
                    )
                )
                return@flow
            }
            emit(
                dynamicApiCall.dynamicPostCall(
                    url = CARD_FUND_TRANSFER,
                    request = request,
                    headers = headers,
                    kClass = TransferCardResult::class.java
                ).first()
            )
        }.flowOn(Dispatchers.IO)

    override fun cardPasswordInquiry(
        request: TotpRequest,
        headers: Map<String, String>
    ) = dynamicApiCall.dynamicPostCall(
        url = CARD_PASSWORD_INQUIRY,
        request = request,
        headers = headers,
        kClass = TotpResult::class.java
    )


    override fun getCardShotItems(): Flow<List<CardShotItemInfo>> {
        return flow<List<CardShotItemInfo>> {
            dataBaseHelper.getAllCardEntities().collect { entities ->
                logger()
                emit(entities.map { it.toCardShotItemInfo() })
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun removeCardShotItem(cardId: Int) {
        dataBaseHelper.deleteCardEntity(cardId)
    }

    override suspend fun setToDefaultCardShotItem(cardId: Int) {
        dataBaseHelper.setToDefaultCardEntity(cardId)
    }


    override suspend fun upsertCardShotItem(cardShotItemInfo: CardShotItemInfo): Flow<Boolean> =
        flow {
            dataBaseHelper.upsertCardEntity(cardShotItemInfo.toCardEntity())
            emit(true)
        }.catch {
            emit(false)
        }.flowOn(Dispatchers.IO)


    /**-----------------------------------Card info ------------------------*/


    override fun getCards(request: CardNumberRequest): Flow<CustomResponse<CardNumberResult>> =
        dynamicApiCall.dynamicPostCall(
            url = GET_CARDS,
            request = request,
            kClass = CardNumberResult::class.java
        )


    /**
     * Mr.C 1403/01/25
     * ---------------------------------------HUB---------------------------------
     * */
    override suspend fun hubEnrollment(hubEnrollmentRequest: HubEnrollmentRequest) =
        dynamicApiCall.dynamicPostCall(
            url = HUB_ENROLLMENT,
            request = hubEnrollmentRequest,
            kClass = HubEnrollmentResponse::class.java
        )

    override fun getAllHubCardInfo(): Flow<CustomResponse<List<HubCardInfo>>> {
        return flow<CustomResponse<List<HubCardInfo>>> {
            dataBaseHelper.getAllCardEntities().collect { cachedCards ->
                if (cachedCards.isEmpty()) {
                    val callResult = dynamicApiCall.dynamicGetCall(
                        url = ALL_HUB_CARD_INFO,
                        kClass = HubCardsResponse::class.java
                    ).first()
                    when (callResult.status) {
                        CustomResponse.Status.SUCCESS -> {
                            if (callResult.data?.hubCards.isNullOrEmpty()) {
                                emit(CustomResponse.Success(data = listOf<HubCardInfo>()))
                                return@collect
                            }

                            val nonEmptyEntities =
                                callResult.data?.hubCards
                            //insert
                            nonEmptyEntities?.forEach {
                                dataBaseHelper.upsertCardEntity(it.toCardEntity())
                            }
                            emit(
                                CustomResponse.Success(nonEmptyEntities)
                            )
                        }

                        CustomResponse.Status.Fail -> {
                            emit(CustomResponse.Fail())
                        }

                        CustomResponse.Status.ERROR -> {
                            emit(CustomResponse.Error(callResult.message))
                        }

                        else -> {}
                    }

                    return@collect
                }
                emit(
                    CustomResponse.Success(
                        cachedCards.map { it.toHubCardInfo() }
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Mr.C 1403/1/15
     * after getting public key we call this channel service to get cardData and register it channel side
     * */

    override suspend fun hubCardInfoAdd(hubCardInfoRequest: HubCardInfoRequest) =
        dynamicApiCall.dynamicPostCall(
            url = HUB_CARD_INFO_ADD,
            request = hubCardInfoRequest,
            kClass = HubCardInfoAddResponse::class.java
        ).onEach {
            it.data?.let { hubCardInfoAddResponse ->
                if ((it.status == CustomResponse.Status.SUCCESS)) {
                    hubCardInfoAddResponse.hubCardInfo?.let { hubCardInfo ->
                        insertOrUpdateHubRegisteredCard(hubCardInfo)
                    }
                }
            }
        }

    private suspend fun insertOrUpdateHubRegisteredCard(hubCardInfo: HubCardInfo) {
        if (hubCardInfo.transactionId.isNotBlank()) {
            dataBaseHelper.upsertCardEntity(hubCardInfo.toCardEntity())
        }
    }


    override suspend fun deleteHubCardInfos() {
        dataBaseHelper.deleteCardEntities()
    }

    override suspend fun getHubPublicKey(
        keyRetrievalRequest: KeyRetrievalRequest
    ) = dynamicApiCall.dynamicPostCall(
        url = "BuildConfig.HUB_KEY_RETREIVAL_FULL_URL,",
        request = keyRetrievalRequest,
        kClass = KeyRetrievalResponse::class.java
    )

    override fun saveTempHubTransactionId(transactionId: String) {
        dynamicPreferences.saveData(HUB_TRANSACTION_ID_KEY, transactionId)
    }

    override fun getTempHubTransactionId(): String =
        dynamicPreferences.loadData(HUB_TRANSACTION_ID_KEY, "") as String

    override fun saveHubPublicKeyPref(keyData: String) {
        dynamicPreferences.saveData(HUB_PUBLIC_KEY, keyData)
    }

    override fun getHubPublicKeyPref(): String =
        dynamicPreferences.loadData(HUB_PUBLIC_KEY, "") as String

    override suspend fun hubCardHolderInquiry(
        hubCardHolderInquiryRequest: com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry.HubCardHolderInquiryRequest,
    ) = dynamicApiCall.dynamicPostCall(
        url = HUB_CARD_HOLDER_INQUIRY,
        request = hubCardHolderInquiryRequest,
        kClass = com.drp.refah.card_facilities.data.model.card_to_card.hub.card_holder_inquiry.HubCardHolderInquiryResponse::class.java
    )

    /**
     * Mr.C 1403/1/28
     * */

    override suspend fun hubSendOtp(
        hubSendOtpRequest: HubSendOtpRequest
    ) = dynamicApiCall.dynamicPostCall(
        url = HUB_SEND_OTP,
        request = hubSendOtpRequest,
        kClass = HubSendOtpResponse::class.java
    )

    override suspend fun hubCardTransfer(
        hubCardTransferRequest: HubCardTransferRequest
    ) = dynamicApiCall.dynamicPostCall(
        url = HUB_CARD_TRANSFER,
        request = hubCardTransferRequest,
        kClass = HubCardTransferResponse::class.java
    )

    /**
     * Mr.C 1403/2/1
     * */

    override suspend fun hubReActivation(
        hubReActivationRequest: HubReActivationRequest
    ) = dynamicApiCall.dynamicPostCall(
        url = HUB_REACTIVATION,
        request = hubReActivationRequest,
        kClass = HubReActivationResponse::class.java
    )

    override fun hubTransactionLogs(
        hubTransactionLogsRequest: HubTransactionLogsRequest
    ) = dynamicApiCall.dynamicPostCall(
        url = HUB_TRANSACTION_LOGS,
        request = hubTransactionLogsRequest,
        kClass = HubTransactionLogsResponse::class.java
    )

    companion object {

        const val HUB_TRANSACTION_ID_KEY = "hub_transaction_id"
        const val HUB_PUBLIC_KEY = "hub_public_key"
        const val SHAHKAR_USER_DATA = "shahkar_user_data"
        const val NATIONAL_CODE: String = "national_code"
        const val PHONE_NUMBER: String = "phone_number"
    }

}