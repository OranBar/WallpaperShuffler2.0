package com.example.testinggrounds;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.IOException;
import java.io.InputStream;


//Need class WPScaler and multiple subclasses that define scaling methods, if I want to do more of those.
public class WPChanger {

    public Context context;

    public WPChanger(Context context){
        this.context = context;
    }

    private Bitmap scaleBitmapKeepingAspectRatio(int targetWidth, Bitmap wallpaper){
        float aspectRatio = wallpaper.getWidth() /
                (float) wallpaper.getHeight();
        int targetHeight = Math.round(targetWidth / aspectRatio);

        Bitmap bitmap = Bitmap.createScaledBitmap(wallpaper, targetWidth, targetHeight, true);
        wallpaper = bitmap;
        return wallpaper;
    }

    private Bitmap scaleBitmap(int targetWidth, int targetHeight, Bitmap wallpaper){
        Bitmap bitmap = Bitmap.createScaledBitmap(wallpaper, targetWidth, targetHeight, true);
        wallpaper = bitmap;
        return wallpaper;
    }

    public void setWallpaper(Uri bm_uri, boolean stretch){

        Log.v("OBTask","Changing Wallpaper!");

        // 1. Get screen size.
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int screenWidth  = metrics.widthPixels;
//        final int screenHeight = metrics.heightPixels;
        final int screenHeight = 2280;

        WallpaperManager wallpaperManager =  WallpaperManager.getInstance(context);

//        wallpaperManager.setWallpaperOffsets(0,0);

        try {
            //import non-scaled bitmap wallpaper
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            InputStream is = null;
            ContentResolver contentResolver = context.getContentResolver();
            is = contentResolver.openInputStream(bm_uri);
            Bitmap wallpaper = BitmapFactory.decodeStream(is, null, options);

            if(stretch){
                wallpaper = scaleBitmap(screenWidth, screenHeight, wallpaper);
            } else {
                wallpaper = scaleBitmapKeepingAspectRatio(screenWidth, wallpaper);
                if ( wallpaperNeedsPadding(screenWidth, screenHeight, wallpaper)){
                    //add padding to wallpaper so background image scales correctly
                    int xPadding = Math.max(0, screenWidth - wallpaper.getWidth()) / 2;
                    int yPadding = Math.max(0, screenHeight - wallpaper.getHeight()) / 2;

                    Bitmap paddedWallpaper = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

                    //TODO: Create a big black bitmap here, then keep going

                    int[] pixels = new int[wallpaper.getWidth() * wallpaper.getHeight()];
                    wallpaper.getPixels(pixels, 0, wallpaper.getWidth(), 0, 0, wallpaper.getWidth(), wallpaper.getHeight());
                    paddedWallpaper.setPixels(pixels, 0, wallpaper.getWidth(), xPadding, yPadding, wallpaper.getWidth(), wallpaper.getHeight());

                    wallpaper = paddedWallpaper;


                }
            }

            wallpaperManager.setBitmap(wallpaper, null, false, WallpaperManager.FLAG_LOCK);
            wallpaperManager.setBitmap(wallpaper);

            //Scale while keeping aspect ratio

//                decodedSampleBitmap = Bitmap.createScaledBitmap(
//                        decodedSampleBitmap, b_width, b_height, false);
//            Bitmap wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper, options);


            //Check if needs padding
//            if ( wallpaperNeedsPadding(wallpaper)){
//                //add padding to wallpaper so background image scales correctly
//                int xPadding = Math.max(0, wallpaperManager.getDesiredMinimumWidth() - wallpaper.getWidth()) / 2;
//                int yPadding = Math.max(0, wallpaperManager.getDesiredMinimumHeight() - wallpaper.getHeight()) / 2;
//
//                Bitmap paddedWallpaper = Bitmap.createBitmap(wallpaperManager.getDesiredMinimumWidth(), wallpaperManager.getDesiredMinimumHeight(), Bitmap.Config.ARGB_8888);
//
//                int[] pixels = new int[wallpaper.getWidth() * wallpaper.getHeight()];
//                wallpaper.getPixels(pixels, 0, wallpaper.getWidth(), 0, 0, wallpaper.getWidth(), wallpaper.getHeight());
//                paddedWallpaper.setPixels(pixels, 0, wallpaper.getWidth(), xPadding, yPadding, wallpaper.getWidth(), wallpaper.getHeight());
//
//                wallpaperManager.setBitmap(paddedWallpaper, null, false, WallpaperManager.FLAG_LOCK);
//                wallpaperManager.setBitmap(paddedWallpaper);
//
//            } else {
//                wallpaperManager.setBitmap(wallpaper);
//            }
        } catch (IOException e) {
            Log.e("OBTask", "Change Wallpaper FAILED", e);
//            Log.e("OBTask", e.getMessage());
        }

        Log.v("OBTask", "Change Wallpaper Successful");
    }

    private boolean wallpaperNeedsPadding(int screenWidth, int screenHeight, Bitmap wallpaper) {
        return (screenWidth > wallpaper.getWidth() || screenHeight > wallpaper.getHeight());
    }


}
