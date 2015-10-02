package com.ajscape.pixatoon.viewer.camera;

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

import com.ajscape.pixatoon.common.FilterConfigListener;
import com.ajscape.pixatoon.common.FilterManager;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.viewer.picture.PictureActivity;
import com.ajscape.pixatoon.common.Filter;
import com.ajscape.pixatoon.common.FilterType;
import com.ajscape.pixatoon.common.FilterSelectorFragment;
import com.ajscape.pixatoon.common.FilterSelectorListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraActivity extends Activity implements CvCameraViewListener2, FilterSelectorListener, FilterConfigListener {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "CameraActivity";

    private FilterSelectorFragment filterSelectorFragment;
    private Fragment filterConfigFragment;
    private boolean isFilterSelectorDisplayed = false;
    private boolean isFilterConfigDisplayed = false;

    private CameraBridgeViewBase openCvCameraView;
    private FilterManager filterManager;
    private Mat inputMat, filteredMat;

    private BaseLoaderCallback openCvLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("image_filters");
                    openCvCameraView.enableView();
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

        openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camView);
        openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        openCvCameraView.setCvCameraViewListener(this);

        filterManager = FilterManager.getInstance();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, openCvLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            openCvLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        inputMat = new Mat(height, width, CvType.CV_8UC4);
        filteredMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        inputMat = inputFrame.rgba();
        Filter currentFilter = filterManager.getCurrentFilter();
        if(currentFilter != null) {
            currentFilter.process(inputMat, filteredMat);
            return filteredMat;
        }
        return inputMat;
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

                Intent intent = new Intent(getBaseContext(),PictureActivity.class);
                intent.putExtra("EXTRA_IMG_PATH", imgPath);
                startActivity(intent);
            }
        }
    }

    public void showFilterSelector(View view) {
        if(!isFilterSelectorDisplayed) {
            filterSelectorFragment = filterManager.getFilterSelectorFragment();

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
            filterConfigFragment = filterManager.getCurrentFilter().getConfigFragment();
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
    public void onFilterSelect(FilterType filterType) {
        filterManager.setCurrentFilter(filterType);
        Log.d(TAG, "current filter set to "+ filterType.name());
    }

    @Override
    public void onFilterApply() {
        filterManager.applyCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter selected");
    }

    @Override
    public void onFilterCancel() {
        filterManager.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter cancelled");
    }

    @Override
    public void onFilterConfigChanged() {

    }
}
