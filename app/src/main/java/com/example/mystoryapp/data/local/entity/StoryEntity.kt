package com.example.mystoryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Story")
data class StoryEntity(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int = 0,

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
) : Serializable