package com.autochips.weather;

public class WeatherCache {

    private int _id;

    private String firstday_condition;

    private String firstday_high;

    private String firstday_low;

    private int firstday_code = -1;

    private String secday_condition;

    private String secday_high;

    private String secday_low;
    
    private int secday_code = -1;

    private String update_time;

    private String current_temp;

    private String current_condition;

    private int current_code = -1;

    private String source;

    private String woeid;

    private String unit;

    public WeatherCache() {

    }

    public WeatherCache(Weather curCache, Weather firstCache, Weather secCache,
            String updatetime) {
        current_temp = curCache.currentTemp;
        current_condition = curCache.condition;
        current_code = curCache.code;

        firstday_condition = firstCache.condition;
        firstday_high = firstCache.highTemp;
        firstday_low = firstCache.lowTemp;
        firstday_code = firstCache.code;

        secday_condition = secCache.condition;
        secday_high = secCache.highTemp;
        secday_low = secCache.lowTemp;
        secday_code = secCache.code;

        update_time = updatetime;
    }

    public WeatherCache(String arg1, String updatetime) {
        update_time = updatetime;
        firstday_condition = arg1;
        firstday_high = arg1;
        firstday_low = arg1;
        current_condition = arg1;
        current_temp = arg1;
        secday_condition = arg1;
        secday_high = arg1;
        secday_low = arg1;
    }

    public void setFirstWeather(Weather weather) {
        firstday_condition = weather.condition;
        firstday_high = weather.highTemp;
        firstday_low = weather.lowTemp;
        firstday_code = weather.code;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getFirstdayCondition() {
        return firstday_condition;
    }

    public void setFirstdayCondition(String firstday_condition) {
        this.firstday_condition = firstday_condition;
    }

    public String getFirstdayHigh() {
        return firstday_high;
    }

    public void setFirstdayHigh(String firstday_high) {
        this.firstday_high = firstday_high;
    }

    public String getFirstdayLow() {
        return firstday_low;
    }

    public void setFirstdayLow(String firstday_low) {
        this.firstday_low = firstday_low;
    }

    public String getSecdayCondition() {
        return secday_condition;
    }

    public void setSecdayCondition(String secday_condition) {
        this.secday_condition = secday_condition;
    }

    public String getSecdayHigh() {
        return secday_high;
    }

    public void setSecdayHigh(String secday_high) {
        this.secday_high = secday_high;
    }

    public String getSecdayLow() {
        return secday_low;
    }

    public void setSecdayLow(String secday_low) {
        this.secday_low = secday_low;
    }

    public String getUpdateTime() {
        return update_time;
    }

    public void setUpdateTime(String update_time) {
        this.update_time = update_time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCurrentTemp() {
        return current_temp;
    }

    public void setCurrentTemp(String current_temp) {
        this.current_temp = current_temp;
    }

    public String getCurrentCondition() {
        return current_condition;
    }

    public void setCurrentCondition(String current_condition) {
        this.current_condition = current_condition;
    }

    public int getFirstdayCode() {
        return firstday_code;
    }

    public void setFirstdayCode(int firstday_code) {
        this.firstday_code = firstday_code;
    }

    public int getSecdayCode() {
        return secday_code;
    }

    public void setSecdayCode(int secday_code) {
        this.secday_code = secday_code;
    }

    public int getCurrentCode() {
        return current_code;
    }

    public void setCurrentCode(int current_code) {
        this.current_code = current_code;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
