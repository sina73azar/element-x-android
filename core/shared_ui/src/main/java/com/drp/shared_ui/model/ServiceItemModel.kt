package com.drp.shared_ui.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class ServiceItemModel(
    val id: Int,
    @DrawableRes var startIcon: Int? = null,
    @DrawableRes var endIcon: Int? = null,
    var startIconTint: Color = Color.Unspecified,
    var endIconTint: Color = Color.Unspecified,
    val title: String
)