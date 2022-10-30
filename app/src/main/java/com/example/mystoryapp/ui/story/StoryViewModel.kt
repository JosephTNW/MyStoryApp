package com.example.mystoryapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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