package com.autochips.weather;

import java.util.Locale;

import android.app.Application;

public class WeatherApp extends Application {

    public static volatile boolean IS_WIFI_CONNECTED;

    @Override
    public void onCreate() {       
        IS_WIFI_CONNECTED = Utils.isWiFiConnected(this);
        String language = Locale.getDefault().getLanguage();
        if ("zh".equalsIgnoreCase(language)) {
            Constants.IS_EN = false;
        } else {
            Constants.IS_EN = true;
        }
    }
}
