package com.ajscape.pixatoon.filters;

/**
 * Created by AtulJadhav on 9/21/2015.
 */
public class Native {
    public static native void colorCartoonFilter(long srcMatAddr, long dstMatAddr, int thickness, int threshold);

    public static native void grayCartoonFilter(long srcMatAddr, long dstMatAddr, int thickness, int threshold);

    public static native void setSketchTextures(long darkTexMatAddr,long mediumTexMatAddr,long lightTexMatAddr );

    public static native void graySketchFilter(long srcMatAddr, long dstMatAddr);

    public static native void pencilSketchFilter(long srcMatAddr, long dstMatAddr, int blurRadius, int contrast);

    public static native void oilPaintFilter(long srcMatAddr, long dstMatAddr, int radius, int levels);

    public static native void waterColorFilter(long srcMatAddr, long dstMatAddr, int spatialRadius, int colorRadius, int maxLevels, int scaleFactor);
}
