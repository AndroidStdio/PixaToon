package com.ajscape.pixatoon.filters.graysketch;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;
import com.ajscape.pixatoon.viewer.MainActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class GraySketchFilter extends Filter {

    private Mat[] mSketchTextures;

    public GraySketchFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
        mSketchTextures = new Mat[3];
    }

    public void loadSketchTextures(Resources res, int darkTexRes, int mediumTexRes, int lightTexRes) {
        mSketchTextures[0] = loadResource(res, darkTexRes);
        mSketchTextures[1] = loadResource(res, mediumTexRes);
        mSketchTextures[2] = loadResource(res, lightTexRes);
        Native.setSketchTextures(
                mSketchTextures[0].getNativeObjAddr(),
                mSketchTextures[1].getNativeObjAddr(),
                mSketchTextures[2].getNativeObjAddr() );
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.graySketchFilter(src.getNativeObjAddr(), dst.getNativeObjAddr());
    }

    @Override
    public void resetConfig() {
    }

    private static Mat loadResource(Resources res, int drawable) {
        Mat mat, tempMat;
        Bitmap bmp = BitmapFactory.decodeResource(res, drawable);

        tempMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC4);
        mat = new Mat(tempMat.size(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, tempMat);
        bmp.recycle();
        bmp = null;
        Imgproc.cvtColor(tempMat, mat, Imgproc.COLOR_RGBA2GRAY);
        return mat;
    }
}
