package com.example.mystoryapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.UsualResponse

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String

    fun sendRegistration(name: String, email: String, password: String) {
        this.password = password
        this.name = name
        this.email = email
    }

    fun getRegResult(): LiveData<Result<UsualResponse>> {
        return storyRepository.register(email, password, name)
    }

    fun login(): LiveData<Result<UsualResponse>> {
        return storyRepository.login(email, password)
    }

    fun getLoginInfo(): String? {
        return storyRepository.checkToken()
    }
}