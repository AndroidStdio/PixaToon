package com.ajscape.pixatoon.filters.colorcartoon;

import android.app.Fragment;

import com.ajscape.pixatoon.filters.Native;
import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class ColorCartoonFilter extends Filter {

    private int threshold;
    private int thickness;

    public ColorCartoonFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.colorCartoonFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), thickness, threshold);
    }

    @Override
    public void resetConfig() {
        thickness = 40;
        threshold = 50;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }
}
