package com.example.mystoryapp.ui.story

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.remote.Client
import com.example.mystoryapp.data.response.GetStoryResult
import com.example.mystoryapp.data.response.StoryGetResponse
import com.example.mystoryapp.data.response.UsualResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(application: Application, context: Context) : AndroidViewModel(application) {

    val storyLists = MutableLiveData<ArrayList<GetStoryResult>>()
    val result = MutableLiveData<UsualResponse?>()
    private val pref = SharedPref(context)

    private var storyDao: StoryDao?
    private var storyDb: StoryDatabase?

    init {
        storyDb = StoryDatabase.getInstance(application)
        storyDao = storyDb?.StoryDao()
    }

    fun getStoryList(context: Context) {
        val client = Client(context)
        client.instanceApi()
            .getStory()
            .enqueue(object : Callback<StoryGetResponse> {
                override fun onResponse(
                    call: Call<StoryGetResponse>,
                    response: Response<StoryGetResponse>
                ) {
                    if (response.isSuccessful) {
                        storyLists.postValue(response.body()?.listStory)
                        val results = response.body()?.let {
                            UsualResponse(
                                it.error,
                                it.message
                            )
                        }
                        result.postValue(
                            results
                        )
                    }
                }

                override fun onFailure(call: Call<StoryGetResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failed", it) }
                }

            })
    }

    fun addLocalStory(storyResult: ArrayList<GetStoryResult>) {
        CoroutineScope(Dispatchers.IO).launch {
            var i = 0
            while (i < 5) {
                val storyData = StoryEntity(
                    0,
                    storyResult[i].photoUrl,
                    storyResult[i].name,
                    storyResult[i].description
                )
                storyDao?.addStory(storyData)
                i++
            }
        }
    }

    fun getResult(): LiveData<UsualResponse?> {
        return result
    }

    fun getStoryResult(): LiveData<ArrayList<GetStoryResult>> {
        return storyLists
    }

    fun resetLocalStory() {
        CoroutineScope(Dispatchers.IO).launch {
            storyDao?.clearStory()
        }
    }

    fun clearPrefs() {
        pref.clearLoginInfo()
    }
}