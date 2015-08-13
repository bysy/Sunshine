package com.example.android.sunshine.app.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.sync.OwmHelper;

/**
 * Update the weather in a service.
 */
public class SunshineService extends IntentService {
    public static final String LOCATION_SETTING = "LOCATION_SETTING";
    private static final String LOG_TAG = SunshineService.class.getSimpleName();

   /** Provide a broadcast receiver to start the location update service. */
    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String location = intent.getStringExtra(LOCATION_SETTING);
            if (location==null) { location = Utility.getPreferredLocation(context); }
            Intent i = new Intent(context, SunshineService.class);
            i.putExtra(LOCATION_SETTING, location);
            context.startService(i);
        }
    }

    public SunshineService() {
        super(SunshineService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent==null) { return; }
        final String location = intent.getStringExtra(LOCATION_SETTING);
        if (location==null || location.isEmpty()) {
            Log.e(LOG_TAG, LOCATION_SETTING + " is invalid");
            return;
        }
        OwmHelper.update(this, location);
    }
}
