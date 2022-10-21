package com.example.mystoryapp.ui.upload

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.UsualResponse
import com.example.mystoryapp.utils.timeStamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    val usualResponse = MutableLiveData<UsualResponse>()
    val errorMessage = MutableLiveData<String>()

    fun sendStory(imgMultipart: MultipartBody.Part, desc: RequestBody) {
        storyRepository.addStory(imgMultipart, desc)
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