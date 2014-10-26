// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class EditFragment extends Fragment {

    ImageButton mPickButton;
    ImageButton mEditButton;
    ImageView mPreviewImage;
    Uri mImageUri;
    Uri mEditedImageUri;
    String imageFilePath;
    Boolean changed;

    private static final int PICK_IMAGE = 1;
    private static final int EDIT_IMAGE = 2;

    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPickButton = (ImageButton) getActivity().findViewById(R.id.pickButton);
        mEditButton = (ImageButton) getActivity().findViewById(R.id.editButton);
        mPreviewImage = (ImageView) getActivity().findViewById(R.id.previewImage);

        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE);
                */

                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent( getActivity(), FeatherActivity.class);
                editIntent.setData(mImageUri);
                editIntent.putExtra(Constants.EXTRA_IN_API_KEY_SECRET, "fe1a79bf567ddd0f");
                startActivityForResult(editIntent, EDIT_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == EDIT_IMAGE){
            Log.i("Request Code: ", "Edit Image");
            if (resultCode == getActivity().RESULT_OK){
                Log.i("Result Code:", "OK");
                switch( requestCode ) {
                    case 2:
                        mEditedImageUri = data.getData();
                        Log.i("Edited Uri:", mEditedImageUri.toString());
                        Bundle extra = data.getExtras();
                        if (null != extra){
                            changed = extra.getBoolean(Constants.EXTRA_OUT_BITMAP_CHANGED);
                        }
                        break;
                }
            }

            mPreviewImage.setImageBitmap(BitmapFactory.decodeFile(mEditedImageUri.getPath()));
        }


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

        }



    }

}
