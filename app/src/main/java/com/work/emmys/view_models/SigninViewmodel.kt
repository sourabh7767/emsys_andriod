package com.work.emmys.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.emmys.data.Repository
import com.work.emmys.data.models.LoginResponse
import com.work.emmys.data.remote.Resource
import com.work.emmys.models.LoginData
import com.work.emmys.utils.NetworkUtil.Companion.hasInternetConnection
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


class SignInViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: Repository
) : ViewModel() {
    var loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    fun apiLogin(headers: HashMap<String, String>, loginData: LoginData) {
        CoroutineScope(Dispatchers.Main).launch {
            loginResponse.value = Resource.Loading()

            try {
                if (hasInternetConnection(context)) {
                    val response = repository.login(headers, loginData)
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()!!.status == 201)
                                loginResponse.value = Resource.Success(response.body()!!)
                            else
                                loginResponse.value =
                                    Resource.Error(response.body()!!.message ?: "")
                        } else
                            loginResponse.value =
                                Resource.Error(response.errorBody().toString() ?: "No Data Found")
                    } else
                        loginResponse.value =
                            Resource.Error(
                                response.errorBody().toString() ?: "Something went wrong"
                            )
                }
            } catch (ex: Exception) {
                Log.i("errorr", ex.localizedMessage.toString())
                when (ex) {
                    is IOException -> loginResponse.value = Resource.Error("Network Failure")
                    else -> loginResponse.value = Resource.Error("Conversion Error")
                }
            }
        }
    }

}