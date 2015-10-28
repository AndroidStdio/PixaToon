package com.ajscape.pixatoon.filters.pencilsketch;

import android.app.Fragment;

import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class PencilSketchFilter extends Filter {

    private int contrast;
    private int blurRadius;

    public PencilSketchFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.pencilSketchFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), blurRadius, contrast);
    }

    @Override
    public void resetConfig() {
        blurRadius = 20;
        contrast = 50;
    }

    public int getContrast() {
        return contrast;
    }

    public void setContrast(int contrast) {
        this.contrast = contrast;
    }

    public int getBlurRadius() {
        return blurRadius;
    }

    public void setBlurRadius(int blurRadius) {
        this.blurRadius = blurRadius;
    }
}
