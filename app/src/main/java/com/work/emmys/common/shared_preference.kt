package com.work.emmys

import android.content.Context

class SharedPreference {
    val USER_EMAIL = "user_email"
    val USER_PASSWORD = "user_password"
    val USER_LOGGED_IN = "user_logged_in"
    private var context: Context? = null

    val PREFS_NAME = "emmys_prefs"

    constructor(context: Context?) {
        this.context = context
    }

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
}