package com.drp.data.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.reflect.Type

class JsonConverter {
    @TypeConverter
    fun receiptListToJsonString(value: List<ReceiptItem>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<ReceiptItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun stringJsonToReceiptList(value: String): List<ReceiptItem> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<ReceiptItem>>() {}.type
        return gson.fromJson<List<ReceiptItem>>(value, type)
    }
}