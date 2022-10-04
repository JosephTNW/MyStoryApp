package com.example.mystoryapp.data.response

import com.google.gson.annotations.SerializedName

data class UsualResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,
)
