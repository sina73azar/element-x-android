package com.drp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.drp.data.database.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * @see AMB-291
 * @author Mr.C
 * */
@Dao
interface ContactDao {

    @get:Query("select * from ContactEntity")
    val all: Flow<List<ContactEntity>>

    @Upsert
    suspend fun upsert(vararg contactEntity: ContactEntity)

    @Query("delete from ContactEntity")
    suspend fun nukeTable()

    @Insert
    suspend fun insertContacts(contacts: List<ContactEntity>)

    /** */
    @Query("SELECT * FROM ContactEntity WHERE contactType = :contactType")
    fun queryByType(contactType: String): Flow<List<ContactEntity>>


    @Query("SELECT * FROM ContactEntity WHERE title LIKE '%' || :searchedText || '%' OR value LIKE '%' || :searchedText || '%'")
    fun searchContacts(searchedText: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM ContactEntity WHERE contactType = :contactType AND (title LIKE '%' || :searchedText || '%' OR value LIKE '%' || :searchedText || '%')")
    fun searchContacts(contactType: String, searchedText: String): Flow<List<ContactEntity>>

    @Delete(entity = ContactEntity::class)
    fun deleteContact(contactEntity: ContactEntity)

}