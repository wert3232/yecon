
package com.yecon.yeconfactorytesting;

import static com.yecon.yeconfactorytesting.Constants.*;

public class FactoryConfigInfo {
    private int mDefaultVolume;

    private int mGpsStartValue;
    private int mGpsEndValue;

    private int mFlashType;

    private String mWifiConnectSSID;
    private String mWifiConnectPassword;

    private int mTestType;

    public FactoryConfigInfo() {
        mDefaultVolume = DEFAULT_VOLUME;

        mGpsStartValue = GPS_MIN_SNR_VALUE;
        mGpsEndValue = GPS_MAX_SNR_VALUE;

        mFlashType = MEM_1G_FLASH_16G;

        mWifiConnectSSID = WIFI_DEFAULT_CONNECT_SSID;
        mWifiConnectPassword = WIFI_DEFAULT_CONNECT_PASSWORD;

        mTestType = TEST_TYPE_DEFAULT;
    }

    public int getDefaultVolume() {
        return mDefaultVolume;
    }

    public void setDefaultVolume(int defaultVolume) {
        this.mDefaultVolume = defaultVolume;
    }

    public int getGpsStartValue() {
        return mGpsStartValue;
    }

    public void setGpsStartValue(int gpsStartValue) {
        this.mGpsStartValue = gpsStartValue;
    }

    public int getGpsEndValue() {
        return mGpsEndValue;
    }

    public void setGpsEndValue(int gpsEndValue) {
        this.mGpsEndValue = gpsEndValue;
    }

    public int getFlashType() {
        return mFlashType;
    }

    public void setFlashType(int flashType) {
        this.mFlashType = flashType;
    }

    public String getWifiConnectSSID() {
        return mWifiConnectSSID;
    }

    public void setWifiConnectSSID(String wifiConnectSSID) {
        this.mWifiConnectSSID = wifiConnectSSID;
    }

    public String getWifiConnectPassword() {
        return mWifiConnectPassword;
    }

    public void setWifiConnectPassword(String wifiConnectPassword) {
        this.mWifiConnectPassword = wifiConnectPassword;
    }

    public int getTestType() {
        return mTestType;
    }

    public void setTestType(int testType) {
        this.mTestType = testType;
    }

}
