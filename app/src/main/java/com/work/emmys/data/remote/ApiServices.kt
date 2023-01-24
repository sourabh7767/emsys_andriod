package com.work.emmys.data.remote

import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.data.models.LoginResponse
import com.work.emmys.models.LoginData
import com.work.emmys.utils.ApiConstant
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by PaL on 22,January,2023
 */
interface ApiServices {

    @POST(ApiConstant.LOGIN)
    suspend fun login(
        @HeaderMap headers: HashMap<String, String>,
        @Body loginData: LoginData
    ): Response<LoginResponse>

    @GET(ApiConstant.INVOICE_LIST)
    suspend fun getInvoiceList(
        @HeaderMap headerMap: HashMap<String, String>,
        @QueryMap params: HashMap<String, String>
    ): Response<InvoiceResponse>
}