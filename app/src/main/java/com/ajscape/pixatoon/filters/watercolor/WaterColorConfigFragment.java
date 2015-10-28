package com.ajscape.pixatoon.filters.watercolor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.FilterConfigListener;
import com.ajscape.pixatoon.filters.FilterManager;

public class WaterColorConfigFragment extends Fragment {

    private FilterConfigListener callback;
    private WaterColorFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterConfigListener)getActivity();
        filter = (WaterColorFilter)FilterManager.getInstance().getCurrentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filterconfig_watercolor, container, false);
        initializeSeekbars(view);
        return view;
    }

    private void initializeSeekbars(View view) {
        SeekBar spatialRadiusSeekBar = (SeekBar)view.findViewById(R.id.spatialRadiusSeekBar);
        SeekBar colorRadiusSeekBar = (SeekBar)view.findViewById(R.id.colorRadiusSeekBar);
        SeekBar maxLevelsSeekBar = (SeekBar)view.findViewById(R.id.maxLevelsSeekBar);
        SeekBar scaleFactorSeekBar = (SeekBar)view.findViewById(R.id.scaleFactorSeekBar);

        spatialRadiusSeekBar.setProgress(filter.getSpatialRadius());
        colorRadiusSeekBar.setProgress(filter.getColorRadius());
        maxLevelsSeekBar.setProgress(filter.getMaxLevels());
        scaleFactorSeekBar.setProgress(filter.getScaleFactor());

        spatialRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setSpatialRadius(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        colorRadiusSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setColorRadius(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        maxLevelsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setMaxLevels(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        scaleFactorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setScaleFactor(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
