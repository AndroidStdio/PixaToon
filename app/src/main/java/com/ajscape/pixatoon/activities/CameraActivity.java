package com.ajscape.pixatoon.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.ajscape.pixatoon.MainController;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.ImageFilterType;
import com.ajscape.pixatoon.fragments.FilterSelectorFragment;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraActivity extends Activity implements CvCameraViewListener2, FilterSelectorFragment.FilterSelectorListener {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "CameraActivity";

    private FilterSelectorFragment filterSelectorFragment;
    private Fragment filterConfigFragment;
    private boolean isFilterSelectorDisplayed = false;
    private boolean isFilterConfigDisplayed = false;

    private CameraBridgeViewBase mOpenCvCameraView;
    private MainController controller;
    private Mat mRgba;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("image_filters");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        controller = (MainController)getApplicationContext();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        controller.applyImageFilter(mRgba);
        return mRgba;
    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                cursor.close();
                Log.d(TAG,"Image selected - "+imgPath);

                Intent intent = new Intent(getBaseContext(),ImageActivity.class);
                intent.putExtra("EXTRA_IMG_PATH", imgPath);
                startActivity(intent);
            }
        }
    }

    public void showFilterSelector(View view) {
        if(!isFilterSelectorDisplayed) {
            filterSelectorFragment = controller.getFilterSelectorFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.camFilterSelectorPanel, filterSelectorFragment)
                    .commit();
            isFilterSelectorDisplayed = true;
            Log.d(TAG,"filter selector opened");
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(filterSelectorFragment)
                    .commit();
            isFilterSelectorDisplayed = false;
            Log.d(TAG,"filter selector closed");
        }
    }

    public void showFilterConfig(View view) {
        if(!isFilterConfigDisplayed) {
            filterConfigFragment = controller.getFilterConfigFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.camFilterConfigPanel, filterConfigFragment)
                    .commit();
            isFilterConfigDisplayed = true;
            Log.d(TAG,"filter config opened");
        }
        else {
            getFragmentManager()
                    .beginTransaction()
                    .remove(filterConfigFragment)
                    .commit();
            isFilterConfigDisplayed = false;
            Log.d(TAG,"filter config closed");
        }
    }

    @Override
    public void onFilterSet(ImageFilterType filterType) {
        controller.setCurrentFilter(filterType);
        Log.d(TAG, "current filter set to "+ filterType.name());
    }

    @Override
    public void onFilterSelect() {
        controller.selectCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter selected");
    }

    @Override
    public void onFilterCancel() {
        controller.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter cancelled");
    }
}
