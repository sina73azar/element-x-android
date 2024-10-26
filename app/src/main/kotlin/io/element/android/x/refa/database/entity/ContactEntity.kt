package com.drp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.shared_ui.model.SearchSheetItemModel
import kotlin.random.Random


@Entity
data class ContactEntity(
    val contactType: String,
    val title: String,
    @PrimaryKey
    val value: String,
)


fun ContactEntity.toAutoCompleteItem() = AutoCompleteItem(
    id = Random(1000L).nextLong(),
    title = this.title,
    value = this.value,
)

fun ContactEntity.toSearchSheetItemModel() = SearchSheetItemModel(
    value = this.value,
    name = this.title
)

