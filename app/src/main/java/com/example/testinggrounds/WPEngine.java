package com.example.testinggrounds;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WPEngine {

    public Context context;

    public WPEngine(Context context) {
        this.context = context;
    }

    public boolean changeWallpaper_Once(boolean sound_on_change, boolean stretch){

        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(context);
        WPShuffler wpShuffler = new WPShuffler(context, wpDatabase);
        WPChanger wpChanger = new WPChanger(context);

        if(wpDatabase.getDirectories().size() == 0){
            Log.e("OBError", "No directories in list");
            return false;
        }

        Uri selectedWallpaper = wpShuffler.next();
        wpChanger.setWallpaper(selectedWallpaper, stretch);
        wpDatabase.logWallpaperChanged(selectedWallpaper);

        //Update last change time and widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wpwidget);
        ComponentName thisWidget = new ComponentName(context, WPWidget.class);

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

//    public boolean changeWallpaper_Once(boolean sound_on_change){
//
//        OneTimeWorkRequest changeWallpaper_work = new OneTimeWorkRequest.Builder(ChangeWallpaper_Worker.class).build();
//
//        WorkManager.getInstance(context).enqueue(changeWallpaper_work);
//        return true;
//    }

    public void changeWallpaper_Repeated(int waitTime){
        if(waitTime < 15){
            Log.e("OBWPEngine", "Wait time is too low ("+waitTime+" < 15 (min) ) ");
        }
        PeriodicWorkRequest changeWallpaper_periodicWork = new PeriodicWorkRequest.Builder(ChangeWallpaper_Worker.class, 15, TimeUnit.MINUTES)
                .addTag("WC")
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork("ChangeWallpaper_Loop", ExistingPeriodicWorkPolicy.REPLACE, changeWallpaper_periodicWork);
        Log.v("OBDebug", "Work Enqueued ");
    }

    public void stopChangeWallpaper_Repeated(){
        Log.v("OBTask", "My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
        Log.v("OBTask", "Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
        Log.v("OBTask", "Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());

        if(WorkManager.getInstance().getWorkInfosByTag("WC").cancel(true)){
            Log.v("OBTasks", "attempt to kill service");
            WorkManager.getInstance().pruneWork();
        }

        Log.v("OBTask", "--My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
        Log.v("OBTask", "--Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
        Log.v("OBTask", "--Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());
        //------------------------

        ListenableFuture<List<WorkInfo>> info = WorkManager.getInstance().getWorkInfosForUniqueWork("ChangeWallpaper_Loop");
        Log.v("OBTask", "(Unique) My Work is "+info.toString());
        Log.v("OBTask", "(Unique) Is Canceled = "+info.isCancelled());
        Log.v("OBTask", "(Unique) Is Done = "+info.isDone());

        WorkManager.getInstance().cancelUniqueWork("ChangeWallpaper_Loop");
        WorkManager.getInstance().pruneWork();



        Log.v("OBTask", "--(Unique) My Work is "+info.toString());
        Log.v("OBTask", "--(Unique) Is Canceled = "+info.isCancelled());
        Log.v("OBTask", "--(Unique) Is Done = "+info.isDone());

        Log.v("OBTask","Stopped Wallpaper change task??");
    }

    //Perchè non mi andava di fare un'altra classe e fare felice la V*ri. Quella bastarda fumatrice
    public static boolean isWPWorker_running() {
        return isWorkScheduled("WC");
    }

    private static boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance();
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = running | state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("OBTask", "Error while checking worker status", e);
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("OBTask", "Error while checking worker status", e);
            return false;
        }
    }


    public void togglePlayStop(){
        if(isWPWorker_running()){
            stopChangeWallpaper_Repeated();
        }else{
            //TODO hardcoded time
            changeWallpaper_Repeated(15);
        }
    }
}
