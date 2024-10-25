package com.drp.refah.card_facilities.data.model.topup.inquiry

import androidx.room.PrimaryKey
import java.util.Calendar

data class CheckAmountResult(
    val amount: Long,
    val max: Long?,
    val min: Long?,
    val fee: Long?,
    var name: String?,
    val transactionType: String,
    var valid: Boolean,
    var lastTime: Long = Calendar.getInstance().timeInMillis
)
