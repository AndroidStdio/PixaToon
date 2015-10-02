package com.ajscape.pixatoon.viewer.picture;

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
import android.view.View;
import android.widget.ImageView;

import com.ajscape.pixatoon.common.FilterConfigListener;
import com.ajscape.pixatoon.common.FilterManager;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.common.Filter;
import com.ajscape.pixatoon.common.FilterType;
import com.ajscape.pixatoon.common.FilterSelectorFragment;
import com.ajscape.pixatoon.common.FilterSelectorListener;
import com.ajscape.pixatoon.viewer.camera.CameraActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageActivity extends Activity implements FilterSelectorListener, FilterConfigListener {

    private FilterManager mFilterManager;
    private Fragment mFilterConfigFragment;
    private FilterSelectorFragment mFilterSelectorFragment;
    private ImageView mImgView;
    private Bitmap mInputBitmap;
    private Bitmap mFilteredBitmap;
    private Mat mInputMat, mFilteredMat;
    private boolean mIsFilterConfigDisplayed = false;
    private boolean mIsFilterSelectorDisplayed = false;
    //private Thread mImageUpdaterThread;

    private static final String TAG = "ImageActivity";
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mFilterManager = FilterManager.getInstance();

        mImgView = (ImageView)findViewById(R.id.imgView);

        Bundle extras =  getIntent().getExtras();
        if(extras != null) {
            String srcImgPath = extras.getString("EXTRA_IMG_PATH");
            Log.d(TAG, "Image picked - " + srcImgPath);
            loadImage(srcImgPath);
        }
    }

    public void loadImage(String imgPath) {
        mInputBitmap = com.ajscape.pixatoon.common.Utils.decodeSampledBitmapFromFile(imgPath,500,500);
        mFilteredBitmap = com.ajscape.pixatoon.common.Utils.decodeSampledBitmapFromFile(imgPath, 500, 500);
        mInputMat = new Mat(mInputBitmap.getHeight(), mInputBitmap.getWidth(), CvType.CV_8UC4);
        mFilteredMat = new Mat(mInputBitmap.getHeight(), mInputBitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(mInputBitmap, mInputMat);
        mImgView.setImageBitmap(mInputBitmap);
    }

    public void updateImage() {
        Filter currentFilter = mFilterManager.getCurrentFilter();
        if(currentFilter != null) {
            Utils.bitmapToMat(mInputBitmap, mInputMat);
            mFilterManager.processCurrentFilter(mInputMat, mFilteredMat);
            Utils.matToBitmap(mFilteredMat, mFilteredBitmap);
            mImgView.setImageBitmap(mFilteredBitmap);
        }
    }

    public void switchCamera(View view) {
        Intent intent = new Intent(getBaseContext(), CameraActivity.class);
        mFilterManager.reset();
        startActivity(intent);
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

                loadImage(imgPath);
            }
        }
    }

    public void showFilterSelector(View view) {
        if(!mIsFilterSelectorDisplayed) {
            mFilterSelectorFragment = new FilterSelectorFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.imgFilterSelectorPanel, mFilterSelectorFragment)
                    .commit();
            mIsFilterSelectorDisplayed = true;
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterSelectorFragment)
                    .commit();
            mIsFilterSelectorDisplayed = false;
        }
    }

    public void showFilterConfig(View view) {
        if(!mIsFilterConfigDisplayed) {
            mFilterConfigFragment = mFilterManager.getCurrentFilter().getConfigFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.imgFilterConfigPanel, mFilterConfigFragment)
                    .commit();
            mIsFilterConfigDisplayed = true;
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(mFilterConfigFragment)
                    .commit();
            mIsFilterConfigDisplayed = false;
        }
    }

    @Override
    public void onFilterSelect(FilterType filterType) {
        mFilterManager.setCurrentFilter(filterType);
        updateImage();
    }

    @Override
    public void onFilterApply() {
        mFilterManager.applyCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(mFilterSelectorFragment)
                .commit();
        mIsFilterSelectorDisplayed = false;
    }

    @Override
    public void onFilterCancel() {
        mFilterManager.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(mFilterSelectorFragment)
                .commit();
        mIsFilterSelectorDisplayed = false;
        updateImage();
    }

    @Override
    public void onFilterConfigChanged() {
        updateImage();
    }
}
