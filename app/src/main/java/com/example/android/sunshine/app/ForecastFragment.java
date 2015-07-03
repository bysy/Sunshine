package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> forecastData = makeFakeData();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                forecastData);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        if (lv!=null) {
            lv.setAdapter(adapter);
        }

        FetchWeatherTask fwt = new FetchWeatherTask();
        fwt.execute();

        return rootView;
    }

    private List<String> makeFakeData() {
        final String[] data = { "Today -- Sunny -- 76/62",
                "Tomorrow -- Mostly cloudy -- 72/67",
                "Tuesday -- Sunny -- 84/70",
                "Wednesday -- Rain likely -- 78/58",
                "Thursday -- Mostly sunny -- 75/62",
                "Friday -- Cloudy -- 74/64",
                "Saturday -- Sunny -- 77/60" };
        return Arrays.asList(data);
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        private String TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String doInBackground(Void ... unused) {
            // From from github gist, slightly modified:

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043,usa&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                // We have a forecast JSON string.
                return buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
