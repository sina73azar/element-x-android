package com.drp.data.database.impl

import com.drp.data.database.AppDataBase
import com.drp.data.database.entity.CardSuperAppEntity
import com.drp.data.database.entity.ContactEntity
import com.drp.data.database.entity.TransactionEntity
import com.drp.data.enums.ContactType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataBaseRequestImpl @Inject constructor(
    private val appDataBase: AppDataBase
    ):DataBaseRequest {

    override fun getAllCardEntities(): Flow<List<CardSuperAppEntity>> {
        return appDataBase.cardEntityDao().all
    }

    override suspend fun upsertCardEntity(cardEntity: CardSuperAppEntity) {
        appDataBase.cardEntityDao().upsert(cardEntity)
    }

    override suspend fun deleteCardEntities() {
        appDataBase.cardEntityDao().nukeTable()
    }

    override suspend fun deleteCardEntity(cardId: Int) {
        appDataBase.cardEntityDao().deleteCardEntity(cardId)
    }

    override suspend fun setToDefaultCardEntity(cardId: Int) {
        coroutineScope {
            appDataBase.cardEntityDao().setToNonDefaultCardEntity()
        }
        coroutineScope {
            appDataBase.cardEntityDao().setToDefaultCardEntity(cardId)
        }

    }

    /**ContactEntity*/
    override fun getAllContacts(): Flow<List<ContactEntity>> {
        return appDataBase.contactDao().all
    }

    override suspend fun upsertContact(contactEntity: ContactEntity) {
        appDataBase.contactDao().upsert(contactEntity)
    }

    override suspend fun insertContacts(contacts: List<ContactEntity>) {
        appDataBase.contactDao().insertContacts(contacts)
    }

    override suspend fun deleteContacts() {
        appDataBase.contactDao().nukeTable()
    }

    override suspend fun deleteContact(contactEntity: ContactEntity) {
        appDataBase.contactDao().deleteContact(contactEntity)
    }

    override fun queryContactsByType(contactType: ContactType): Flow<List<ContactEntity>> {
        return appDataBase.contactDao().queryByType(contactType.toString())
    }

    override fun searchContacts(searchedText: String): Flow<List<ContactEntity>> {
        return appDataBase.contactDao().searchContacts(searchedText)
    }

    override fun searchContacts(
        searchedText: String,
        contactType: ContactType
    ): Flow<List<ContactEntity>> {
        return appDataBase.contactDao().searchContacts(searchedText, contactType.toString())
    }

    override suspend fun insertTransaction(transactionEntity: TransactionEntity) {
        appDataBase.transactionEntityDao().insertTransaction(transactionEntity)
    }

    override suspend fun deleteTransaction(timeStamp: Long) {
        appDataBase.transactionEntityDao().deleteTransaction(timeStamp)
    }

    override fun getTransactionsBySourceCardNo(sourceCardNo: String): Flow<List<TransactionEntity>> {
        return appDataBase.transactionEntityDao().getTransactionsBySourceCardNo(sourceCardNo)
    }
}