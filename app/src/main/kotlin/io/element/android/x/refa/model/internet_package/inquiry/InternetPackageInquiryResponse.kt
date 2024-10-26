package com.drp.data.model.internet_package.inquiry

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InternetPackageInquiryResponse(

    @field:SerializedName("topupOperator")
    val topupOperator: String? = null,

    @field:SerializedName("products")
    val products: List<PackageItem>? = null,

    ) : Serializable