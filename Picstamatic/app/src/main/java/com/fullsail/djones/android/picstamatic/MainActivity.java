// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class MainActivity extends Activity {

    DataObject dataObject;
    String userCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataObject = new DataObject();
        userCheck = null;

        loadData();

        try {
            userCheck = dataObject.getmFirstName().toString();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (userCheck == null){
            ConfigureFragment frag = new ConfigureFragment();
            getFragmentManager().beginTransaction().replace(R.id.mainContainer, frag).commit();
        } else {
            MainFragment frag = new MainFragment();
            getFragmentManager().beginTransaction().replace(R.id.mainContainer, frag).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData(){
        try{
            FileInputStream fin = openFileInput("data.txt");
            ObjectInputStream oin = new ObjectInputStream(fin);
            dataObject = ((DataObject) oin.readObject());

        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
