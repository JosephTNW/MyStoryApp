package com.example.mystoryapp.ui.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.LoginResult
import com.example.mystoryapp.data.response.UsualResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun sendRegistration(name: String, email: String, password: String) {
        storyRepository.register(email, password, name)
    }

    fun getRegResult(): LiveData<UsualResponse> {
        return storyRepository.getResult()
    }

    fun login(email: String, password: String) {
        storyRepository.login(email, password)
    }

    fun getLoginInfo(): String? {
        return storyRepository.checkToken()
    }
}