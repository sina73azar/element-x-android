package com.drp.refah.card_facilities.data.model.bill.inquiry.separated


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("date")
    val date: Long? = null,

    @SerializedName("customer_type")
    val customerType: String? = null,

    @SerializedName("tax_amount")
    val taxAmount: Long? = null,

    @SerializedName("reactive_amount")
    val reactiveAmount: Long? = null,

    @SerializedName("year")
    val year: Int? = null,

    @SerializedName("trackID")
    val trackID: String? = null,

    @SerializedName("discount_amount")
    val discountAmount: Long? = null,

    @SerializedName("other_debt_amount")
    val otherDebtAmount: Long? = null,

    @SerializedName("previous_reading_date")
    val previousReadingDate: Long? = null,

    @SerializedName("previous_date")
    val previousDate: Long? = null,

    @SerializedName("current_date")
    val currentDate: Long? = null,

    @SerializedName("average_consumption")
    val averageConsumption: String? = null,

    @SerializedName("demand_amount")
    val demandAmount: Long? = null,

    @SerializedName("previous_debt")
    val previousDebt: Long? = null,

    @SerializedName("branch_debt_amount")
    val branchDebtAmount: Long? = null,

    @SerializedName("paytoll_amount")
    val paytollAmount: Long? = null,

    @SerializedName("subscription_amount")
    val subscriptionAmount: Long? = null,

    @SerializedName("bill_payable_amount")
    val billPayableAmount: Long? = null,

    @SerializedName("current_reading_date")
    val currentReadingDate: Long? = null,

    @SerializedName("season_amount")
    val seasonAmount: Long? = null,

    @SerializedName("friday_consumption")
    val fridayConsumption: Long? = null,

    @SerializedName("free_amount")
    val freeAmount: Long? = null,

    @SerializedName("phase")
    val phase: String? = null,

    @SerializedName("bill_id")
    val billId: String? = null,

    @SerializedName("amount")
    val amount: Long? = null,

    @SerializedName("period")
    val period: Int? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("gas_discount_amount")
    val gasDiscountAmount: Long? = null,

    @SerializedName("normal_consumption")
    val normalConsumption: String? = null,

    @SerializedName("bill_exportation_date")
    val billExportationDate: Long? = null,

    @SerializedName("cold_days_count")
    val coldDaysCount: Int? = null,

    @SerializedName("reject_date")
    val rejectDate: Long? = null,

    @SerializedName("total_days_count")
    val totalDaysCount: Int? = null,

    @SerializedName("timeStamp")
    val timeStamp: Long? = null,

    @SerializedName("low_consumption")
    val lowConsumption: Long? = null,

    @SerializedName("operation_time")
    val operationTime: Long? = null,

    @SerializedName("voltage_type")
    val voltageType: String? = null,

    @SerializedName("consumption_debt_amount")
    val consumptionDebtAmount: Long? = null,

    @SerializedName("insurance_amount")
    val insuranceAmount: Long? = null,

    @SerializedName("peak_consumption")
    val peakConsumption: String? = null,

    @SerializedName("reactive_consumption")
    val reactiveConsumption: Long? = null,

    @SerializedName("company_name")
    val companyName: String? = null,

    @SerializedName("electricity_tax_amount")
    val electricityTaxAmount: Long? = null,

    @SerializedName("energy_amount")
    val energyAmount: Long? = null,

    @SerializedName("period_amount")
    val periodAmount: Long? = null,

    @SerializedName("customer_name")
    val customerName: String? = null,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    @SerializedName("amper")
    val amper: String? = null,

    @SerializedName("pay_id")
    val payId: String? = null,

    @SerializedName("warm_days_count")
    val warmDaysCount: Int? = null
) : Serializable