package com.example.testinggrounds;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class ChangeWallpaper_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WPEngine engine = new WPEngine(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sound_on_change  = preferences.getBoolean("sound_on", false);
        boolean preserve_aspect_ratio = preferences.getBoolean("preserve_aspect_ratio", false);
        boolean stretch = !preserve_aspect_ratio;

        engine.changeWallpaper_Once(sound_on_change, stretch);

        finish();
    }

}