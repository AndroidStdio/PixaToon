package com.ajscape.pixatoon.activities;

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

import com.ajscape.pixatoon.MainController;
import com.ajscape.pixatoon.R;
import com.ajscape.pixatoon.filters.ImageFilterType;
import com.ajscape.pixatoon.fragments.FilterSelectorFragment;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ImageActivity extends Activity implements FilterSelectorFragment.FilterSelectorListener {

    private MainController controller;
    private Fragment filterConfigFragment;
    private FilterSelectorFragment filterSelectorFragment;
    private ImageView imgView;
    private Bitmap imgBitmap;
    private Mat srcImgMat;
    private boolean isFilterConfigDisplayed = false;
    private boolean isFilterSelectorDisplayed = false;

    private static final String TAG = "ImageActivity";
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        controller = (MainController)getApplicationContext();

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
        imgBitmap = BitmapFactory.decodeFile(imgPath,bmpFactoryOptions);
        srcImgMat = new Mat();
        Utils.bitmapToMat(imgBitmap, srcImgMat);
        imgView.setImageBitmap(imgBitmap);
    }

    public void updateImage() {
        Mat filteredMat = srcImgMat.clone();
        controller.applyImageFilter(filteredMat);
        Utils.matToBitmap(filteredMat, imgBitmap);
        imgView.setImageBitmap(imgBitmap);
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
            filterConfigFragment = controller.getFilterConfigFragment();

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
    public void onFilterSet(ImageFilterType filterType) {
        controller.setCurrentFilter(filterType);
        updateImage();
    }

    @Override
    public void onFilterSelect() {
        controller.selectCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
    }

    @Override
    public void onFilterCancel() {
        controller.cancelCurrentFilter();
        getFragmentManager()
                .beginTransaction()
                .remove(filterSelectorFragment)
                .commit();
        isFilterSelectorDisplayed = false;
        updateImage();
    }
}
