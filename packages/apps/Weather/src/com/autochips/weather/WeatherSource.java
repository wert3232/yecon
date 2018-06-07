package com.autochips.weather;

import android.graphics.drawable.Drawable;

public interface WeatherSource {

    public void initData();

    public Weather getCurrentWeather();

    public Weather getTodayWeather();

    public Weather getTomorrowWeather();

    public Weather getNewestWeather();

    public void queryWeather(String woeid, HttpTaskListener listener);

    public void parseWeatherData(String response) throws Exception;
    public void parseCityData(String response) throws Exception;
    public String getConditionByCode(String code);

    public Drawable getDrawableByCode(String code);

    public void refreshConditionList();

}
