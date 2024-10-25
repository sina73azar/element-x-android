package com.drp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drp.data.database.dao.CardEntityDao
import com.drp.data.database.dao.ContactDao
import com.drp.data.database.dao.TransactionEntityDao
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.database.entity.CardSuperAppEntity

@Database(
    entities = [
        CardSuperAppEntity::class, ContactEntity::class, TransactionEntity::class ],
    version = Constant.VERSION,
    exportSchema = false
)
@TypeConverters(JsonConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun cardEntityDao(): CardEntityDao
    abstract fun contactDao(): ContactDao
    abstract fun transactionEntityDao(): TransactionEntityDao
}