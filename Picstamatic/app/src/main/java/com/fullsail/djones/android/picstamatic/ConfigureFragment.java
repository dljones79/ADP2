// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ConfigureFragment extends Fragment {

    EditText mFirstName;
    EditText mLastName;
    EditText mStreet;
    EditText mCity;
    EditText mState;
    EditText mZip;
    EditText mEmail;
    String fNameStr;
    String lNameStr;
    String streetStr;
    String cityStr;
    String stateStr;
    String zipStr;
    String emailStr;
    Button saveButton;

    DataObject dataObject;

    //TODO: Create Phone Field!!!

    public ConfigureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mFirstName = (EditText)getActivity().findViewById(R.id.lNameText);
        mLastName = (EditText)getActivity().findViewById(R.id.fNameText);
        mStreet = (EditText)getActivity().findViewById(R.id.streetText);
        mState = (EditText)getActivity().findViewById(R.id.stateText);
        mCity = (EditText)getActivity().findViewById(R.id.cityText);
        mZip = (EditText)getActivity().findViewById(R.id.zipText);
        mEmail = (EditText)getActivity().findViewById(R.id.eMailText);
        saveButton = (Button)getActivity().findViewById(R.id.saveButton);

        dataObject = new DataObject();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fNameStr = mFirstName.getText().toString();
                lNameStr = mLastName.getText().toString();
                streetStr = mStreet.getText().toString();
                cityStr = mCity.getText().toString();
                stateStr = mState.getText().toString();
                zipStr = mZip.getText().toString();
                emailStr = mEmail.getText().toString();

                dataObject.setmFirstName(fNameStr);
                dataObject.setmLastName(lNameStr);
                dataObject.setmStreetAddress(streetStr);
                dataObject.setmCity(cityStr);
                dataObject.setmState(stateStr);
                dataObject.setmZipCode(zipStr);
                dataObject.setmEmail(emailStr);

                try {
                    FileOutputStream fos = getActivity().openFileOutput("data.txt", getActivity().MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(dataObject);
                    oos.close();

                } catch (IOException e){
                    e.printStackTrace();
                }
                getActivity().finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveButton.performClick();
        }
        return true;
    }


}
