package com.tutors.varsity.util;

import android.graphics.BitmapFactory;

/**
 * Created by Landon on 5/21/15.
 */
public class BitmapHelper {

    public static boolean wideScreen(int width, int height) {

        boolean isWideScreen = false;
        int gcd = calculateGCD(width,height);
        int w = width / gcd;
        int h = height / gcd;


        if (h == 4 && w == 3) {
            isWideScreen = false;
        }
        else if ((h==16 && w==9) || (w==16 && h==9)) {
            isWideScreen = true;
        }
        else if (w==91 && h==162) {
            isWideScreen = true;
        }
        else if (w == width && h ==height ) {
            isWideScreen = false;
        }
        return isWideScreen;
    }

    public static int calculateGCD(int m, int n) {
        if(n==0) {
            return m;
        }
        else if (n>m) {
            return calculateGCD(n,m);
        }
        else {
            return calculateGCD(n,m%n);
        }
    }

    public static android.graphics.Bitmap optimize(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);


        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inPurgeable = true;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //Bitmap pic = BitmapFactory.decodeFile(file);
        android.graphics.Bitmap pic = BitmapFactory.decodeFile(path,options);

        return pic;

    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        if (inSampleSize == 1) {
            if (options.outHeight > reqHeight && options.outWidth > reqWidth) {
                inSampleSize = 2;
            }
        }

        return inSampleSize;
    }
}
