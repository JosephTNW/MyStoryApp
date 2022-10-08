package com.example.mystoryapp.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.ui.login.LoginViewModel
import com.example.mystoryapp.ui.register.RegisterViewModel
import com.example.mystoryapp.ui.story.StoryViewModel
import com.example.mystoryapp.ui.upload.UploadViewModel

class ViewModelFactory(private val application: Application, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(context) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(context) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(application, context) as T
        } else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(application, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
    }
}