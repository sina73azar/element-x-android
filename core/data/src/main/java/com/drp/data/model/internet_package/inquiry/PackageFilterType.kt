package com.drp.refah.card_facilities.data.model.internet_package.inquiry

import com.drp.data.model.internet_package.inquiry.PackageItem

data class PackageFilterType(
    var title: String,
    var packages: ArrayList<PackageItem>,
//    var icon: Int?
)
