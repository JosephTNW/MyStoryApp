package com.example.mystoryapp.ui.upload

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private lateinit var img : MultipartBody.Part
    private lateinit var desc: RequestBody
    private var lat: RequestBody? = null
    private var lon: RequestBody? = null

    fun sendStory(imgMultipart: MultipartBody.Part, desc: RequestBody, lat: RequestBody?, lon: RequestBody?) {
        this.desc = desc
        this.img = imgMultipart
        this.lat = lat
        this.lon = lon
    }

    fun addStory() = storyRepository.addStory(img, desc, lat, lon)

    fun resetLocalStory() {
        storyRepository.clearLocalStory()
    }

    fun clearPrefs() {
        storyRepository.clearToken()
    }
}