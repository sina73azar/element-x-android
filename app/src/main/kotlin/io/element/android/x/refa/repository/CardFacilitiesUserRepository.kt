package com.drp.data.repository

import com.drp.data.database.entity.ContactEntity
import com.drp.data.enums.BillType
import com.drp.data.enums.CardOtpRequestType
import com.drp.data.enums.ContactType
import com.drp.data.model.ShahkarUserData
import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryRequest
import com.drp.data.model.shahkar.inquiry.ShahkarInquiryResult
import com.drp.data.model.shahkar.validate.ShahkarValidateRequest
import com.drp.data.model.shahkar.validate.ShahkarValidateResult
import com.drp.data.model.wallet_add.WalletResponse
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.enums.AuthenticationType
import com.drp.shared_ui.model.CardShotItemInfo
import kotlinx.coroutines.flow.Flow

interface CardFacilitiesUserRepository {
    fun sendOtp(
        reason: String? = null,
        receiver: String? = null,
        amount: Long? = null
    ): Flow<CustomResponse<Any>>

//    fun cardPasswordInquiry(
//        selectedCard: CardShotItemInfo? = null,
//        amount: Long,
//        trk2EquivData: Trk2EquivData,
//        requestType: CardOtpRequestType
//    ): Flow<CustomResponse<TotpResult>>

    fun cardOtp(
        selectedCard: CardShotItemInfo? = null,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        requestType: CardOtpRequestType
    ): Flow<CustomResponse<Unit>>

    fun cardToCardOtp(
        selectedCard: CardShotItemInfo? = null,
        cardYear: String,
        cardMonth: String,
        cvv2: String,
        destinationCardNumber: String,
        amount: Long
    ): Flow<CustomResponse<WalletResponse>>

    fun addToWalletOtp(
        amount: Long,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        description: String,
        userData: ShahkarUserData
    ): Flow<CustomResponse<WalletResponse>>

    fun minusFromWalletOtp(
        destinationIban: String,
        destinationCard: String,
        amount: Long,
        description: String,
        userData: ShahkarUserData
    ): Flow<CustomResponse<WalletResponse>>

    fun topUpWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        amount: Long,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String
    ): Flow<CustomResponse<WalletResponse>>

    fun internetPackageWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        topUpOperatorTab: MobileOperatorTab,
        topUpPhoneNumber: String,
        packageItem: PackageItem
    ): Flow<CustomResponse<WalletResponse>>

    fun separatedBillWithWalletOtp(
        isFromWallet: Boolean,
        userData: ShahkarUserData,
        sourceCardNumber: String,
        sourceCardCvv2: String,
        sourceCardExpireYear: String,
        sourceCardExpireMonth: String,
        billType: BillType,
        billPaymentInfo: BillPaymentInfo
    ): Flow<CustomResponse<WalletResponse>>

    fun getWalletBalance(): Flow<CustomResponse<WalletResponse>>

    fun addServiceAuthenticationId(
        id: Int
    )

    fun removeServiceAuthenticationId(
        id: Int
    )

    fun isServiceAuthenticationEnabled(
        id: Int
    ): Boolean

    fun getAuthenticationServiceIds(): MutableList<Int>
    fun clearServiceAuthenticationList()
    fun setAuthenticationType(authType: AuthenticationType)
    fun getAuthenticationType(): AuthenticationType
    fun setPassword(password: String)
    fun clearPassword(password: String)
    fun checkPasswordMatch(password: String): Boolean
    fun getShahkarUserData(): ShahkarUserData
    fun logout()


    fun getPublicKey(): String


    /** Local contacts */
    suspend fun upsertContact(contactEntity: ContactEntity)
    suspend fun insertContacts(contacts: List<ContactEntity>)
    suspend fun deleteContacts()
    suspend fun deleteContact(contactEntity: ContactEntity)
    fun getAllContacts(): Flow<List<ContactEntity>>
    fun queryContactsByType(contactType: ContactType): Flow<List<ContactEntity>>
    fun searchContacts(searchedText: String): Flow<List<ContactEntity>>
    fun searchContacts(searchedText: String, contactType: ContactType): Flow<List<ContactEntity>>

    fun shahkarInquiry(request: ShahkarInquiryRequest): Flow<CustomResponse<ShahkarInquiryResult>>
    fun authValidate(request: ShahkarValidateRequest): Flow<CustomResponse<ShahkarValidateResult>>
    fun saveShahkarUserData(shahkarUserData: ShahkarUserData)
}
