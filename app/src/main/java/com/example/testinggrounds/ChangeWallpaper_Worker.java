package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ChangeWallpaper_Worker extends Worker {

    private String workrequestId = "";
    public boolean sound_on_change;

    public ChangeWallpaper_Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.v("OBChangeWallpaper", "Create");
        workrequestId = " (WorkRequestId= "+this.getId()+")";
        sound_on_change = IsSoundOn_SharedPref();
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
//        wpChanger.SetWallpaper(selectedWallpaper);
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

        boolean success = engine.changeWallpaper(IsSoundOn_SharedPref());
        if(success == false){
            return Result.failure();
        }
        return Result.success();
    }

    private boolean IsSoundOn_SharedPref(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        //Treat loaded set as immutable please.
        boolean is_sound_on = sharedPref.getBoolean(getApplicationContext().getString(R.string.sound_on), true);

        return is_sound_on;
    }
}
