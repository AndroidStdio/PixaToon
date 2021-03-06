package com.ajscape.pixatoon.filters;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajscape.pixatoon.R;


public class FilterSelectorFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FilterSelectorFragment:";
    private FilterSelectorListener callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterSelectorListener)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_filterselector, container, false);
        view.findViewById(R.id.colorCartoonFilterBtn).setOnClickListener(this);
        view.findViewById(R.id.grayCartoonFilterBtn).setOnClickListener(this);
        view.findViewById(R.id.pencilSketchFilterBtn).setOnClickListener(this);
        view.findViewById(R.id.colorSketchFilterBtn).setOnClickListener(this);
        view.findViewById(R.id.pencilSketch2FilterBtn).setOnClickListener(this);
        view.findViewById(R.id.oilPaintFilterBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "click detected - ID=");
        int viewId = v.getId();
        switch(viewId) {
            case R.id.colorCartoonFilterBtn:
                Log.d(TAG,"Color-Cartoon Filter Btn clicked");
                callback.onFilterSelect(FilterType.COLOR_CARTOON);
                return;
            case R.id.grayCartoonFilterBtn:
                Log.d(TAG, "Gray-Cartoon Filter Btn clicked");
                callback.onFilterSelect(FilterType.GRAY_CARTOON);
                return;
            case R.id.pencilSketchFilterBtn:
                Log.d(TAG, "Pencil-Sketch Filter Btn clicked");
                callback.onFilterSelect(FilterType.PENCIL_SKETCH);
                return;
            case R.id.colorSketchFilterBtn:
                Log.d(TAG, "Color-Sketch Filter Btn clicked");
                callback.onFilterSelect(FilterType.COLOR_SKETCH);
                return;
            case R.id.pencilSketch2FilterBtn:
                Log.d(TAG, "Pencil-Sketch2 Filter Btn clicked");
                callback.onFilterSelect(FilterType.PENCIL_SKETCH2);
                return;
            case R.id.oilPaintFilterBtn:
                Log.d(TAG, "Oil-Paint Filter Btn clicked");
                callback.onFilterSelect(FilterType.OIL_PAINT);
                return;
        }
    }
}
