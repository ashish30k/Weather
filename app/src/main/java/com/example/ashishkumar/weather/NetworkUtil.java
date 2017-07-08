package com.example.ashishkumar.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.example.ashishkumar.weather.Constants.APP_ID_PARAM;
import static com.example.ashishkumar.weather.Constants.APP_ID_PARAM_VALUE;
import static com.example.ashishkumar.weather.Constants.SERVER_ERROR;
import static com.example.ashishkumar.weather.Constants.WEATHER_SEARCH_EMPTY_MESSAGE;

/**
 * Created by ashishkumar on 7/7/17.
 */

public class NetworkUtil {
    private String LOG_TAG = NetworkUtil.class.getSimpleName();

    public String fetchWeatherDetails(final String url, String city) throws IOException {
        String modifiedUrl = url + "?q=" + city + "&" + APP_ID_PARAM + "=" + APP_ID_PARAM_VALUE;
        Log.d(LOG_TAG, "modifiedUrl :: " + modifiedUrl);


        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(modifiedUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                    throw new IOException(WEATHER_SEARCH_EMPTY_MESSAGE);
                } else {
                    throw new IOException(SERVER_ERROR);
                }
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream, 500);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.d(LOG_TAG, "result :: " + result);
        return result;
    }

    public Bitmap downloadWeatherConditionsIcon(String url, String iconID) throws IOException {
        String modifiedUrl = url + iconID + ".png";
        Log.d(LOG_TAG, "modifiedUrl :: " + modifiedUrl);

        InputStream stream = null;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(modifiedUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException(SERVER_ERROR);
            }
            stream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[maxLength];
        int numberOfChars = 0;
        int readSize = 0;
        while (numberOfChars < maxLength && readSize != -1) {
            numberOfChars += readSize;
            readSize = reader.read(buffer, numberOfChars, buffer.length - numberOfChars);
        }
        if (numberOfChars != -1) {
            numberOfChars = Math.min(numberOfChars, maxLength);
            result = new String(buffer, 0, numberOfChars);
        }
        return result;
    }
}
