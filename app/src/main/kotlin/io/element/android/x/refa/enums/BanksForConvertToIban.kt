package com.drp.shared_ui.enums

enum class BanksForConvertToIban(
    val bankNameForServiceCall: String,
    val bankNameInPersian: String
) {
    Ansar("Ansar", "بانک انصار"),
    GHAVAMIN("Ghavamin", "بانک قوامین"),
    HEKMAT("HekmateIranian", "بانک حکمت ایرانیان"),
    KOSAR("Kosar", "بانک کوثر"),
    SEPAH("Sepah", "بانک سپه"),

    Ayandeh("Ayande", "بانک آینده"),
    DEY("Dey", "بانک دی"),
    EGHTESAD_NOVIN("EghtesadeNovin", "بانک اقتصاد نوین"),
    GARDESHGARI("Gardeshgari", "بانک گردشگری"),
    IRAN_ZAMIN("IranZamin", "بانک ایران زمین"),

    // todo logo change
    KARAFARIN("KarAfarin", "بانک کارآفرین"),
    KESHAVARZI("Keshavarzi", "بانک کشاورزی"),
    KHAVAR_MIANEH("KhavareMiane", "بانک خاورمیانه"),
    MASKAN("Maskan", "بانک مسکن"),
    MEHR_IRAN("MehrIran", "بانک مهر ایران"),
    MELALL("MelalAskarie", "بانک ملل"),
    MELLAT("Mellat", "بانک ملت"),
    MELLI("MellieIran", "بانک ملی"),
    PARSIAN("Parsian", "بانک پارسیان"),
    PASARGAD("Pasargad", "بانک پاسارگاد"),
    POST_BANK("PostBankeIran", "پست بانک"),

    REFAH("RefaheKargaran", "بانک رفاه"),
    RESALAT("Resalat", "بانک رسالت"),
    TOSEE_SADERAT("ToseeSaderateIran", "بانک توسعه صادرات"),
    SADERAT("SaderateIran", "بانک صادرات"),
    SAMAN("Saman", "بانک سامان"),
    SANAT_O_MADAN("SanatoMadan", "بانک صنعت و معدن"),
    SARMAYEH("Sarmaye", "بانک سرمایه"),
    SHAHR("Shahr", "بانک شهر"),
    SINA("Sina", "بانک سینا"),
    TOSEE_TAAVON("ToseeTaavon", "بانک توسعه تعاون"),
    TEJARAT("Tejarat", "بانک تجارت"),
    ETEBARI_TOSEE("Tosee", "بانک اعتباری توسعه"),
}