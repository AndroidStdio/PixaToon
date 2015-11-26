package com.ajscape.pixatoon.viewer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureViewerFragment extends Fragment {

    private static final String TAG="PictureViewer:";
    private PictureSurfaceView mPictureView;
    private Bitmap mScaledInputBitmap, mScaledOutputBitmap;
    private Mat mInputMat, mScaledInputMat, mScaledOutputMat;
    private FilterManager mFilterManager;
    private boolean mInputRotated = false;
    private PictureUpdateThread mUpdateThread;
    private AtomicBoolean mPendingUpdate = new AtomicBoolean(false);

    public PictureViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_picture_viewer, container, false);
        mPictureView = (PictureSurfaceView)view.findViewById(R.id.pictureView);
        mFilterManager = FilterManager.getInstance();
        Log.d(TAG,"Picture fragment view created");
        Bundle args = getArguments();
        if(args != null && args.containsKey("pictureFilePath")) {
            loadPicture(args.getString("pictureFilePath"));
        }
        return view;
    }

    public void loadPicture(String pictureFilePath) {
        Log.d(TAG,"load picture from path="+pictureFilePath);

        // Load mat from filepath
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap inputBitmap = BitmapFactory.decodeFile(pictureFilePath, options);
        mInputMat = new Mat(inputBitmap.getHeight(), inputBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(inputBitmap, mInputMat);

        // Get dimensions of screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // If input is landscape, rotate for better screen coverage
        if(inputBitmap.getWidth()>inputBitmap.getHeight()) {
            inputBitmap = PictureUtils.rotateBitmap(inputBitmap, 90);
            mInputRotated = true;
        } else {
            mInputRotated = false;
        }

        // Get scaled bitmap and mat fit to screen, for preview filter display
        mScaledInputBitmap = PictureUtils.resizeBitmap(inputBitmap, width, height);
        mScaledOutputBitmap = mScaledInputBitmap.copy(mScaledInputBitmap.getConfig(), true);
        mScaledInputMat = new Mat(mScaledInputBitmap.getHeight(), mScaledInputBitmap.getWidth(), CvType.CV_8UC4);
        mScaledOutputMat = new Mat(mScaledInputBitmap.getHeight(), mScaledInputBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(mScaledInputBitmap, mScaledInputMat);
        mScaledInputMat.copyTo(mScaledOutputMat);

        // Set view with scaled bitmap
        mPictureView.setImageBitmap(mScaledInputBitmap);
    }

    public void updatePicture() {
        if(mUpdateThread == null || !mUpdateThread.isAlive()) {
            mUpdateThread = new PictureUpdateThread();
            mUpdateThread.start();
        } else {
            mPendingUpdate.set(true);
        }
    }

    public void getPicture(FilterPictureCallback pictureCallback) {
        Filter currentFilter = mFilterManager.getCurrentFilter();
        if(currentFilter != null) {
            Mat outputMat = new Mat(mInputMat.size(), mInputMat.type());
            currentFilter.process(mInputMat, outputMat);
            Bitmap outputBitmap = Bitmap.createBitmap(outputMat.width(),outputMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outputMat,outputBitmap);
            if(mInputRotated)
                outputBitmap = PictureUtils.rotateBitmap(outputBitmap,-90);
            pictureCallback.onPictureAvailable(outputBitmap);
        }
    }

    class PictureUpdateThread extends Thread {

        @Override
        public void run() {
            do {
                mPendingUpdate.set(false);
                Filter currentFilter = mFilterManager.getCurrentFilter();
                if (currentFilter != null) {
                    currentFilter.process(mScaledInputMat, mScaledOutputMat);
                    Utils.matToBitmap(mScaledOutputMat, mScaledOutputBitmap);
                }
                mPictureView.setImageBitmap(mScaledOutputBitmap);
            }while(mPendingUpdate.get() == true);
        }
    }
}
