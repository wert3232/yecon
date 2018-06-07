
package com.yecon.gpstest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

public class GPSTestActivity extends Activity implements Listener {
    private static final String TAG = "GPSTest";
    private static final boolean DEBUG = true;

    private final DecimalFormat unitFormat = new DecimalFormat("0.######");
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat(
            "yyyy/MM/dd  HH:mm:ss");

    private final int CHECK_LOCATION_DELAY = 1500;

    private final int MSG_CHECK_LOCATION = 101;

    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private String mProvider;

    // GpsStatus mGpsStatus;
    Iterator<GpsSatellite> mIterator;
    public ArrayList<GPSInfoItem> mGPSInfoItemList;
    Canvas mCanvas;

    boolean isLocation = false;

    TextView lat_tv = null;
    TextView lon_tv = null;
    TextView hig_tv = null;
    TextView spe_tv = null;
    TextView utc_tv = null;

    GPSSnrPrnView mSnrPrnView;
    GPSEarthView mEarthView;
    Button mResetBtn = null;
    // Reset time Button/View
    Clock mClock;
    TextView dsecondsView = null;
    TextView secondsView = null;
    TextView minView = null;
    // ///////////////////////////
    String lat_st = null;
    String lon_st = null;
    String utc_st = null;

    AlertDialog alert;

    float hig_st = 0.0f;
    float spe_st = 0.0f;
    float s = 3.6f;
    float speed = 0.0f;
    private static int mPid = -1;

    Runnable timeRefresh = new Runnable() {
        public void run() {
            getSystemTime();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DEBUG) {
            Log.i(TAG, "onCreate - onCreate");
        }

        mPid = android.os.Process.myPid();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.gps_test_main);

        mSnrPrnView = (GPSSnrPrnView) findViewById(R.id.gps_snr_prn_view);
        mEarthView = (GPSEarthView) findViewById(R.id.gps_earth_view);
        mGPSInfoItemList = new ArrayList<GPSInfoItem>();
        mClock = new Clock(this);

        initView();
        preView();
        initSet();

        mProvider = LocationManager.GPS_PROVIDER;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.addGpsStatusListener(this);
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        updateLocation(mLocation);

        // Detect the GPS's gpio were open or not
        checkGPSEnabled();

        mLocationListener = new LocationListener() {

            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (DEBUG) {
                    Log.i(TAG, "onStatusChanged - onStatusChanged");
                }
            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {

            }

            public void onLocationChanged(Location location) {
                if (DEBUG) {
                    Log.i(TAG, "onLocationChanged - onLocationChanged");
                }
                updateLocation(location);
            }
        };

        mLocationManager.requestLocationUpdates(mProvider, 1000, 0,
                mLocationListener);

        mClock.startcount();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        updateLocation(mLocation);
        mLocationManager.requestLocationUpdates(mProvider, 1000, 0,
                mLocationListener);

        if (DEBUG) {
            Log.i(TAG, "onResume - onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DEBUG) {
            Log.i(TAG, "onPause - onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DEBUG) {
            Log.i(TAG, "onStop - onStop");
        }
    }

    private void initView() {
        lat_tv = (TextView) findViewById(R.id.latitude_tv);
        lon_tv = (TextView) findViewById(R.id.longitude_tv);
        hig_tv = (TextView) findViewById(R.id.height_tv);
        spe_tv = (TextView) findViewById(R.id.speed_tv);
        utc_tv = (TextView) findViewById(R.id.UTC_tv);

        mResetBtn = (Button) findViewById(R.id.restart_bt);
        mResetBtn.setOnClickListener(onReset);

        // Reset_time
        dsecondsView = (TextView) findViewById(R.id.dsecondsView);
        secondsView = (TextView) findViewById(R.id.secondsView);
        minView = (TextView) findViewById(R.id.minView);
    }

    private void preView() {
        lat_tv.setText("90.000000");
        lon_tv.setText("0.000000");
        hig_tv.setText("150.000000 " + getResources().getString(R.string.meter));
        spe_tv.setText("0.000000 " + getResources().getString(R.string.unit));
    }

    protected void initSet() {
        isLocation = false;
        if (!mGPSInfoItemList.isEmpty()) {
            mGPSInfoItemList.clear();
        }

        mSnrPrnView.setMList(mGPSInfoItemList);
        mSnrPrnView.setIsLocation(isLocation);
        new Thread(new SPThread()).start();

        mEarthView.setMList(mGPSInfoItemList);
        mEarthView.setIsLocation(isLocation);
        new Thread(new EarthThread()).start();
    }

    private void checkGPSEnabled() {
        boolean enable = mLocationManager.isProviderEnabled(mProvider);
        if (DEBUG) {
            Log.i(TAG, "onCreate - enable: " + enable);
        }
        if (!enable) {
            createDialog();
        }
    }

    private void removeListener() {
        mLocationManager.removeUpdates(mLocationListener);
        mLocationManager.removeGpsStatusListener(this);
    }

    private void getSystemTime() {
        Date curDate = new Date(System.currentTimeMillis());
        String systemTime = timestampFormat.format(curDate);
        utc_tv.setText(systemTime);
        if (!isLocation) {
            mHandler.postDelayed(timeRefresh, 1000);
        }
    }

    private void createDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                GPSTestActivity.this);
        mBuilder.setTitle(R.string.open_gps_gpio_title)
                .setMessage(R.string.open_gps_gpio_text)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                alert.dismiss();
                                launchSettingsSecurityGpsAndFinish();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                    int which) {
                                alert.dismiss();
                                finish();
                            }
                        });
        alert = mBuilder.create();
        alert.show();
    }

    /**
     * open GPS settings in Settings
     */
    private void launchSettingsSecurityGpsAndFinish() {
        // Intent mIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(mIntent);
        finish();
    }

    private String getFormatTime(int time) {
        return time < 10 ? "0" + time : "" + time;
    }

    /**
     * get the satellites located messages!
     * 
     * @param loc
     */
    protected void updateLocation(final Location location) {
        if (location == null) {
            return;
        }

        checkLocation();

        if (!isLocation) {
            isLocation = true;
        }

        long mTime = location.getTime();
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(mTime);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String utcTime = year + "/" + month + "/" + day + "  "
                + getFormatTime(hour) + ":" + getFormatTime(minute) + ":"
                + getFormatTime(second);
        if (DEBUG) {
            Log.i(TAG, "utcTime = " + utcTime);
        }

        if (location.hasSpeed()) {
            spe_st = location.getSpeed();
            speed = spe_st * s;
            spe_tv.setText(unitFormat.format(speed) + " "
                    + getResources().getString(R.string.unit));
        }

        if (location.hasAltitude()) {
            hig_st = (float) location.getAltitude();
            hig_tv.setText(unitFormat.format(hig_st) + " "
                    + getResources().getString(R.string.meter));
        }

        lat_st = String.valueOf(unitFormat.format(location.getLatitude()));
        lat_tv.setText(lat_st);

        lon_st = String.valueOf(unitFormat.format(location.getLongitude()));
        lon_tv.setText(lon_st);

        utc_tv.setText(utcTime);

        mClock.stopcount();
    }

    /**
     * Get the satellites' Prn/Snr/Azi/Ele messages.
     */
    @Override
    public void onGpsStatusChanged(int event) {
        if (DEBUG) {
            Log.i(TAG, "onGpsStatusChanged - event = " + event);
        }

        try {
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            Iterable<GpsSatellite> iStatellite = gpsStatus.getSatellites();

            mIterator = iStatellite.iterator();
            if (!mGPSInfoItemList.isEmpty()) {
                mGPSInfoItemList.clear();
            }

            @SuppressWarnings("unused")
            int snrIsEmpty = 0;
            while (mIterator.hasNext()) {
                GpsSatellite satellite = mIterator.next();
                float snr = satellite.getSnr();
                int prn = satellite.getPrn();
                float azi = satellite.getAzimuth();
                float ele = satellite.getElevation();

                if (DEBUG) {
                    // Log.i(TAG, "snr = " + snr + " prn = " + prn);
                }

                mGPSInfoItemList.add(new GPSInfoItem(snr, prn, azi, ele));
                if (snr - 0.0 < 0.000001) {
                    snrIsEmpty++;
                }
            }

            if (DEBUG) {
                // Log.i(TAG, "snrIsEmpty = " + snrIsEmpty);
            }

            // if (snrIsEmpty == mGPSInfoItemList.size()) {
            // mGPSInfoItemList.clear();
            // }

            Collections.sort(mGPSInfoItemList, new Comparator<GPSInfoItem>() {
                public int compare(GPSInfoItem i1, GPSInfoItem i2) {
                    float snr1 = i1.mSnr;
                    float snr2 = i2.mSnr;
                    if (snr1 < snr2) {
                        return 1;
                    } else if (snr1 == snr2) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });

            mSnrPrnView.setMList(mGPSInfoItemList);
            mSnrPrnView.setIsLocation(isLocation);
            new Thread(new SPThread()).start();

            mEarthView.setMList(mGPSInfoItemList);
            mEarthView.setIsLocation(isLocation);
            new Thread(new EarthThread()).start();
        } catch (Exception e) {
            e.printStackTrace();

            if (DEBUG) {
                Log.i(TAG, "onGpsStatusChanged - error: " + e.getMessage());
            }
        }
    }

    /**
     * while press Reset key,the TextView will be invalidate.
     */
    protected void clearLocationView() {
        lat_tv.setText("90.000000");
        lon_tv.setText("0.000000");
        hig_tv.setText("150.000000" + " "
                + getResources().getString(R.string.meter));
        spe_tv.setText("0.000000" + " "
                + getResources().getString(R.string.unit));
        // utc_tv.setText("");

        if (!mClock.isStarted) {
            dsecondsView.setText("0");
            secondsView.setText("00");
            minView.setText("00");
        }

    }

    protected Button.OnClickListener onReset = new OnClickListener() {

        public void onClick(View v1) {
            mClock.stopcount();
            clearLocationView();
            initSet();
            mClock.startcount();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME
                || keyCode == KeyEvent.KEYCODE_SEARCH) {
            // initSet();
            removeListener();
            // android.os.Process.killProcess(mPid);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class SPThread implements Runnable {
        public void run() {
            mSnrPrnView.postInvalidate();
        }
    }

    class EarthThread implements Runnable {
        public void run() {
            mEarthView.postInvalidate();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_LOCATION:
                    // check location nok
                    if (isLocation) {
                        isLocation = false;
                    }
                    utc_tv.setText("");
                    break;
            }
        }
    };

    private void checkLocation() {
        if (mHandler.hasMessages(MSG_CHECK_LOCATION)) {
            mHandler.removeMessages(MSG_CHECK_LOCATION);
        }
        mHandler.sendEmptyMessageDelayed(MSG_CHECK_LOCATION,
                CHECK_LOCATION_DELAY);
    }
}
