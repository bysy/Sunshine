package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        View detailView = findViewById(R.id.detail_fragment_container);
        if (detailView==null) {
            mTwoPane = false;
            Log.d(TAG, "Single-pane layout.");
        } else {
            mTwoPane = true;
            Log.d(TAG, "Double-pane layout.");
            FragmentManager fm = getSupportFragmentManager();
            ForecastFragment forecastFragment =
                    (ForecastFragment) fm.findFragmentById(R.id.fragment_forecast);
            forecastFragment.setUseTodayLayout(false);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.detail_fragment_container, new DetailActivityFragment(), DETAIL_FRAGMENT_TAG);
            ft.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        final String curLocation = Utility.getPreferredLocation(this);
        if (curLocation==null) {
            Log.e(TAG, "Couldn't find preferred location.");
            return;
        }
        if (!mLocation.equals(curLocation)) {
            Log.v(TAG, "Location change detected.");
            mLocation = curLocation;
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if (ff==null) {
                Log.e(TAG, "Couldn't find forecast layout.");
                return;
            }
            ff.onLocationChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
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

        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            case R.id.action_view_location: {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(getGeoUri());
                if (mapIntent.resolveActivity(getPackageManager())!=null) {
                    startActivity(mapIntent);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private Uri getGeoUri() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final String locKey = getString(R.string.pref_location_key);
        final String location = settings.getString(locKey, "");
        final Uri uri = Uri.parse("geo:0,0?q="+Uri.encode(location));
        return uri;
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        // when running on a phone, start the detail activity. On larger screens, load the detail fragment
        if (!mTwoPane) {
            Intent i = new Intent(this, DetailActivity.class);
            i.setData(dateUri);
            startActivity(i);
         } else {
            DetailActivityFragment detailFragment = new DetailActivityFragment();
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.URI_KEY, dateUri);
            detailFragment.setArguments(args);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.detail_fragment_container, detailFragment, DETAIL_FRAGMENT_TAG);
            ft.commit();
        }
    }
}
