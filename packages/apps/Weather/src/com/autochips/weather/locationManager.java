package com.autochips.weather;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class locationManager {
	private LocationManager mlocationManager;
	private String locationProvider;
	Location location;
	Context mContext;
	 private static locationManager sInstance;
	 public static locationManager getInstance() {
		 if(sInstance == null)
	        sInstance = new locationManager();
	      return sInstance;
	    }
	void createloacation(Context context) {
		mContext = context;
		mlocationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		//
		List<String> providers = mlocationManager.getProviders(true);
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			//
			locationProvider = LocationManager.GPS_PROVIDER;
		} 
		else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			//
			locationProvider = LocationManager.NETWORK_PROVIDER;
		} 
		else {

			return;
		}
		//
		location = mlocationManager.getLastKnownLocation(locationProvider);

		if (location != null) {
			Log.e("Map", "LastKnownLocation : Lat: " + location.getLatitude()
					+ " Lng: " + location.getLongitude());
		} else {
			//Toast.makeText(context, "LastKnownLocation is null",
				//	Toast.LENGTH_SHORT).show();
		}
		//
		mlocationManager.requestLocationUpdates(locationProvider,  1000*60*10/*毫秒，间隔时间*/, 10000 /*米*/	,locationListener);
	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle arg2) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			//
			if (location != null) {
				Log.e("Map","Location changed : Lat: " + location.getLatitude()
								+ " Lng: " + location.getLongitude());
				sInstance.location = location;
				queryWether();
			}

		}
	};

	public void removeUpdates() {
		mlocationManager.removeUpdates(locationListener);  
	}
	
	public void queryWether()
	{
		if (WeatherApp.IS_WIFI_CONNECTED) {
           // mContext.startService(new Intent(this, WeatherService.class));
			 Intent intent = new Intent(mContext,WeatherService.class); 
			 mContext.startService(intent);
        }
	}
	
	public Location getNewLocation(Context mContext)
	{
		if(locationProvider != null)
			location = mlocationManager.getLastKnownLocation(locationProvider);	
		else
		{
			mlocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	
			List<String> providers = mlocationManager.getProviders(true);
			if (providers.contains(LocationManager.GPS_PROVIDER)) {		
				locationProvider = LocationManager.GPS_PROVIDER;
			}
			else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {		
				locationProvider = LocationManager.NETWORK_PROVIDER;
			} 
			if(locationProvider != null)
				location = mlocationManager.getLastKnownLocation(locationProvider);	
		}
		return location;
	}
}
