package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = ForecastFragment.class.getSimpleName();
    private static final String BUNDLE_POSITION_KEY = "BUNDLE_POSITION_KEY";
    private static final int FORECAST_LOADER_ID = 0;
    static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private ForecastAdapter mForecastAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;  // for Tablet UI
    private boolean mUseTodayLayout = true;
    private TextView mEmptyTextView;

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter!=null) {
            mForecastAdapter.setUseTodayLayout(useTodayLayout);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!key.equals(getString(R.string.location_status_key))) {
            return;
        }
        updateEmptyView();
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, savedInstanceState, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        if (lv == null) {
            return rootView;
        }
        lv.setAdapter(mForecastAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                mPosition = position;
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Uri locationDateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        cursor.getString(COL_LOCATION_SETTING),
                        cursor.getLong(COL_WEATHER_DATE));
                if (getActivity() instanceof Callback) {
                    ((Callback) getActivity()).onItemSelected(locationDateUri);
                } else {
                    Log.w(TAG, "Parent activity doesn't implement Callback.");
                }
            }
        });
        mListView = lv;
        mEmptyTextView = (TextView) rootView.findViewById(R.id.empty_list_text_view);
        mListView.setEmptyView(mEmptyTextView);
        // Restore state
        if (savedInstanceState!=null) {
            mPosition = savedInstanceState.getInt(BUNDLE_POSITION_KEY, 0);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_POSITION_KEY, mPosition);
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                Utility.getPreferredLocation(getActivity()),
                System.currentTimeMillis());
        final String[] projection = FORECAST_COLUMNS;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        return new CursorLoader(
                getActivity(),
                weatherUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        updateEmptyView();
        if (mPosition!=ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);  // for tablet UI
        }
    }

    private void updateEmptyView() {
        if (!mForecastAdapter.isEmpty()) {
            return;
        }
        final String base = getString(R.string.empty_forecast_text);

        if (!Utility.isNetworkAvailable(getActivity())) {
            mEmptyTextView.setText(base + "\n" + getString(R.string.no_connectivity_text));
        } else {
            switch (Utility.getLocationStatus(getActivity())) {
                case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                    mEmptyTextView.setText(base + "\n" + getString(R.string.server_down_text));
                    break;
                case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                    mEmptyTextView.setText(base + "\n" + getString(R.string.server_response_invalid_text));
                    break;
                case SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN:
                default:
                    mEmptyTextView.setText(base + "\n" + getString(R.string.server_unknown_error_text));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }
}
