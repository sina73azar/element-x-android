package com.drp.refah.ui.data.enums

enum class SecondAuthenticationMethod (val value:String){
    STATIC_PASSWORD("STATIC_PASSWORD"),
    SMS("SMS"),
    OTP("OTP"),
    DYNAMIC("DYNAMIC"),
    LOCAL_SDK("LOCAL_SDK"),
    HUB_OTP("HUB_OTP"),
}