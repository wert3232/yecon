package com.android.yecon.gpstest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
public class GPSTest extends Activity
{

    /**
     * 数据格式化
     */
    private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
    private final DecimalFormat sevenSigDigits = new DecimalFormat("0.######");
    private final DecimalFormat speedFormat = new DecimalFormat("0.#");
    private final DecimalFormat hightFormat = new DecimalFormat("0.##");
    /**
     * 定位 收星
     */
    Location location = null;
    LocationManager mLm = null;
    /* public static */
    LocationListener mLL = null;
    Listener mL = null;
    String provider = null;

    Iterator<GpsSatellite> mIterator = null;

    ArrayList<Item> mList = new ArrayList<Item>();

    class Item
    {
        float mSnr;
        int mPrn;
        float mAzi;
        float mEle;

        public Item(float snr, int prn, float azi, float ele)
        {
            mSnr = snr;
            mPrn = prn;
            mAzi = azi;
            mEle = ele;

        }
    }

    /**
     * 界面申明
     */
    TextView lat_tv = null;
    TextView lon_tv = null;
    TextView hig_tv = null;
    TextView spe_tv = null;
    TextView utc_tv = null;
    Button back_bt = null;
    Button reset_bt = null;

    GPSSnrPrnView mSPview = null;
    GpsEarthView mEview = null;

    String lat_st = null;
    String lon_st = null;
    String utc_st = null;

    float hig_st = 0.0f;
    float spe_st = 0.0f;
    float s = 3.6f;
    float speed = 0.0f;
    private static int mPid = -1;
// boolea xxx=true;

    /**
	 * 
	 */
    boolean isLocation = false;
private boolean closeRun=true;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        mPid = android.os.Process.myPid();

        int h = getWindowManager().getDefaultDisplay().getHeight();
        int w = getWindowManager().getDefaultDisplay().getWidth();
        System.out.println(h + " ...." + w);
        System.out.println("dddddddffffff");

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        System.out.println("111");
        setContentView(R.layout.main);
        System.out.println("2222");
        mSPview = (GPSSnrPrnView) findViewById(R.id.gpssnrprnview);
        /* new Thread(new GameThread()).start(); */
        mEview = (GpsEarthView) findViewById(R.id.gpsearthview);

        /*
         * back_bt = (Button)findViewById(R.id.back); back_bt.setOnClickListener(onBackScreen); reset_bt =
         * (Button)findViewById(R.id.restart_bt); reset_bt.setOnClickListener(onReset); // lat_tv =
         * (TextView)findViewById(R.id.latitude_tv); lon_tv = (TextView)findViewById(R.id.longitude_tv); hig_tv =
         * (TextView)findViewById(R.id.height_tv); spe_tv = (TextView)findViewById(R.id.speed_tv); utc_tv =
         * (TextView)findViewById(R.id.UTC_tv);
         */

        initView();

        provider = LocationManager.GPS_PROVIDER;

        mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!isGpsOn(this)){
        	setGPSSwitch(GPSTest.this,true);
        }
        location = mLm.getLastKnownLocation(provider);

        mLL = new LocationListener()
        {

            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            public void onProviderDisabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            public void onLocationChanged(Location location)
            {
                // TODO Auto-generated method stub
                updateLocation(location);
            }
        };

        mLm.requestLocationUpdates(provider, 1000, 0, mLL);

        mL = new Listener()
        {

            @Override
            public void onGpsStatusChanged(int event)
            {

                if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){

		                GpsStatus xGpsStatus = mLm.getGpsStatus(null);
		
		                Iterable<GpsSatellite> iStatellite = xGpsStatus.getSatellites();
		                mIterator = iStatellite.iterator();
		                if (!mList.isEmpty())
		                {
		                    mList.clear();
		                }
		
		                while (mIterator.hasNext())
		                {
		
		                    GpsSatellite satellite = mIterator.next();
		                    float snr = satellite.getSnr();
		                    int prn = satellite.getPrn();
		                    float azi = satellite.getAzimuth();
		                    float ele = satellite.getElevation();
		
		                    mList.add(new Item(snr, prn, azi, ele));
		                }
		                /*
		                 * Collections.sort(mList, new Comparator<Item>(){
		                 * @Override public int compare(Item i1, Item i2) { // TODO Auto-generated method stub float pnr1 =
		                 * i1.mPrn; float pnr2 = i2.mPrn; if(pnr1 > pnr2) return 1; else if(pnr1 == pnr2) return 0; else return
		                 * -1; }});
		                 */
		
		                Collections.sort(mList, new Comparator<Item>()
		                {
		
		                    public int compare(Item i1, Item i2)
		                    {
		                        // TODO Auto-generated method stub
		                        float snr1 = i1.mSnr;
		                        float snr2 = i2.mSnr;
		                        if (snr1 < snr2)
		                            return 1;
		                        else if (snr1 == snr2)
		                            return 0;
		                        else
		                            return -1;
		                    }
		                });
		
		                mSPview.setMList(mList);
		                mSPview.setIsLocation(isLocation);
		
		                new Thread(new SPThread()).start();
		
		                mEview.setMList(mList);
		                mEview.setIsLocation(isLocation);
		                new Thread(new EarthThread()).start();

            }
            } 
        };

        mLm.addGpsStatusListener(mL);
    }

    protected void updateLocation(Location loc)
    {
        // TODO Auto-generated method stub
        if (!isLocation)
        {

            isLocation = true;
        }

        long mTime = loc.getTime();

        Date d = new Date(mTime);
        String s2 = timestampFormat.format(d);

        /*
         * Calendar calendar = new GregorianCalendar(); TimeZone.getTimeZone("Etc/UTC") calendar.setTimeInMillis(mTime);
         * int year = calendar.get(Calendar.YEAR); int month = calendar.get(Calendar.MONTH)+1 ; int day =
         * calendar.get(Calendar.DAY_OF_MONTH); int hour = calendar.get(Calendar.HOUR_OF_DAY); int minute =
         * calendar.get(Calendar.MINUTE); int second = calendar.get(Calendar.SECOND); String mT =
         * getResources().getString(R.string.utc)+year + "/" + month + "/" + day + "  " + hour + ":" + minute + ":" +
         * second;
         */

        if (loc != null)
        {
            if (loc.hasSpeed())
            {
                spe_st = loc.getSpeed();
                speed = spe_st * s;
                spe_tv.setText(getResources().getString(R.string.speed) + " " + speedFormat.format(speed)
                    + " " + getResources().getString(R.string.unit));
            }

            if (loc.hasAltitude())
            {
                hig_st = (float) loc.getAltitude();
                hig_tv.setText(getResources().getString(R.string.height) + " " + hightFormat.format(hig_st)
                    + " " + getResources().getString(R.string.meter));
            }

            lat_st =
                getResources().getString(R.string.latitude) + " "
                    + String.valueOf(sevenSigDigits.format(loc.getLatitude()));

            lat_tv.setText(lat_st);

            lon_st =
                getResources().getString(R.string.longitude) + " "
                    + String.valueOf(sevenSigDigits.format(loc.getLongitude()));
            lon_tv.setText(lon_st);

            utc_tv.setText(getResources().getString(R.string.utc) + " " + s2);

        }
    }

    private void initView()
    {
        // TODO Auto-generated method stub
        lat_tv = (TextView) findViewById(R.id.latitude_tv);
        lon_tv = (TextView) findViewById(R.id.longitude_tv);
        hig_tv = (TextView) findViewById(R.id.height_tv);
        spe_tv = (TextView) findViewById(R.id.speed_tv);
        utc_tv = (TextView) findViewById(R.id.UTC_tv);

        back_bt = (Button) findViewById(R.id.back);
        back_bt.setOnClickListener(onBackScreen);

        reset_bt = (Button) findViewById(R.id.restart_bt);
        reset_bt.setOnClickListener(onReset);
    }

    protected void clearLocationView()
    {
        // TODO Auto-generated method stub
        lat_tv.setText(getResources().getString(R.string.latitude));
        lon_tv.setText(getResources().getString(R.string.longitude));
        hig_tv.setText(getResources().getString(R.string.height));
        spe_tv.setText(getResources().getString(R.string.speed));
        utc_tv.setText(getResources().getString(R.string.utc));
    }

    protected OnClickListener onBackScreen = new OnClickListener()
    {

        public void onClick(View v)
        {
            // TODO Auto-generated method stub
            mLm.removeUpdates(mLL);
            mLm.removeGpsStatusListener(mL);
            finish();
            /* android.os.Process.killProcess(mPid); */

        }
    };

    protected OnClickListener onReset = new OnClickListener()
    {

        public void onClick(View v1)
        {
            // TODO Auto-generated method stub
        	Bundle extras = new Bundle();
        	extras.putBoolean("ephemeris", true);
			extras.putBoolean("position", true);
			extras.putBoolean("time", true);
			extras.putBoolean("iono", true);
			extras.putBoolean("utc", true);
			extras.putBoolean("health", true);
        	mLm.sendExtraCommand(
					LocationManager.GPS_PROVIDER, "delete_aiding_data", extras);
        	try{
        	Thread.sleep(500);
        	}catch (Exception e){
        		
        	}
            mLm.removeUpdates(mLL);
            mLm.removeGpsStatusListener(mL);
            isLocation = false;

            /*
             * mHandler.post(new Runnable() { public void run() { // TODO Auto-generated method stub
             * Log.i("jzx","============================>Runnable!"); clearLocationView(); mSPview.invalidate();//
             * disable! I don't why. } });
             */
            clearLocationView();
            if (!mList.isEmpty())
            {
                mList.clear();
            }

            mSPview.setMList(mList);
            mSPview.setIsLocation(isLocation);
            new Thread(new SPThread()).start();
            mEview.setMList(mList);
            mEview.setIsLocation(isLocation);
            new Thread(new EarthThread()).start();

            mLm.requestLocationUpdates(provider, 1000, 0, mLL);
            mLm.addGpsStatusListener(mL);

        }
    };

    
      @Override protected void onPause() { // TODO Auto-generated method stub 
    	  super.onPause(); 
    	  mLm.removeUpdates(mLL);
    	  android.os.Process.killProcess(mPid); 
      }
      
      @Override protected void onResume() { // TODO Auto-generated method stub 
    	  super.onResume();
      mLm.requestLocationUpdates(provider, 1000, 0, mLL); 
      }
     

    class SPThread implements Runnable
    {
        public void run()
        {

            // 使用postInvalidate可以直接在线程中更新界面
			if(closeRun){
			
			mSPview.postInvalidate();

			}
            
        }
    }

    class EarthThread implements Runnable
    {
        public void run()
        {

            // 使用postInvalidate可以直接在线程中更新界面
            mEview.postInvalidate();

        }
    }

    @Override
    protected void onDestroy()
    {
        if (null != mLm)
        {
            if (null != mLL)
            {
                mLm.removeUpdates(mLL);
            }
            if (null != mL)
            {
                mLm.removeGpsStatusListener(mL);
            }
        }
       closeRun=false;
        super.onDestroy();
    }
    
    private void setGPSSwitch(Context context,boolean isOn) {
		Intent gpsIntent = new Intent("com.android.settings.CHANGE_GPS_SWITCH");
	    gpsIntent.putExtra("gpsSwitch", isOn);
	    context.sendBroadcast(gpsIntent);
	    
	}
    
private boolean isGpsOn(Context context){
   return mLm.isProviderEnabled(LocationManager.GPS_PROVIDER);
}
}
