package com.ajscape.pixatoon.filters.oilpaint;

import android.app.Fragment;

import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.Native;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class OilPaintFilter extends Filter {

    private int levels;
    private int radius;

    public OilPaintFilter(FilterType filterType, Fragment configFragment) {
        super(filterType, configFragment);
        resetConfig();
    }

    @Override
    public void process(Mat src, Mat dst) {
        Native.oilPaintFilter(src.getNativeObjAddr(), dst.getNativeObjAddr(), radius, levels);
    }

    @Override
    public void resetConfig() {
        radius = 35;
        levels = 40;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
