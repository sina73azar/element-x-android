package com.drp.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.drp.data.database.entity.CardSuperAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardEntityDao {


    @get:Query("select * from CardSuperAppEntity")
    val all: Flow<List<CardSuperAppEntity>>

    @Upsert
    suspend fun upsert(vararg msg: CardSuperAppEntity)

    @Query("delete from CardSuperAppEntity")
    suspend fun nukeTable()
    @Query("delete from CardSuperAppEntity where id = :cardId")
    suspend fun deleteCardEntity(cardId: Int)
    @Query("update CardSuperAppEntity set cardDefault = 1 where id = :cardId")
    suspend fun setToDefaultCardEntity(cardId: Int)

    @Query("update CardSuperAppEntity set cardDefault = 0 where cardDefault = 1")
    suspend fun setToNonDefaultCardEntity()

}