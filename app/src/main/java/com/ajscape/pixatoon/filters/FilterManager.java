package com.ajscape.pixatoon.filters;

import android.app.Application;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonFilter;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonConfigFragment;
import com.ajscape.pixatoon.filters.graycartoon.GrayCartoonConfigFragment;
import com.ajscape.pixatoon.filters.graycartoon.GrayCartoonFilter;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AtulJadhav on 9/20/2015.
 */

public class FilterManager extends Application {

    private ArrayList<Filter> mFilterList;
    private HashMap<FilterType, Filter> mFilterType2FilterMap;
    private Filter mCurrentFilter;
    private Filter mLastAppliedFilter;
    private FilterSelectorFragment mFilterSelectorFragment;
    private FilterProcessor mFilterProcessor;

    private static FilterManager sInstance;

    public static FilterManager getInstance() {
        if(sInstance == null)
            sInstance = new FilterManager();
        return sInstance;
    }

    private FilterManager() {
        mFilterList = new ArrayList<>();
        mFilterType2FilterMap = new HashMap<>();
        mFilterSelectorFragment = new FilterSelectorFragment();
        mFilterProcessor = new FilterProcessor(true);

        // initialize filters and add to list
        buildFilterList();

        // hash filters to maps for easy retreival
        for( Filter filter : mFilterList) {
            mFilterType2FilterMap.put( filter.getType(), filter);
        }
    }

    private void buildFilterList() {
        mFilterList.add( new ColorCartoonFilter(
                FilterType.COLOR_CARTOON,
                new ColorCartoonConfigFragment()) );

        mFilterList.add( new GrayCartoonFilter(
                FilterType.GRAY_CARTOON,
                new GrayCartoonConfigFragment()) );
    }

    public Filter getCurrentFilter() {
        return mCurrentFilter;
    }

    public void processCurrentFilter(Mat srcMat, Mat dstMat) {
        if(mCurrentFilter != null)
            mFilterProcessor.processFilter(mCurrentFilter, srcMat, dstMat);
        else
            srcMat.copyTo(dstMat);
    }

    public void setCurrentFilter(FilterType filterType) {
        mLastAppliedFilter = mCurrentFilter;
        mCurrentFilter = mFilterType2FilterMap.get(filterType);
    }

    public void applyCurrentFilter() {
        if(mLastAppliedFilter != null && mLastAppliedFilter != mCurrentFilter)
            mLastAppliedFilter.resetConfig();
        mLastAppliedFilter = mCurrentFilter;
    }

    public void cancelCurrentFilter() {
        mCurrentFilter = mLastAppliedFilter;
    }

    public void reset() {
        mCurrentFilter = null;
        mLastAppliedFilter = null;
        mFilterProcessor.changeInputMode();
    }
}


