package com.yecon.carsetting.tuoxian;

import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.R;
import com.yecon.carsetting.wifi.Fragment_Wifi_search;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.Switch;

public class WifiSettings extends Activity  implements OnClickListener{
	private FragmentManager mFragmentManager;
	private static Context mContext;
	private Switch wifiSwitch;
	private WifiManager mWifiManager;
	private Fragment_Wifi_search fragment_Wifi_search;
	public final static int ON =   0x100001;
    public final static int OFF =  0x100002;
    public final static int AUTO = 0x100003;
    private AtTimerHelpr mAtTimerHelpr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuoxian_wifi);
		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setWifiSwitch(AUTO);
		//一汽要求,不自动流转 mAtTimerHelpr.start(10);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	private void initView() {
		wifiSwitch = (Switch)findViewById(R.id.wifiSwitch);
		Fragment fragment= getFragmentManager().findFragmentById(R.id.fragment_wifi);
		if(fragment != null && fragment instanceof Fragment_Wifi_search){
			fragment_Wifi_search = (Fragment_Wifi_search) fragment;
		}
		findViewById(R.id.back).setOnClickListener(this);
		wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	mWifiManager.setWifiEnabled(isChecked);
            }
        });
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		/** AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};*/
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}


	private void initData() {
		mFragmentManager = getFragmentManager();
		mContext = this;
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		IntentFilter filter=new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction("action.wifi.setting.password.change");
		registerReceiver(mReceiver, filter);
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}
	
	public void setWifiSwitch(int isOpen){
       if (isOpen == ON){
            wifiSwitch.setChecked(true);
       }else if(isOpen == OFF){
            wifiSwitch.setChecked(false);
       }else if(isOpen == AUTO){
           int wifiState = mWifiManager.getWifiState();
           if(wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING){
               wifiSwitch.setChecked(true);
           }else if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING){
               wifiSwitch.setChecked(false);
           }else{
               wifiSwitch.setChecked(false);
           }
       }
    }
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi状态变化
				int wifiState = mWifiManager.getWifiState();
	            if(wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING){
//	               wifiSwitch.setChecked(true);
	               setWifiSwitch(ON);
	               
	            }else if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING){
//	               wifiSwitch.setChecked(false);
	               setWifiSwitch(OFF);
	            }else{
//	               wifiSwitch.setChecked(false);
	               setWifiSwitch(OFF);
	            }
	            
	            if(wifiState == WifiManager.WIFI_STATE_ENABLED){
	            	if(fragment_Wifi_search != null){
	            		fragment_Wifi_search.setTimer_WifiStartScan();
	            	}
	            }
            }else if("action.wifi.setting.password.change".equals(intent.getAction())){
            	abortBroadcast();
            	//一汽要求,不自动流转 mAtTimerHelpr.reset();
            }
		}
	};
	
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
}
