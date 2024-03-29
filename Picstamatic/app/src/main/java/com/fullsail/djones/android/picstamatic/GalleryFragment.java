// David Jones
// ADP2 1410 - Full Sail University
// Picstamatic

package com.fullsail.djones.android.picstamatic;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;


//////////////////////
// Currently not using a.t.m.
// Having some issues with loading images from storage
// Currently just using device gallery from main activity
///////////////////////

public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    SimpleCursorAdapter mAdapter;

    private GridView gridView;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.gridview);

        mAdapter = new SimpleCursorAdapter(
                getActivity().getBaseContext(),
                R.layout.row_grid,
                null,
                new String[] { "_data"} ,
                new int[] { R.id.img},
                0
        );

        gridView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i("onCreateLoader", "Loader created.");

        // this was working somewhat, but only pulling images from a certain folder
        // i deleted that folder because it was from a previous application
        // now no images are loaded into gridview

        Uri mUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

        Log.i("URI: ", mUri.toString());

        return new CursorLoader(getActivity(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
