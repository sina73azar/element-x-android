package com.drp.data.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BillType(
    val PaymentText: String,
    val inquiryText: String,
    val faName: String = "",
    val englishName: String = "",
    val walletIndex: String = ""
) : Parcelable {
    ELECTRICITY("پرداخت قبض برق", "استعلام قبض برق", "برق", "billelectricPaymentId", "1"),
    GAS("پرداخت قبض گاز", "استعلام قبض گاز", "گاز", "billgasPaymentId", "2"),
    FIXEDLINE(
        "پرداخت قبض تلفن ثابت",
        "استعلام قبض تلفن ثابت",
        "تلفن ثابت",
        "billfixedLinePaymentId",
        "7"
    ),
    MTNMOBILE("پرداخت قبض ایرانسل", "استعلام قبض ایرانسل", "ایرانسل", "billmobilePaymentId", "5"),
    MCIMOBILE(
        "پرداخت قبض همراه اول",
        "استعلام قبض همراه اول",
        "همراه اول",
        "billmobilePaymentId",
        "4"
    ),
    MOBILE("پرداخت قبض تلفن همراه", "استعلام قبض تلفن همراه", "تلفن همراه"),
    WATER("پرداخت قبض آب", "استعلام قبض آب", "آب", "billwaterPaymentId", "3"),
    TEHRANMUNICIPALITYBUSINESSNWASTETOLL("", ""),
    TEHRANMUNICIPALITYRENOVATIONTOLL("", ""),
    TEHRANMUNICIPALITYRENOVATIONWASTETOLL("", ""),
    TEHRANMUNICIPALITYBUSINESSTOLL("", ""),
    TRAFFICFINES("", ""),
    RIGHTELMOBILE("پرداخت قبض رایتل", "استعلام قبض رایتل", "رایتل", "billmobilePaymentId", "6"),
    FIXEDLINEEXTENDED(
        "پرداخت قبض تلفن ثابت", "استعلام قبض تلفن ثابت", "تلفن ثابت",
        "billfixedLinePaymentId"
    ),
    IRAN_INSURANCE("پرداخت حق بیمه ایران", ""),
    DEFAULT("", ""),
    MOBILE_EXTENDED("پرداخت قبض تلفن همراه", "استعلام قبض تلفن همراه", "تلفن همراه")
}