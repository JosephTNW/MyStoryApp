package com.example.mystoryapp.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mystoryapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM Story")
    fun getLocalStory(): List<StoryEntity>

    @Query("DELETE FROM STORY")
    fun clearStory()

    @Insert()
    suspend fun addStory(story: StoryEntity)
}