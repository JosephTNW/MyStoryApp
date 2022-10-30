package com.example.mystoryapp.data.paging.fake

import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.Api
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.StoryGetResponse
import com.example.mystoryapp.data.response.UsualResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService: Api {
    override suspend fun register(name: String, email: String, password: String): UsualResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): UsualResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStory(
        page: String?,
        size: String?,
        location: String?
    ): StoryGetResponse {
        var listStory = ArrayList<StoryEntity>()
        for (i in 0..100){
            val story = StoryEntity(
                i.toString(),
                "https://cdn.memes.com/up/24551061595129931/i/1595304338102.jpg",
                "mr. $i",
                "desc $i",
                i.toDouble(),
                i.toDouble(),
                "$i$i-$i$i-$i$i"
            )
            listStory.add(story)
        }
        return StoryGetResponse(false, "story successfully fetched.", listStory)
    }
}