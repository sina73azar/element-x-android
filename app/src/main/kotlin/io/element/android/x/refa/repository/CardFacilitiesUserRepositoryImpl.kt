package com.drp.data.repository

import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.impl.DataBaseRequest
import com.drp.data.enums.BillType
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.enums.ContactType
import com.drp.data.model.Parameter
import com.drp.data.model.ShahkarUserData
import com.drp.data.model.balance.BalanceOtpRequest
import com.drp.data.model.bill.SeparatedBillPaymentWithWalletRequest
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.model.internet_package.payment.InternetPackagePaymentWithWalletRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.model.topup.payment.PaymentTopUpWithWalletRequest
import com.drp.data.model.wallet_add.AddToWalletRequest
import com.drp.data.model.wallet_add.WalletResponse
import com.drp.data.network.CustomResponse
import com.drp.data.network.EndPoints
import com.drp.data.network.EndPoints.ADD_TO_WALLET_OTP_END_POINT
import com.drp.data.network.EndPoints.CARD_TO_CARD_OTP_END_POINT
import com.drp.data.network.EndPoints.DYN_PIN_END_POINT
import com.drp.data.network.EndPoints.GET_WALLET_BALANCE_END_POINT
import com.drp.data.network.EndPoints.MINUS_FROM_WALLET_OTP_END_POINT
import com.drp.data.network.EndPoints.PAYMENT_WALLET_OTP_END_POINT
import com.drp.data.network.EndPoints.SHAHKAR_INQUIRY_ENDPOINT
import com.drp.data.network.EndPoints.SHAHKAR_VALIDATE_ENDPOINT
import com.drp.data.network.api_call.DynamicApiCall
import com.drp.data.repository.CardFacilitiesRepositoryImpl.Companion.SHAHKAR_USER_DATA
import com.drp.data.sharepref.DynamicPreferences
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.data.model.bill.otp.OtpRequest
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.utils.ASCII_ENCODING
import com.drp.utils.EXECUTER_CALL_TYPE
import com.drp.utils.P_ACTORPERSONID
import com.drp.utils.P_AMOUNT
import com.drp.utils.P_DESCRIPTION
import com.drp.utils.P_DST_CARD_NO
import com.drp.utils.P_FROMIBAN
import com.drp.utils.P_MOBILE
import com.drp.utils.P_PAYMENTTYPE
import com.drp.utils.P_POSTCALL_BODY
import com.drp.utils.P_POSTCALL_METHOD
import com.drp.utils.P_POSTCALL_URL
import com.drp.utils.P_REFCODE
import com.drp.utils.P_SEND_OTP_SMS
import com.drp.utils.P_SRC_CARD_CVV2
import com.drp.utils.P_SRC_CARD_EXP_MONTH
import com.drp.utils.P_SRC_CARD_EXP_YEAR
import com.drp.utils.P_SRC_CARD_NO
import com.drp.utils.P_TOIBAN
import com.drp.utils.P_WALLET
import com.drp.utils.P_WALLET_ID
import com.drp.utils.REQUEST_ID
import com.google.gson.Gson
import io.element.android.x.refa.Constants
import io.element.android.x.refa.Constants.AUTHENTICATION_IDS_LIST
import io.element.android.x.refa.Constants.AUTHENTICATION_TYPE
import io.element.android.x.refa.Constants.PASSWORD_AUTHENTICATION_VALUE
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CardFacilitiesUserRepositoryImpl @Inject constructor(
    private val dynamicApiCall: DynamicApiCall,
    private val dynamicPreferences: DynamicPreferences,
    private val dataBaseHelper: DataBaseRequest
) :
    CardFacilitiesUserRepository {
    override fun sendOtp(
        reason: String?,
        receiver: String?,
        amount: Long?
    ): Flow<CustomResponse<Any>> {
        val request = OtpRequest(
            "",
            reason,
            receiver,
            amount
        )
        return dynamicApiCall.dynamicPostCall(
            url = EndPoints.OTP_REQUEST_END_POINT,
            request = request,
            kClass = Any::class.java
        )
    }
    override fun saveShahkarUserData(shahkarUserData: ShahkarUserData) {
        val shahkarUserDataJson = Json.encodeToString(shahkarUserData)
        dynamicPreferences.saveData(SHAHKAR_USER_DATA, shahkarUserDataJson)
    }
    override fun authValidate(request: ShahkarValidateRequest): Flow<CustomResponse<ShahkarValidateResult>> {
        return dynamicApiCall.dynamicPostCall(
            url = SHAHKAR_VALIDATE_ENDPOINT,
            request = request,
            kClass = ShahkarValidateResult::class.java
        )
    }
    override fun shahkarInquiry(request: ShahkarInquiryRequest): Flow<CustomResponse<ShahkarInquiryResult>> {
        return dynamicApiCall.dynamicPostCall(
            url = SHAHKAR_INQUIRY_ENDPOINT,
            request = request,
            kClass = ShahkarInquiryResult::class.java
        )
    }
//    override fun cardPasswordInquiry(
//        selectedCard: CardShotItemInfo?,
//        amount: Long,
//        trk2EquivData: Trk2EquivData,
//        requestType: CardOtpRequestType
//    ): Flow<CustomResponse<TotpResult>> {
//        val headers: Map<String, String> =
//            mapOf(
//                "pin" to "",
//                "cvv2" to trk2EquivData.cvv2,
//                "cardExpirationYearMonth" to trk2EquivData.expireDate
//            )
//        val request = TotpRequest(
//            CardInfo(sourceCardNumber = selectedCard?.pan),
//            "",
//            trk2EquivData,
//            amount,
//            requestType.name
//        )
//        return dynamicApiCall.dynamicPostCall(
//            url = CARD_PASSWORD_INQUIRY_END_POINT,
//            request = request,
//            headers = headers,
//            kClass = TotpResult::class.java
//        )
//    }

    override fun cardOtp(
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        requestType: CardOtpRequestType
    ): Flow<CustomResponse<Unit>> {
        val request = BalanceOtpRequest(
            cardExpiration = cardYear.substring(2, 4).plus("/").plus(cardMonth),
            cardNumber = selectedCard?.pan ?: "",
            cvv2 = cvv2,
            pin = "0",
            amount = 0L,
            dynPinRequestType = requestType.dynPinRequestType ?: 0,
            additionalInformation = ""
        )
        return dynamicApiCall.dynamicPostCall(
            url = DYN_PIN_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = Unit::class.java
        )
    }

    override fun addToWalletOtp(
        amount: Long,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        description: String,
        userData: ShahkarUserData
    ): Flow<CustomResponse<WalletResponse>> {
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = amount.toString()
                ),
                Parameter(
                    name = P_SRC_CARD_NO,
                    value = sourceCardNumber
                ),
                Parameter(
                    name = P_SRC_CARD_CVV2,
                    value = sourceCardCvv2
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_YEAR,
                    value = sourceCardExpireYear.substring(2, 4)
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_MONTH,
                    value = sourceCardExpireMonth
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = description
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = ADD_TO_WALLET_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun minusFromWalletOtp(
        destinationIban: String,
        destinationCard: String,
        amount: Long,
        description: String,
        userData: ShahkarUserData
    ): Flow<CustomResponse<WalletResponse>> {
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = amount.toString()
                ),
                Parameter(
                    name = P_TOIBAN,
                    value = destinationCard.ifEmpty { "IR".plus(destinationIban) }
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = description
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = MINUS_FROM_WALLET_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun cardToCardOtp(
        selectedCard: CardShotItemInfo?,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        destinationCardNumber: String,
        amount: Long
    ): Flow<CustomResponse<WalletResponse>> {
        val userData = getShahkarUserData()
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = amount.toString()
                ),
                Parameter(
                    name = P_SRC_CARD_NO,
                    value = selectedCard?.pan!!
                ),
                Parameter(
                    name = P_SRC_CARD_CVV2,
                    value = cvv2
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_YEAR,
                    value = cardYear.substring(2, 4)
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_MONTH,
                    value = cardMonth
                ),
                Parameter(
                    name = P_DST_CARD_NO,
                    value = destinationCardNumber
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = ""
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = CARD_TO_CARD_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun topUpWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        amount: Long,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String
    ): Flow<CustomResponse<WalletResponse>> {
        val body = Gson().toJson(
            PaymentTopUpWithWalletRequest(
                sourceAccountNo = /*if (isFromWallet) "%MAINACCOUNT%" else ""*/"%MAINACCOUNT%",
                amount = amount,
                mobileNumber = topUpPhoneNumber,
                topUpOperator = topUpOperatorTab.walletIndex
            )
        )
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_PAYMENTTYPE,
                    value = if (isFromWallet) "1" else "2"
                ),
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = amount.toString()
                ),
                Parameter(
                    name = P_FROMIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_TOIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_SRC_CARD_NO,
                    value = if (!isFromWallet) sourceCardNumber else ""
                ),
                Parameter(
                    name = P_SRC_CARD_CVV2,
                    value = if (!isFromWallet) sourceCardCvv2 else ""
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_YEAR,
                    value = if (!isFromWallet) sourceCardExpireYear.substring(2, 4) else "0"
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_MONTH,
                    value = if (!isFromWallet) sourceCardExpireMonth else "0"
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = ""
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                ),
                Parameter(
                    name = P_POSTCALL_URL,
                    value = "/ApiGateway/Neo/TopUp"
                ),
                Parameter(
                    name = P_POSTCALL_METHOD,
                    value = "POST"
                ),
                Parameter(
                    name = P_POSTCALL_BODY,
                    value = body
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = PAYMENT_WALLET_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun internetPackageWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String,
        packageItem: PackageItem
    ): Flow<CustomResponse<WalletResponse>> {

        val body = Gson().toJson(
            InternetPackagePaymentWithWalletRequest(
                mobileNumber = topUpPhoneNumber,
//                accessParameter = topUpPhoneNumber,
//                sourceAccountNo = "",
//                amount = packageItem.price + packageItem.tax,
//                topUpOperator = topUpOperatorTab.walletIndex,
                productCode = packageItem.code,
//                amountWithTax = (packageItem.price + packageItem.tax).toString(),
//                amountWithoutTax = packageItem.price.toString()
            )
        )

        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_PAYMENTTYPE,
                    value = if (isFromWallet) "1" else "2"
                ),
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = (packageItem.price + packageItem.tax).toString()
                ),
                Parameter(
                    name = P_FROMIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_TOIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_SRC_CARD_NO,
                    value = if (!isFromWallet) sourceCardNumber else ""
                ),
                Parameter(
                    name = P_SRC_CARD_CVV2,
                    value = if (!isFromWallet) sourceCardCvv2 else ""
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_YEAR,
                    value = if (!isFromWallet) sourceCardExpireYear.substring(2, 4) else "0"
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_MONTH,
                    value = if (!isFromWallet) sourceCardExpireMonth else "0"
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = ""
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                ),
                Parameter(
                    name = P_POSTCALL_URL,
                    value = "/ApiGateway/Bills/TopUpPackageAdd"
                ),
                Parameter(
                    name = P_POSTCALL_METHOD,
                    value = "POST"
                ),
                Parameter(
                    name = P_POSTCALL_BODY,
                    value = body
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = PAYMENT_WALLET_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun separatedBillWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        billType: BillType,
        billPaymentInfo: BillPaymentInfo
    ): Flow<CustomResponse<WalletResponse>> {
        val body = Gson().toJson(
            SeparatedBillPaymentWithWalletRequest(
                billType = billType.walletIndex,
                sourceAccountNo = "%MAINACCOUNT%",
                amount = billPaymentInfo.amount,
                billId = billPaymentInfo.billId!!,
                paymentId = billPaymentInfo.paymentId!!
            )
        )

        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_PAYMENTTYPE,
                    value = if (isFromWallet) "1" else "2"
                ),
                Parameter(
                    name = P_WALLET,
                    value = userData.walletId.toString()
                ),
                Parameter(
                    name = P_AMOUNT,
                    value = billPaymentInfo.amount.toString()
                ),
                Parameter(
                    name = P_FROMIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_TOIBAN,
                    value = ""
                ),
                Parameter(
                    name = P_SRC_CARD_NO,
                    value = if (!isFromWallet) sourceCardNumber else ""
                ),
                Parameter(
                    name = P_SRC_CARD_CVV2,
                    value = if (!isFromWallet) sourceCardCvv2 else ""
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_YEAR,
                    value = if (!isFromWallet) sourceCardExpireYear.substring(2, 4) else "0"
                ),
                Parameter(
                    name = P_SRC_CARD_EXP_MONTH,
                    value = if (!isFromWallet) sourceCardExpireMonth else "0"
                ),
                Parameter(
                    name = P_SEND_OTP_SMS,
                    value = "1"
                ),
                Parameter(
                    name = P_DESCRIPTION,
                    value = ""
                ),
                Parameter(
                    name = P_ACTORPERSONID,
                    value = userData.personId.toString()
                ),
                Parameter(
                    name = P_REFCODE,
                    value = ""
                ),
                Parameter(
                    name = P_MOBILE,
                    value = userData.phoneNumber ?: ""
                ),
                Parameter(
                    name = P_POSTCALL_URL,
                    value = "/ApiGateway/Neo/PayBill"
                ),
                Parameter(
                    name = P_POSTCALL_METHOD,
                    value = "POST"
                ),
                Parameter(
                    name = P_POSTCALL_BODY,
                    value = body
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = "RequestID"
        )
        return dynamicApiCall.dynamicPostCall(
            url = PAYMENT_WALLET_OTP_END_POINT,
            request = request,
//            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun getWalletBalance(): Flow<CustomResponse<WalletResponse>> {
        val userData = getShahkarUserData()
        val request = AddToWalletRequest(
            parameters = listOf(
                Parameter(
                    name = P_WALLET_ID,
                    value = userData.walletId?.toString() ?: ""
                )
            ),
            callType = EXECUTER_CALL_TYPE,
            encoding = ASCII_ENCODING,
            requestId = REQUEST_ID
        )
        return dynamicApiCall.dynamicPostCall(
            url = GET_WALLET_BALANCE_END_POINT,
            request = request,
            headers = mapOf("Authorization" to "Basic d2FsbGV0OmFzZDEyMyFAIw=="),
            kClass = WalletResponse::class.java
        )
    }

    override fun addServiceAuthenticationId(id: Int) {
        if (!isServiceAuthenticationEnabled(id))
            getAuthenticationServiceIds().let {
                it.add(id)
                saveAuthenticationServiceIds(it)
            }
    }

    override fun removeServiceAuthenticationId(id: Int) {
        if (isServiceAuthenticationEnabled(id)) {
            getAuthenticationServiceIds().let {
                it.remove(id)
                saveAuthenticationServiceIds(it)
            }
        }
    }

    override fun isServiceAuthenticationEnabled(id: Int): Boolean =
        getAuthenticationServiceIds().contains(id)

    override fun getAuthenticationServiceIds() = Json.decodeFromString<MutableList<Int>>(
        dynamicPreferences.loadData(
            AUTHENTICATION_IDS_LIST,
            Json.encodeToString<MutableList<Int>>(mutableListOf())
        ) as String
    )

    override fun clearServiceAuthenticationList() {
        dynamicPreferences.removeData(AUTHENTICATION_IDS_LIST)
    }

    private fun saveAuthenticationServiceIds(ids: MutableList<Int>) = dynamicPreferences.saveData(
        AUTHENTICATION_IDS_LIST,
        Json.encodeToString(ids)
    )

    override fun setAuthenticationType(authType: com.drp.shared_ui.enums.AuthenticationType) {
        dynamicPreferences.saveData(AUTHENTICATION_TYPE, authType.name)
    }

    override fun getAuthenticationType(): com.drp.shared_ui.enums.AuthenticationType =
        com.drp.shared_ui.enums.AuthenticationType.valueOf(
            dynamicPreferences.loadData(
                AUTHENTICATION_TYPE,
                com.drp.shared_ui.enums.AuthenticationType.NONE.name
            ) as String
        )

    override fun setPassword(password: String) {
        dynamicPreferences.saveData(PASSWORD_AUTHENTICATION_VALUE, password)
    }

    override fun clearPassword(password: String) {
        dynamicPreferences.removeData(PASSWORD_AUTHENTICATION_VALUE)
    }

    override fun checkPasswordMatch(password: String): Boolean {
        val savedPass = (dynamicPreferences.loadData(PASSWORD_AUTHENTICATION_VALUE, "") as String)
        return savedPass == password
    }

    override fun getShahkarUserData(): ShahkarUserData =
        Json.decodeFromString<ShahkarUserData>(
            dynamicPreferences.loadData(
                SHAHKAR_USER_DATA,
                Json.encodeToString(ShahkarUserData())
            ) as String
        )

    override fun logout() {
        dynamicPreferences.removeData(SHAHKAR_USER_DATA)
    }

    override fun getPublicKey() =
        dynamicPreferences.loadData(Constants.PUBLIC_KEY, "") as String

    /** Local contacts */

    override fun getAllContacts(): Flow<List<ContactEntity>> {
        return dataBaseHelper.getAllContacts()
    }

    override suspend fun upsertContact(contactEntity: ContactEntity) {
        dataBaseHelper.upsertContact(contactEntity)
    }

    override suspend fun insertContacts(contacts: List<ContactEntity>) {
        dataBaseHelper.insertContacts(contacts)
    }

    override suspend fun deleteContacts() {
        dataBaseHelper.deleteContacts()
    }

    override suspend fun deleteContact(contactEntity: ContactEntity) {
        dataBaseHelper.deleteContact(contactEntity)
    }

    override fun queryContactsByType(contactType: ContactType): Flow<List<ContactEntity>> {
        return dataBaseHelper.queryContactsByType(contactType)
    }

    override fun searchContacts(searchedText: String): Flow<List<ContactEntity>> {
        return dataBaseHelper.searchContacts(searchedText)
    }

    override fun searchContacts(
        searchedText: String,
        contactType: ContactType
    ): Flow<List<ContactEntity>> {
        return dataBaseHelper.searchContacts(searchedText, contactType)
    }
}
