package com.example.testinggrounds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DisableWifi extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_disable_wifi);

        Log.v("DisableWifi","Click-a-roo!");

        disableWifi();
        finish();
    }

    public void disableWifi(){
        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }


}
