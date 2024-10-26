package com.drp.data.enums

enum class SimType(val walletIndex: Int) {
    PREPAID(walletIndex = 0),
    POSTPAID(walletIndex = 1),
    ALL(walletIndex = 2)
}
