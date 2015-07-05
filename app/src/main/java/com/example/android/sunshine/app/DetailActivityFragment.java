package com.example.android.sunshine.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView fv = (TextView) view.findViewById(R.id.detail_forecast_textview);
        String forecast = null; {
            Intent intent = getActivity().getIntent();
            if (intent!=null) {
                forecast = intent.getStringExtra(ForecastFragment.FORECAST_STRING);
            }
            if (forecast==null) {
                forecast = "";
            }
        }
        fv.setText(forecast);
        return view;
    }
}
