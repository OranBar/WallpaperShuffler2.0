package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeWallpaper_Activity extends AppCompatActivity {

    public boolean sound_on_change = true;
    private WPShuffler wpShuffler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.v("OBChangeWallpaperActivity", "Create");
//
//        Context context = this.getApplicationContext();
//        wpShuffler = new WPShuffler(context);
//
//        sound_on_change = IsSoundOn_SharedPref();
//
//        wpShuffler.next();
//        Log.v("OBChangeWallpaperActivity", "Wallpaper Changed");
//
//        if(sound_on_change){
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        }

        WPEngine engine = new WPEngine(this.getApplicationContext());

        engine.changeWallpaper(IsSoundOn_SharedPref());

        finish();
    }

    private boolean IsSoundOn_SharedPref(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        //Treat loaded set as immutable please.
        boolean is_sound_on = sharedPref.getBoolean(getApplicationContext().getString(R.string.sound_on), true);

        return is_sound_on;
    }
}