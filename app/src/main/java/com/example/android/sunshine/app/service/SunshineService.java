package com.example.android.sunshine.app.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Update the weather in a service.
 */
public class SunshineService extends IntentService {
    public SunshineService() {
        super(SunshineService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
