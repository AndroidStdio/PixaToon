package com.ajscape.pixatoon.viewer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.FilterManager;
import com.ajscape.pixatoon.filters.FilterType;
import com.ajscape.pixatoon.filters.pencilsketch.PencilSketchFilter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraViewerFragment extends Fragment implements CvCameraViewListener2, OpenCvCameraView.PictureCallback {

    private static final String TAG = "MainActivity:";
    private OpenCvCameraView mCameraView;
    private FilterManager mFilterManager;
    private FilterPictureCallback mPictureCallback;
    private Mat mRgba;

    private BaseLoaderCallback mOpenCvLoaderCallback = new BaseLoaderCallback( getActivity() ) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("image_filters");
                    mCameraView.enableView();
                    ((PencilSketchFilter)mFilterManager.getFilter(FilterType.PENCIL_SKETCH)).loadSketchTexture(
                            getActivity().getApplicationContext().getResources(),
                            R.drawable.sketch_texture1 );

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CameraViewerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_camera_viewer, container, false);
        mCameraView = (OpenCvCameraView)view.findViewById(R.id.cameraView);
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);
        mCameraView.setPictureCallback(this);
        mFilterManager = FilterManager.getInstance();
        Log.d(TAG,"Camera fragment created");
        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mCameraView != null)
            mCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getActivity(), mOpenCvLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mOpenCvLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null)
            mCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {}

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mFilterManager.processCurrentFilter(mRgba, mRgba);
        return mRgba;
    }

    public void takePicture(FilterPictureCallback pictureCallback) {
        Log.d(TAG, "take picture called");
        mPictureCallback = pictureCallback;
        mCameraView.takePicture();
    }
    
    public boolean switchCamera() {
        // TODO: 10/9/2015
        return false;
    }
    

    @Override
    public void onPictureTaken(byte[] data, int height, int width) {
        Log.d(TAG,"picture captured with width="+width+" and height="+height);
        if(mPictureCallback != null) {
            // Convert picture jpeg data to bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap pictureBitmap = BitmapFactory.decodeByteArray(data,0,data.length, options);

            // Convert picture to mat, apply filter, and convert filtered mat back to bitmap
            Mat pictureMat = new Mat(pictureBitmap.getHeight(), pictureBitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(pictureBitmap, pictureMat);
            mFilterManager.getCurrentFilter().process(pictureMat, pictureMat);
            Utils.matToBitmap(pictureMat, pictureBitmap);

            // Return filtered bitmap as callback param
            mPictureCallback.onPictureAvailable(pictureBitmap);
        }
        else {
            Log.e(TAG,"picture callback not set");
        }
    }
}
