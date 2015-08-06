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
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout = true;

    @Override
    public int getItemViewType(int position) {
        return (position==0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /** Note that the Cursor must be to a projection as defined in ForecastFragment */
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int viewResource =
                getItemViewType(cursor.getPosition())==VIEW_TYPE_FUTURE_DAY ?
                        R.layout.list_item_forecast : R.layout.list_item_forecast_today;
        View view = LayoutInflater.from(context).inflate(viewResource, parent, false);
        view.setTag(new ViewHolder(view));
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
        final String highTemperature = Utility.formatTemperature(mContext, maxC, isMetric);
        final String lowTemperature = Utility.formatTemperature(mContext, minC, isMetric);
        final String friendlyDay = Utility.getFriendlyDayString(mContext, dateInMillis);

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        view.setContentDescription(String.format(
                mContext.getString(R.string.format_forecast_item_description),
                        friendlyDay, desc, highTemperature, lowTemperature));
        viewHolder.dateView.setText(friendlyDay);
        viewHolder.highTemperatureView.setText(highTemperature);
        viewHolder.lowTemperatureView.setText(lowTemperature);

        // Load art for today, icons for other days
        final int weatherResource =
                (getItemViewType(cursor.getPosition()))==VIEW_TYPE_TODAY ?
                Utility.getArtResourceForWeatherCondition(weatherId) :
                        Utility.getIconResourceForWeatherCondition(weatherId);
        viewHolder.iconView.setImageDrawable(
                mContext.getResources().getDrawable(weatherResource));
        viewHolder.descriptionView.setText(desc);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    /** Cache subviews of list item view. */
    class ViewHolder {
        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTemperatureView;
        final TextView lowTemperatureView;

        ViewHolder(View item) {
            iconView = (ImageView) item.findViewById(R.id.list_item_icon);
            dateView = (TextView) item.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) item.findViewById(R.id.list_item_forecast_textview);
            highTemperatureView = (TextView) item.findViewById(R.id.list_item_high_textview);
            lowTemperatureView = (TextView) item.findViewById(R.id.list_item_low_textview);
            if (iconView==null ||
                    dateView==null ||
                    descriptionView==null ||
                    highTemperatureView==null ||
                    lowTemperatureView==null) {
                throw new IllegalArgumentException("Incompatible view");
            }
        }
    }
}
