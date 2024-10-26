package com.drp.refah.ui.data.model
import kotlinx.serialization.Serializable
@Serializable
data class CustomToggleModel(
    var id: Int,
    var title: String,
    var icon: Int?  = null
)
