package com.drp.refah.card_facilities.data.model.card_to_card.hub

enum class HubSecurityType(val code: Int) {
    NON_USE_SECURITY_FACTOR(0),
    SIGNATURE(1),
    MAC(2)
}
