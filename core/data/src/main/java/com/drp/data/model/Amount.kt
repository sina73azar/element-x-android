package com.drp.data.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class Amount(
    val ledgerBalance: Long,
    var depositableAmount: Long,
    val currency: String
) : Serializable