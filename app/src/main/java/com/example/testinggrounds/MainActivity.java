package com.example.testinggrounds;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;

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

        BindOnClick_AndChangeNames_OfAllButtons();
    }

    private void BindOnClick_AndChangeNames_OfAllButtons() {
        BindOnClick_AndChangeNames_First_Row_Buttons();
        BindOnClick_AndChangeNames_OfConsoleButtons();
        BindOnClick_CheckService();
        BindOnClick_OfChangeButton();
        BindOnClick_OfStopper();
        BindOnClick_AndChangeNames_OfTestButton();
        BindSoundSwitch();
    }

    private void BindOnClick_AndChangeNames_First_Row_Buttons() {
        Button firstButton = (Button) findViewById(R.id.WPbutton1);
        Button secondButton = (Button) findViewById(R.id.WPbutton2);

        firstButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putStringSet(getString(R.string.images_dirs_key), new HashSet<String>());
                editor.apply();
            }
        });
        firstButton.setText("Clear Directories");

        secondButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        secondButton.setText("Debug");
    }

    private void BindOnClick_CheckService(){
        Button checkServiceBtn = (Button) findViewById(R.id.checkBtn);
        checkServiceBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String dbgMsg = "Worker running? "+isWorkScheduled("WC");
                Log.v("OBTask", dbgMsg);
                Toast startSequenceToast = Toast.makeText(getApplicationContext(), dbgMsg, Toast.LENGTH_LONG);
                startSequenceToast.show();
            }
        });
    }

    private boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance();
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = running | state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void BindOnClick_AndChangeNames_OfConsoleButtons() {
        console = (TextView) findViewById(R.id.console_textview);

        consoleButtons = new Button[3];
        consoleButtons[0] = (Button) findViewById(R.id.ConsoleBtn1);
        consoleButtons[1] = (Button) findViewById(R.id.ConsoleBtn2);
        consoleButtons[2] = (Button) findViewById(R.id.ConsoleBtn3);

        //------- {Buttons setup

        //First
        consoleButtons[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                displayTotalDirsAndFiles();

            }
        });
        consoleButtons[0].setText("Tot_Dirs/Imgs");

        //Second
        consoleButtons[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pickFolder();
            }
        });
        consoleButtons[1].setText("Pick Folder");

        //Third
        consoleButtons[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                m_uri_data = new Data.Builder()
//                        .putString(ChangeWallpaper_Worker.DIR_URI_STR_KEY, "")
//                        .putStringArray(ChangeWallpaper_Worker.DIR_LIST_URIS_STR_KEY, (dirSet.toArray(new String[0])))
                        .build();

                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ChangeWallpaper_Worker.class)
                        .setInputData(m_uri_data)
                        .addTag("WC")
                        .build();

                WorkManager.getInstance().enqueue(request);

                Log.v("OBTask", "Started One time Work "+request.getId());
            }
        });
        consoleButtons[2].setText("Next Wallpaper");
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
    private Data m_uri_data;

    private void change_wallpaper_once_with_worker(){

        m_uri_data = get_img_dir_uri_data();

        OneTimeWorkRequest onetime_changeWallpaper_work = new OneTimeWorkRequest.Builder(ChangeWallpaper_Worker.class)
                .setInputData(m_uri_data)
                .addTag("WC")
                .build();

        WorkManager.getInstance().enqueue(onetime_changeWallpaper_work);
    }

    private Data get_img_dir_uri_data(){

        displayTotalDirsAndFiles();

        Data m_uri_data = new Data.Builder()
//                .putStringArray(ChangeWallpaper_Worker.DIR_LIST_URIS_STR_KEY, (dirSet.toArray(new String[0])))
                .build();

        return m_uri_data;
    }

    private void BindOnClick_OfChangeButton(){
        final Button changeTwiceButton = (Button) findViewById(R.id.changeButton);
        changeTwiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                change_wallpaper_once_with_worker();

                m_uri_data = get_img_dir_uri_data();

                changeWallpaper_work = new PeriodicWorkRequest.Builder(ChangeWallpaper_Worker.class, 15, TimeUnit.MINUTES, 1, TimeUnit.MINUTES)
                        .setInputData(m_uri_data)
                        .addTag("WC")
                        .build();

//                WorkManager.getInstance().enqueue(changeWallpaper_work);
                WorkManager.getInstance().enqueueUniquePeriodicWork("ChangeWallpaper_Loop", ExistingPeriodicWorkPolicy.REPLACE, changeWallpaper_work);

//                Log( "Started Work "+changeWallpaper_work.getId()+" at time "+ Calendar.getInstance().getTime());

            }
        });

        changeTwiceButton.setText("> Start <");
    }

    private void BindOnClick_OfStopper() {
        Button addImageButton = (Button) findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Tag
                Log.v("OBTask", "My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
                Log.v("OBTask", "Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
                Log.v("OBTask", "Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());

                if(WorkManager.getInstance().getWorkInfosByTag("WC").cancel(true)){
                    Log.v("OBTasks", "attempt to kill service");
                    WorkManager.getInstance().pruneWork();
                }

                Log.v("OBTask", "--My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
                Log.v("OBTask", "--Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
                Log.v("OBTask", "--Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());
                //------------------------

                ListenableFuture<List<WorkInfo>> info = WorkManager.getInstance().getWorkInfosForUniqueWork("ChangeWallpaper_Loop");
                Log.v("OBTask", "(Unique) My Work is "+info.toString());
                Log.v("OBTask", "(Unique) Is Canceled = "+info.isCancelled());
                Log.v("OBTask", "(Unique) Is Done = "+info.isDone());

                WorkManager.getInstance().cancelUniqueWork("ChangeWallpaper_Loop");
                WorkManager.getInstance().pruneWork();

                Log.v("OBTask", "--(Unique) My Work is "+info.toString());
                Log.v("OBTask", "--(Unique) Is Canceled = "+info.isCancelled());
                Log.v("OBTask", "--(Unique) Is Done = "+info.isDone());

                Log.v("OBTask","Stopped Wallpaper change task??");
            }
        });
        addImageButton.setText("Stopper");
    }

    private void BindOnClick_AndChangeNames_OfTestButton() {
        //Test button
        final Button test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("OBTask", "My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
                Log.v("OBTask", "Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
                Log.v("OBTask", "Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());

                ListenableFuture<List<WorkInfo>> info = WorkManager.getInstance().getWorkInfosForUniqueWork("ChangeWallpaper_Loop");

                Log.v("OBTask", "(Unique) My Work is "+info.toString());
                Log.v("OBTask", "(Unique) Is Canceled = "+info.isCancelled());
                Log.v("OBTask", "(Unique) Is Done = "+info.isDone());

                WorkManager.getInstance().cancelAllWork();

                Log.v("OBTask", "--My Work is "+WorkManager.getInstance().getWorkInfosByTag("WC").toString());
                Log.v("OBTask", "--Is Canceled = "+WorkManager.getInstance().getWorkInfosByTag("WC").isCancelled());
                Log.v("OBTask", "--Is Done = "+WorkManager.getInstance().getWorkInfosByTag("WC").isDone());

                Log.v("OBTask", "--(Unique) My Work is "+info.toString());
                Log.v("OBTask", "--(Unique) Is Canceled = "+info.isCancelled());
                Log.v("OBTask", "--(Unique) Is Done = "+info.isDone());
            }
        });
        test.setText("SuperStopper");
    }


    private void BindSoundSwitch(){
        Switch soundSwitch = (Switch) findViewById(R.id.switch1);
        soundSwitch.setChecked(IsSoundOn_SharedPref());
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Update_IsSoundOn_SharedPref(isChecked);
                Toast t = Toast.makeText(getApplicationContext(), "Sound is : "+isChecked, Toast.LENGTH_LONG);
                t.show();
            }
        });
    }

    public void changeWallpaper(Bitmap bm){

        Log.v("OBTask","WPChanger!");

        WallpaperManager wallpaperManager =  WallpaperManager.getInstance(getApplicationContext());

        try {
            wallpaperManager.setBitmap(bm);
            wallpaperManager.setBitmap(bm, null ,true, WallpaperManager.FLAG_LOCK);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
