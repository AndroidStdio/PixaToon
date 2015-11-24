package com.ajscape.pixatoon.filters.graycartoon;

import android.app.Fragment;

import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class GrayCartoonFilter extends Filter {

    private int threshold;
    private int thickness;

    public GrayCartoonFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.grayCartoonFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), thickness, threshold);
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
