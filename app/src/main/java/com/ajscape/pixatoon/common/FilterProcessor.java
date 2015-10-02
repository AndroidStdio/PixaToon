package com.ajscape.pixatoon.common;

import org.opencv.core.Mat;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by AtulJadhav on 10/2/2015.
 */
public class FilterProcessor {

    private ArrayBlockingQueue<Mat> mFilteredMatQueue;
    private static final int MAX_QUEUE_SIZE = 5;
    private boolean mInputMode;

    public FilterProcessor(boolean initInputMode) {
        mInputMode = initInputMode;
        mFilteredMatQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    }

    synchronized void enqueueFilteredMat(Mat filteredMat, boolean inputMode) {
        if(inputMode == mInputMode)
            mFilteredMatQueue.add(filteredMat);
    }

    public Mat processFilter(Filter filter, Mat srcMat, Mat dstMat) {
        if(mFilteredMatQueue.size() < MAX_QUEUE_SIZE) {
            Thread processorThread = new FilterProcessorThread(this,filter,srcMat,dstMat,mInputMode);
            processorThread.start();
        }
        while(mFilteredMatQueue.isEmpty());
        return mFilteredMatQueue.remove();
    }

    public void changeInputMode() {
        while(!mFilteredMatQueue.isEmpty())
            mFilteredMatQueue.remove();
        mInputMode = !mInputMode;
    }
}

class FilterProcessorThread extends Thread {
    private Filter mFilter;
    private Mat mSrcMat;
    private Mat mDstMat;
    private boolean mInputMode;
    private FilterProcessor mProcessor;

    public FilterProcessorThread(FilterProcessor processor, Filter filter, Mat srcMat, Mat dstMat, boolean inputMode) {
        mProcessor = processor;
        mFilter = filter;
        mSrcMat = srcMat;
        mDstMat = dstMat;
        mInputMode = inputMode;
    }

    @Override
    public void run() {
        mFilter.process(mSrcMat, mDstMat);
        mProcessor.enqueueFilteredMat(mDstMat, mInputMode);
    }
}
