// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walgreens.quickprint.sdk.CustomerInfo;
import com.walgreens.quickprint.sdk.RemoteCart;
import com.walgreens.quickprint.sdk.UploadProgressListener;
import com.walgreens.quickprint.sdk.UploadStatus;
import com.walgreens.quickprint.sdk.UploadStatusListener;
import com.walgreens.quickprint.sdk.WagCheckoutContext;
import com.walgreens.quickprint.sdk.WagCheckoutContextException;
import com.walgreens.quickprint.sdk.WagCheckoutContextFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class OrderFragment extends Fragment implements LocationListener{

    // For user data
    DataObject dataObject;

    // For Walgreens SDK
    WagCheckoutContext checkoutContext = null;

    // Buttons and image view
    ImageButton mChooseButton;
    ImageButton mOrderButton;
    ImageView mPreviewImage;
    TextView mUploadText;
    ProgressBar mProgressBar;

    // For picking image from gallery
    Uri mImageUri;
    String imageFilePath;
    private static final int PICK_IMAGE = 1;

    // ArrayList of images to order
    ArrayList<File> imageArray;

    // Location
    Double mLatitude;
    Double mLongitude;
    LocationManager mManager;
    private static final int REQUEST_ENABLE_GPS = 0x02101;

    // Checkout
    String checkoutUrl;
    List<String> sessionCookies;

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

        // create and array to store images
        imageArray = new ArrayList<File>();

        // Get location for ordering
        mManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        enableGps();

        // set ui elements to their variables
        mChooseButton = (ImageButton) getActivity().findViewById(R.id.chooseButton);
        mOrderButton = (ImageButton) getActivity().findViewById(R.id.orderButton);
        mPreviewImage = (ImageView) getActivity().findViewById(R.id.previewImage);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        mUploadText = (TextView) getActivity().findViewById(R.id.uploadText);

        dataObject = new DataObject();
        mProgressBar.setVisibility(View.INVISIBLE);

        // call custom method to load user data
        loadData();

        // allow process in main ui thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // create customer info object for walgreens
        CustomerInfo cInfo = new CustomerInfo();
        cInfo.setFirstName(dataObject.getmFirstName());
        cInfo.setLastName(dataObject.getmLastName());
        cInfo.setEmail(dataObject.getmEmail());
        cInfo.setPhone(dataObject.getmPhone());

        // create a walgreens checkoutcontext
        try {
            checkoutContext = WagCheckoutContextFactory.createContext(getActivity().getApplication());
            Log.i("Wag Checkout: ", "Context Good!");
        } catch (WagCheckoutContextException e) {
            e.printStackTrace();
        }

        // initialize checkoutcontext
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

        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // listener for uploading images
                UploadStatusListener statusListener = new UploadStatusListener() {
                    @Override
                    public void onError(WagCheckoutContextException e, File file) {
                        Log.i("Upload Error: ", e.toString());
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("Upload Success: ", "Upload was successful.");
                    }

                    @Override
                    public void onComplete(ArrayList<UploadStatus> uploadStatuses) {
                        Log.i("Upload:", "Upload Complete");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mUploadText.setVisibility(View.INVISIBLE);

                        Location loc = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        RemoteCart remoteCart = null;
                        try {
                            remoteCart = checkoutContext.postCart(loc);
                        } catch (WagCheckoutContextException e){
                            e.printStackTrace();
                        }
                        checkoutUrl = remoteCart.getCheckoutUrl();
                        sessionCookies = remoteCart.getCookies();

                        Log.i("Checkout URL: ", checkoutUrl);
                        Log.i("Cookies: ", sessionCookies.toString());

                        // Create and initialize a new WebView
                        WebView checkOutContainer = new WebView(getActivity());
                        getActivity().setContentView(checkOutContainer);

                        // Handle JavaScript alerts and JavaScript confirmation
                        checkOutContainer.setWebChromeClient(new WebChromeClient());

                        // Initialize checkOutContainer
                        checkOutContainer.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                        checkOutContainer.getSettings().setSaveFormData(false);
                        checkOutContainer.getSettings().setSavePassword(false);
                        checkOutContainer.getSettings().setJavaScriptEnabled(true);
                        checkOutContainer.setVerticalScrollBarEnabled(false);
                        checkOutContainer.setHorizontalScrollBarEnabled(false);

                        checkOutContainer.addJavascriptInterface(new JsObject(), "quickprint");
                        checkOutContainer.setWebViewClient(new SomeWebViewClient());

                        CookieSyncManager.createInstance(getActivity());
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeSessionCookie();

                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }

                        for (String cookie : sessionCookies) {
                            cookieManager.setCookie(checkoutUrl, cookie);
                        }

                        CookieSyncManager.getInstance().sync();

                        checkOutContainer.loadUrl(checkoutUrl);
                    }

                    @Override
                    public void onCancelUpload() {

                    }
                };

                UploadProgressListener progressListener = new UploadProgressListener() {
                    @Override
                    public void onProgress(double v, File file) {

                    }
                };

                Log.i("Images: ", imageArray.toString());

                checkoutContext.setUploadProgressListener(progressListener);
                checkoutContext.setUploadStatusListener(statusListener);
                checkoutContext.uploadImages(imageArray);
                mProgressBar.setVisibility(View.VISIBLE);
                mUploadText.setVisibility(View.VISIBLE);
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

            /*
            Bitmap bm = BitmapFactory.decodeFile(imageFilePath);
            Bitmap compressed = codec(bm, Bitmap.CompressFormat.JPEG, 100);
            File file = new File(compressed.toString());
            */
            File file = new File(imageFilePath);

            imageArray.add(file);

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

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
    private class uploadImage extends AsyncTask<Void, Void, Void>{

        ProgressDialog progress;

        @Override
        protected void onPreExecute(){
            progress = new ProgressDialog(getActivity());
            progress.show(getActivity(), "", "Uploading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            checkoutContext.uploadImages(imageArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            if( progress.isShowing())
            {
                progress.dismiss();
            }

            super.onPostExecute(v);
        }

    }
    */

    // custom method to get current location
    private void enableGps() {
        if(mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

            Location loc = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc != null) {
                mLatitude = loc.getLatitude();
                mLongitude = loc.getLongitude();
            }

        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("GPS Unavailable")
                    .setMessage("Please enable GPS in the system settings.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(settingsIntent, REQUEST_ENABLE_GPS);
                        }

                    })
                    .show();
        }
    }

    /*
    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format, int quality){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }
    */

    class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }
    }

    class SomeWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        public void onCheckoutComplete(){

        }

        public void onCheckoutError(int errorCode, String message){

        }

        public void onCheckoutCancel(){

        }
    }
}
