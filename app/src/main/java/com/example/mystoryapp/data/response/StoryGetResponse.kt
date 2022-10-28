package com.example.mystoryapp.data.response

import com.example.mystoryapp.data.local.entity.StoryEntity

data class StoryGetResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryEntity>
)
