package com.work.emmys.views

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.work.emmys.R
import com.work.emmys.adapters.AdapterInvoiceList
import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.data.remote.Resource
import com.work.emmys.databinding.ActivityInvoiceListBinding
import com.work.emmys.listeners.ItemClickListener
import com.work.emmys.models.LoginData
import com.work.emmys.utils.Constants
import com.work.emmys.utils.SharedPreference
import com.work.emmys.utils.Utils
import com.work.emmys.view_models.SignInViewModel
import com.work.emmys.view_models.VMInvoiceList
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class InvoiceListActivity : AppCompatActivity(), ItemClickListener {
    private lateinit var binding: ActivityInvoiceListBinding
    private lateinit var adapterInvoiceList: AdapterInvoiceList
    private lateinit var progressDialog: ProgressDialog

    private var currentLanguage = "en"
    private var myLocale: Locale? = null

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var viewModel: VMInvoiceList

    @Inject
    lateinit var signInViewModel: SignInViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null)
            currentLanguage = savedInstanceState.getString(Constants.LANGUAGE) ?: "en"

        Log.e("LangCode", currentLanguage)

        Utils.setAppTheme(sharedPreference.getBool(Constants.DARk_MODE))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_list)

        try {
            progressDialog = ProgressDialog.show(
                this, "", resources.getString(R.string.please_wait), true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        adapterInvoiceList = AdapterInvoiceList(emptyList(), this)
        binding.rvInvoiceList.adapter = adapterInvoiceList
        binding.rvInvoiceList.requestFocus()

        binding.layoutItems.swDarkMode.isChecked = sharedPreference.getBool(Constants.DARk_MODE)
        setListener()
        getApiData()
        setObserver()
    }

    private fun getApiData() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val db = Firebase.firestore
        db.collection("users").get().addOnSuccessListener {
            for (document in it) {
                if (currentUser?.equals(document.id) == true) {
                    sharedPreference.setStr(Constants.APP_ID, document.data["appId"]?.toString())
                    sharedPreference.setStr(Constants.APP_KEY, document.data["apiKey"]?.toString())
                    sharedPreference.setStr(
                        Constants.USERNAME,
                        document.data["userName"]?.toString()
                    )
                    authWithServer()
                    break
                } else {
                    try {
                        progressDialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
//                Log.d("Firestore docc", "${document.id} => ${document.data}")
            }
        }.addOnFailureListener {
            Log.d("Firestore", "Error getting documents.", it)
        }
    }

    private fun setListener() {
        binding.ivReset.setOnClickListener {
            binding.etSearch.text?.clear()
            apiGetInvoices()
            Toast.makeText(this, "Refreshing List...", Toast.LENGTH_SHORT).show()
        }

        binding.ivSearch.setOnClickListener {
            if (binding.etSearch.text.toString().isNotEmpty()) apiGetInvoices()
        }

        binding.layoutItems.ivEnglish.setOnClickListener {
            sharedPreference.setStr(Constants.LANGUAGE, "en")
            setLocale()
            refresh()
        }

        binding.layoutItems.ivFrench.setOnClickListener {
            sharedPreference.setStr(Constants.LANGUAGE, "es")
            setLocale()
            refresh()
        }

        binding.layoutItems.tvLogout.setOnClickListener {
            Firebase.auth.signOut()
            sharedPreference.clear()
            startActivity(
                Intent(
                    this, SignInActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.layoutItems.swDarkMode.setOnCheckedChangeListener { compoundButton, b ->
            if (!compoundButton?.isPressed!!) {
                return@setOnCheckedChangeListener
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (compoundButton.isChecked) {
                sharedPreference.setBool(Constants.DARk_MODE, true)
                refresh()
            } else {
                sharedPreference.setBool(Constants.DARk_MODE, false)
                refresh()
            }
        }

        binding.ivMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
                GravityCompat.START
            )
            else binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.etSearch.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            apiGetInvoices()
                            return true
                        }
                        else -> {}
                    }
                }
                return false
            }
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) apiGetInvoices()
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })

        binding.layoutItems.tvInvoiceList.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            apiGetInvoices()
        }

        binding.layoutItems.tvInvoice.setOnClickListener {
            if (binding.layoutItems.tvInvoiceList.isVisible) {
                binding.layoutItems.tvInvoiceList.visibility = View.GONE
            } else {
                binding.layoutItems.tvInvoiceList.visibility = View.VISIBLE

            }
        }
    }

    private fun refresh() {
        val refresh = Intent(
            this, InvoiceListActivity::class.java
        )
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(refresh)
    }

    private fun authWithServer() {
        val headers = HashMap<String, String>()
        headers["App-Id"] = "${sharedPreference.getStr(Constants.APP_ID)}"
        headers["Api-Key"] = "${sharedPreference.getStr(Constants.APP_KEY)}"
        /*  headers["App-Id"] = "91a859ed-9d1d-4202-9bcb-cdcf2ffddb43"
          headers["Api-Key"] = "5e4d8606d8c1f65f2ce12459"*/
        headers["Content-Type"] = "application/json"

        signInViewModel.apiLogin(
            headers,
            LoginData(sharedPreference.getStr(Constants.USERNAME).toString(), "MOBILE")
        )
    }

    private fun setLocale() {
        val configuration = Configuration(resources?.configuration)
        configuration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED

        var localLan = sharedPreference.getStr(Constants.LANGUAGE)
//        if (localeName != currentLanguage) {
        if (localLan.equals("DNF")) {
            localLan = currentLanguage
        }

        Handler(Looper.getMainLooper()).postDelayed({
            myLocale = Locale(localLan!!)
            configuration.setLocale(myLocale)
            @Suppress("DEPRECATION")
            this.resources?.updateConfiguration(configuration, resources.displayMetrics)
        }, 300)
    }

    private fun setObserver() {
        signInViewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { newsResponse ->
                        val token = newsResponse.response[0].token.access
                        sharedPreference.setStr(Constants.AUTH_TOKEN, token)
                        apiGetInvoices()
                    }
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    progressDialog.show()
//                    paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.invoiceResponse.observe(this) {
            Utils.hideKeyboard(this)

            when (it) {
                is Resource.Success -> {
                    adapterInvoiceList.addData(emptyList())
                    progressDialog.dismiss()
                    it.data?.let { data ->
                        if (data.response.size > 0) {
                            adapterInvoiceList.addData(data.response)
                            binding.tvNoDataFound.visibility = View.GONE

                        } else {
                            binding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }

                is Resource.Error -> {
                    progressDialog.dismiss()
                    it.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
//                    progressDialog.show()
                }
            }
        }
    }

    private fun apiGetInvoices() {
        if (sharedPreference.getStr(Constants.APP_KEY).equals("DNF")) {
            return
        }

        val headers = HashMap<String, String>()
        headers["App-Id"] = "${sharedPreference.getStr(Constants.APP_ID)}"
        headers["Api-Key"] = "${sharedPreference.getStr(Constants.APP_KEY)}"
        headers["Authorization"] = "Bearer ${sharedPreference.getStr(Constants.AUTH_TOKEN)}"
        headers["Content-Type"] = "application/json"

        val params: HashMap<String, String> = HashMap()
        params["page"] = "1"
        params["results_per_page"] = "50"
        params["sort_by"] = "date"
        params["order"] = "desc"
        params["results"] = "invoice"

        if (binding.etSearch.text.toString().isNotEmpty()) params["number"] =
            binding.etSearch.text.toString()

        viewModel.apiGetInvoice(headers, params)

//        Utils.convertDate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.LANGUAGE, currentLanguage)
    }

    override fun attachBaseContext(newBase: Context?) {
        // Get configuration and resources before onCreate method
        val resources = newBase?.resources
        val configuration = Configuration(resources?.configuration)
        configuration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED
//        val context = newBase?.createConfigurationContext(configuration)

        // Set locale with configuration saved
        val sharedPreferences = SharedPreference(newBase)
        val langue = sharedPreferences.getStr(Constants.LANGUAGE)
        val locale = Locale(langue.toString())
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        @Suppress("DEPRECATION")
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        super.attachBaseContext(newBase)
    }

    /*Invoice Click Listener*/
    override fun onItemClick(item: Any?) {
        val invoice = item as InvoiceResponse.Response
        startActivity(
            Intent(this, InvoiceDetailActivity::class.java).putExtra(
                Constants.DATA,
                Gson().toJson(invoice)
            )
        )
    }

}