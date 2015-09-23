package com.ajscape.pixatoon.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ajscape.pixatoon.MainController;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.activities.ImageActivity;
import com.ajscape.pixatoon.filters.ImageFilterType;

import java.util.HashMap;


public class FilterSelectorFragment extends Fragment implements View.OnClickListener{

    public interface FilterSelectorListener {
        public void onFilterSet(ImageFilterType filterType);
        public void onFilterSelect();
        public void onFilterCancel();
    }

    private static final String TAG = "FilterSelectorFragment:";
    private MainController controller;
    private FilterSelectorListener mCallback;
    private HashMap<ImageFilterType, Button> filterTypeToBtnMap;
    private HashMap<Integer, ImageFilterType> btnIdToFilterTypeMap;
    private Button activeFilerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallback = (FilterSelectorListener)getActivity();
        controller = (MainController)getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(com.ajscape.pixatoon.R.layout.fragment_filterselector, container, false);
        initialize(view);
        if(activeFilerBtn!=null)
            activeFilerBtn.setSelected(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"click detected");
        int viewId = v.getId();
        switch(viewId) {
            case R.id.filterSelectBtn:
                Log.d(TAG,"Select Btn clicked");
                mCallback.onFilterSelect();
                return;
            case R.id.filterCancelBtn:
                Log.d(TAG,"Cancel Btn clicked");
                mCallback.onFilterCancel();
                return;
        }
        if( (activeFilerBtn==null || (activeFilerBtn!=null && viewId!=activeFilerBtn.getId())) && btnIdToFilterTypeMap.containsKey(viewId)) {
            Log.d(TAG,"Filter Btn clicked");
            if(activeFilerBtn != null)
                activeFilerBtn.setSelected(false);
            activeFilerBtn = (Button)v;
            activeFilerBtn.setSelected(true);
            mCallback.onFilterSet(btnIdToFilterTypeMap.get(viewId));
        }
    }

    private void initialize(View v) {
        filterTypeToBtnMap = new HashMap<>();
        btnIdToFilterTypeMap = new HashMap<>();

        addFilterBtn(R.id.cartoonFilterBtn, ImageFilterType.CARTOON, v);

        v.findViewById(R.id.filterSelectBtn).setOnClickListener(this);
        v.findViewById(R.id.filterCancelBtn).setOnClickListener(this);
    }

    private void addFilterBtn(int btnId, ImageFilterType filterType, View v) {
        Button filterBtn = (Button)v.findViewById(btnId);
        btnIdToFilterTypeMap.put(btnId, filterType);
        filterTypeToBtnMap.put(filterType, filterBtn);
        filterBtn.setOnClickListener(this);
    }

}
