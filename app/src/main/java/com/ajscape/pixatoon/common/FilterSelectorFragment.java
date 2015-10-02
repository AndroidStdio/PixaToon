package com.ajscape.pixatoon.common;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ajscape.pixatoon.R;


public class FilterSelectorFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FilterSelectorFragment:";
    private FilterManager filterManager;
    private FilterSelectorListener callback;
    private Button activeFilerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (FilterSelectorListener)getActivity();
        filterManager = FilterManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(com.ajscape.pixatoon.R.layout.fragment_filterselector, container, false);
        view.findViewById(R.id.filterSelectBtn).setOnClickListener(this);
        view.findViewById(R.id.filterCancelBtn).setOnClickListener(this);
        view.findViewById(R.id.colorCartoonFilterBtn).setOnClickListener(this);
        if(activeFilerBtn!=null)
            activeFilerBtn.setSelected(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "click detected");
        int viewId = v.getId();
        switch(viewId) {
            case R.id.filterSelectBtn:
                Log.d(TAG,"Select Btn clicked");
                callback.onFilterApply();
                return;
            case R.id.filterCancelBtn:
                Log.d(TAG,"Cancel Btn clicked");
                callback.onFilterCancel();
                return;
        }
        FilterType filterType = filterManager.getFilterTypeByBtnId(viewId);
        if( (activeFilerBtn==null || (activeFilerBtn!=null && viewId!=activeFilerBtn.getId())) && filterType != null) {
            Log.d(TAG,"Filter Btn clicked");
            if(activeFilerBtn != null)
                activeFilerBtn.setSelected(false);
            activeFilerBtn = (Button)v;
            activeFilerBtn.setSelected(true);
            callback.onFilterSelect(filterType);
        }
    }
}
