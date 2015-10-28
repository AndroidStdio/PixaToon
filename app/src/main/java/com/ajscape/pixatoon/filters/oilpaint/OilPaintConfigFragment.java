package com.ajscape.pixatoon.filters.oilpaint;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.FilterConfigListener;
import com.ajscape.pixatoon.filters.FilterManager;

public class OilPaintConfigFragment extends Fragment {

    private FilterConfigListener callback;
    private SeekBar radiusSeekBar, levelsSeekBar;
    private OilPaintFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterConfigListener)getActivity();
        filter = (OilPaintFilter)FilterManager.getInstance().getCurrentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filterconfig_oilpaint, container, false);
        initializeSeekbars(view);
        return view;
    }

    private void initializeSeekbars(View view) {
        radiusSeekBar = (SeekBar)view.findViewById(R.id.radiusSeekBar);
        levelsSeekBar = (SeekBar)view.findViewById(R.id.levelsSeekBar);

        radiusSeekBar.setProgress(filter.getRadius());
        levelsSeekBar.setProgress(filter.getLevels());

        radiusSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setRadius(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        levelsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setLevels(progress);
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
