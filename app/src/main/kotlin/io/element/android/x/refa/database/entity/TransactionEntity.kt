package com.drp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drp.shared_ui.model.receipt.ReceiptItem
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class TransactionEntity(
    @field: PrimaryKey
    val timeStamp: Long,
    val transactionType: String,
    val transactionValue: String,
    val amount: Long,
    val transactionStatus: String,
    val sourceCardNo: String,
    val receiptItem: List<ReceiptItem>
)