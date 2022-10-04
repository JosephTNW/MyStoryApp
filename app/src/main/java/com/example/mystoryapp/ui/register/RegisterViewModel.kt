package com.example.mystoryapp.ui.register

import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.LoginResult
import com.example.mystoryapp.data.response.UsualResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: SharedPref) : ViewModel() {

    val regResult = MutableLiveData<UsualResponse>()
    val logResult = MutableLiveData<LoginResult>()

    fun sendRegistration(name: String, email: String, password: String) {
        val client = Client(pref)
        client.instanceApi()
            .register(name, email, password)
            .enqueue(object : Callback<UsualResponse> {
                override fun onResponse(
                    call: Call<UsualResponse>,
                    response: Response<UsualResponse>
                ) {
                    if (response.isSuccessful) {
                        regResult.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<UsualResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failed", it) }
                }
            })
    }

    fun getRegResult(): LiveData<UsualResponse>{
        return regResult
    }

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
                            logResult.postValue(response.body()?.loginResult)
                            viewModelScope.launch {
                                logResult.value?.let { pref.saveLoginInfo(it.token) }
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        t.message?.let { Log.d("Failed", it)}
                    }
                })
    }

    fun getLoginInfo() : LiveData<String> {
        return pref.readLoginInfo().asLiveData()
    }
}