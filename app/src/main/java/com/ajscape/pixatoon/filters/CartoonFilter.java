package com.ajscape.pixatoon.filters;

import com.ajscape.pixatoon.Native;

import org.opencv.core.Mat;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class CartoonFilter implements ImageFilter{

    private ImageFilterType filterType = ImageFilterType.CARTOON;

    @Override
    public ImageFilterType getType() {
        return filterType;
    }

    @Override
    public void apply(Mat img) {
        Native.applyCartoonFilter(img.getNativeObjAddr());
    }
}
