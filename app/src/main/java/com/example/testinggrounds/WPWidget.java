package com.example.testinggrounds;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class WPWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, ChangeWallpaper_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wpwidget);
        views.setOnClickPendingIntent(R.id.b_goToNextWp, pendingIntent);

        Intent intent2 = new Intent(context, OpenCurrentWP.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);

        views.setOnClickPendingIntent(R.id.b_openCurrWp, pendingIntent2);


        Intent intent3 = new Intent(context, PlayPauseWPShuffle.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 0, intent3, 0);
        views.setOnClickPendingIntent(R.id.b_playPause, pendingIntent3);


        Intent intentSync = new Intent(context, WPWidget.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
        views.setOnClickPendingIntent(R.id.b_playPause, pendingSync);


        //Yoyo, se qualcosa va a puttane, è qui il problema. Metodo statico usato!
        if(new WPEngine(context).isWPWorker_running()){
            views.setTextViewText(R.id.b_playPause, "||");
        }else{
            views.setTextViewText(R.id.b_playPause, "|>");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);


        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WPWidget.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

        onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

