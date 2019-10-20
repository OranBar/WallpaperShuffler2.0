package com.example.testinggrounds;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ChangeWallpaper_Activity extends AppCompatActivity {

//    public static final String IMG_URI_STR_KEY = "IMG_URI";
//    public static final String DIR_URI_STR_KEY = "DIR_URI";
//    public static final String DIR_LIST_URIS_STR_KEY = "DIR_LIST_URIS";
//    public static final String CHOSEN_WALLPAPER_URI_RESULT_KEY = "CHOSEN_WALLPAPER_URI_RESULT";

    public boolean sound_on_change = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("OBChangeWallpaperActivity", "Create");
        //sound_on_change = IsSoundOn_SharedPref();

        disableWifi();

        changeWallpaper();
        Log.v("OBChangeWallpaperActivity", "Wallpaper Changed");

        finish();
    }

    public void disableWifi(){
        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    private static int Current_Index = -1;



    private Set<String> loadDirSet(){
        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        Set<String> images_set = sharedPref.getStringSet(context.getString(R.string.images_dirs_key), new HashSet<String>());

        return images_set;
    }

    public  void changeWallpaper() {
        String[] dirsUris_str = loadDirSet().toArray(new String[0]);

        if(dirsUris_str == null || dirsUris_str.length == 0){
            Log.e("OBError", "No directories in list");
            return;
        }

        Uri wallpaperUriToSet = pickWallpaperFromFolders(dirsUris_str);

        Log.v("ChangeWallpaperService","Chosen wallpaper uri is "+wallpaperUriToSet);

        changeWallpaper(wallpaperUriToSet);
    }

    private Random rnd = new Random();

    private int getImagesCount_FromDirs(String[] foldersUri_str){
        int total = 0;
        for(String uri_str : foldersUri_str){
            total += getImagesCount_FromDir(uri_str);
        }
        return total;
    }

    private int getImagesCount_FromDir(String singleFolderUri_str){
        int total = 0;
        total += getFilesFromDir(Uri.parse(singleFolderUri_str)).length;
        return total;
    }

    private Uri pickWallpaperFromFolders(String[] foldersUri_str){
        Uri rnd_wallpaper = null;
        int imagesTotal = getImagesCount_FromDirs(foldersUri_str);

        Log.v("ObTask", "Total wallpapers = "+imagesTotal);

        int chosenIndex = rnd.nextInt(imagesTotal);
//        int chosenIndex = 11;

        Log.v("ObTask", "Chosen Index = "+chosenIndex);

//        Looper.prepare();
//        Looper.loop();
//        Toast startSequenceToast = Toast.makeText(getApplicationContext(), "Chosen Index = "+chosenIndex, Toast.LENGTH_LONG);
//        startSequenceToast.show();
//        Looper looper = Looper.myLooper();
//        looper.quit();

        int directoryIndex = 0;
        int currDirImagesCount = getImagesCount_FromDir(foldersUri_str[directoryIndex]);
        while(currDirImagesCount  <= chosenIndex){
            Log.v("OBTask", "Curr Dir Index="+directoryIndex+"-#Files= "+currDirImagesCount);
            chosenIndex = chosenIndex - currDirImagesCount ;
            directoryIndex++;
            currDirImagesCount = getImagesCount_FromDir(foldersUri_str[directoryIndex]);
        }

        Log.v("OBTask", "Chosen Dir Index="+directoryIndex);
        Log.v("ObTask", "Chosen Index = "+chosenIndex);

        Uri wallpaperDirUri = Uri.parse(foldersUri_str[directoryIndex]);
        rnd_wallpaper = getFilesFromDir(wallpaperDirUri)[chosenIndex].getUri();

        return rnd_wallpaper;
    }

    public void changeWallpaper(Uri bm_uri){

        Log.v("OBTask","Changing Wallpaper!");

        WallpaperManager wallpaperManager =  WallpaperManager.getInstance(getApplicationContext());

        try {
            //import non-scaled bitmap wallpaper
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            InputStream is = null;
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            is = contentResolver.openInputStream(bm_uri);
            Bitmap wallpaper = BitmapFactory.decodeStream(is, null, options);


            float aspectRatio = wallpaper.getWidth() /
                    (float) wallpaper.getHeight();
            int b_width = 1080;
            int b_height = Math.round(b_width / aspectRatio);

            Bitmap bitmap = Bitmap.createScaledBitmap(wallpaper, b_width, b_height, true);
            wallpaper = bitmap;
//                decodedSampleBitmap = Bitmap.createScaledBitmap(
//                        decodedSampleBitmap, b_width, b_height, false);
//            Bitmap wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper, options);

            if (wallpaperManager.getDesiredMinimumWidth() > wallpaper.getWidth() &&
                    wallpaperManager.getDesiredMinimumHeight() > wallpaper.getHeight()) {
                //add padding to wallpaper so background image scales correctly
                int xPadding = Math.max(0, wallpaperManager.getDesiredMinimumWidth() - wallpaper.getWidth()) / 2;
                int yPadding = Math.max(0, wallpaperManager.getDesiredMinimumHeight() - wallpaper.getHeight()) / 2;
                Bitmap paddedWallpaper = Bitmap.createBitmap(wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight(), Bitmap.Config.ARGB_8888);
                int[] pixels = new int[wallpaper.getWidth() * wallpaper.getHeight()];
                wallpaper.getPixels(pixels, 0, wallpaper.getWidth(), 0, 0, wallpaper.getWidth(), wallpaper.getHeight());
                paddedWallpaper.setPixels(pixels, 0, wallpaper.getWidth(), xPadding, yPadding, wallpaper.getWidth(), wallpaper.getHeight());

                wallpaperManager.setBitmap(paddedWallpaper, null, false, WallpaperManager.FLAG_LOCK);
                wallpaperManager.setBitmap(paddedWallpaper);
            } else {
                wallpaperManager.setBitmap(wallpaper);
            }
        } catch (IOException e) {
            Log.e("OBTask", "Change Wallpaper FAILED");
            Log.e("OBTask", e.getMessage());
        }

        Log.v("OBTask", "Change Wallpaper Successful");

        if(sound_on_change){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
    }

    public void changeWallpaper_(Uri bm_uri){

        Log.v("OBTask","Changing Wallpaper!");

        WallpaperManager wallpaperManager =  WallpaperManager.getInstance(getApplicationContext());

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = getApplicationContext().getSystemService(WindowManager.class);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
//            width = width << 1; // best wallpaper width is twice screen width

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            InputStream is = null;
            is = contentResolver.openInputStream(bm_uri);
            BitmapFactory.decodeStream(is, null, options);


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            is = contentResolver.openInputStream(bm_uri);


            Bitmap decodedSampleBitmap = BitmapFactory.decodeStream(is);


            try {
                float aspectRatio = decodedSampleBitmap.getWidth() /
                        (float) decodedSampleBitmap.getHeight();
                int b_width = 1080;
                int b_height = Math.round(b_width / aspectRatio);

//                Bitmap bitmap = Bitmap.createScaledBitmap(decodedSampleBitmap, width, height, true);
//                decodedSampleBitmap = bitmap;
//                decodedSampleBitmap = Bitmap.createScaledBitmap(
//                        decodedSampleBitmap, b_width, b_height, false);

                wallpaperManager.setBitmap(decodedSampleBitmap,null,false, WallpaperManager.FLAG_LOCK);
                wallpaperManager.setBitmap(decodedSampleBitmap,null,false);

//                wallpaperManager.setBitmap(decodedSampleBitmap);
            } catch (IOException e) {
                Log.e("OBError", "Cannot set image as wallpaper", e);
            }

            Log.v("OBTask", "Change Wallpaper Successful");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("OBTask", "Change Wallpaper FAILED");
        }

        if(sound_on_change){
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap getBitmapFromImageUri(Uri imageUri){
        Bitmap newWallpaper = null;

        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            InputStream is = null;
            is = contentResolver.openInputStream(imageUri);
            newWallpaper = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return newWallpaper;
    }

    private DocumentFile[] getFilesFromDir(Uri folderUri){
        DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), folderUri);

        DocumentFile[] files = documentFile.listFiles();
        return files;
    }

    private boolean IsSoundOn_SharedPref(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        //Treat loaded set as immutable please.
        boolean is_sound_on = sharedPref.getBoolean(getApplicationContext().getString(R.string.sound_on), true);

        return is_sound_on;
    }
}
