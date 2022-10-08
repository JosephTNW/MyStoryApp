package com.example.mystoryapp.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.mystoryapp.R

class StoryStackWidget : AppWidgetProvider() {

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, StoryWidgetService::class.java).also {
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            it.data = it.toUri(Intent.URI_INTENT_SCHEME).toUri()
        }

        val views = RemoteViews(context.packageName, R.layout.story_stack_widget).also {
            it.setRemoteAdapter(R.id.stack_story, intent)
            it.setEmptyView(R.id.stack_story, R.id.empty_widget)
        }

        val toastIntent = Intent(context, StoryStackWidget::class.java).also {
            it.action = TOAST_ACTION
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val toastPendingIntent = PendingIntent.getBroadcast(
            context, 0, toastIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else 0
        )

        views.setPendingIntentTemplate(R.id.stack_story, toastPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        private const val TOAST_ACTION =
            "com.example.mystoryapp.ui.widget.StoryStackWidget.TOAST_ACTION"
    }
}