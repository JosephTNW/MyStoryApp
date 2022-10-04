package com.example.mystoryapp.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.data.SharedPref
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.response.GetStoryResult
import kotlinx.coroutines.runBlocking

internal class StoryRemoteViewsFactory(private val mContext: Context, private val pref: SharedPref) : RemoteViewsService.RemoteViewsFactory {
    private var mWidgetItems = listOf<StoryEntity>()
    private lateinit var dao: StoryDao

    private fun getDataDB() {
        runBlocking {
            mWidgetItems = dao.getLocalStory()
        }
    }

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext.applicationContext).StoryDao()
        getDataDB()
    }

    override fun onDataSetChanged() {
        getDataDB()
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.story_stack_widget)
        try {
            val bitmap: Bitmap = Glide.with(mContext.applicationContext)
                .asBitmap()
                .load(mWidgetItems[position].photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.stack_story, bitmap)
        } catch (e: Exception){
            e.printStackTrace()
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}