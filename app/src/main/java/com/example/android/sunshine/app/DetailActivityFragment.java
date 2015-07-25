package com.example.android.sunshine.app;

import android.content.Context;
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

import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * Show weather details for the day sent in via the intent's data Uri.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static String TAG = DetailActivityFragment.class.getSimpleName();
    private static final int CURSOR_LOADER = 0;
    private String mForecast;
    private ShareActionProvider mShareActionProvider;

    static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };
    static final int COL_DATE = 0;
    static final int COL_DESC = 1;
    static final int COL_MAX = 2;
    static final int COL_MIN = 3;
    static final int COL_WEATHER_ID = 4;
    static final int COL_HUMIDITY = 5;
    static final int COL_WIND_SPEED = 6;
    static final int COL_WIND_DEGREES = 7;
    static final int COL_PRESSURE = 8;

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
        final String[] projection = DETAIL_COLUMNS;
        return new CursorLoader(
                getActivity(),
                in.getData(),
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }
        View view = getActivity().findViewById(R.id.detail_container);
        TextView dayView = (TextView) view.findViewById(R.id.detail_day_textview);
        TextView dateView = (TextView) view.findViewById(R.id.detail_date_textview);
        TextView highView = (TextView) view.findViewById(R.id.detail_high_temp_textview);
        TextView lowView = (TextView) view.findViewById(R.id.detail_low_temp_textview);
        ImageView iconView = (ImageView) view.findViewById(R.id.detail_icon_view);
        TextView descView = (TextView) view.findViewById(R.id.detail_desc_textview);
        TextView humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        TextView windView = (TextView) view.findViewById(R.id.detail_wind_textview);
        TextView pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        Context context = getActivity();

        final long dateInMillis = data.getLong(COL_DATE);
        dayView.setText(Utility.getDayName(context, dateInMillis));
        dateView.setText(Utility.getFormattedMonthDay(context, dateInMillis));
        final boolean isMetric = Utility.isMetric(context);
        highView.setText(Utility.formatTemperature(context, data.getFloat(COL_MAX), isMetric));
        lowView.setText(Utility.formatTemperature(context, data.getFloat(COL_MIN), isMetric));

        // TODO: replace with real image
        iconView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));

        descView.setText(data.getString(COL_DESC));
        humidityView.setText(
                String.format(getString(R.string.format_humidity),
                        data.getFloat(COL_HUMIDITY)));
        windView.setText(
                Utility.getFormattedWind(context,
                        data.getFloat(COL_WIND_SPEED), data.getFloat(COL_WIND_DEGREES)));
        pressureView.setText(
                String.format(getString(R.string.format_pressure),
                        data.getFloat(COL_PRESSURE)));

        mForecast = convertCursorRowToUXFormat(data, isMetric);
        maybeSetShareIntent();
    }

    /*
    This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
    string.
 */
    static String convertCursorRowToUXFormat(Cursor cursor, boolean isMetric) {
        // get row indices for our cursor
        final int idx_max_temp = COL_MAX;
        final int idx_min_temp = COL_MIN;
        final int idx_date = COL_DATE;
        final int idx_short_desc = COL_DESC;

        String highAndLow = Utility.formatHighLows(
                cursor.getDouble(idx_max_temp),
                cursor.getDouble(idx_min_temp),
                isMetric);

        return Utility.formatDate(cursor.getLong(idx_date)) +
                " - " + cursor.getString(idx_short_desc) +
                " - " + highAndLow;
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
