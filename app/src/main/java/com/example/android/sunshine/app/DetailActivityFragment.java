package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = DetailActivityFragment.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;
    private String mForecast;
    private ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider==null) {
            Log.d("TAG", "ShareActionProvider is unexpectedly null");
            return;
        }
        maybeSetShareIntent();
    }

    private void maybeSetShareIntent() {
        if (mForecast==null || mShareActionProvider==null) { return; }
        mShareActionProvider.setShareIntent(getShareIntent());
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
        mForecast = ForecastFragment.convertCursorRowToUXFormat(
                data, Utility.isMetric(getActivity()));
        fv.setText(mForecast);
        maybeSetShareIntent();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // pass
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //noinspection deprecation since we target older API
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        final String hashtag = "#SunshineApp";
        final String shareText =
                (mForecast.isEmpty()) ? hashtag : mForecast + " " + hashtag;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }
}
