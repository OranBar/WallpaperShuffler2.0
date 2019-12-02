package com.example.testinggrounds;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


//Need class WPScaler and multiple subclasses that define scaling methods, if I want to do more of those.
public class WPChanger {

    public Context context;

    public WPChanger(Context context){
        this.context = context;
    }

    public void setWallpaper(Uri bm_uri){

        Log.v("OBTask","Changing Wallpaper!");

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


            float aspectRatio = wallpaper.getWidth() /
                    (float) wallpaper.getHeight();
            int b_width = 1080;
            int b_height = Math.round(b_width / aspectRatio);

            Bitmap bitmap = Bitmap.createScaledBitmap(wallpaper, b_width, b_height, true);
            wallpaper = bitmap;
//                decodedSampleBitmap = Bitmap.createScaledBitmap(
//                        decodedSampleBitmap, b_width, b_height, false);
//            Bitmap wallpaper = BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper, options);



            if (wallpaperManager.getDesiredMinimumWidth() > wallpaper.getWidth() ||
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
            Log.e("OBTask", "Change Wallpaper FAILED", e);
//            Log.e("OBTask", e.getMessage());
        }

        Log.v("OBTask", "Change Wallpaper Successful");
    }


}
