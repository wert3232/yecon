package com.autochips.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class CountryRegion implements Parcelable {

    private int _id;

    private String country_name;

    private String country_name_en;

    private String country_code;

    private String state_name;

    private String state_name_en;

    private String state_code;

    private String city_name;

    private String city_name_en;

    private String city_code;

    private String woeid;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getCountryName() {
        return country_name;
    }

    public void setCountryName(String countryName) {
        this.country_name = countryName;
    }

    public String getCountryNameEn() {
        return country_name_en;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.country_name_en = countryNameEn;
    }

    public String getCountryCode() {
        return country_code;
    }

    public void setCountryCode(String countryCode) {
        this.country_code = countryCode;
    }

    public String getStateName() {
        return state_name;
    }

    public void setStateName(String stateName) {
        this.state_name = stateName;
    }

    public String getStateNameEn() {
        return state_name_en;
    }

    public void setStateNameEn(String stateNameEn) {
        this.state_name_en = stateNameEn;
    }

    public String getStateCode() {
        return state_code;
    }

    public void setStateCode(String stateCode) {
        this.state_code = stateCode;
    }

    public String getCityName() {
        return city_name;
    }

    public void setCityName(String cityName) {
        this.city_name = cityName;
    }

    public String getCityNameEn() {
        return city_name_en;
    }

    public void setCityNameEn(String cityNameEn) {
        this.city_name_en = cityNameEn;
    }

    public String getCityCode() {
        return city_code;
    }

    public void setCityCode(String cityCode) {
        this.city_code = cityCode;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public int getRegionLevel() {
        if (!StringUtil.isEmpty(state_name))
            return REGION_LEVEL_STATE;
        if (!StringUtil.isEmpty(city_name))
            return REGION_LEVEL_CITY;
        if (!StringUtil.isEmpty(country_name))
            return REGION_LEVEL_COUNTRY;
        return REGION_LEVEL_ROOT;

    }

    public String getTitle() {
        String title = "";
        if (!StringUtil.isEmpty(country_name)) {
            title = country_name;
            if (Constants.IS_EN)
                title = country_name_en;
        }
        if (!StringUtil.isEmpty(state_name)) {
            title = state_name;
            if (Constants.IS_EN)
                title = state_name_en;
        }
        if (!StringUtil.isEmpty(city_name)) {
            title = city_name;
            if (Constants.IS_EN)
                title = city_name_en;
        }
        return title;
    }

    public final static int REGION_LEVEL_ROOT = 0;
    public final static int REGION_LEVEL_COUNTRY = 1;
    public final static int REGION_LEVEL_STATE = 2;
    public final static int REGION_LEVEL_CITY = 3;

    public static final Parcelable.Creator<CountryRegion> CREATOR = new Creator<CountryRegion>() {
        public CountryRegion createFromParcel(Parcel source) {
            CountryRegion cRegion = new CountryRegion();
            cRegion._id = source.readInt();
            cRegion.country_name = source.readString();
            cRegion.country_name_en = source.readString();
            cRegion.country_code = source.readString();
            cRegion.state_name = source.readString();
            cRegion.state_name_en = source.readString();
            cRegion.state_code = source.readString();
            cRegion.city_name = source.readString();
            cRegion.city_name_en = source.readString();
            cRegion.city_code = source.readString();
            cRegion.woeid = source.readString();
            return cRegion;
        }

        public CountryRegion[] newArray(int size) {
            return new CountryRegion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(country_name);
        dest.writeString(country_name_en);
        dest.writeString(country_code);
        dest.writeString(state_name);
        dest.writeString(state_name_en);
        dest.writeString(state_code);
        dest.writeString(city_name);
        dest.writeString(city_name_en);
        dest.writeString(city_code);
        dest.writeString(woeid);
    }
}
