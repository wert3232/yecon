package com.tuoxianui.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfileManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.autochips.bluetooth.LocalBluetoothProfileManager;
import com.autochips.storage.EnvironmentATC;
import com.yecon.common.YeconEnv;
import com.yecon.mediaprovider.YeconMediaStore;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class TopBar extends FrameLayout implements OnClickListener{
	private Context mContext;
	private TextView titleView;
	private ImageView usbStateView;
	private ImageView bluetoothView;
	private ImageView wifiLevelView;
	private View show_time;
	private StorageManager mStorageManager;
	private String topTitle;
	private int usbState = 0;
	private boolean hasSDCard = false;
	private boolean hasUSB = false;
	private SharedPreferences mPref;
	Handler handler;
	private WifiInfo mWifiInfo;
	private WifiManager mWifiManager;
	private TopBarBackCall backCall;
	private ObservableEmitter stateEmitter;
	private String[] storages = new String[] {
			YeconMediaStore.EXTERNAL_PATH,
			YeconMediaStore.EXT_SDCARD1_PATH,
			YeconMediaStore.EXT_SDCARD2_PATH,
			YeconMediaStore.UDISK1_PATH,
			YeconMediaStore.UDISK2_PATH,
			YeconMediaStore.UDISK3_PATH,
			YeconMediaStore.UDISK4_PATH,
			YeconMediaStore.UDISK5_PATH
	};
	public TopBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TuoXian);
		topTitle = ta.getString(R.styleable.TuoXian_top_title);
		init();
	}

	public TopBar(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public TopBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		/*handler.post(new Runnable() {			
			@Override
			public void run() {
				checkStorge();
				checkBluetooth();
			}
		});*/
		checkBluetooth();
		checkStorge();
	}

	@Override
	protected void onDetachedFromWindow() {
//		Log.e("TopBar","onDetachedFromWindow" + getContext().getApplicationInfo().packageName);
		stateEmitter.onComplete();
		getContext().unregisterReceiver(mStorageReceiver);
		getContext().unregisterReceiver(mBluetoothReceiver);
		getContext().unregisterReceiver(otherReceiver);
		getContext().unregisterReceiver(wifiIntentReceiver);
		getContext().unregisterReceiver(mTimeReceiver);
		super.onDetachedFromWindow();
	};
	private void init(){
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		IntentFilter mStorageFilter = new IntentFilter();
		IntentFilter mBluetoothFilter  = new IntentFilter();
		IntentFilter mWifiIntentFilter = new IntentFilter();   
		IntentFilter mOtherFilter  = new IntentFilter();
		IntentFilter mTimeFilter  = new IntentFilter();
		mStorageManager = (StorageManager) mContext.getSystemService(mContext.STORAGE_SERVICE);
		
		
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View view = layoutInflater.inflate(R.layout.top_bar, this);
		titleView = (TextView) view.findViewById(R.id.top_bar_title);
		usbStateView = (ImageView) view.findViewById(R.id.top_ic_usb);
		bluetoothView = (ImageView) view.findViewById(R.id.top_ic_bluetooth);
		wifiLevelView = (ImageView) view.findViewById(R.id.top_ic_wifi_level);
		wifiLevelView.setImageLevel(10);
		
		show_time = view.findViewById(R.id.show_time);
		show_time.setVisibility(SystemProperties.get("persist.sys.showtime","1").equals("1")?View.VISIBLE:View.INVISIBLE);
		
		view.findViewById(R.id.top_ic_return_home).setOnClickListener(this);
		
		//蓝牙
		mBluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mBluetoothFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		mBluetoothFilter.addAction(LocalBluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE);
		mBluetoothFilter.addAction(BluetoothProfileManager.ACTION_PROFILE_STATE_UPDATE);
		mBluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		mBluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		mBluetoothFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		
		//sd卡
		mStorageFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		mStorageFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mStorageFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mStorageFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		mStorageFilter.addDataScheme("file");
		
		//wifi
		mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mWifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); 
		mWifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION); 
		
		mTimeFilter.addAction("com.wesail.showtime");
		
		mBluetoothFilter.setPriority(100); 
		mStorageFilter.setPriority(100);
		
		mOtherFilter.addAction(DeviceStateMode.SHARED_PREFERENCES_NAME);
		
		getContext().registerReceiver(mStorageReceiver, mStorageFilter);
		getContext().registerReceiver(mBluetoothReceiver, mBluetoothFilter);
		getContext().registerReceiver(otherReceiver, mOtherFilter);
		getContext().registerReceiver(wifiIntentReceiver, mWifiIntentFilter);
		getContext().registerReceiver(mTimeReceiver, mTimeFilter);
		
		titleView.setText(getApplicationName());
		if(!TextUtils.isEmpty(topTitle)){
			titleView.setText(topTitle);
		}
		
//		mPref = mContext.getSharedPreferences(DeviceStateMode.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS + Context.MODE_WORLD_READABLE);
//		setListener();
//		setAsync();

		//RxJava 状态快速变化只取1秒内最后一个
		Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
				stateEmitter = observableEmitter;
			}
		})
				.throttleLast(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Integer>() {
					@Override
					public void onSubscribe(Disposable disposable) {

					}

					@Override
					public void onNext(Integer level) {
						wifiLevelView.setImageLevel(level);
						Log.d("TopBar","level: "  + level);
					}

					@Override
					public void onError(Throwable throwable) {

					}

					@Override
					public void onComplete() {

					}
				});
	}
	/*private void setListener(){
		mStorageManager.registerListener(eventListener);
	}
	private void setAsync(){
		HandlerThread thr = new HandlerThread("top bar thread");
		thr.start();
		handler = new Handler(thr.getLooper());
	}*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.top_ic_return_home) {
			/*Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);// 添加Category属性              
            mContext.startActivity(intent);*/
            if(backCall != null){
            	backCall.onHomeReturn();
            }else{
            	mContext.sendBroadcast(new Intent(Context.ACTION_RETURN_HOME));
            }
		}
	}
	public void setTitle(String titleStr){
		titleView.setText(titleStr);
	}
	
	BroadcastReceiver mTimeReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			LogE("TopBar",intent.getAction() + "receive");
			if("com.wesail.showtime".equals(intent.getAction())){
				show_time.setVisibility(SystemProperties.get("persist.sys.showtime","1").equals("1")?View.VISIBLE:View.INVISIBLE);
			}
		}
	};
	BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			LogE("TopBar",intent.getAction() + "receive");
			//蓝牙相关
			if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()) ){
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
			            BluetoothAdapter.ERROR);
			    switch (state) {
			    	case BluetoothAdapter.STATE_OFF:
			            LogE("STATE_OFF 手机蓝牙关闭");
			            break;
			        case BluetoothAdapter.STATE_TURNING_OFF:
			            LogE("STATE_TURNING_OFF 蓝牙正在关闭");
			            break;
			        case BluetoothAdapter.STATE_ON:
			            LogE("STATE_ON 蓝牙开启");
			            break;
			        case BluetoothAdapter.STATE_TURNING_ON:
			            LogE("STATE_TURNING_ON 蓝牙正在开启");
			            break;
			    }
			    LogE("ACTION_STATE_CHANGED");
			}
			else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())){
				LogE("ACTION_CONNECTION_STATE_CHANGED");
			}
			else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
				 int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
				 LogE(" ACTION_BOND_STATE_CHANGED   bondState: " + bondState);
			}
			else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction()) ){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    LogE(device.getName() + " ACTION_ACL_CONNECTED");
			    bluetoothView.setSelected(true);
			}
			else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction()) ){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			    LogE(device.getName() + " ACTION_ACL_DISCONNECTED");
			    bluetoothView.setSelected(false);
			}
		}};
	BroadcastReceiver mStorageReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			
			LogE("TopBar",intent.getAction() + "receive");
			
			//sd卡相关
			if(Intent.ACTION_MEDIA_EJECT.equals(intent.getAction()) ){
			    LogE("TopBar","ACTION_MEDIA_EJECT");
//			    setUsbState(intent.getAction());
			}
			else if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) ){
			    LogE("TopBar","ACTION_MEDIA_MOUNTED");
//			    setUsbState(intent.getAction());
			}
			else if(Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction()) ){
			    LogE("TopBar","ACTION_MEDIA_UNMOUNTED");
//			    setUsbState(intent.getAction());
			}
			else if(Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction()) ){
			    LogE("TopBar","ACTION_MEDIA_REMOVED");
//			    setUsbState(intent.getAction());
			}
			checkStorge();
		}
	};
	
	BroadcastReceiver otherReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			
			//sd卡相关
			if(DeviceStateMode.SHARED_PREFERENCES_NAME.equals(intent.getAction()) ){
				int state = intent.getIntExtra(DeviceStateMode.USB_STATE, -1);
				if(state == 0){
					setUsbState(0);
				}else if(state == 2){	
					setUsbState(2);
				}
			}
		}
	};
	
	private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {  
		@Override  
		public void onReceive(Context context, Intent intent) {
//			int wifi_state = intent.getIntExtra("wifi_state", 0);       
//			int level = Math.abs(((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getRssi());          
			
			String action = intent.getAction();
			mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			mWifiInfo = mWifiManager.getConnectionInfo();
			if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
				int wifiState = mWifiManager.getWifiState();
				switch (wifiState) {       
					case WifiManager.WIFI_STATE_DISABLING:
	
						break;
					case WifiManager.WIFI_STATE_DISABLED:
//						wifiLevelView.setImageLevel(10);
						stateEmitter.onNext(10);
						//Log.w("TopBar","WIFI_STATE_DISABLED level: 10");
						break;
					case WifiManager.WIFI_STATE_ENABLING:
//						wifiLevelView.setImageLevel(10);
						stateEmitter.onNext(10);
						//Log.w("TopBar","WIFI_STATE_ENABLING level: 10");
						break;
					case WifiManager.WIFI_STATE_ENABLED:
						if(isWifiConnect()) {
							int level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
//							wifiLevelView.setImageLevel(level);
							stateEmitter.onNext(level);
							//Log.i("TopBar","WIFI_STATE_ENABLED level:" + level);
						}else{
//							wifiLevelView.setImageLevel(10);
							stateEmitter.onNext(10);
							//Log.i("TopBar","WIFI_STATE_ENABLED level:" + 10);
						}
						break;
					case WifiManager.WIFI_STATE_UNKNOWN:
//						wifiLevelView.setImageLevel(10);
						stateEmitter.onNext(10);
						//Log.w("TopBar","WIFI_STATE_DISABLED level: 10");
						break;
					}
				Log.e("TopBar","WIFI_STATE_CHANGED_ACTION");
			}else if(WifiManager.RSSI_CHANGED_ACTION.equals(action)){
				if(isWifiConnect()){
					int level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
//					wifiLevelView.setImageLevel(level);
					stateEmitter.onNext(level);
					//Log.i("TopBar","RSSI_CHANGED_ACTION level:" + level);
				} else{
					stateEmitter.onNext(10);
				}
			}else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
				Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    
	            if (null != parcelableExtra) {    
	                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
	                State state = networkInfo.getState();  
	                boolean isConnected = state==State.CONNECTED;//当然，这边可以更精确的确定状态  
	                if(isConnected){
	                	int level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 5);
//						wifiLevelView.setImageLevel(level);
						stateEmitter.onNext(level);
						//Log.i("TopBar","NETWORK_STATE_CHANGED_ACTION level: " + level);
	                }else{  
//	                	wifiLevelView.setImageLevel(10);
						stateEmitter.onNext(10);
						//Log.w("TopBar","NETWORK_STATE_CHANGED_ACTION level: 10");
	                }
	                Log.i("TopBar","NETWORK_STATE_CHANGED_ACTION: " + mWifiInfo.getRssi() + isConnected);
	            }	
			}
		}
	}; 

	
	/*StorageEventListener eventListener = new StorageEventListener() {
		@Override
		public void onStorageStateChanged(final String path,final String oldState,final String newState) {
			LogE("TopBar"," path:" + path + "  oldState : " + oldState + "  newState: " + newState);
			handler.post(new Runnable() {			
				@Override
				public void run() {
					//setUsbState(newState);
					checkStorge();
				}
			});
		}
		@Override
		public void onUsbMassStorageConnectionChanged(boolean connected) {
			LogE("TopBar"," onUsbMassStorageConnectionChanged:" + connected);
		}
	};*/
	
	
	//set usb ico
	private void setUsbState(int state){
		usbState = state;
		LogE("setUsbState "+state);
		if(usbStateView == null) return;
		LogE("setUsbState PERSIST_LAST_STORAGE_DEVICE "+SystemProperties.getInt(Context.PERSIST_LAST_STORAGE_DEVICE, 0));
		switch (state) {
		case 0:
			usbStateView.getDrawable().setLevel(0);
			break;
		case 1:
			usbStateView.getDrawable().setLevel(1);
			break;
		case 2:
			usbStateView.getDrawable().setLevel(2);
			if(SystemProperties.getInt(Context.PERSIST_LAST_STORAGE_DEVICE, 0) == 2){	
				usbStateView.getDrawable().setLevel(2);
			}else if(SystemProperties.getInt(Context.PERSIST_LAST_STORAGE_DEVICE, 0) == 3){
				usbStateView.getDrawable().setLevel(3);
			}else{
				hasSDCard = false;
				hasUSB = false;
				EnvironmentATC env = new EnvironmentATC(mContext);
				for (String storage : storages) {
					boolean checkResult = YeconEnv.checkStorageExist(env, storage);
					LogE(storage + " : " + checkResult);
					if(checkResult){
						if(storage.matches("(.*)ext_sdcard(.*)")){
							hasSDCard = checkResult;
						}
						if(storage.matches("(.*)udisk(.*)")){
							hasUSB = checkResult;
						}
					}
				}
				if(hasSDCard)
					usbStateView.getDrawable().setLevel(3);
			}
			break;
		default:
			break;
		}
	}
	
	private void setUsbState(String stateStr){
		LogE("ExternalStorageState", stateStr);
		if		(Environment.MEDIA_REMOVED.equals(stateStr) ||					//不存在存储媒体
				Intent.ACTION_MEDIA_REMOVED.equals(stateStr) ||				//扩展介质被移除
				Intent.ACTION_MEDIA_EJECT.equals(stateStr) 	||			//用户想要移除扩展介质
				"bad_removal".equalsIgnoreCase(stateStr)
				){
			LogE("TopBar"," bad_removal: 0");
			setUsbState(0);
		}else if(Environment.MEDIA_MOUNTED.equals(stateStr) ||				//存储媒体存在并具有读/写权限 
				Intent.ACTION_MEDIA_MOUNTED.equals(stateStr)||				//扩展介质被插入，而且已经被挂载
				"mounted".equalsIgnoreCase(stateStr)
				){
			LogE("TopBar"," mounted: 2");
			setUsbState(2);
		}
		else if(//Environment.MEDIA_UNMOUNTED.equals(stateStr) || 			//存储媒体存在，但是没有被挂载
				//Intent.ACTION_MEDIA_UNMOUNTED.equals(stateStr)				//扩展介质存在，但是还没有被挂载	
				"checking".equalsIgnoreCase(stateStr)
				){
			LogE("TopBar"," checking: 1");
			setUsbState(0);
		}
	}
	private void setUsbState(boolean hasSDCard,boolean hasUSB){
		if(hasSDCard || hasUSB){
			setUsbState(2);
		}else
			setUsbState(0);
	}
	
	//check sd or Disk state
	private void checkStorge(){
		hasSDCard = false;
		hasUSB = false;
		EnvironmentATC env = new EnvironmentATC(mContext);
		for (String storage : storages) {
			boolean checkResult = YeconEnv.checkStorageExist(env, storage);
			LogE(storage + " : " + checkResult);
			if(checkResult){
				if(storage.matches("(.*)ext_sdcard(.*)")){
					hasSDCard = checkResult;
				}
				if(storage.matches("(.*)udisk(.*)")){
					hasUSB = checkResult;
				}
			}
		}
		LogE("" + hasUSB +" " + hasSDCard);
		setUsbState(hasSDCard,hasUSB);
	}
	
	//check bluetooth state
	private void checkBluetooth(){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		int connectState = adapter.getConnectionState();
		switch (connectState) {
			case BluetoothProfile.STATE_DISCONNECTING:
			case BluetoothProfile.STATE_DISCONNECTED:
				bluetoothView.setSelected(false);
				break;
			case BluetoothProfile.STATE_CONNECTED:
				bluetoothView.setSelected(true);
				break;	
			default:
				break;
		}
	}
	public String getApplicationName() { 
		PackageManager packageManager = null;
		String applicationName = "";
		try {
			packageManager = mContext.getApplicationContext().getPackageManager(); 
			applicationName = (String) packageManager.getApplicationLabel(mContext.getApplicationInfo()); 
		} catch (Exception e) {
			LogE(this.getClass().getName(), e.toString());
		}
		return applicationName; 
	}
	
	private void LogE(String tag,String msg){
		boolean isOpen= false;
		if(isOpen){
			Log.e(tag, msg);
		}
	}
	private void LogE(String msg){
			LogE("TopBar", msg);
	}
	
	public interface TopBarBackCall{
		public void onHomeReturn();
	}
	
	public void setTopBarBackCall(TopBarBackCall backCall){
		this.backCall = backCall;
	}

	public boolean isWifiConnect() {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifiInfo.isConnected();
	}

}
