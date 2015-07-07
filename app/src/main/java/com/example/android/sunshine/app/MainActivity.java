package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
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
}
