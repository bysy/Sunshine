package com.example.android.sunshine.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Provide the widget implementation.
 */
public class TodayWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        startWidgetUpdaterService(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SunshineSyncAdapter.ACTION_DATA_UPDATED)) {
            startWidgetUpdaterService(context);
        } else {
            super.onReceive(context, intent);
        }
    }

    private void startWidgetUpdaterService(Context context) {
        context.startService(new Intent(context, TodayWidgetUpdateIntentService.class));
    }
}
