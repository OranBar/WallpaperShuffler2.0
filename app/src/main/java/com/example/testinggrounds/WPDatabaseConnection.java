package com.example.testinggrounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.util.HashSet;
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
        total += getFilesFromDir(Uri.parse(singleFolderUri_str)).length;
        return total;
    }

    public DocumentFile[] getFilesFromDir(Uri folderUri){
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, folderUri);

        DocumentFile[] files = documentFile.listFiles();
        return files;
    }

    public void logWallpaperChanged(Uri wpUri) {

    }
}
