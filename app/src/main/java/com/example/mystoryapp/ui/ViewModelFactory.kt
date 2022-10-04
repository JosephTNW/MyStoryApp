package com.example.mystoryapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.ui.login.LoginViewModel
import com.example.mystoryapp.ui.register.RegisterViewModel
import com.example.mystoryapp.ui.story.StoryViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val pref: SharedPref, private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)){
            return StoryViewModel(pref, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
    }
}