package com.example.mystoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
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
    private val sharedPref: SharedPref
) {

    private val result = MutableLiveData<UsualResponse>()

    fun login(email: String, password: String): LiveData<Result<UsualResponse>> = liveData {
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
            result.postValue(loginStatus)
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
            result.postValue(registerStatus)
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

    fun getResult(): LiveData<UsualResponse> {
        return result
    }

    fun clearLocalStory(){
        CoroutineScope(Dispatchers.IO).launch{
            storyDao.clearStory()
        }
    }

    fun getStory(): LiveData<Result<List<StoryEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStory()
            val story = response.listStory
            val storyList = story.map { list ->
                StoryEntity(
                    photoUrl = list.photoUrl,
                    name = list.name,
                    desc = list.description
                )
            }

            storyDao.clearStory()
            storyDao.addStory(storyList)
        } catch (e: Exception) {
            Log.d("StoryRepository", "getStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<StoryEntity>>> = storyDao.getLocalStory().map { Result.Success(it) }
        emitSource(localData)
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody) = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.addStory(file, description)
            val addStatus = UsualResponse(
                response.error,
                response.message,
            )
            result.postValue(addStatus)
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
            sharedPref: SharedPref
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDao, sharedPref)
            }.also { instance = it }
    }
}