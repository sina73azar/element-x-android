package com.drp.card_facilities.presentation.card_to_card.refahi.transfer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.database.entity.ContactEntity
import com.drp.data.network.CustomResponse
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.refah.card_facilities.data.model.CardInfo
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.otp.TotpRequest
import com.drp.refah.card_facilities.data.model.bill.otp.TotpResult
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.transfer.TransferCardRequest
import com.drp.data.model.card_to_card.refahi.transfer.TransferCardResult
import com.drp.data.enums.ContactType
import com.drp.refah.card_facilities.utility.SUPER_APP_CARD_TO_CARD_MOCK_RES
import com.drp.shared_ui.model.CardShotItemInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardTransferViewModel @Inject constructor(
    private val userRepository: CardFacilitiesUserRepository,
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ViewModel() {

    private val mutableTotpResult: MutableLiveData<CustomResponse<TotpResult>> = MutableLiveData()
    val totpResult: MutableLiveData<CustomResponse<TotpResult>> = mutableTotpResult


    private val mutableTransferResult: MutableLiveData<CustomResponse<TransferCardResult>> =
        MutableLiveData()
    val transferResult: MutableLiveData<CustomResponse<TransferCardResult>> = mutableTransferResult


    fun totp(card: CardInfo, destCard: String, trk2EquivData: Trk2EquivData, amount: Long) {
        val headers: Map<String, String> =
            mapOf(
                "pin" to "",
                "cvv2" to trk2EquivData.cvv2, "cardExpirationYearMonth" to trk2EquivData.expireDate
            )
        val request = TotpRequest(card, destCard, trk2EquivData, amount)
        viewModelScope.launch {
            cardFacilitiesRepository.cardPasswordInquiry(request, headers)
                .collect {
                    mutableTotpResult.postValue(it)
                }
        }
    }


    fun transfer(
        fundTransfer: FundTransfer,
        trk2EquivData: Trk2EquivData,
        cardShotItemInfo: CardShotItemInfo
    ) {
        mutableTransferResult.postValue(CustomResponse.Loading())
//        val publicKeyStr = userRepository.getPublicKey()
//        if (publicKeyStr.isNotEmpty()) {
//            val publicKey: PublicKey? = getPublic(publicKeyStr)
//            val pinByte: ByteArray? = encryptWithRsa(publicKey, trk2EquivData.pin.toByteArray())
//            val pinEncrypted: String = Base64.encodeToString(pinByte, Base64.NO_WRAP)
//            trk2EquivData.pin = pinEncrypted
        val headers: Map<String, String> =
            mapOf(
                "pin" to trk2EquivData.pin,
                "cvv2" to trk2EquivData.cvv2,
                "cardExpirationYearMonth" to trk2EquivData.expireDate
            )
        val request = TransferCardRequest(fundTransfer, trk2EquivData)
        viewModelScope.launch {
            cardFacilitiesRepository.cardFundTransfer(request, headers, SUPER_APP_CARD_TO_CARD_MOCK_RES)
                .collect {

                    CoroutineScope(Dispatchers.IO).launch {
                        if (it.status == CustomResponse.Status.SUCCESS) {
                            upsertSourceCardShotItem(cardShotItemInfo)
                            it.data?.fundTransfer?.destination?.let { nonNullDestinationPan ->
                                it.data?.fundTransfer?.personName?.let {

                                    upsertContactPan(
                                        nonNullDestinationPan, """
                                        ${it.firstName} ${it.lastName} 
                                    """.trimIndent()
                                    )
                                }
                            }
                        }

                        /*TODO: save transaction in a table later if needed*/


                        mutableTransferResult.postValue(it)
                    }
                }
        }
    }


    private suspend fun upsertSourceCardShotItem(cardShotItemInfo: CardShotItemInfo) {
        viewModelScope.launch {
            cardFacilitiesRepository.upsertCardShotItem(cardShotItemInfo).first()
        }.join()
    }

    private suspend fun upsertContactPan(pan: String, title: String) {
        viewModelScope.launch {
            userRepository.upsertContact(
                ContactEntity(
                    ContactType.PAN.toString(),
                    title = title,
                    value = pan
                )
            )
        }.join()
    }

}
