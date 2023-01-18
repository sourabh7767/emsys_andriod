package com.work.emmys.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.work.emmys.R
import com.work.emmys.SharedPreference

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreference = SharedPreference(applicationContext)
        var isLoggedIn = sharedPreference.getBool(sharedPreference.USER_LOGGED_IN)

        Log.e("Home--->", "$isLoggedIn")
    }
}