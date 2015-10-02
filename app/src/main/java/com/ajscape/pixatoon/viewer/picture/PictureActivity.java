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
import org.opencv.core.Mat;

public class PictureActivity extends Activity implements FilterSelectorListener, FilterConfigListener {

    private FilterManager filterManager;
    private Fragment filterConfigFragment;
    private FilterSelectorFragment filterSelectorFragment;
    private ImageView imgView;
    private Bitmap inputBitmap;
    private Bitmap filteredBitmap;
    private Mat inputMat, filteredMat;
    private boolean isFilterConfigDisplayed = false;
    private boolean isFilterSelectorDisplayed = false;

    private static final String TAG = "PictureActivity";
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        filterManager = FilterManager.getInstance();

        imgView = (ImageView)findViewById(R.id.imgView);

        Bundle extras =  getIntent().getExtras();
        if(extras != null) {
            String srcImgPath = extras.getString("EXTRA_IMG_PATH");
            Log.d(TAG, "Image picked - " + srcImgPath);
            loadImage(srcImgPath);
        }
    }

    public void loadImage(String imgPath) {
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        inputBitmap = BitmapFactory.decodeFile(imgPath,bmpFactoryOptions);
        filteredBitmap = BitmapFactory.decodeFile(imgPath,bmpFactoryOptions);
        inputMat = new Mat();
        filteredMat = new Mat();
        Utils.bitmapToMat(inputBitmap, inputMat);
        imgView.setImageBitmap(inputBitmap);
    }

    public void updateImage() {
        Filter currentFilter = filterManager.getCurrentFilter();
        if(currentFilter != null) {
            currentFilter.process(inputMat, filteredMat);
            Utils.matToBitmap(filteredMat, filteredBitmap);
            imgView.setImageBitmap(filteredBitmap);
        }
    }

    public void switchCamera(View view) {
        Intent intent = new Intent(getBaseContext(), CameraActivity.class);
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
        if(!isFilterSelectorDisplayed) {
            filterSelectorFragment = new FilterSelectorFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.imgFilterSelectorPanel, filterSelectorFragment)
                    .commit();
            isFilterSelectorDisplayed = true;
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(filterSelectorFragment)
                    .commit();
            isFilterSelectorDisplayed = false;
        }
    }

    public void showFilterConfig(View view) {
        if(!isFilterConfigDisplayed) {
            filterConfigFragment = filterManager.getCurrentFilter().getConfigFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.imgFilterConfigPanel, filterConfigFragment)
                    .commit();
            isFilterConfigDisplayed = true;
        }
        else {

            getFragmentManager()
                    .beginTransaction()
                    .remove(filterConfigFragment)
                    .commit();
            isFilterConfigDisplayed = false;
        }
    }

    @Override
    public void onFilterSelect(FilterType filterType) {
        filterManager.setCurrentFilter(filterType);
        updateImage();
    }

    @Override
    public void onFilterApply() {
        filterManager.applyCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
    }

    @Override
    public void onFilterCancel() {
        filterManager.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        updateImage();
    }

    @Override
    public void onFilterConfigChanged() {
        updateImage();
    }
}
