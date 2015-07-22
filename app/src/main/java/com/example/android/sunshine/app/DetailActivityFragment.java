package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER = 0;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent in = getActivity().getIntent();
        if (in==null) { return null; }
        return new CursorLoader(
                getActivity(),
                in.getData(),
                ForecastFragment.FORECAST_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        TextView fv = (TextView) getActivity().findViewById(R.id.detail_forecast_textview);
        if (!data.moveToFirst()) { return; }
        fv.setText(ForecastFragment.convertCursorRowToUXFormat(data, Utility.isMetric(getActivity())));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // pass
    }
}
