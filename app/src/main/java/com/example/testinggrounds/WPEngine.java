package com.example.testinggrounds;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class WPEngine {

    public Context context;

    public WPEngine(Context context) {
        this.context = context;
    }

    public boolean changeWallpaper(boolean sound_on_change){

        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(context);
        WPShuffler wpShuffler = new WPShuffler(context, wpDatabase);
        WPChanger wpChanger = new WPChanger(context);


        if(wpDatabase.getDirectories().size() == 0){
            Log.e("OBError", "No directories in list");
            return false;
        }

        Uri selectedWallpaper = wpShuffler.next();
        wpChanger.SetWallpaper(selectedWallpaper);
        wpDatabase.logWallpaperChanged(selectedWallpaper);

        //Update widget time
//        Intent updateWidget = new Intent(context, WPWidget.class); // Widget.class is your widget class
//        updateWidget.setAction("update_widget");
//        PendingIntent pending = PendingIntent.getBroadcast(context, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
//        try {
//            pending.send();
//        } catch (PendingIntent.CanceledException e) {
//            Log.e("OB_WPEngine", "Exception", e);
//        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wpwidget);
        ComponentName thisWidget = new ComponentName(context, WPWidget.class);
//        remoteViews.setTextViewText(R.id.lastchange_txt, );
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);
        String timeString = sharedPref.getString(context.getString(R.string.last_changetime), "");
        remoteViews.setTextViewText(R.id.lastchange_txt, timeString);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        //-----------------

        Log.v("OBChangeWallpaper", "Wallpaper Changed");

        if(sound_on_change){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }

        return true;
    }
}
