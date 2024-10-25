package com.drp.data.model.shahkar

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("P_ERROR")
    var pError: Long?,
    /*@SerializedName("P_STACK")
    var pStack: PStack?,*/
    @SerializedName("P_RQID")
    var pRqId: Long?,
    @SerializedName("P_WALLETID")
    var pWalletId: Long?,
    @SerializedName("P_PERSONID")
    var pPersonId: Long?,
    @SerializedName("P_ACCESSTOKEN")
    var pAccessToken: String?,

    /** balance fields */

    @SerializedName("P_CASHABLEBALANCE")
    var pCashBalance: Long?,
    @SerializedName("P_TOTALBALANCE")
    var pTotalBalance: Long?,
    @SerializedName("P_BLOCKEDAMOUNT")
    var pBlockedAmount: Long?
)
