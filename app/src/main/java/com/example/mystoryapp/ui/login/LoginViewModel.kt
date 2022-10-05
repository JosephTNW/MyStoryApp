package com.example.mystoryapp.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(context: Context) : ViewModel(){

    val result = MutableLiveData<LoginResponse>()
    val pref = SharedPref(context)

    fun login(email : String, password: String, context: Context) {
        val client = Client(context)
        client.instanceApi()
            .login(email, password)
            .enqueue(object : Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        result.postValue(response.body())
                        viewModelScope.launch {
                            result.value?.loginResult?.let { pref.saveLoginInfo(it.token) }
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failed", it)}
                }
            })
    }

    fun getLoginResult(): LiveData<LoginResponse>{
        return result
    }

    fun getSavedToken(): String? {
        return pref.readLoginInfo()
    }
}