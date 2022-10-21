package com.example.mystoryapp.di

import android.content.Context
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val client = Client(context)
        val apiService = client.instanceApi()
        val database = StoryDatabase.getInstance(context)
        val dao = database.StoryDao()
        val sharedPref = SharedPref(context)
        return StoryRepository.getInstance(apiService, dao, sharedPref)
    }
}