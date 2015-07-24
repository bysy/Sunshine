package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    /** Note that the Cursor must be to a projection as defined in ForecastFragment */
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        final float maxC = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        final float minC = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        final int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        final String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        final boolean isMetric = Utility.isMetric(mContext);

        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        if (dateView!=null) {
            dateView.setText(Utility.getFriendlyDayString(mContext, dateInMillis));
        }
        TextView maxTemperatureView = (TextView) view.findViewById(R.id.list_item_high_textview);
        if (maxTemperatureView!=null) {
            maxTemperatureView.setText(Utility.formatTemperature(maxC, isMetric));
        }
        TextView minTemperatureView = (TextView) view.findViewById(R.id.list_item_low_textview);
        if (minTemperatureView!=null) {
            minTemperatureView.setText(Utility.formatTemperature(minC, isMetric));
        }
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        if (iconView!=null) {
            // TODO: Load actual image for weatherId
            iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        }
        TextView descView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        if (descView!=null) { descView.setText(desc); }
    }
}
