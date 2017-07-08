package com.example.ashishkumar.weather;

/**
 * Created by ashishkumar on 7/7/17.
 */

public class Constants {
    //constants for Fragments tags
    public final static String FRAGMENT_TAG_WEATHER_SEARCH = "FRAGMENT_WEATHER_SEARCH";

    public final static String BASE_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    public final static String BASE_WEATHER_ICON_URL = "http://openweathermap.org/img/w/";
    public final static String APP_ID_PARAM = "APPID";
    public final static String APP_ID_PARAM_VALUE = "7a860d96f116c74843edfbcdfe57f442";

    public final static String WEATHER_SEARCH_EMPTY_MESSAGE = "No Data Found";
    public final static String SERVER_ERROR = "Network Issue";
    public final static String NO_USER_INPUT_MESSAGE = "Please enter some city";

    public static final String BUNDLE_EXTRA_WEATHER_DETAILS = "weather_details";

    public static final String SHARED_PREF_FILE_NAME = "com.example.ashishkumar.weather.PREFERENCE_FILE_KEY";
    public static final String SHARED_PREF_CITY_KEY = "city";

}
