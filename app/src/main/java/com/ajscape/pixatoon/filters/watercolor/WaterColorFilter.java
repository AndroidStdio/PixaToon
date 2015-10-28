package com.ajscape.pixatoon.filters.watercolor;

import android.app.Fragment;

import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class WaterColorFilter extends Filter {

    private int spatialRadius;
    private int colorRadius;
    private int maxLevels;
    private int scaleFactor;

    public WaterColorFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.waterColorFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), spatialRadius, colorRadius, maxLevels, scaleFactor);
    }

    @Override
    public void resetConfig() {
        spatialRadius = 25;
        colorRadius = 40;
        maxLevels = 10;
        scaleFactor = 50;
    }

    public int getSpatialRadius() {
        return spatialRadius;
    }

    public void setSpatialRadius(int spatialRadius) {
        this.spatialRadius = spatialRadius;
    }

    public int getColorRadius() {
        return colorRadius;
    }

    public void setColorRadius(int colorRadius) {
        this.colorRadius = colorRadius;
    }

    public int getMaxLevels() {
        return maxLevels;
    }

    public void setMaxLevels(int maxLevels) {
        this.maxLevels = maxLevels;
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
}
