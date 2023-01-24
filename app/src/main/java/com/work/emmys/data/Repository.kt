package com.work.emmys.data

import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.data.models.LoginResponse
import com.work.emmys.data.remote.ApiServices
import com.work.emmys.data.remote.Resource
import com.work.emmys.models.LoginData
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by PaL on 20,January,2023
 */

class Repository @Inject constructor(private val apiServices: ApiServices) {

    suspend fun login(
        headers: HashMap<String, String>,
        loginData: LoginData
    ): Response<LoginResponse> {
        return apiServices.login(headers, loginData)
    }

    suspend fun apiGetInvoiceList(
        headers: HashMap<String, String>,
        invoiceData: HashMap<String, String>
    ): Response<InvoiceResponse> {
        return apiServices.getInvoiceList(headers, invoiceData)
    }
}