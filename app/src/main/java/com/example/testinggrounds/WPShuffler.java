package com.example.testinggrounds;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.Random;

//Linked to WPDatabaseConnection
public class WPShuffler {

    public Context context;
    public WPDatabaseConnection wpDatabase;
    public WPChanger wpChanger;

    public WPShuffler(Context context){
        this.context = context;
        this.wpDatabase = new WPDatabaseConnection(context);
        this.wpChanger = new WPChanger(context);
    }

    public void next(){
        int wpIndex = selectNextWallpaper();
        Uri wpUri = getWallpaper(wpIndex);
        wpChanger.changeWallpaper(wpUri);
        wpDatabase.logWallpaperChanged(wpUri);
    }

    protected int selectNextWallpaper(){
        return new Random().nextInt(wpDatabase.getTotalWallpapersCount());
    }

    public Uri getWallpaper(int index){
        int chosenIndex = index;
        int directoryIndex = 0;
        String[] dirs = wpDatabase.getDirectories();
        int currDirImagesCount = wpDatabase.getImagesCount_FromDir(dirs[directoryIndex]);
        while(currDirImagesCount  <= chosenIndex){
            Log.v("OBTask", "Curr Dir Index="+directoryIndex+"-#Files= "+currDirImagesCount);
            chosenIndex = chosenIndex - currDirImagesCount ;
            directoryIndex++;
            currDirImagesCount = wpDatabase.getImagesCount_FromDir(dirs[directoryIndex]);
        }

        Log.v("OBTask", "Chosen Dir Index="+directoryIndex);
        Log.v("ObTask", "Chosen Index = "+chosenIndex);

        Uri wallpaperDirUri = Uri.parse(dirs[directoryIndex]);
        Uri wallpaper = wpDatabase.getFilesFromDir(wallpaperDirUri)[chosenIndex].getUri();

        return wallpaper;
    }


}
