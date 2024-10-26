package com.drp.refah.card_facilities.utility.enums

enum class ReasonType(val index:Int,val type:String) {
    SATNA(1, "TRANSFER"),
    PAYA(2, "TRANSFER"),
    POL(3, "TRANSFER"),
    SPECIAL_PAYMENT_ORDER(4, "TRANSFER"),
    CARD(5, "TRANSFER"),
    BILL(6, "BILL"),
    CELL_PACKAGE(7, "PURCHASE"),
    CELL_RECHARGE(8, "PURCHASE"),
    LOAN_PAYMENT(9, "TRANSFER"),
    RECURRING_PAYMENT(10, "TRANSFER"),
    INTERNAL(11, "TRANSFER"),
}