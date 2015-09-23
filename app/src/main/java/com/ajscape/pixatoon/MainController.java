package com.ajscape.pixatoon;

import android.app.Application;
import android.app.Fragment;

import com.ajscape.pixatoon.filters.CartoonFilter;
import com.ajscape.pixatoon.filters.ImageFilter;
import com.ajscape.pixatoon.filters.ImageFilterType;
import com.ajscape.pixatoon.fragments.CartoonFilterConfigFragment;
import com.ajscape.pixatoon.fragments.FilterSelectorFragment;

import org.opencv.core.Mat;

import java.util.HashMap;

/**
 * Created by AtulJadhav on 9/20/2015.
 */
public class MainController extends Application {

    private ImageFilter currentFilter;
    private ImageFilter selectedFilter;
    private Fragment selectedFilterConfigFragment;
    private FilterSelectorFragment filterSelectorFragment;
    private HashMap<ImageFilterType, ImageFilter> filterMap;
    private HashMap<ImageFilterType, Fragment> filterConfigFragmentMap;

    public MainController() {
        filterMap = new HashMap<ImageFilterType, ImageFilter>();
        filterConfigFragmentMap = new HashMap<ImageFilterType, Fragment>();

        filterMap.put(ImageFilterType.CARTOON, new CartoonFilter());
        filterConfigFragmentMap.put(ImageFilterType.CARTOON, new CartoonFilterConfigFragment());
        filterSelectorFragment = new FilterSelectorFragment();

        // Default filter
        setCurrentFilter(ImageFilterType.NONE);
        selectCurrentFilter();
    }

    public void setCurrentFilter(ImageFilterType filterType) {
        currentFilter = filterMap.get(filterType);
    }

    public ImageFilterType getCurrentFilterType() {
        if(currentFilter != null)
            return currentFilter.getType();
        else
            return ImageFilterType.NONE;
    }

    public void selectCurrentFilter() {
        selectedFilter = currentFilter;
        if(selectedFilter != null)
            selectedFilterConfigFragment = filterConfigFragmentMap.get(selectedFilter.getType());
        else
            selectedFilterConfigFragment = null;
    }

    public void cancelCurrentFilter() {
        currentFilter = selectedFilter;
    }

    public void applyImageFilter(Mat img) {
        if(currentFilter != null)
            currentFilter.apply(img);
    }

    public Fragment getFilterConfigFragment() {
        return selectedFilterConfigFragment;
    }

    public FilterSelectorFragment getFilterSelectorFragment() { return filterSelectorFragment; }

    public void saveImage(Mat img) {
        //TODO
    }
}
