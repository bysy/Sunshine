package com.example.android.sunshine.app.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Update widgets.
 */
public class TodayWidgetUpdateIntentService extends IntentService {
    private static final String TAG = TodayWidgetUpdateIntentService.class.getSimpleName();

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;
    private static final int INDEX_MAX_TEMP = 2;

    public TodayWidgetUpdateIntentService() {
        super(TAG+"Worker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                Utility.getPreferredLocation(this),
                System.currentTimeMillis());

        // Query the DB
        final Cursor cursor = getContentResolver().query(uri, FORECAST_COLUMNS, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        final int weatherArtId = Utility.getArtResourceForWeatherCondition(
                cursor.getInt(INDEX_WEATHER_ID));
        final String weatherDesc = cursor.getString(INDEX_SHORT_DESC);
        final String weatherMaxTemperature = Utility.formatTemperature(this,
                cursor.getDouble(INDEX_MAX_TEMP),
                Utility.isMetric(this));
        cursor.close();

        final AppWidgetManager widgetMgr = AppWidgetManager.getInstance(this);
        final int ids[] = widgetMgr
                .getAppWidgetIds(new ComponentName(this, TodayWidgetProvider.class));

        final RemoteViews rvs = new RemoteViews(getPackageName(), R.layout.today_widget);
        rvs.setImageViewResource(R.id.today_widget_art, weatherArtId);
        rvs.setTextViewText(R.id.today_widget_high_temperature, weatherMaxTemperature);
        rvs.setOnClickPendingIntent(
                R.id.today_widget,
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        setDescription(rvs, R.id.today_widget_art, weatherDesc);
        widgetMgr.updateAppWidget(ids, rvs);
    }

    /** Update content description if possible. */
    private void setDescription(RemoteViews rvs, int viewId, String description) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            rvs.setContentDescription(viewId, description);
        }
    }
}
