package com.example.mystoryapp.ui.story

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.data.response.StoryGetResponse
import com.example.mystoryapp.data.response.UsualResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoryList(): LiveData<Result<List<StoryEntity>>> {
        return storyRepository.getStory()
    }

    fun getResult() : LiveData<UsualResponse>{
        return storyRepository.getResult()
    }

    fun resetLocalStory() {
        storyRepository.clearLocalStory()
    }

    fun clearPrefs() {
        storyRepository.clearToken()
    }
}