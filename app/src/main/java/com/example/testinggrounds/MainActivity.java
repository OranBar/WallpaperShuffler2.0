package com.example.testinggrounds;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.PeriodicWorkRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private TextView console;
    private Button[] consoleButtons;

    PeriodicWorkRequest changeWallpaper_work = null;

    private void Log(String arg){
        String consoleText = console.getText().toString();
        consoleText = consoleText + arg;
        console.setText(consoleText);
        Log.v("ObTask", arg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startWorkerBtn = findViewById(R.id.startStop_btn);
        startWorkerBtn.setEnabled(true);

        ProgressBar progressBar = findViewById(R.id.engines_up_bar);
        progressBar.setVisibility(View.INVISIBLE);

        BindOnClick_OfStartStopButton();
        bindFAB();
        bindSettings();

        boolean workerIsRunning = WPEngine.isWPWorker_running();
        updateDirsAndImgsText();
        updateBackground(workerIsRunning);
        updateButton(workerIsRunning);
        updateWidget();
        updateChangeTimes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button startWorkerBtn = (Button) findViewById(R.id.startStop_btn);
        startWorkerBtn.setEnabled(true);

        ProgressBar progressBar = findViewById(R.id.engines_up_bar);
        progressBar.setVisibility(View.INVISIBLE);

        updateChangeTimes();
    }

    private void bindSettings() {
        Button settingsBtn = findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateDirsAndImgsText() {
        TextView dirsAndImgs = findViewById(R.id.dirs_img_txt);
        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(this.getApplicationContext());
        int dirs = wpDatabase.getDirectories().size();
        int images = wpDatabase.getAllWallpapers().size();
        String text = String.format("Dirs/Imgs\n%d/%d", dirs, images);
        dirsAndImgs.setText(text);
    }

    private void bindFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFolder();
            }
        });
    }

    public void clearDirectories(View v) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getString(R.string.images_dirs_key), new HashSet<String>());
        editor.apply();
    }



    private void isServiceRunning_toast(){
        String dbgMsg = "Worker running? "+WPEngine.isWPWorker_running();
        Log.v("OBTask", dbgMsg);
        Toast startSequenceToast = Toast.makeText(getApplicationContext(), dbgMsg, Toast.LENGTH_LONG);
        startSequenceToast.show();
    }

    private void displayTotalDirsAndFiles() {
        WPDatabaseConnection wpDatabase = new WPDatabaseConnection(this.getApplicationContext());

        int dirs = wpDatabase.getDirectories().size();
        int images = wpDatabase.getAllWallpapers().size();

        Toast startSequenceToast = Toast.makeText(getApplicationContext(), "Total Directories: "+dirs+"\nTotal Images: "+images, Toast.LENGTH_LONG);
        startSequenceToast.show();

        Log.v("OBTask", "Total Directories: "+dirs+"\nTotal Images: "+images);

        int i = 0;
        for(String dir : wpDatabase.getDirectories()){
            Log.v("OBTask", "Directory "+i+" - "+dir+" | Uri = "+Uri.parse(dir));
        }
    }

    private void BindOnClick_OfStartStopButton(){
        final Button startWorkerBtn = (Button) findViewById(R.id.startStop_btn);
        startWorkerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean workerIsRunning = WPEngine.isWPWorker_running();
                Log.v("OBDebug", "Worker is running "+workerIsRunning);
                final WPEngine wpEngine = new WPEngine(getApplicationContext());

                if(workerIsRunning){
                    //Stop
                    wpEngine.stopChangeWallpaper_Repeated();
                }else{
                    //Avoid double clicks
                    startWorkerBtn.setEnabled(false);

                    //Start progress bar anim until the program schedules the worker and closes itself
                    ProgressBar progressBar = findViewById(R.id.engines_up_bar);
                    progressBar.setVisibility(View.VISIBLE);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String interval_raw = preferences.getString("interval_time", "15");
                    int interval = Integer.parseInt(interval_raw);
                    Log.v("OBDebug", "interval is "+interval);

                    boolean sound_on_change  = preferences.getBoolean("sound_on", false);

                    //wpEngine.changeWallpaper_Once(sound_on_change);
                    wpEngine.changeWallpaper_Repeated(interval);

                    finish();
                }
                workerIsRunning = !workerIsRunning;//It is now opposite of when we asked the engine;

                Log.v("OBDebug", "Updating UI");
                updateBackground(workerIsRunning);
                updateButton(workerIsRunning);
                updateWidget();
                updateChangeTimes();

                Log.v("OBDebug", "Finish");

            }
        });

        startWorkerBtn.setText("> Start <");
    }

    private void updateChangeTimes(){
        WPDatabaseConnection db = new WPDatabaseConnection(getApplicationContext());

        String formattedFooterText = "";

        Date lastChangeTime = db.getLastChangeTime();

        if(WPEngine.isWPWorker_running()){
            Date nextChangeTime = null;
            int minutesBetweenChanges = db.getMinutesBetweenChanges();
            if(lastChangeTime != null){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(lastChangeTime);
                calendar.add(Calendar.MINUTE, minutesBetweenChanges);
                nextChangeTime = calendar.getTime();

                formattedFooterText = String.format("Last change -> %s\nNext change -> %s", db.formatDate(lastChangeTime), db.formatDate(nextChangeTime) );
            } else {
                formattedFooterText = String.format("Last change -> %s\nNext change -> %s", "-", "-");
            }
        }else {
            formattedFooterText = String.format("Last change -> %s\nNext change -> %s", db.formatDate(lastChangeTime), "-");
        }

        TextView footer_textview = findViewById(R.id.footer_txt);
        footer_textview.setText(formattedFooterText);

    }

    private void updateButton(boolean workerIsRunning) {
        final Button startWorkerBtn = (Button) findViewById(R.id.startStop_btn);

        if(workerIsRunning){
            startWorkerBtn.setBackgroundResource(R.drawable.stop_button);
            startWorkerBtn.setText("Stop");
        }else{
            startWorkerBtn.setBackgroundResource(R.drawable.start_button);
            startWorkerBtn.setText("Start");
        }
    }

    private void updateBackground(boolean workerIsRunning) {
        RelativeLayout layout = findViewById(R.id.activity_main);
        if(workerIsRunning){
            layout.setBackgroundResource(R.drawable.green_gradient);
        }else{
            layout.setBackgroundResource(R.drawable.red_gradient);
        }

    }

    private void updateWidget(){
        Intent intentSync = new Intent(getApplicationContext(), WPWidget.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intentSync);
    }

//    private void BindSoundSwitch(){
//        Switch soundSwitch = (Switch) findViewById(R.id.switch1);
//        soundSwitch.setChecked(IsSoundOn_SharedPref());
//        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Update_IsSoundOn_SharedPref(isChecked);
//                Toast t = Toast.makeText(getApplicationContext(), "Sound is : "+isChecked, Toast.LENGTH_LONG);
//                t.show();
//            }
//        });
//    }

    private static final int RESULT_LOAD_FOLDER = 3;

    public void pickFolder(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, RESULT_LOAD_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_FOLDER && resultCode == RESULT_OK && null != data) {
            Uri folderUri = data.getData();

            this.getContentResolver().takePersistableUriPermission(folderUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

//            AddDirReference(folderUri);
            WPDatabaseConnection wpDatabase = new WPDatabaseConnection(this.getApplicationContext());

            wpDatabase.addDirectory(folderUri);

            updateDirsAndImgsText();
        }
    }


    private boolean IsSoundOn_SharedPref(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        //Treat loaded set as immutable please.
        boolean is_sound_on = sharedPref.getBoolean(getApplicationContext().getString(R.string.sound_on), true);

        return is_sound_on;
    }

    public void Update_IsSoundOn_SharedPref(boolean turnSoundOn) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sound_on), turnSoundOn);
        editor.apply();
    }

}
