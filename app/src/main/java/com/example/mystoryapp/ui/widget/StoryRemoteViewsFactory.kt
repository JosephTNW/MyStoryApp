package com.example.mystoryapp.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.map
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.room.StoryDao
import com.example.mystoryapp.data.local.room.StoryDatabase
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.di.Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


internal class StoryRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private val mWidgetItems = arrayListOf<Bitmap>()
    private lateinit var dao: StoryDao

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext.applicationContext).StoryDao()
    }

    override fun onDataSetChanged() {
        val tokenIdentifier = Binder.clearCallingIdentity()
        runBlocking(Dispatchers.IO) {
            try {
                dao.getStoryForWidget().map {
                    val bitmap = try {
                        Glide.with(mContext)
                            .asBitmap()
                            .load(it.photoUrl)
                            .submit()
                            .get()
                    } catch (e: Exception) {
                        BitmapFactory.decodeResource(mContext.resources, R.drawable.no_image)
                    }
                    mWidgetItems.add(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Binder.restoreCallingIdentity(tokenIdentifier)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.front_image, mWidgetItems[position])

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}