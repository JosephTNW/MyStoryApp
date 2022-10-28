package com.example.mystoryapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Story")
data class StoryEntity(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val desc: String,

    @field:ColumnInfo(name = "lon")
    val lon: Double? = null,

    @field:ColumnInfo(name = "lat")
    val lat: Double? = null,

    @field:ColumnInfo(name = "createdAt")
    val createdAt: String? = null,
) : Parcelable