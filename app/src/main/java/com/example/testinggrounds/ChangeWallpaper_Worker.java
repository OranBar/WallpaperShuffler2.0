package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ChangeWallpaper_Worker extends Worker {

    private String workrequestId = "";

    public ChangeWallpaper_Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.v("OBChangeWallpaper", "Create");
        workrequestId = " (WorkRequestId= "+this.getId()+")";
    }

//    @NonNull
//    @Override
//    public ListenableWorker.Result doWork() {
//        Log.v("OBChangeWallpaper","Starting change sequence"+workrequestId);
//
//        Context context = this.getApplicationContext();
//        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(context);
//        WPShuffler wpShuffler = new WPShuffler(context, wpDatabase);
//        WPChanger wpChanger = new WPChanger(context);
//
//        String[] dirsUris_str = wpDatabase.getDirectories();
//
//        if(dirsUris_str == null || dirsUris_str.length == 0){
//            Log.e("OBError", "No directories in list");
//            return Result.failure();
//        }
//
//        sound_on_change = IsSoundOn_SharedPref();
//
//        Uri selectedWallpaper = wpShuffler.next();
//        wpChanger.setWallpaper(selectedWallpaper);
//        wpDatabase.logWallpaperChanged(selectedWallpaper);
//
////        wpShuffler.next();
//        Log.v("OBChangeWallpaperActivity", "Wallpaper Changed");
//
//        if(sound_on_change){
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        }
//
//        return Result.success();
//    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        WPEngine engine = new WPEngine(this.getApplicationContext());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean sound_on_change  = preferences.getBoolean("sound_on", false);
        boolean preserve_aspect_ratio = preferences.getBoolean("preserve_aspect_ratio", false);
        boolean stretch = !preserve_aspect_ratio;

        boolean success = engine.changeWallpaper_Once(sound_on_change, stretch);
        if(success == false){
            return Result.failure();
        }
        return Result.success();
    }

}
