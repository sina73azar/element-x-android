package com.drp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drp.data.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface TransactionEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @get:Query("SELECT * FROM TransactionEntity")
    val allTransactions: Flow<List<TransactionEntity>>

    @Query("DELETE FROM TransactionEntity WHERE timeStamp=:timeStamp")
    suspend fun deleteTransaction(timeStamp: Long)

    @Query("SELECT * FROM TransactionEntity WHERE sourceCardNo LIKE '%' || :searchedCardNo || '%'")
    fun getTransactionsBySourceCardNo(searchedCardNo: String): Flow<List<TransactionEntity>>


}