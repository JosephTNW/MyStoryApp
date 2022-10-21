package com.example.mystoryapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystoryapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM Story")
    fun getLocalStory(): LiveData<List<StoryEntity>>

    @Query("SELECT * FROM Story")
    fun getStoryForWidget(): List<StoryEntity>

    @Query("DELETE FROM STORY")
    fun clearStory()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<StoryEntity>)
}