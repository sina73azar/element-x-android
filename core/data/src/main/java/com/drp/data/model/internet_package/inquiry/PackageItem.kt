package com.drp.data.model.internet_package.inquiry

import kotlinx.serialization.Serializable

@Serializable
data class PackageItem(

    val code: String,
    val duration: String,
    val price: Long,
    val tax: Long,
    val title: String,


    /*@field:SerializedName("duration")
    val duration: String,

    @field:SerializedName("durationUnit")
    val durationUnit: String,

    @field:SerializedName("volume")
    val volume: String? = null,

    @field:SerializedName("operatorDescription")
    val operatorDescription: String? = null,

    @field:SerializedName("amount")
    val amount: Long,

    @field:SerializedName("amountWithTax")
    val amountWithTax: Long,

    @field:SerializedName("operatorId")
    val operatorId: String? = null,

    @field:SerializedName("operatorTitle")
    val operatorTitle: String? = null,

    @field:SerializedName("tags")
    val tags: Tags? = null,

    @field:SerializedName("simType")
    val simType: Int? = null*/
)

