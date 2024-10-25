package com.drp.data.sharepref

interface DynamicPreferences {
    fun saveData(key: String?, value: Any?)
    fun loadData(key: String?, defaultValue: Any): Any
    fun removeData(key: String)
    fun clear()
}