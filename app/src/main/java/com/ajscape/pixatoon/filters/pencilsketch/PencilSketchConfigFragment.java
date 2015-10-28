package com.ajscape.pixatoon.filters.pencilsketch;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.FilterConfigListener;
import com.ajscape.pixatoon.filters.FilterManager;

public class PencilSketchConfigFragment extends Fragment {

    private FilterConfigListener callback;
    private SeekBar blurRadiusSeekBar, contrastSeekBar;
    private PencilSketchFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterConfigListener)getActivity();
        filter = (PencilSketchFilter)FilterManager.getInstance().getCurrentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filterconfig_pencilsketch, container, false);
        initializeSeekbars(view);
        return view;
    }

    private void initializeSeekbars(View view) {
        blurRadiusSeekBar = (SeekBar)view.findViewById(R.id.blurRadiusSeekBar);
        contrastSeekBar = (SeekBar)view.findViewById(R.id.contrastSeekBar);

        blurRadiusSeekBar.setProgress(filter.getBlurRadius());
        contrastSeekBar.setProgress(filter.getContrast());

        blurRadiusSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setBlurRadius(progress);
                callback.onFilterConfigChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                filter.setContrast(progress);
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
