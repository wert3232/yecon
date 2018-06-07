package com.autochips.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class WeatherService extends Service {

    private WeatherSource mWeatherSource;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWeatherSource = WeatherSourceFactory.getInstance(this
                .getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.sendUpdateWeatherBroadcast(this.getApplicationContext(),
                DBFactory.getInstance(this.getApplicationContext()));
        mWeatherSource.getNewestWeather();
        return Service.START_STICKY;
    }

    private IWeatherService.Stub mBinder = new IWeatherService.Stub() {
        public void getNewestWeather() throws RemoteException {
            mWeatherSource.getNewestWeather();
        }
    };
}
