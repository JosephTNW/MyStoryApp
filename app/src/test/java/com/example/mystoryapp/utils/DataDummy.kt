package com.example.mystoryapp.utils

import androidx.paging.PagingSource
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.LoginResult
import com.example.mystoryapp.data.response.UsualResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun usualResponse(): UsualResponse{
        return UsualResponse(false, "Process Successful")
    }

    fun usualFailed(): UsualResponse{
        return UsualResponse(true, "Process Failed")
    }

    fun loginResponse(): LoginResponse{
        return LoginResponse(
            false,
            "Success",
            LoginResult(
                "3j32099urwjririejiwejjoj",
                "testing",
                "3iejdsu83uqieeo02eq2ekskopkcml"
            )
        )
    }

    fun failLoginResponse(): LoginResponse {
        return LoginResponse(
            true,
            "invalid password",
            LoginResult(
                null,
                null,
                null
            )
        )
    }

    fun generateDummyStoryEntity(): List<StoryEntity>{
        val storyList = ArrayList<StoryEntity>()
        for (i in 0..10){
            val story = StoryEntity(
                "$i",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/33/Patrick_Star.svg/1200px-Patrick_Star.svg.png",
                "Story $i",
                "Desc $i",
                i.toDouble(),
                i.toDouble(),
                "$i$i-$i$i-$i$i"
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateMultipartFile() = MultipartBody.Part.create("dummyMultipartFile".toRequestBody())

    fun reqBody(content: String) = content.toRequestBody()
}