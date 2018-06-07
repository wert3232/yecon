package com.autochips.weather;

import android.content.Context;

public class WeatherSourceFactory {
    private static WeatherSource sInstance;

    public static WeatherSource getInstance(Context context) {
        sInstance = new YahooWeatherSource(context);
        return sInstance;
    }
}
