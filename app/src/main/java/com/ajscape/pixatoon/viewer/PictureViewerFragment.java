package com.ajscape.pixatoon.viewer;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.Filter;
import com.ajscape.pixatoon.filters.FilterManager;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureViewerFragment extends Fragment {

    private static final String TAG="PictureViewer:";
    private ImageView mPictureView;
    private Bitmap mInputBitmap;
    private Bitmap mFilteredBitmap;
    private Mat mInputMat, mFilteredMat;
    private FilterManager mFilterManager;

    public PictureViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_picture_viewer, container, false);
        mPictureView = (ImageView)view.findViewById(R.id.pictureView);
        mFilterManager = FilterManager.getInstance();
        Log.d(TAG,"Picture fragment view created");
        Bundle args = getArguments();
        if(args != null && args.containsKey("pictureFilePath")) {
            loadPicture(args.getString("pictureFilePath"));
        }
        return view;
    }

    public void loadPicture(String pictureFilePath) {
        Log.d(TAG,"load picture at path="+pictureFilePath);
        mInputBitmap = PictureUtils.decodeSampledBitmapFromFile(pictureFilePath, 500, 500);
        mFilteredBitmap = Bitmap.createBitmap(mInputBitmap.getWidth(), mInputBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mInputMat = new Mat(mInputBitmap.getHeight(), mInputBitmap.getWidth(), CvType.CV_8UC4);
        mFilteredMat = new Mat(mInputBitmap.getHeight(), mInputBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(mInputBitmap, mInputMat);
        mPictureView.setImageBitmap(mInputBitmap);
    }

    public void updatePicture() {
        Filter currentFilter = mFilterManager.getCurrentFilter();
        if(currentFilter != null) {
            Utils.bitmapToMat(mInputBitmap, mInputMat);
            mFilterManager.processCurrentFilter(mInputMat, mFilteredMat);
            Utils.matToBitmap(mFilteredMat, mFilteredBitmap);
            mPictureView.setImageBitmap(mFilteredBitmap);
        } else {
            mPictureView.setImageBitmap(mInputBitmap);
        }
    }

    public void getPicture(FilterPictureCallback pictureCallback) {
        pictureCallback.onPictureAvailable(mFilteredBitmap);
    }
}
