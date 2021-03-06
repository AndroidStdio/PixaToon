package com.ajscape.pixatoon.viewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
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

    private static final String SAVE_FOLDER = "Pixatoon";
    private static final String SAVE_FILENAME_PREFIX = "IMG";

    private static int lastSaveFileIndex = 0;

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

    public static String saveBitmap(Activity activity, Bitmap bitmap) throws IOException {
        File file;
        try {
            file = createSaveFile();
            OutputStream fOut = new FileOutputStream(file);
            // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.close();

            // Add saved file to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);

        } catch(IOException e) {
            String errorMsg = "Unable to save image";
            Log.e(TAG, errorMsg + ":" + e.getMessage());
            throw new IOException(errorMsg);
        }
        return file.getPath();
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static File createSaveFile() throws IOException {
        File file;
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        String saveFolderPath = path + '/' + SAVE_FOLDER;
        int saveFileIndex = lastSaveFileIndex;
        do {
            String saveFileName = SAVE_FILENAME_PREFIX + String.format("%03d", ++saveFileIndex) + ".jpg";
            String saveFilePath = saveFolderPath + '/' + saveFileName;
            file = new File(saveFilePath); // the File to save to
            file.getParentFile().mkdirs();
        } while(file.exists());
        file.createNewFile();
        return file;
    }
}
