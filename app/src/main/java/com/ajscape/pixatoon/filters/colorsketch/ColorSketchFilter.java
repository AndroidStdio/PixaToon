package com.ajscape.pixatoon.filters.colorsketch;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class ColorSketchFilter extends Filter {

    private Mat mSketchTexture;
    private int sketchBlend;
    private int contrast;

    public ColorSketchFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    public int getSketchBlend() {
        return sketchBlend;
    }

    public void setSketchBlend(int sketchBlend) {
        this.sketchBlend = sketchBlend;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public void loadSketchTexture(Resources res, int sketchTexRes) {
        mSketchTexture = loadResource(res, sketchTexRes);
        Native.setSketchTexture(mSketchTexture.getNativeObjAddr());
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.colorSketchFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), sketchBlend, contrast);
    }

    @Override
    public void resetConfig() {
        sketchBlend = 80;
        contrast = 30;
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
