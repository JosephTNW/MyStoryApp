package com.example.mystoryapp.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetStoryResult(
    val id : String,
    val name : String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Float,
    val lon: Float
) : Parcelable
