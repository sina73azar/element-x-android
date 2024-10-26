package com.drp.data.enums

enum class ContactType(val savedContactTitle: String, val iconName: String = "") {
    PAN("شماره کارت"),
    MOBILE_NO("شماره موبایل", iconName = "mobile_phone_ic"),
    TELEPHONE_NO("شناسه قبض تلفن ثابت", "mokhaberat"),
    WATER_BILL_ID("شناسه قبض آب", "ab"),
    GAS_BILL_ID("شناسه قبض گاز", "gaz"),
    ELECTRIC_BILL_ID("شناسه قبض برق", "bargh"),
    BILL_ID("شناسه قبض", "ic_bill_barcode"),
    INSTALLMENT_ID("شناسه قسط وام"),
    INSURANCE_ID("شناسه بیمه")
}