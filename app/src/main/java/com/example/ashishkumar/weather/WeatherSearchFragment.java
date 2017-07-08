package com.example.ashishkumar.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashishkumar.weather.modal.WeatherDetailsResponse;

import java.io.IOException;
import java.text.DecimalFormat;

import static com.example.ashishkumar.weather.Constants.BASE_WEATHER_ICON_URL;
import static com.example.ashishkumar.weather.Constants.BASE_WEATHER_URL;
import static com.example.ashishkumar.weather.Constants.NO_USER_INPUT_MESSAGE;
import static com.example.ashishkumar.weather.Constants.SHARED_PREF_CITY_KEY;
import static com.example.ashishkumar.weather.Constants.SHARED_PREF_FILE_NAME;

/**
 * Created by ashishkumar on 7/7/17.
 */

public class WeatherSearchFragment extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = WeatherSearchFragment.class.getSimpleName();
    View mDetailsView;
    ProgressDialog mProgressDialog;
    private EditText mSearchEditText;
    private TextView mCityTv;
    private TextView mTempTv;
    private TextView mConditionTv;
    private ImageView mConditionImageView;

    public static WeatherSearchFragment newInstance() {
        return new WeatherSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_search, container, false);
        mDetailsView = view.findViewById(R.id.details_view);
        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        Button searchButton = (Button) view.findViewById(R.id.search_button);
        mCityTv = (TextView) view.findViewById(R.id.city);
        String savedCity = getSavedCity();
        //set the last displayed city and call the service to fetch the current data
        if (!TextUtils.isEmpty(savedCity)) {
            mSearchEditText.setText(savedCity);
            mSearchEditText.setSelection(savedCity.length());
            new WeatherDetailsFetchAsyncTask().execute(getUserInput());
        }
        mTempTv = (TextView) view.findViewById(R.id.temp);
        mConditionTv = (TextView) view.findViewById(R.id.weather_condition_tv);
        mConditionImageView = (ImageView) view.findViewById(R.id.weather_condition_iv);
        searchButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.search_screen_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_button) {
            //hide the keyboard
            ((WeatherSearchActivity) getActivity()).hideKeyboard(v);
            if (TextUtils.isEmpty(getUserInput())) {
                displayToast(NO_USER_INPUT_MESSAGE);
            } else {
                new WeatherDetailsFetchAsyncTask().execute(getUserInput());
            }
        }

    }

    private String getUserInput() {
        return mSearchEditText.getText().toString();
    }

    private double convertKelvinToFahrenheit(double kelvin) {
        return (kelvin - 273) * 9 / 5 + 32;
    }

    //save the searched city inside shared preferences
    private void saveSearchedCity(String city) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREF_CITY_KEY, city);
        editor.apply();
    }

    private String getSavedCity() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREF_CITY_KEY, "");
    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    //set the visibility to gone and dismiss the dialog
    private void resetUIOnError() {
        //set the bitmap to null
        mConditionImageView.setImageBitmap(null);
        mDetailsView.setVisibility(View.GONE);
        mProgressDialog.dismiss();
    }

    private class WeatherDetailsFetchAsyncTask extends AsyncTask<String, String, WeatherDetailsResponse> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getContext(), "",
                    getString(R.string.loading), true);
            mProgressDialog.setCancelable(false);
        }

        @Override
        protected WeatherDetailsResponse doInBackground(String... params) {
            WeatherDetailsResponse weatherDetailsResponse = null;
            try {
                String response = new NetworkUtil().fetchWeatherDetails(BASE_WEATHER_URL, params[0]);
                weatherDetailsResponse = WeatherDetailsResponse.parseJSON(response);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return weatherDetailsResponse;
        }

        @Override
        protected void onPostExecute(WeatherDetailsResponse weatherDetailsResponse) {
            super.onPostExecute(weatherDetailsResponse);
            if (!TextUtils.isEmpty(errorMessage)) {
                displayToast(errorMessage);
                resetUIOnError();
            } else {
                //if the returned weather place's country is other than US then simply display Toast message
                if (!weatherDetailsResponse.getSys().getCountry().equalsIgnoreCase("US")) {
                    displayToast(getString(R.string.non_us_city_error_messgage));
                    resetUIOnError();
                } else {
                    //save city to the shared pref and display data to UI
                    saveSearchedCity(weatherDetailsResponse.getName());
                    //setting the city name to the edit text
                    mSearchEditText.setText(weatherDetailsResponse.getName());
                    mSearchEditText.setSelection(weatherDetailsResponse.getName().length());

                    if (weatherDetailsResponse.getWeather() != null && weatherDetailsResponse.getWeather().get(0) != null) {
                        mConditionTv.setText(weatherDetailsResponse.getWeather().get(0).getMain());
                        new WeatherIconFetchAsyncTask().execute(weatherDetailsResponse.getWeather().get(0).getIcon());
                    }
                    mCityTv.setText(weatherDetailsResponse.getName());
                    double fahrenheit = convertKelvinToFahrenheit(weatherDetailsResponse.getMain().getTemp());
                    mTempTv.setText(new DecimalFormat("#").format(fahrenheit) + getString(R.string.fahrenheit));
                }
            }
        }

        private class WeatherIconFetchAsyncTask extends AsyncTask<String, String, Bitmap> {
            private String errorMessage;

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = new NetworkUtil().downloadWeatherConditionsIcon(BASE_WEATHER_ICON_URL, params[0]);
                } catch (IOException e) {
                    errorMessage = e.getMessage();
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mProgressDialog.dismiss();
                if (!TextUtils.isEmpty(errorMessage)) {
                    displayToast(errorMessage);
                } else if (bitmap != null) {
                    mConditionImageView.setImageBitmap(bitmap);
                    //now make all UI elements for weather details visible
                    mDetailsView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
