package com.drp.refah.card_facilities.utility

/**
 * Created by sharafkar.a on 21/1/2020.
 */
class BillUtils {
    private var mBillId: String? = null
    private var mPayId: String? = null

    constructor() {}
    constructor(billId: String?, payId: String?) {
        mBillId = billId
        mPayId = payId
    }


    fun billAmount(payId: String): Long{
        return if (!payId.isNullOrEmpty()){
            payId.toLong() / 100000 * 1000
        }else{
            0
        }
    }

    fun validateBillId(billId: String): Boolean {
        return try {
            if (billId.length < 6 || billId.length > 13) return false
            val checkDigit = billId.substring(billId.length - 1)
            val billIdChars = billId.substring(0, billId.length - 1).toCharArray()
            verifyUtilityBillCheckDigit(checkDigit, billIdChars)
        } catch (e: Exception) {
            false
        }
    }

    fun validatePayId(payId: String): Boolean {
        return try {
            if (payId.length < 6 || payId.length > 13) return false
            val checkDigit = payId.substring(payId.length - 2, payId.length - 1)
            val payIdChars = payId.substring(0, payId.length - 2).toCharArray()
            verifyUtilityBillCheckDigit(checkDigit, payIdChars)
        } catch (e: Exception) {
            false
        }
    }

    fun validatePayAndBillId(billId: String, payId: String): Boolean {
        return try {
            val checkDigit = payId.substring(payId.length - 1)
            val utilityBillsChars = (billId + payId.substring(0, payId.length - 1)).toCharArray()
            verifyUtilityBillCheckDigit(checkDigit, utilityBillsChars)
        } catch (e: Exception) {
            false
        }
    }

    private fun verifyUtilityBillCheckDigit(
        checkDigit: String,
        chars: CharArray
    ): Boolean {
        var multiply = 2
        var result = 0
        for (i in chars.indices.reversed()) {
            val billIdChar = Character.getNumericValue(chars[i])
            result += billIdChar * multiply
            if (multiply < 7) ++multiply else multiply = 2
        }
        var modResult = result % 11
        if (modResult == 0 || modResult == 1) modResult = 0
        return if (modResult != 0) 11 - modResult == Integer.valueOf(checkDigit) else 0 == Integer.valueOf(
            checkDigit
        )
    }
}