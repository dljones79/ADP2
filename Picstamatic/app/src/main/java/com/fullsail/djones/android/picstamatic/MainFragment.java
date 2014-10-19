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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MainFragment extends Fragment {

    ImageButton mCaptureButton;
    ImageButton mGalleryButton;
    ImageButton mOrderButton;
    ImageButton mEditButton;
    ImageButton mConfigButton;
    Uri mImageUri;

    private static final int REQUEST_TAKE_PICTURE = 0x10101;

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

        mCaptureButton = (ImageButton)getActivity().findViewById(R.id.captureButton);
        mGalleryButton = (ImageButton)getActivity().findViewById(R.id.galleryButton);
        mEditButton = (ImageButton)getActivity().findViewById(R.id.editButton);
        mOrderButton = (ImageButton)getActivity().findViewById(R.id.orderButton);
        mConfigButton = (ImageButton)getActivity().findViewById(R.id.configButton);

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
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Gallery");
                Intent intent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Edit");
                Intent intent = new Intent(getActivity(), EditActivity.class);
                startActivity(intent);
            }
        });

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Order");
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                startActivity(intent);
            }
        });

        mConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Button Press: ", "Config");
                Intent intent = new Intent(getActivity(), ConfigureActivity.class);
                startActivity(intent);
            }
        });
    }

    private Uri getOutputUri(){
        String imageName = new SimpleDateFormat("MMddyyy_HHmmss")
                .format(new Date(System.currentTimeMillis()));
        File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File appDir = new File(imageDir, "Picstamatic");
        appDir.mkdirs();

        File image = new File(appDir, imageName + ".jpg");
        try{
            image.createNewFile();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(image);
    } // End Uri getOutputUri

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode != getActivity().RESULT_CANCELED){
            if (mImageUri != null){
                addImageToGallery(mImageUri);
            }
        }
    }

    private void addImageToGallery(Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        getActivity().sendBroadcast(scanIntent);
    }
}
