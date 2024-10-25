package com.drp.refah.card_facilities.utility.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MobileOperatorTab(val title: String, val walletIndex: Int) {
    MTN("ایرانسل", 0),
    MCI("همراه اول", 1),
    RIGHTEL("رایتل", 2)
}