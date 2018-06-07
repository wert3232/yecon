package com.autochips.weather;

import java.util.List;

public interface Dao {

    public List<CountryRegion> getCountryList();

    public List<CountryRegion> getStateList(String countryNameEn,
            String countryCode);

    public List<CountryRegion> getCityList(String countryNameEn,
            String stateNameEn);

    public void saveOrUpdateCurrentCity(CountryRegion cr);

    public CountryRegion getCurrentCity();

    public WeatherCache getWeatherCache();
    public CountryRegion getCurrentCityByCityName(String cityNameEn, String curwoied);
    public void saveOrUpdateWeatherCache(WeatherCache cache);
}
