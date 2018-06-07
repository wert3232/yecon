package com.mtksetting;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LogoSetting extends Service{
	private static String TAG1 = "LogoSetting";
	private MyBinder myBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG1, "onBind");
		return myBinder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG1, "onCreate");

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG1, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG1, "onRebind");
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG1, "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG1, "onUnbind");

		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		public LogoSetting getService() {
			return LogoSetting.this;
		}
	}
	
	public int set_Logo(int position){
		return setLogo(position);
	}
	
	private final static String TAG = "logosetting native";
	
	static {
		// The runtime will add "lib" on the front and ".o" on the end of
		// the name supplied to loadLibrary.
		//Log.i(TAG1, "System.load(\"libLogo.so\");");
		System.loadLibrary("mtklogo");
		Log.i(TAG1,"load ok");
	}
	
	public static native int setLogo(int a);
}
