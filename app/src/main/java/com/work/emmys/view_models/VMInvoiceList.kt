package com.work.emmys.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.work.emmys.data.Repository
import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.data.remote.Resource
import com.work.emmys.utils.NetworkUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * Created by PaL on 22,January,2023
 */
class VMInvoiceList @Inject constructor(@ApplicationContext private val context: Context,
                                        private val repository: Repository
) :ViewModel() {
    var invoiceResponse: MutableLiveData<Resource<InvoiceResponse>> = MutableLiveData()

    fun apiGetInvoice(headers: HashMap<String, String>, invoiceData: HashMap<String, String>) {
        CoroutineScope(Dispatchers.Main).launch {
            invoiceResponse.value = Resource.Loading()

            try {
                if (NetworkUtil.hasInternetConnection(context)) {
                    val response = repository.apiGetInvoiceList(headers, invoiceData)
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()!!.status == 200)
                                invoiceResponse.value = Resource.Success(response.body()!!)
                            else
                                invoiceResponse.value =
                                    Resource.Error(response.body()!!.message ?: "")
                        } else
                            invoiceResponse.value =
                                Resource.Error(response.errorBody().toString() ?: "No Data Found")
                    } else
                        invoiceResponse.value =
                            Resource.Error(
                                response.errorBody().toString() ?: "Something went wrong")
                }
            } catch (ex: Exception) {
                Log.i("errorr", ex.localizedMessage.toString())
                when (ex) {
                    is IOException -> invoiceResponse.value = Resource.Error("Network Failure")
                    else -> invoiceResponse.value = Resource.Error("Conversion Error")
                }
            }
        }
    }
}