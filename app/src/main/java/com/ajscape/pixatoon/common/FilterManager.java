package com.ajscape.pixatoon.common;

import android.app.Application;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonFilter;
import com.ajscape.pixatoon.filters.colorcartoon.ColorCartoonConfigFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AtulJadhav on 9/20/2015.
 */

public class FilterManager extends Application {

    private ArrayList<Filter> filterList;
    private HashMap<FilterType, Filter> filterType2FilterMap;
    private HashMap<Integer, FilterType> btnId2FilterTypeMap;
    private Filter currentFilter;
    private Filter lastAppliedFilter;
    private FilterSelectorFragment filterSelectorFragment;
    private static FilterManager instance;

    public static FilterManager getInstance() {
        if(instance == null)
            instance = new FilterManager();
        return instance;
    }

    private FilterManager() {
        filterList = new ArrayList<>();
        filterType2FilterMap = new HashMap<>();
        btnId2FilterTypeMap = new HashMap<>();
        filterSelectorFragment = new FilterSelectorFragment();

        // initialize filters and add to list
        buildFilterList();

        // hash filters to maps for easy retreival
        for( Filter filter : filterList) {
            filterType2FilterMap.put( filter.getType(), filter);
            btnId2FilterTypeMap.put( filter.getFilterSelectorBtnId(), filter.getType());
        }
    }

    private void buildFilterList() {
        filterList.add( new ColorCartoonFilter(
                FilterType.COLOR_CARTOON,
                new ColorCartoonConfigFragment(),
                R.id.colorCartoonFilterBtn) );
    }

    public Filter getCurrentFilter() {
        return currentFilter;
    }

    public FilterSelectorFragment getFilterSelectorFragment() { return filterSelectorFragment; }

    public void setCurrentFilter(FilterType filterType) {
        lastAppliedFilter = currentFilter;
        currentFilter = filterType2FilterMap.get(filterType);
    }

    public void applyCurrentFilter() {
        if(lastAppliedFilter != null && lastAppliedFilter != currentFilter)
            lastAppliedFilter.resetConfig();
        lastAppliedFilter = currentFilter;
    }

    public void cancelCurrentFilter() {
        currentFilter = lastAppliedFilter;
    }

    public Filter getFilterByType(FilterType filterType) {
        return filterType2FilterMap.get(filterType);
    }

    public FilterType getFilterTypeByBtnId(int filterSelectorBtnId) {
        return btnId2FilterTypeMap.get(filterSelectorBtnId);
    }

    public void reset() {
        currentFilter = null;
        lastAppliedFilter = null;
    }
}