package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class ChangeWallpaper_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WPEngine engine = new WPEngine(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sound_on_change  = preferences.getBoolean("sound_on", false);
        engine.changeWallpaper_Once(sound_on_change);

        finish();
    }

}