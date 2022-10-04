package com.example.mystoryapp.data.remote

import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.UsualResponse
import com.example.mystoryapp.data.response.StoryGetResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UsualResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
        ) : Call<UsualResponse>

    @GET("stories")
    fun getStory(
    ) : Call<StoryGetResponse>
}