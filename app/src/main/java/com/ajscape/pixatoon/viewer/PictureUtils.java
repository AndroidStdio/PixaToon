package com.ajscape.pixatoon.viewer;

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
public class PictureUtils {
    public static final String TAG = "PictureUtils:";

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
            return scaledBitmap;
        } else {
            return bitmap;
        }
    }

    public static String saveBitmap(ContentResolver cr,Bitmap bitmap) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        String savedFilePath = path+ "/Pixatoon/test.jpg";
        File file = new File(savedFilePath); // the File to save to
        file.getParentFile().mkdirs();
        try {
            if(!file.exists())
                file.createNewFile();
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
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
