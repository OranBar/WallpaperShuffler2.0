package com.example.testinggrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class OpenCurrentWP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_open_current_wp);
        Intent intent2 = new Intent();
//        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.setAction(Intent.ACTION_VIEW);
        SharedPreferences sharedPref = this.getSharedPreferences(this.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);
        Uri wpUri = Uri.parse(sharedPref.getString(this.getString(R.string.currWpUri), ""));
        Log.v("OBTask", "Curr wallpaper uri is "+wpUri);
        intent2.setDataAndType(wpUri, "image/*");
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, 0);
        startActivity(intent2);

        finish();
    }
}
