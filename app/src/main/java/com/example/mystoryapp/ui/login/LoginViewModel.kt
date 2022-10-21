package com.example.mystoryapp.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.UsualResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String) {
        storyRepository.login(email, password)
    }

    fun getLoginResult(): LiveData<UsualResponse> {
        return storyRepository.getResult()
    }
}