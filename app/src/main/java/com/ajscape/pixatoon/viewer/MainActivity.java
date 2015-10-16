package com.ajscape.pixatoon.viewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.FilterConfigListener;
import com.ajscape.pixatoon.filters.FilterManager;
import com.ajscape.pixatoon.filters.FilterSelectorFragment;
import com.ajscape.pixatoon.filters.FilterSelectorListener;
import com.ajscape.pixatoon.filters.FilterType;

import java.io.IOException;

public class MainActivity extends Activity implements FilterSelectorListener, FilterConfigListener, View.OnClickListener {

    enum FilterViewerMode {
        CAMERA,
        PICTURE
    }

    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "CameraActivity";

    private FilterViewerMode mFilterViewerMode;
    private CameraViewerFragment mCameraViewerFragment;
    private PictureViewerFragment mPictureViewerFragment;
    private FilterSelectorFragment mFilterSelectorFragment;
    private Fragment mFilterConfigFragment;
    private ImageButton mOpenPictureBtn;
    private ImageButton mSelectFilterBtn;
    private ImageButton mOpenCameraBtn;
    private ImageButton mConfigFilterBtn;
    private ImageButton mViewerActionBtn;

    private FilterManager mFilterManager;

    private FilterPictureCallback mPictureCallback = new FilterPictureCallback() {
        @Override
        public void onPictureAvailable(Bitmap pictureBitmap) {
            saveBitmap(pictureBitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mFilterManager = FilterManager.getInstance();
        mCameraViewerFragment = new CameraViewerFragment();
        mPictureViewerFragment = new PictureViewerFragment();
        mFilterSelectorFragment = new FilterSelectorFragment();

        mOpenPictureBtn = (ImageButton) findViewById(R.id.openPictureBtn);
        mSelectFilterBtn = (ImageButton) findViewById(R.id.selectFilterBtn);
        mOpenCameraBtn = (ImageButton) findViewById(R.id.openCameraBtn);
        mConfigFilterBtn = (ImageButton) findViewById(R.id.configFilterBtn);
        mViewerActionBtn = (ImageButton) findViewById(R.id.viewerActionBtn);

        mOpenPictureBtn.setOnClickListener(this);
        mSelectFilterBtn.setOnClickListener(this);
        mOpenCameraBtn.setOnClickListener(this);
        mConfigFilterBtn.setOnClickListener(this);
        mViewerActionBtn.setOnClickListener(this);
        findViewById(R.id.filterViewer).setOnClickListener(this);

        // Set camera viewer as default
        mFilterViewerMode = FilterViewerMode.CAMERA;
        getFragmentManager()
                .beginTransaction()
                .add(R.id.filterViewer, mCameraViewerFragment)
                .commit();
        if(mFilterManager.getCurrentFilter()==null)
            mViewerActionBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.openPictureBtn:
                closeCurrentFilterConfig();
                closeFilterSelector();
                openPicture();
                break;
            case R.id.selectFilterBtn:
                closeCurrentFilterConfig();
                if(!mFilterSelectorFragment.isVisible())
                    openFilterSelector();
                else
                    closeFilterSelector();
                break;
            case R.id.openCameraBtn:
                closeCurrentFilterConfig();
                closeFilterSelector();
                openCameraFilterViewer();
                break;
            case R.id.configFilterBtn:
                closeFilterSelector();
                if(!isFilterConfigVisible())
                    openCurrentFilterConfig();
                else
                    closeCurrentFilterConfig();
                break;
            case R.id.viewerActionBtn:
                if(mFilterViewerMode == FilterViewerMode.CAMERA)
                    mCameraViewerFragment.takePicture(mPictureCallback);
                else
                    mPictureViewerFragment.getPicture(mPictureCallback);
                break;
            case R.id.filterViewer:
                closeCurrentFilterConfig();
                closeFilterSelector();
        }
    }

    private void openPictureFilterViewer(String pictureFilePath) {
        if(mFilterViewerMode != FilterViewerMode.PICTURE) {
            mFilterManager.reset();
            Bundle args = new Bundle();
            args.putString("pictureFilePath", pictureFilePath);
            mPictureViewerFragment.setArguments(args);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.filterViewer, mPictureViewerFragment)
                    .commit();
            mFilterViewerMode = FilterViewerMode.PICTURE;
            mViewerActionBtn.setImageResource(R.drawable.save_icon);
            mOpenCameraBtn.setImageResource(R.drawable.camera_icon);
        } else {
            mPictureViewerFragment.loadPicture(pictureFilePath);
        }
        if(mFilterManager.getCurrentFilter()==null)
            mViewerActionBtn.setVisibility(View.INVISIBLE);
    }

    private void openCameraFilterViewer() {
        if(mFilterViewerMode != FilterViewerMode.CAMERA) {
            mFilterManager.reset();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.filterViewer, mCameraViewerFragment)
                    .commit();
            mFilterViewerMode = FilterViewerMode.CAMERA;
            mViewerActionBtn.setImageResource(R.drawable.camera_icon);
            mOpenCameraBtn.setImageResource(R.drawable.switch_camera_icon);
        }
        else {
            boolean switched = mCameraViewerFragment.switchCamera();
            if(!switched) {
                Toast.makeText(getApplicationContext(), "Front camera not detected", Toast.LENGTH_SHORT).show();
            }
        }
        if(mFilterManager.getCurrentFilter()==null)
            mViewerActionBtn.setVisibility(View.INVISIBLE);
    }

    private void openPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
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
                String pictureFilePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d(TAG, "Picture picked- " + pictureFilePath);

                mFilterManager.reset();
                openPictureFilterViewer(pictureFilePath);
            }
        }
    }

    private void openFilterSelector() {
        if(!mFilterSelectorFragment.isVisible()) {
            mViewerActionBtn.setVisibility(View.INVISIBLE);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.filterSelectorPanel, mFilterSelectorFragment)
                    .commit();
            Log.d(TAG, "filter selector opened");
        }
    }

    private void closeFilterSelector() {
        if(mFilterSelectorFragment.isVisible()) {
            mViewerActionBtn.setVisibility(View.VISIBLE);
            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterSelectorFragment)
                    .commit();
            Log.d(TAG,"filter selector closed");
        }
    }

    @Override
    public void onFilterSelect(FilterType filterType) {
        mFilterManager.setCurrentFilter(filterType);
        Log.d(TAG, "current filter set to " + filterType.name());

        if(mFilterViewerMode == FilterViewerMode.PICTURE) {
            mPictureViewerFragment.updatePicture();
        }
    }

    private boolean isFilterConfigVisible() {
        if(mFilterConfigFragment!=null && mFilterConfigFragment.isVisible())
            return true;
        else
            return false;
    }

    private void openCurrentFilterConfig() {
        if (mFilterManager.getCurrentFilter()!=null && !isFilterConfigVisible()) {
            mViewerActionBtn.setVisibility(View.INVISIBLE);
            mFilterConfigFragment = mFilterManager.getCurrentFilter().getConfigFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.filterConfigPanel, mFilterConfigFragment)
                    .commit();
            Log.d(TAG, "filter config opened");
        }
    }

    private void closeCurrentFilterConfig() {
        if (isFilterConfigVisible()) {
            mViewerActionBtn.setVisibility(View.VISIBLE);
            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterConfigFragment)
                    .commit();
            Log.d(TAG,"filter config closed");
        }
    }

    @Override
    public void onFilterConfigChanged() {
        if(mFilterViewerMode == FilterViewerMode.PICTURE)
            mPictureViewerFragment.updatePicture();
    }

    private void saveBitmap(Bitmap bitmap) {
        try {
            String savedPicturePath = PictureUtils.saveBitmap(getContentResolver(), bitmap);
            Toast.makeText(getApplicationContext(), "Saved picture at " + savedPicturePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error: Unable to take picture", Toast.LENGTH_SHORT).show();
        }
    }
}
