package com.drp.shared_ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComboModel(
    val id:Int,
    val title:String
): Parcelable
