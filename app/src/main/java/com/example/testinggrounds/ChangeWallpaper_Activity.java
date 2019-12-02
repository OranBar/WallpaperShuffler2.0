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

        // 1. Get screen size.
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        final int screenWidth  = metrics.widthPixels;
        final int screenHeight = metrics.heightPixels;

        // 2. Make the wallpaperManager fit the screen size.
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        wallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight);

        WPEngine engine = new WPEngine(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sound_on_change  = preferences.getBoolean("sound_on", false);
        engine.changeWallpaper_Once(sound_on_change);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        finish();
    }

}