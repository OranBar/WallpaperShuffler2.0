package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WPDatabaseConnection {

    public Context context;

    public WPDatabaseConnection(Context context){
        this.context = context;
    }

    public String[] getDirectories(){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        Set<String> images_dirs_set = sharedPref.getStringSet(context.getString(R.string.images_dirs_key), new HashSet<String>());

        return images_dirs_set.toArray(new String[images_dirs_set.size()]);
    }

    public int getTotalWallpaperDirectoriesCount(){
        return getDirectories().length;
    }

    public int getTotalWallpapersCount(){
        return getImagesCount_FromDirs(getDirectories());
    }

    public int getImagesCount_FromDirs(Set<String> foldersUri_str){
        int total = 0;
        for(String uri_str : foldersUri_str){
            total += getImagesCount_FromDir(uri_str);
        }
        return total;
    }

    public int getImagesCount_FromDirs(String[] foldersUri_str){
        int total = 0;
        for(String uri_str : foldersUri_str){
            total += getImagesCount_FromDir(uri_str);
        }
        return total;
    }

    public int getImagesCount_FromDir(String singleFolderUri_str){
        int total = 0;
        total += getImages_FromDir(singleFolderUri_str).size();
        return total;
    }

    public List<Uri> getImages_FromDir(String dir){
        List<Uri> rslt = new ArrayList<>();
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, Uri.parse(dir));
        DocumentFile[] files = documentFile.listFiles();

        for (DocumentFile file : files){
            if(file.getType().contains("image")){
                rslt.add(file.getUri());
            }
        }
        return rslt;
    }

    public void logWallpaperChanged(Uri wpUri) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-HH-mm");
        String formattedDate = df.format(df);
        editor.putString(context.getString(R.string.last_changetime), formattedDate);
        editor.apply();
    }

    public List<Uri> getAllWallpapers() {
        int directoryIndex = 0;
        String[] dirs = this.getDirectories();
        List<Uri> rslt = new ArrayList<>();
        for(String dir : dirs){
            rslt.addAll(this.getImages_FromDir(dir));
        }
        return rslt;
    }
}
