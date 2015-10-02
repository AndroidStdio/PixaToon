package com.ajscape.pixatoon.common;

import com.ajscape.pixatoon.common.FilterType;

/**
 * Created by AtulJadhav on 9/29/2015.
 */
public interface FilterSelectorListener {
    public void onFilterSelect(FilterType filterType);
    public void onFilterApply();
    public void onFilterCancel();
}