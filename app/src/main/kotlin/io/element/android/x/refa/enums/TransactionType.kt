package com.drp.data.enums

enum class TransactionType(val type: String) {
    CARD_TO_CARD("انتقال وجه کارت به کارت"),
    BILL("پرداخت قبض"),
    TOP_UP("خرید شارژ"),
    NET_PACK("خرید بسته اینترنتی"),
    LOAN("پرداخت تسهیلات"),
    IRAN_INSURANCE("بیمه ایران"),
    INSURANCE("پرداخت حق بیمه"),
    CHARGE_WALLET("افزایش موجودی کیف پول")
}