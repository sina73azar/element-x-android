package com.drp.data.database.impl

import com.drp.data.database.entity.CardSuperAppEntity
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.ContactType
import kotlinx.coroutines.flow.Flow

interface DataBaseRequest {

  /**CardEntity*/
  fun getAllCardEntities(): Flow<List<CardSuperAppEntity>>

  suspend fun upsertCardEntity(cardEntity: CardSuperAppEntity)

  suspend fun deleteCardEntities()
  suspend fun deleteCardEntity(cardId: Int)
  suspend fun setToDefaultCardEntity(cardId: Int)


  /**ContactEntity*/
  suspend fun upsertContact(contactEntity: ContactEntity)
  suspend fun insertContacts(contacts: List<ContactEntity>)
  suspend fun deleteContacts()
  suspend fun deleteContact(contactEntity: ContactEntity)
  fun getAllContacts(): Flow<List<ContactEntity>>
  fun queryContactsByType(contactType: ContactType): Flow<List<ContactEntity>>
  fun searchContacts(searchedText: String): Flow<List<ContactEntity>>
  fun searchContacts(searchedText: String, contactType: ContactType): Flow<List<ContactEntity>>

  /**TransactionEntity*/
  suspend fun insertTransaction(transactionEntity: TransactionEntity)
  suspend fun deleteTransaction(timeStamp: Long)
  fun getTransactionsBySourceCardNo(sourceCardNo: String): Flow<List<TransactionEntity>>


 }