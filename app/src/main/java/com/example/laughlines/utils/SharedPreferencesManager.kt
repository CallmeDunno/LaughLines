package com.example.laughlines.utils

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(private val sharedPre: SharedPreferences) {

    fun putString(key: String, value: String){
        sharedPre.edit().apply {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String): String?{
        return sharedPre.getString(key, null)
    }

    fun clear() {
        sharedPre.edit().apply {
            clear()
            apply()
        }
    }

}