package com.drp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.refah.card_facilities.data.model.card_to_card.hub.card_info.HubCardInfo
import com.drp.utils.panMaskFormatter


@Entity
data class CardSuperAppEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var transactionId: String? = null,
    var cardId: Long? = null,
    var maskedPan: String? = null,
    var referenceExpiryDate: String? = null,
    var referenceExpiryDatePersian: String? = null,
    var panExpiryDatePersian: String? = null,
    var pan: String? = null,
    var panExpiryMonth: String? = null,
    var panExpiryYear: String? = null,
    var assuranceLevel: String? = null,
    var expired: Boolean = false,
    var isRefahSource: Boolean = false,
    var bankName: String? = null,
    var bankIcon: String? = null,
    var cardDefault: Boolean = false
)

fun CardSuperAppEntity.toCardShotItemInfo(): CardShotItemInfo {
    return CardShotItemInfo(
        id = id,
        isRefahSource = isRefahSource,
        pan = pan!!,
        maskedPan = maskedPan,
        bankName = bankName!!,
        bankIcon = bankIcon!!,
        panExpiryMonth = panExpiryMonth,
        panExpiryYear = panExpiryYear,
        default = cardDefault
    )
}

/**
 * non nullable fields should be checked by caller before mapping to avoid throwing exception
 * */
@Throws(NullPointerException::class)
fun CardSuperAppEntity.toHubCardInfo() = HubCardInfo(
    transactionId = transactionId!!,
    cardId = cardId!!,
    maskedPan = maskedPan!!,
    referenceExpiryDate = referenceExpiryDate,
    referenceExpiryDatePersian = referenceExpiryDatePersian,
    panExpiryMonth = panExpiryMonth,
    panExpiryYear = panExpiryYear,
    panExpiryDatePersian = panExpiryDatePersian,
    pan = pan,
    assuranceLevel = assuranceLevel,
    expired = expired,
    default = cardDefault
)

fun HubCardInfo.toCardEntity(): CardSuperAppEntity {
    return CardSuperAppEntity(
        transactionId = transactionId,
        cardId = cardId,
        maskedPan = maskedPan,
        referenceExpiryDate = referenceExpiryDate,
        referenceExpiryDatePersian = referenceExpiryDatePersian,
        panExpiryMonth = panExpiryMonth,
        panExpiryYear = panExpiryYear,
        panExpiryDatePersian = panExpiryDatePersian,
        pan = pan,
        assuranceLevel = assuranceLevel,
        expired = expired,
        isRefahSource = false,
        bankName = null,
        bankIcon = null,
        cardDefault = default
    )
}


/**
 * @param isRefahSource if isRefahSource is null pass from caller we set it to true because hub cards uses HubCardInfo.toCardEntity extention function instead
 * */
fun CardShotItemInfo.toCardEntity(): CardSuperAppEntity {


    val cardEntity = CardSuperAppEntity(
        id = if (this.id != -1) id else 0,
        transactionId = null,
        cardId = null,
        pan = this.pan,
        maskedPan = maskedPan ?: panMaskFormatter(this.pan),
        panExpiryMonth = panExpiryMonth,
        panExpiryYear = panExpiryYear,
        panExpiryDatePersian = null,
        bankIcon = bankIcon,
        bankName = bankName,
        isRefahSource = isRefahSource ?: false,
        cardDefault = default
    )

    return cardEntity

}


