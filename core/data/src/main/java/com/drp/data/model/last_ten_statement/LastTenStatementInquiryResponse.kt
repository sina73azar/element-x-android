package com.drp.data.model.last_ten_statement

import com.drp.data.model.last_ten_statement.BankStatement
import kotlinx.serialization.Serializable

@Serializable
data class LastTenStatementInquiryResponse(
    val statements: List<BankStatement>
)
