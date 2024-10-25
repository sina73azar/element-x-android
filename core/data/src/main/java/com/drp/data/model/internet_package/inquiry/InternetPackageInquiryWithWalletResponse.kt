package com.drp.data.model.internet_package.inquiry

import java.io.Serializable

data class InternetPackageInquiryWithWalletResponse(
    val parameters: InternetPackageInquiryResult
)

data class InternetPackageInquiryResult(

    /*@field:SerializedName("topupOperator")
    val topupOperator: Int? = null,*/

//    @field:SerializedName("topupPackages")
    val products: List<PackageItem>? = null

) : Serializable