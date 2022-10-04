package com.example.mystoryapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import com.example.mystoryapp.data.local.entity.StoryEntity
import androidx.room.Query

@Dao
interface StoryDao {
    @Query("SELECT * FROM Story")
    fun getLocalStory(): List<StoryEntity>

    @Query("DELETE FROM STORY")
    fun clearStory()

    @Insert
    suspend fun addStory(story: StoryEntity)
}