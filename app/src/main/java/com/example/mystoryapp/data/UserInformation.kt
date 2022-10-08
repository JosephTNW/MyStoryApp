package com.example.mystoryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserInformation(
    val name: String,
    val email: String,
    val password: String
) : Parcelable
