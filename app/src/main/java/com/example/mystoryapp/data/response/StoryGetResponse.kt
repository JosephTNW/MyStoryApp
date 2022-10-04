package com.example.mystoryapp.data.response

data class StoryGetResponse(
    val error: Boolean,
    val message: String,
    val listStory: ArrayList<GetStoryResult>
)
