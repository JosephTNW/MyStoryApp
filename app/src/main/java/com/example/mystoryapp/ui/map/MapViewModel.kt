package com.example.mystoryapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.repository.Result
import com.example.mystoryapp.data.repository.StoryRepository

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel(){
    fun getStoryList(): LiveData<Result<List<StoryEntity>>> {
        return storyRepository.getStory()
    }
}