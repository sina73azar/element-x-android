package com.drp.shared_ui.model

import java.io.Serializable

data class AutoCompleteItem(
    val id: Long,
    val title: String,
    val value: String,
    var selected: Boolean = false,
    val bank: BankData? =null
) : Serializable