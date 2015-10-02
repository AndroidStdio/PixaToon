package com.ajscape.pixatoon.common;

import android.app.Fragment;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public abstract class Filter {
    private FilterType filterType;
    private Fragment configFragment;
    private int filterSelectorBtnId;

    public Filter(FilterType filterType, Fragment configFragment, int filterSelectorBtnId) {
        this.filterType = filterType;
        this.configFragment = configFragment;
        this.filterSelectorBtnId = filterSelectorBtnId;
    }

    public FilterType getType() {
        return filterType;
    }

    public Fragment getConfigFragment() {
        return configFragment;
    }

    public int getFilterSelectorBtnId() {
        return filterSelectorBtnId;
    }

    public abstract void process(Mat src, Mat dst);

    public abstract void resetConfig();
}
