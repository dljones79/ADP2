// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MainFragment extends Fragment {

    // Component definitions
    ImageButton mCaptureButton;
    ImageButton mGalleryButton;
    ImageButton mOrderButton;
    ImageButton mEditButton;
    ImageButton mConfigButton;
    Uri mImageUri;

    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int PICK_IMAGE = 2;
    private static final int EDIT_IMAGE = 3;
    Uri mEditedImageUri;
    Boolean changed;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set ui components to variables
        mCaptureButton = (ImageButton)getActivity().findViewById(R.id.captureButton);
        mGalleryButton = (ImageButton)getActivity().findViewById(R.id.galleryButton);
        mEditButton = (ImageButton)getActivity().findViewById(R.id.editButton);
        mOrderButton = (ImageButton)getActivity().findViewById(R.id.orderButton);
        mConfigButton = (ImageButton)getActivity().findViewById(R.id.configButton);

        // Capture button listener
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Capture");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageUri = getOutputUri();
                if (mImageUri != null){
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                }
                startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURE);
            }
        }); // end of capture button listener

        // Gallery button listener
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Gallery");
                /*
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intent);
                */

                // Create intent to open gallery
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        }); // end of gallery listener

        // Edit button listener
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Edit");
                Intent intent = new Intent(getActivity(), EditActivity.class);
                startActivity(intent);
            }
        }); // end of edit button listener

        // order button listener
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Order");
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                startActivity(intent);
            }
        }); // end of order button listener

        // settings button listener
        mConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Config");
                Intent intent = new Intent(getActivity(), ConfigureActivity.class);
                startActivity(intent);
            }
        }); // end of settings button listener
    }

    // custom method to save out captured image
    private Uri getOutputUri(){
        // get date and time for naming image
        String imageName = new SimpleDateFormat("MMddyyy_HHmmss")
                .format(new Date(System.currentTimeMillis()));
        // get the image directory
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // create a new image folder
        File appDir = new File(imageDir, "Picstamatic");
        appDir.mkdirs();

        // add image to folder
        File image = new File(appDir, imageName + ".jpg");
        try{
            image.createNewFile();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(image);
    } // End Uri getOutputUri

    // On results being returned from other activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        // if we just took a picture
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode != getActivity().RESULT_CANCELED){
            if (mImageUri != null){
                addImageToGallery(mImageUri);
            }
        }

        // if we're picking an image from gallery
        if (requestCode == PICK_IMAGE){

            mImageUri = data.getData();

            Intent editIntent = new Intent( getActivity(), FeatherActivity.class);
            editIntent.setData(mImageUri);
            editIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "fe1a79bf567ddd0f");
            startActivityForResult(editIntent, EDIT_IMAGE);
        }

        // if we just edited an image
        if (requestCode == EDIT_IMAGE){
            mEditedImageUri = data.getData();
            Log.i("Edited Uri:", mEditedImageUri.toString());
            Bundle extra = data.getExtras();
            if (null != extra){
                changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
            }
        }
    }

    // add the saved image to the gallery and broadcast the intent
    private void addImageToGallery(Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        getActivity().sendBroadcast(scanIntent);
    }
}
