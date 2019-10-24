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

    public void addDirectory(Uri directoryUri){
        String directoryUri_str = directoryUri.toString();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        //Treat loaded set as immutable please.
        Set<String> loaded_set_immutable = sharedPref.getStringSet(context.getString(R.string.images_dirs_key), new HashSet<String>());
        //----
        Set<String> images_set = new HashSet<>(loaded_set_immutable);

        images_set.add(directoryUri_str);

        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.clear();
        editor.putStringSet(context.getString(R.string.images_dirs_key), images_set);
        editor.apply();
    }

    public Set<String> getDirectories(){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.OBbWallpaperShuffler_SharedPrefName), Context.MODE_PRIVATE);

        Set<String> images_dirs_set = sharedPref.getStringSet(context.getString(R.string.images_dirs_key), new HashSet<String>());

//        Set<String> rslt = new HashSet<>();
//        for(String images_dir : images_dirs_set){
//            images_dir = images_dir.replace("--", "");
//            rslt.add(images_dir);
//        }

        return images_dirs_set;
    }

    public int getTotalWallpaperDirectoriesCount(){
        return getDirectories().size();
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
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-HH:mm");
        String formattedDate = df.format(currentTime);
        editor.putString(context.getString(R.string.last_changetime), formattedDate);
        editor.putString(context.getString(R.string.currWpUri), wpUri.toString());
        editor.apply();
    }

    public List<Uri> getAllWallpapers() {
        int directoryIndex = 0;
        Set<String> dirs = this.getDirectories();
        List<Uri> rslt = new ArrayList<>();
        for(String dir : dirs){
            rslt.addAll(this.getImages_FromDir(dir));
        }
        return rslt;
    }
}
