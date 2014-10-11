// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


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
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
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
}
