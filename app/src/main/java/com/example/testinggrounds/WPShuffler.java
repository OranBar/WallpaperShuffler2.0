package com.example.testinggrounds;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.List;
import java.util.Random;

//Linked to WPDatabaseConnection
public class WPShuffler {

    public Context context;
    public WPDatabaseConnection wpDatabase;
    public WPChanger wpChanger;

    public WPShuffler(Context context, WPDatabaseConnection wpDatabase){
        this.context = context;
        this.wpDatabase = wpDatabase;
        this.wpChanger = new WPChanger(context);
    }

    public final Uri next(){
        Uri wpUri = selectNextWallpaper();
        return wpUri;
    }

    protected Uri selectNextWallpaper(){
        int wpIndex = new Random().nextInt(wpDatabase.getTotalWallpapersCount());
        Uri wpUri = getWallpaper(wpIndex);
        return wpUri;
    }

    //TODO: figure out how to take this function apart and move the logic of the index selection for wallpapers by considering all images flattened into a list
//    private Uri getWallpaper(int index){
//        int chosenIndex = index;
//        int directoryIndex = 0;
//        String[] dirs = wpDatabase.getDirectories();
//        int currDirImagesCount = wpDatabase.getImagesCount_FromDir(dirs[directoryIndex]);
//        while(currDirImagesCount  <= chosenIndex){
//            Log.v("OBTask", "Curr Dir Index="+directoryIndex+"-#Files= "+currDirImagesCount);
//            chosenIndex = chosenIndex - currDirImagesCount ;
//            directoryIndex++;
//            currDirImagesCount = wpDatabase.getImagesCount_FromDir(dirs[directoryIndex]);
//        }
//
//        Log.v("OBTask", "Chosen Dir Index="+directoryIndex);
//        Log.v("ObTask", "Chosen Index = "+chosenIndex);
//
//        Uri wallpaperDirUri = Uri.parse(dirs[directoryIndex]);
//        Uri wallpaper = wpDatabase.getFilesFromDir(wallpaperDirUri)[chosenIndex].getUri();
//
//        return wallpaper;
//    }

    private Uri getWallpaper(int index){
        List<Uri> wallpapers = wpDatabase.getAllWallpapers();

        Uri wallpaper = wallpapers.get(index);

        return wallpaper;
    }

}
