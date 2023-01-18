package com.work.emmys.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.work.emmys.R
import com.work.emmys.SharedPreference
import com.work.emmys.models.SignInModel
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailET: EditText
    private lateinit var passwordHiddenET: EditText
    private lateinit var passwordShowET: EditText
    private lateinit var passwordToggle: ImageButton
    private lateinit var americaFlagButton: ImageButton
    private lateinit var spainFlagButton: ImageButton
    private var passwordShowHide = true
    private var passwordValue = ""

    var currentLanguage = "en"
    var currentLang: kotlin.String? = null
    var myLocale: Locale? = null


    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        emailET = findViewById(R.id.email)
        passwordHiddenET = findViewById(R.id.passwordHidden)
        passwordShowET = findViewById(R.id.passwordShow)
        passwordToggle = findViewById(R.id.passwordHideShow)
        americaFlagButton = findViewById(R.id.flagAmerica)
        spainFlagButton = findViewById(R.id.flagSpain)

        selectLanguages()
        passwordTextChangeListener()

        // Initialize Firebase Auth
        auth = Firebase.auth

    }

    private fun selectLanguages() {
        americaFlagButton.setOnClickListener {
            setLocale("en")
        }
        spainFlagButton.setOnClickListener {
            setLocale("es")
        }
    }

    private fun passwordTextChangeListener() {
        passwordHiddenET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.e("PK===>", "b $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("PK===>", "OC $s")
                passwordValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.e("PK===>", "a")
            }

        })

        passwordShowET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.e("KK===>", "b $s")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("KK===>", "OC $s")
                passwordValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.e("KK===>", "a")
            }

        })
    }

    fun signInFirebase(view: View) {
      var signInModel = SignInModel(emailET.text.toString().trim(), passwordValue.trim())
        if (emailET.text.toString().trim().isEmpty()) {
            Toast.makeText(
                baseContext, resources.getString(R.string.empty_email_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!signInModel.isEmailValid()) {
            Toast.makeText(
                baseContext, resources.getString(R.string.invalid_email_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (signInModel.isPasswordLengthGreaterThan5()) {
            Toast.makeText(
                baseContext, resources.getString(R.string.empty_password_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val progressDialog = ProgressDialog.show(
            this@SignInActivity, "",
            resources.getString(R.string.please_wait), true
        )


        auth.createUserWithEmailAndPassword(
            signInModel.getStrEmailAddress()!!,
            signInModel.getStrPassword()!!
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("PK===>", "createUserWithEmail:success")
//                    val user = auth.currentUser
                    progressDialog.dismiss()
                    saveUserData()
                } else {
                    progressDialog.dismiss()
                    if (task.exception?.message != null && task.exception?.message.toString()
                            .contains(
                                "The email address is already in use by another account"
                            )
                    ) {
                        saveUserData()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("PK===>", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
    }

    private fun saveUserData() {
        val sharedPreference = SharedPreference(applicationContext)
        sharedPreference.setStr(sharedPreference.USER_EMAIL, emailET.text.toString().trim())
        sharedPreference.setStr(sharedPreference.USER_PASSWORD, passwordValue.trim())
        sharedPreference.setBool(sharedPreference.USER_LOGGED_IN, true)
        Log.e("save--->", "ghj")
        val i = Intent(this@SignInActivity, HomeActivity::class.java)
        startActivity(i)

        // close this activity
        finish()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    fun passwordToggleClick(view: View) {
        if (passwordShowHide) {
            passwordToggle.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
            passwordShowHide = false
            passwordHiddenET.visibility = View.GONE
            passwordShowET.visibility = View.VISIBLE
            passwordShowET.text = passwordValue.toEditable()
            passwordShowET.isFocusableInTouchMode = true
            passwordShowET.isFocusable = true
            passwordShowET.requestFocus()
        } else {
            passwordShowHide = true
            passwordToggle.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
            passwordShowET.visibility = View.GONE
            passwordHiddenET.visibility = View.VISIBLE
            passwordHiddenET.text = passwordValue.toEditable()

            passwordHiddenET.isFocusableInTouchMode = true
            passwordHiddenET.isFocusable = true
            passwordHiddenET.requestFocus()
        }

    }

    private fun setLocale(localeName: String) {
//        if (localeName != currentLanguage) {
        currentLanguage = localeName
        myLocale = Locale(localeName)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this, SignInActivity::class.java)
        refresh.putExtra(currentLang, localeName)
        startActivity(refresh)
//        } else {
//            Toast.makeText(this@SignInActivity, "Language already selected!", Toast.LENGTH_SHORT)
//                .show()
//        }
    }

}