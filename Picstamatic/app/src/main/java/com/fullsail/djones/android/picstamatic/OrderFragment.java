// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.walgreens.quickprint.sdk.CustomerInfo;
import com.walgreens.quickprint.sdk.WagCheckoutContext;
import com.walgreens.quickprint.sdk.WagCheckoutContextException;
import com.walgreens.quickprint.sdk.WagCheckoutContextFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class OrderFragment extends Fragment {

    // For user data
    DataObject dataObject;

    // For Walgreens SDK
    WagCheckoutContext checkoutContext = null;

    // Buttons and image view
    ImageButton mChooseButton;
    ImageButton mOrderButton;
    ImageView mPreviewImage;

    // For picking image from gallery
    Uri mImageUri;
    String imageFilePath;
    private static final int PICK_IMAGE = 1;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mChooseButton = (ImageButton) getActivity().findViewById(R.id.chooseButton);
        mOrderButton = (ImageButton) getActivity().findViewById(R.id.orderButton);
        mPreviewImage = (ImageView) getActivity().findViewById(R.id.previewImage);

        dataObject = new DataObject();

        loadData();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        CustomerInfo cInfo = new CustomerInfo();
        cInfo.setFirstName(dataObject.getmFirstName());
        cInfo.setLastName(dataObject.getmLastName());
        cInfo.setEmail(dataObject.getmEmail());
        cInfo.setPhone(dataObject.getmPhone());

        try {
            checkoutContext = WagCheckoutContextFactory.createContext(getActivity().getApplication());
            Log.i("Wag Checkout: ", "Context Good!");
        } catch (WagCheckoutContextException e) {
            e.printStackTrace();
        }

        try {
            checkoutContext.init("extest1", "o5UZbHCGf6IU7OrDJOd3rd34XQ0qcAU3", cInfo,
                    null, null, WagCheckoutContext.EnvironmentType.DEVELOPMENT, "1.0.1");
            Log.i("Wag Checkout", "Init Passed!");
        } catch (WagCheckoutContextException e){
            e.printStackTrace();
            new AlertDialog.Builder(getActivity())
                    .setTitle("Server Down!")
                    .setMessage("We're sorry. Walgreens online ordering is currently down for " +
                            "maintenance. Try again later.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null){
            Log.i("Picking Image", "Good Result.");

            mImageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(mImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            mPreviewImage.setImageBitmap(BitmapFactory.decodeFile(imageFilePath));

        } else {
            Log.i("Picking Image", "Bad Result.");
        }

    }

    public void loadData(){
        try{
            FileInputStream fin = getActivity().openFileInput("data.txt");
            ObjectInputStream oin = new ObjectInputStream(fin);
            dataObject = ((DataObject) oin.readObject());

        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
