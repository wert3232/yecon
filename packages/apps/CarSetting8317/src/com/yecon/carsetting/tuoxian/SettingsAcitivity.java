package com.yecon.carsetting.tuoxian;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.mcu.McuExternalConstant;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autochips.storage.EnvironmentATC;
import com.media.constants.MediaConstants;
import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.carsetting.ApplicationManage;

import android.mcu.McuManager;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog.OnConfirmListener;
import com.yecon.carsetting.fragment.Fragment_Prompt_dialog;
import com.yecon.carsetting.mcu.CarSettingsBootService;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class SettingsAcitivity extends Activity implements onOneButtonListener,
		onOneCheckBoxListener, OnListDlgListener,OnClickListener {
	static final String TAG = "carsetting";
	static Context mContext;
	FragmentManager mFragmentManager; 

	ArrayList<String> list_radio_area;
	private String[] stringarray_radio_area;
	List<Integer> intarray_radio_area;
	private int index_radio_area;

	int ID_OneButton[] = { R.id.set_general, R.id.set_datetime};
	int ID_CheckBox[] = { R.id.set_wifi};

	HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	MyBroadcast mBroadcastReceiver;
	private AtTimerHelpr mAtTimerHelpr;
	private TextView mBtnBack;
	
	
	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();		
	}

	void init_RadioPara() {

		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		ApplicationManage.getInstance().addActivity(this);
		setContentView(R.layout.activity_tuoxian_main);
		initView();
		initBroadcastReceiver();
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		/** AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};**/
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}

	private void initView() {
		mBtnBack = (TextView) findViewById(R.id.back);
		mBtnBack.setOnClickListener(this);
		findViewById(R.id.settings_item_data_time).setOnClickListener(this);
		findViewById(R.id.settings_item_file_manager).setOnClickListener(this);
		findViewById(R.id.settings_item_light).setOnClickListener(this);
		findViewById(R.id.settings_item_sound_balance).setOnClickListener(this);
		findViewById(R.id.settings_item_sound_config).setOnClickListener(this);
		findViewById(R.id.settings_item_sound_recover_factroy).setOnClickListener(this);
		findViewById(R.id.settings_item_sound_about).setOnClickListener(this);
		findViewById(R.id.settings_item_wifi).setOnClickListener(this);
		findViewById(R.id.settings_item_language).setOnClickListener(this);
		findViewById(R.id.settings_item_update).setOnClickListener(this);
		findViewById(R.id.settings_item_rear_mute).setOnClickListener(this);
		//Log.d("TEST","SystemProperties.get(persist.sys.rear.mute) "+SystemProperties.get("persist.sys.rear.mute"));
		if(SystemProperties.get("persist.sys.rear.mute").equals("1"))
			((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_unmute);
	}

	private class MyBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
//		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initBroadcastReceiver() {
		
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		/*case R.id.set_general:
			Function.onSet_general(mFragmentManager);
			break;*/
		default:
			break;
		}

	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		
	}

	@Override
	public void onCheckout(View view, boolean value) {
		

	}

	void setWifiEnable() {

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.settings_item_update:{
				Intent mIntent = new Intent(mContext, SystemUpdateActivity.class);
				mContext.startActivity(mIntent);
				}
				break;
			case R.id.settings_item_data_time:{
				Function.onSet_datetime(mContext);
				}
				break;
			case R.id.settings_item_light:{
				Intent mIntent = new Intent(mContext, LightSettingsAcitivity.class);
				mContext.startActivity(mIntent);	
				}
				break;
			case R.id.settings_item_sound_balance:{
				Function.onSet_SoundEffect(mContext,"balance");
				}
				break;
			case R.id.settings_item_sound_config:{
				Function.onSet_SoundEffect(mContext,"effect");	
				}
				break;
			case R.id.settings_item_sound_recover_factroy:{
				Intent mIntent = new Intent(mContext, TFactroySettingsActivity.class);
				mContext.startActivity(mIntent);
				}
				break;
			case R.id.settings_item_sound_about:{
				Intent mIntent = new Intent(mContext, DeviceAboutAcitivity.class);
				mContext.startActivity(mIntent);
				}	
				break;
			case R.id.settings_item_wifi:{
				Intent mIntent = new Intent(mContext, WifiSettings.class);
				mContext.startActivity(mIntent);	
			}
				break;
			case R.id.settings_item_language:{
				Intent mIntent = new Intent(mContext, LanguageAcitivity.class);
				mContext.startActivity(mIntent);	
			}
				break;
			case R.id.settings_item_file_manager:{
				Function.onStartActivity(mContext,"com.android.file", "com.adnroid.file.MyActivity");
			}
				break;
			case R.id.settings_item_rear_mute:{
				//Log.d("TEST","SystemProperties.get(persist.sys.rear.mute) "+SystemProperties.get("persist.sys.rear.mute"));
				if(SystemProperties.get("persist.sys.rear.mute").equals("1"))
					((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_mute);
				else
					((ImageView)(((FrameLayout)findViewById(R.id.settings_item_rear_mute)).getChildAt(0))).setImageResource(R.drawable.selector_settings_item_rear_unmute);
				Function.onSet_RearMute(mContext);
			}
				break;
		default:
			break;
		}
	}
	
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			L.e(MediaConstants.ACTION_SETTINGS_AUTO_FINISH);
			sendBroadcast(new Intent(MediaConstants.ACTION_SETTINGS_AUTO_FINISH));
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转 mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
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
