package com.ovapp.symbolrecognition

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.app.PendingIntent


/**
 * Implementation of App Widget functionality.
 */
class OpenDrawingActivityWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.open_drawing_activity_widget)
            val configIntent = Intent(context,  DrawingActivity::class.java)

            val configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0)

            remoteViews.setOnClickPendingIntent(R.id.openActivityBtn, configPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)


        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            var intent: Intent = Intent(context, DrawingActivity::class.java)
            context.startActivity(intent)
            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.open_drawing_activity_widget)


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)


        }
    }
}

