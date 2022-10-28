package com.example.mystoryapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.lifecycle.asLiveData
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.data.repository.StoryRepository

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoryList() = storyRepository.getStories().cachedIn(viewModelScope)

    fun resetLocalStory() {
        storyRepository.clearLocalStory()
    }

    fun clearPrefs() {
        storyRepository.clearToken()
    }
}