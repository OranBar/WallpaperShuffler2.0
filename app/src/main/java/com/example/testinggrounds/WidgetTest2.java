package com.example.testinggrounds;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetTest2 extends AppWidgetProvider {

    private static final String SYNC_CLICKED = "automaticWidgetSyncButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_test);
        watchWidget = new ComponentName(context, WidgetTest2.class);

        remoteViews.setOnClickPendingIntent(R.id.b_nextWall, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {



//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

//            RemoteViews remoteViews;
//            ComponentName watchWidget;
//
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_test2);
//            watchWidget = new ComponentName(context, WidgetTest2.class);
//
//            remoteViews.setTextViewText(R.id.sync_button, "TESTING");
//
//            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}