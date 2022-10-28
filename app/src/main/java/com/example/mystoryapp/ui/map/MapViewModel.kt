package com.example.mystoryapp.ui.map

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.repository.StoryRepository

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel(){
    fun getStoryList() = storyRepository.getStoryFromDb()
}