package com.drp.data.sharepref

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.drp.data.sharepref.DynamicPreferences
import javax.inject.Inject

@SuppressLint("CommitPrefEdits")
internal class DynamicPreferencesImpl @Inject constructor(private val sharedPreferences: SharedPreferences) :
    DynamicPreferences {

    override fun saveData(key: String?, value: Any?) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> {
                editor.putString(key, value)
            }

            is Int -> {
                editor.putInt(key, value)
            }

            is Boolean -> {
                editor.putBoolean(key, value)
            }

            is Float -> {
                editor.putFloat(key, value)
            }

            is Long -> {
                editor.putLong(key, value)
            }
        }
        editor.apply()
    }

    override fun loadData(key: String?, defaultValue: Any): Any {
        when (defaultValue) {
            is String -> {
                return sharedPreferences.getString(key, defaultValue) ?: defaultValue
            }

            is Int -> {
                return sharedPreferences.getInt(key, defaultValue)
            }

            is Boolean -> {
                return sharedPreferences.getBoolean(key, defaultValue)
            }

            is Float -> {
                return sharedPreferences.getFloat(key, defaultValue)
            }

            is Long -> {
                return sharedPreferences.getLong(key, defaultValue)
            }

            else -> return defaultValue
        }
    }

    override fun removeData(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}

