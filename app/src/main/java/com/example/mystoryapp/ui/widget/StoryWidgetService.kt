package com.example.mystoryapp.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.mystoryapp.data.SharedPref

class StoryWidgetService : RemoteViewsService() {
    val pref = SharedPref(this)

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StoryRemoteViewsFactory(this.applicationContext, pref)
}