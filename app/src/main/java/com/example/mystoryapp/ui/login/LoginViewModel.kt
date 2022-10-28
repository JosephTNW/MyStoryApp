package com.example.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.repository.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private lateinit var email: String
    private lateinit var password: String

    fun login(email: String, password: String) {
        this.email = email
        this.password = password
    }

    fun getLoginResult() = storyRepository.login(email, password)
}