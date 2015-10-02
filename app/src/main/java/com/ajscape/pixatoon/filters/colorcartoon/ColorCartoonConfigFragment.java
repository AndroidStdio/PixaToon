package com.ajscape.pixatoon.filters.colorcartoon;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.common.FilterConfigListener;
import com.ajscape.pixatoon.common.FilterManager;

public class ColorCartoonConfigFragment extends Fragment {

    private FilterConfigListener callback;
    private SeekBar thicknessSeekBar, thresholdSeekBar;
    private ColorCartoonFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterConfigListener)getActivity();
        filter = (ColorCartoonFilter)FilterManager.getInstance().getCurrentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(com.ajscape.pixatoon.R.layout.fragment_filterconfig_colorcartoon, container, false);
        initializeSeekbars(view);
        return view;
    }

    private void initializeSeekbars(View view) {
        thicknessSeekBar = (SeekBar)view.findViewById(R.id.thicknessSeekBar);
        thresholdSeekBar = (SeekBar)view.findViewById(R.id.thresholdSeekBar);

        thicknessSeekBar.setProgress(filter.getThickness());
        thresholdSeekBar.setProgress(filter.getThreshold());

        thicknessSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setThickness(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setThreshold(progress);
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
