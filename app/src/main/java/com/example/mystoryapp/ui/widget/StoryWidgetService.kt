package com.example.mystoryapp.ui.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.mystoryapp.data.SharedPref

class StoryWidgetService : RemoteViewsService() {
    private lateinit var pref: SharedPref
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory =
        StoryRemoteViewsFactory(this.applicationContext, pref)
}