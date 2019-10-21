package com.example.testinggrounds;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class WPEngine {

    public Context context;

    public WPEngine(Context context) {
        this.context = context;
    }

    public boolean changeWallpaper(boolean sound_on_change){

        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(context);
        WPShuffler wpShuffler = new WPShuffler(context, wpDatabase);
        WPChanger wpChanger = new WPChanger(context);

        String[] dirsUris_str = wpDatabase.getDirectories();

        if(dirsUris_str == null || dirsUris_str.length == 0){
            Log.e("OBError", "No directories in list");
            return false;
        }

        Uri selectedWallpaper = wpShuffler.next();
        wpChanger.SetWallpaper(selectedWallpaper);
        wpDatabase.logWallpaperChanged(selectedWallpaper);

        Log.v("OBChangeWallpaper", "Wallpaper Changed");

        if(sound_on_change){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }

        return true;
    }
}
