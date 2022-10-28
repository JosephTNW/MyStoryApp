package com.example.mystoryapp.data.remote

import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.response.StoryGetResponse
import com.example.mystoryapp.data.response.UsualResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): UsualResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): UsualResponse

    @GET("stories")
    suspend fun getStory(
        @Query("page") page: String? = null,
        @Query("size") size: String? = null,
        @Query("location") location: String? = null
    ): StoryGetResponse
}