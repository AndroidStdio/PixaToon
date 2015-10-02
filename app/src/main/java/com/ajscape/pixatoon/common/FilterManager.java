package com.ajscape.pixatoon.common;

import android.app.Application;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonFilter;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonConfigFragment;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by AtulJadhav on 9/20/2015.
 */

public class FilterManager extends Application {

    private ArrayList<Filter> mFilterList;
    private HashMap<FilterType, Filter> mFilterType2FilterMap;
    private HashMap<Integer, FilterType> mBtnId2FilterTypeMap;
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
        mBtnId2FilterTypeMap = new HashMap<>();
        mFilterSelectorFragment = new FilterSelectorFragment();
        mFilterProcessor = new FilterProcessor(true);

        // initialize filters and add to list
        buildFilterList();

        // hash filters to maps for easy retreival
        for( Filter filter : mFilterList) {
            mFilterType2FilterMap.put( filter.getType(), filter);
            mBtnId2FilterTypeMap.put( filter.getFilterSelectorBtnId(), filter.getType());
        }
    }

    private void buildFilterList() {
        mFilterList.add( new ColorCartoonFilter(
                FilterType.COLOR_CARTOON,
                new ColorCartoonConfigFragment(),
                R.id.colorCartoonFilterBtn) );
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

    public FilterSelectorFragment getFilterSelectorFragment() { return mFilterSelectorFragment; }

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

    public Filter getFilterByType(FilterType filterType) {
        return mFilterType2FilterMap.get(filterType);
    }

    public FilterType getFilterTypeByBtnId(int filterSelectorBtnId) {
        return mBtnId2FilterTypeMap.get(filterSelectorBtnId);
    }

    public void reset() {
        mCurrentFilter = null;
        mLastAppliedFilter = null;
        mFilterProcessor.changeInputMode();
    }
}


