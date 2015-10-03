package com.ajscape.pixatoon.viewer.camera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ajscape.pixatoon.common.FilterConfigListener;
import com.ajscape.pixatoon.common.FilterManager;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.viewer.image.ImageActivity;
import com.ajscape.pixatoon.common.FilterType;
import com.ajscape.pixatoon.common.FilterSelectorFragment;
import com.ajscape.pixatoon.common.FilterSelectorListener;
import com.ajscape.pixatoon.viewer.image.ImageUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class CameraActivity extends Activity implements CvCameraViewListener2, FilterSelectorListener, FilterConfigListener, OpenCvCameraView.PictureCallback {

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "CameraActivity";

    private FilterSelectorFragment mFilterSelectorFragment;
    private Fragment mFilterConfigFragment;
    private boolean mIsFilterSelectorDisplayed = false;
    private boolean mIsFilterConfigDisplayed = false;

    private OpenCvCameraView mOpenCvCameraView;
    private FilterManager mFilterManager;
    private Mat mInputMat, mFilteredMat;

    private BaseLoaderCallback mOpenCvLoaderCallback = new BaseLoaderCallback(this) {
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

        mOpenCvCameraView = (OpenCvCameraView) findViewById(R.id.camView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setPictureCallback(this);

        mFilterManager = FilterManager.getInstance();
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mOpenCvLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mOpenCvLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mInputMat = new Mat(height, width, CvType.CV_8UC4);
        mFilteredMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mInputMat = inputFrame.rgba();
        mFilterManager.processCurrentFilter(mInputMat, mFilteredMat);
        return mFilteredMat;
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
                Log.d(TAG, "Image selected - " + imgPath);

                Intent intent = new Intent(getBaseContext(),ImageActivity.class);
                intent.putExtra("EXTRA_IMG_PATH", imgPath);
                mFilterManager.reset();
                startActivity(intent);
            }
        }
    }

    public void showFilterSelector(View view) {
        if(!mIsFilterSelectorDisplayed) {
            mFilterSelectorFragment = mFilterManager.getFilterSelectorFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.camFilterSelectorPanel, mFilterSelectorFragment)
                    .commit();
            mIsFilterSelectorDisplayed = true;
            Log.d(TAG,"filter selector opened");
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterSelectorFragment)
                    .commit();
            mIsFilterSelectorDisplayed = false;
            Log.d(TAG,"filter selector closed");
        }
    }

    public void showFilterConfig(View view) {
        if(!mIsFilterConfigDisplayed) {
            mFilterConfigFragment = mFilterManager.getCurrentFilter().getConfigFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.camFilterConfigPanel, mFilterConfigFragment)
                    .commit();
            mIsFilterConfigDisplayed = true;
            Log.d(TAG,"filter config opened");
        }
        else {
            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterConfigFragment)
                    .commit();
            mIsFilterConfigDisplayed = false;
            Log.d(TAG,"filter config closed");
        }
    }

    @Override
    public void onFilterSelect(FilterType filterType) {
        mFilterManager.setCurrentFilter(filterType);
        Log.d(TAG, "current filter set to "+ filterType.name());
    }

    @Override
    public void onFilterApply() {
        mFilterManager.applyCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(mFilterSelectorFragment)
                .commit();
        mIsFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter selected");
    }

    @Override
    public void onFilterCancel() {
        mFilterManager.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(mFilterSelectorFragment)
                .commit();
        mIsFilterSelectorDisplayed = false;
        Log.d(TAG,"current filter cancelled");
    }

    @Override
    public void onFilterConfigChanged() {}

    public void takePicture(View view) {
        mOpenCvCameraView.takePicture();
    }

    @Override
    public void pictureTaken(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap pictureBitmap = BitmapFactory.decodeByteArray(data,0,data.length, options);
        Mat srcRgba = new Mat(pictureBitmap.getHeight(), pictureBitmap.getWidth(), CvType.CV_8UC4);
        Mat dstRgba = new Mat(pictureBitmap.getHeight(), pictureBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(pictureBitmap, srcRgba);
        mFilterManager.getCurrentFilter().process(srcRgba, dstRgba);
        Utils.matToBitmap(dstRgba, pictureBitmap);
        try {
            String savedPicturePath = ImageUtils.saveBitmap(getContentResolver(), pictureBitmap);
            Toast.makeText(getApplicationContext(), "Saved picture at "+savedPicturePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error: Unable to take picture", Toast.LENGTH_SHORT).show();
        }
    }
}
