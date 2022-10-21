package com.example.mystoryapp.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.di.Injection
import com.example.mystoryapp.ui.login.LoginViewModel
import com.example.mystoryapp.ui.map.MapViewModel
import com.example.mystoryapp.ui.register.RegisterViewModel
import com.example.mystoryapp.ui.story.StoryViewModel
import com.example.mystoryapp.ui.upload.UploadViewModel

class ViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            RegisterViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            StoryViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            UploadViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(storyRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}