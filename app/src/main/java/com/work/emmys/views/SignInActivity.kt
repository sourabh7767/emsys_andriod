package com.work.emmys.views

import android.R.attr.password
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
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.work.emmys.R
import com.work.emmys.data.remote.Resource
import com.work.emmys.models.LoginData
import com.work.emmys.models.SignInModel
import com.work.emmys.utils.Constants
import com.work.emmys.utils.SharedPreference
import com.work.emmys.utils.Utils
import com.work.emmys.view_models.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailET: EditText
    private lateinit var passwordHiddenET: EditText
    private lateinit var passwordShowET: EditText
    private lateinit var passwordToggle: ImageButton
    private lateinit var americaFlagButton: ImageButton
    private lateinit var spainFlagButton: ImageButton

    @Inject
    lateinit var viewModel: SignInViewModel
    private var passwordShowHide = true
    private var passwordValue = ""
    private var progressDialog: ProgressDialog? = null
//    private val sharedPreference = SharedPreference(applicationContext)

    var signInModel: SignInModel? = null

    @Inject
    lateinit var sharedPreference: SharedPreference

    var currentLanguage = "en"
    var currentLang: String? = null
    var myLocale: Locale? = null

    @SuppressLint("ClickableViewAccessibility", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setAppTheme(sharedPreference.getBool(Constants.DARk_MODE))
        setContentView(R.layout.activity_sign_in)

        emailET = findViewById(R.id.email)
        passwordHiddenET = findViewById(R.id.passwordHidden)
        passwordShowET = findViewById(R.id.passwordShow)
        passwordToggle = findViewById(R.id.passwordHideShow)
        americaFlagButton = findViewById(R.id.flagAmerica)
        spainFlagButton = findViewById(R.id.flagSpain)

        if (AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_NO){
            sharedPreference.setBool(Constants.DARk_MODE,false)
        }else
            sharedPreference.setBool(Constants.DARk_MODE,true)

        setObserver()
        selectLanguages()
        passwordTextChangeListener()

        // Initialize Firebase Auth
        auth = Firebase.auth

    }

    private fun selectLanguages() {
        americaFlagButton.setOnClickListener {
            sharedPreference.setStr(Constants.LANGUAGE, "en")
            setLocale()
        }
        spainFlagButton.setOnClickListener {
            sharedPreference.setStr(Constants.LANGUAGE, "es")
            setLocale()
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

    private fun setObserver() {
        viewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
//                    paginationProgressBar.visibility = View.INVISIBLE
//                    isLoading = false
                    it.data?.let { newsResponse ->
                        val token = newsResponse.response[0].token.access ?: ""
                        sharedPreference.setStr(Constants.AUTH_TOKEN, token)
                        val i = Intent(this@SignInActivity, InvoiceListActivity::class.java)
                        startActivity(i)

                        // close this activity
                        finish()
                    }
                }
                is Resource.Error -> {
//                    paginationProgressBar.visibility = View.INVISIBLE
//                    isLoading = true
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
//                    paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    fun signInFirebase(view: View) {
        signInModel = SignInModel(emailET.text.toString().trim(), passwordValue.trim())

        if (emailET.text.toString().trim().isEmpty()) {
            Toast.makeText(
                baseContext, resources.getString(R.string.empty_email_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!signInModel?.isEmailValid()!!) {
            Toast.makeText(
                baseContext, resources.getString(R.string.invalid_email_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!signInModel!!.isPasswordValid()) {
            Toast.makeText(
                baseContext, resources.getString(R.string.empty_password_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }


        progressDialog = ProgressDialog.show(
            this@SignInActivity, "",
            resources.getString(R.string.please_wait), true
        )
        auth.createUserWithEmailAndPassword(
            signInModel?.getStrEmailAddress()!!,
            signInModel?.getStrPassword()!!
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("PK===>", "createUserWithEmail:success")
                    Log.e("PK===>", "aaaaaaa${task.result}")
                    Log.d("PK===>", "aaaaaa${task.result.user?.uid}")
                    Log.e("PK===>", "aaaaaa${task.result.credential}")

                    saveUserData()
                } else {
                    signIn()
                    /*  progressDialog?.dismiss()
                    if (task.exception?.message != null && task.exception?.message.toString()
                            .contains(
                                "The email address is already in use by another account"
                            )
                    ) {
                        Log.e("PK===>", "createUserWithEmail:failure", task.exception)

                        saveUserData()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("PK1===>", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }*/
                }
            }
    }

    private fun signIn() {
        auth.signInWithEmailAndPassword(
            signInModel?.getStrEmailAddress()!!,
            signInModel?.getStrPassword()!!
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData()
                } else {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserData() {
        progressDialog?.dismiss()
        val user = auth.currentUser
        sharedPreference.setStr(Constants.UID, user?.uid.toString())
        sharedPreference.setStr(sharedPreference.USER_EMAIL, emailET.text.toString().trim())
        sharedPreference.setStr(sharedPreference.USER_PASSWORD, passwordValue.trim())
        sharedPreference.setBool(sharedPreference.USER_LOGGED_IN, true)
        val i = Intent(this@SignInActivity, InvoiceListActivity::class.java)
        startActivity(i)
        finishAffinity()
        Log.e("save--->", "ghj")

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

    private fun authWithServer() {
        val headers = HashMap<String, String>()
        headers["App-Id"] = "91a859ed-9d1d-4202-9bcb-cdcf2ffddb43"
        headers["Api-Key"] = "5e4d8606d8c1f65f2ce12459"
        headers["Content-Type"] = "application/json"

        viewModel.apiLogin(headers, LoginData("prueba", "MOBILE"))
    }

    private fun setLocale() {
        var localLan = sharedPreference.getStr(Constants.LANGUAGE)
//        if (localeName != currentLanguage) {
        localLan = if (localLan.equals("DNF")) {
            currentLanguage
        } else sharedPreference.getStr(Constants.LANGUAGE)

        myLocale = Locale(localLan)

        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this, SignInActivity::class.java)
        refresh.putExtra(currentLang, currentLanguage)
        startActivity(refresh)
    }

}