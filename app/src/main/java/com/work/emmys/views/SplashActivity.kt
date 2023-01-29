package com.work.emmys.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.work.emmys.R
import com.work.emmys.utils.Constants
import com.work.emmys.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity:AppCompatActivity() {
    private var isLoggedIn = false;

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        getUserData()
        Handler().postDelayed(
            Runnable
            // Using handler with postDelayed called runnable run method
            {
                val i : Intent = if(isLoggedIn) {
                    Intent(this@SplashActivity, InvoiceListActivity::class.java)
                } else {
                    Intent(this@SplashActivity, SignInActivity::class.java)
                }
                startActivity(i)

                // close this activity
                finish()
            },  2000
        )

    }

    private fun getUserData() {
//        val sharedPreference = SharedPreference(applicationContext)

        var localLan = sharedPreference.getStr(Constants.LANGUAGE)

         if (localLan.equals("DNF")) {
             sharedPreference.setStr(Constants.LANGUAGE, "en")
         }

        isLoggedIn = sharedPreference.getBool(sharedPreference.USER_LOGGED_IN)
        Log.e("splash--->", "$isLoggedIn")
    }
}