package com.example.mystoryapp.ui.login

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

class LoginViewModel(private val pref: SharedPref) : ViewModel(){

    val result = MutableLiveData<LoginResponse>()

    fun login(email : String, password: String) {
        val client = Client(pref)
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
                            result.value?.let { pref.saveLoginInfo(it.loginResult.token) }
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

    fun getSavedToken(): LiveData<String> {
        return pref.readLoginInfo().asLiveData()
    }
}