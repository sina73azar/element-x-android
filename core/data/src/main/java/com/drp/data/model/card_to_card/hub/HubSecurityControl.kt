package com.drp.refah.card_facilities.data.model.card_to_card.hub

enum class HubSecurityControl(val code: Int) {
    SEND_PIN_CVV2_NO_ENCRYPTION(0),
    SEND_PIN_ENCRYPTION_CVV2_NO_ENCRYPTION(1),
    SEND_PIN_CVV2_ENCRYPTION(2);
}