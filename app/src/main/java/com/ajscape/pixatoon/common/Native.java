package com.ajscape.pixatoon.common;

/**
 * Created by AtulJadhav on 9/21/2015.
 */
public class Native {
    public static native void colorCartoonFilter(long srcMatAddr, long dstMatAddr, int thickness, int threshold);
}
