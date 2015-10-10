package com.ajscape.pixatoon.filters;

import android.app.Fragment;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public abstract class Filter {
    private FilterType filterType;
    private Fragment configFragment;

    public Filter(FilterType filterType, Fragment configFragment) {
        this.filterType = filterType;
        this.configFragment = configFragment;
    }

    public FilterType getType() {
        return filterType;
    }

    public Fragment getConfigFragment() {
        return configFragment;
    }

    public abstract void process(Mat src, Mat dst);

    public abstract void resetConfig();
}
