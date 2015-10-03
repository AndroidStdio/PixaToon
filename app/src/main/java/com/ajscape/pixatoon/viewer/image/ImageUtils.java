package com.ajscape.pixatoon.viewer.image;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by AtulJadhav on 10/2/2015.
 */
public class ImageUtils {
    public static final String TAG = "ImageUtils:";

    public static Bitmap decodeSampledBitmapFromFile(String imgPath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
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

    public static String saveBitmap(ContentResolver cr,Bitmap pictureBitmap) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        String savedFilePath = path+ "/Pixatoon/test.jpg";
        File file = new File(savedFilePath); // the File to save to
        file.getParentFile().mkdirs();
        try {
            if(!file.exists())
                file.createNewFile();
            fOut = new FileOutputStream(file);

            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(cr, file.getAbsolutePath(), file.getName(), file.getName());
        } catch(IOException e) {
            String errorMsg = "Unable to save image at "+ savedFilePath;
            Log.e(TAG, errorMsg + ":" + e.getMessage());
            throw new IOException(errorMsg);
        }
        return savedFilePath;
    }
}
