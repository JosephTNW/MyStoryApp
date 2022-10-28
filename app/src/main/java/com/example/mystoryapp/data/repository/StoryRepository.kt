package com.example.mystoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.paging.StoryRemoteMediator
import com.example.mystoryapp.data.remote.Api
import com.example.mystoryapp.data.response.UsualResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: Api,
    private val storyDao: StoryDao,
    private val storyDatabase: StoryDatabase,
    private val sharedPref: SharedPref
) {

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            val loginResult = response.loginResult
            if (loginResult != null) {
                sharedPref.saveLoginInfo(loginResult.token)
            }
            val loginStatus = UsualResponse(
                response.error,
                response.message
            )
            emit(Result.Success(loginStatus))
        } catch (e: Exception){
            Log.d("StoryRepository", "login: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(email: String, password: String, name: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            val registerStatus = UsualResponse(
                response.error,
                response.message
            )
            emit(Result.Success(registerStatus))
        } catch (e: Exception) {
            Log.d("Story Repository", "register: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun checkToken(): String? {
        return sharedPref.readLoginInfo()
    }

    fun clearToken(){
        sharedPref.clearLoginInfo()
    }

    fun clearLocalStory(){
        CoroutineScope(Dispatchers.IO).launch{
            storyDao.clearStory()
        }
    }

    fun getStories(): LiveData<PagingData<StoryEntity>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator =
            StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.StoryDao().getPagingStories()
            }
        ).liveData
    }

    fun getStoryFromDb(): LiveData<List<StoryEntity>> {
        return storyDao.getLocalStory()
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?) = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.addStory(file, description, lat, lon)
            val uploadStatus = UsualResponse(
                response.error,
                response.message
            )
            emit(Result.Success(uploadStatus))
        } catch (e: Exception) {
            Log.d("StoryRepository", "addStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: Api,
            storyDao: StoryDao,
            storyDatabase: StoryDatabase,
            sharedPref: SharedPref
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao, storyDatabase, sharedPref)
            }.also { instance = it }
    }
}