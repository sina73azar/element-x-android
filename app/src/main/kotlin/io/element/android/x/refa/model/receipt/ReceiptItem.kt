package com.drp.shared_ui.model.receipt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ReceiptItem(
    val order: Int,
    val title: String,
    var value: String? = null,
    val type: ReceiptType = ReceiptType.NORMAL,
    val resIcon: String? = null,
    val isSharable: Boolean = true,
    val clickable: Boolean = false
) : Parcelable