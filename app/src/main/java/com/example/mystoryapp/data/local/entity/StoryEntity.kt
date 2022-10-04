package com.example.mystoryapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Story")
data class StoryEntity (
    @field:ColumnInfo(name = "photoUrl")
    @field:PrimaryKey
    val photoUrl: String,

    @field:ColumnInfo(name="name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val desc: String
): Serializable