package com.work.emmys.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreference @Inject constructor(@ApplicationContext private var context: Context?) {
    val USER_EMAIL = "user_email"
    val USER_PASSWORD = "user_password"
    val USER_LOGGED_IN = "user_logged_in"

    val PREFS_NAME = "emmys_prefs"

    fun sharedPreferenceExist(key: String?): Boolean {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return !prefs.contains(key)
    }

    fun setInt(key: String?, value: Int) {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String?): Int {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getInt(key, 0)
    }

    fun setStr(key: String?, value: String?) {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getStr(key: String?): String? {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(key, "DNF")
    }

    fun setBool(key: String?, value: Boolean) {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBool(key: String?): Boolean {
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getBoolean(key, false)
    }

    fun clear(){
        val prefs = context!!.getSharedPreferences(PREFS_NAME, 0)
        prefs.edit().clear().apply()
    }
}