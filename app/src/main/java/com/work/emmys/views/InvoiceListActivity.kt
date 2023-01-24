package com.work.emmys.views

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.work.emmys.R
import com.work.emmys.adapters.AdapterInvoiceList
import com.work.emmys.data.remote.Resource
import com.work.emmys.databinding.ActivityInvoiceListBinding
import com.work.emmys.utils.Constants
import com.work.emmys.utils.SharedPreference
import com.work.emmys.view_models.VMInvoiceList
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InvoiceListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceListBinding
    private lateinit var adapterInvoiceList: AdapterInvoiceList
    private lateinit var progressDialog :ProgressDialog

    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var viewModel: VMInvoiceList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_list)

         progressDialog = ProgressDialog.show(
            this, "",
            resources.getString(R.string.please_wait), true
        )

        val currentuser = FirebaseAuth.getInstance().currentUser?.uid

        Toast.makeText(this,currentuser,Toast.LENGTH_SHORT).show()

        adapterInvoiceList = AdapterInvoiceList(emptyList())
        binding.rvInvoiceList.adapter = adapterInvoiceList
        binding.rvInvoiceList.requestFocus()

        binding.ivReset.setOnClickListener {
            apiGetInvoices()
            Toast.makeText(this, "Refreshing List...", Toast.LENGTH_SHORT).show()
        }

        setObserver()
        apiGetInvoices()
    }

    private fun setObserver() {
        viewModel.invoiceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    it.data?.let { data ->
                        if (data.response.size>0)
                            adapterInvoiceList.addData(data.response)
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
                }
            }
        }
    }

    private fun apiGetInvoices() {
        val headers = HashMap<String, String>()
        headers["App-Id"] = "91a859ed-9d1d-4202-9bcb-cdcf2ffddb43"
        headers["Api-Key"] = "5e4d8606d8c1f65f2ce12459"
        headers["Authorization"] = "Bearer ${sharedPreference.getStr(Constants.AUTH_TOKEN)}"
        headers["Content-Type"] = "application/json"

        val params: HashMap<String, String> = HashMap()
        params["page"] = "1"
        params["results_per_page"] = "25"
        params["sort_by"] = "date"
        params["order"] = "desc"
        params["results"] = "invoice"

        viewModel.apiGetInvoice(headers, params)
    }
}