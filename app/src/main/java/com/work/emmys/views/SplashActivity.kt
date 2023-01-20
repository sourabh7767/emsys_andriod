package com.work.emmys.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.work.emmys.R
import com.work.emmys.common.SharedPreference
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity:AppCompatActivity() {
    private var isLoggedIn = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        getUserData()
        Handler().postDelayed(
            Runnable
            // Using handler with postDelayed called runnable run method
            {
                lateinit var i : Intent
                if(isLoggedIn) {
                     i = Intent(this@SplashActivity, HomeActivity::class.java)
                } else {
                    i = Intent(this@SplashActivity, SignInActivity::class.java)
                }
                startActivity(i)

                // close this activity
                finish()
            },  2000
        )

    }

    private fun getUserData() {
        val sharedPreference = SharedPreference(applicationContext)
        isLoggedIn = sharedPreference.getBool(sharedPreference.USER_LOGGED_IN)
        Log.e("splash--->", "$isLoggedIn")
    }
}