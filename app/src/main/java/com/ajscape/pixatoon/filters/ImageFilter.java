package com.ajscape.pixatoon.filters;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public interface ImageFilter {
    public ImageFilterType getType();
    public void apply(Mat img);
}
