package com.ajscape.pixatoon.filters.colorcartoon;

import android.app.Fragment;

import com.ajscape.pixatoon.common.Native;
import com.ajscape.pixatoon.common.Filter;
import com.ajscape.pixatoon.common.FilterType;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class ColorCartoonFilter extends Filter {

    private int threshold;
    private int thickness;

    public ColorCartoonFilter(FilterType filterType, Fragment configFragment, int filterSelectorBtnId) {
        super(filterType, configFragment, filterSelectorBtnId);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.colorCartoonFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), thickness, threshold);
    }

    @Override
    public void resetConfig() {
        threshold = 50;
        thickness = 50;
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
