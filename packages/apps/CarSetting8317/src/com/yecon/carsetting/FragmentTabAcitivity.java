package com.yecon.carsetting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import com.yecon.carsetting.fragment.Fragment_List;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.mcu.CarSettingsBootService;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class FragmentTabAcitivity extends Activity implements onOneButtonListener,
		onOneCheckBoxListener, OnListDlgListener {
	static final String TAG = "carsetting";
	static Context mContext;
	FragmentManager mFragmentManager;
	private WifiManager mWifiManager;

	ArrayList<String> list_radio_area;
	private String[] stringarray_radio_area;
	List<Integer> intarray_radio_area;
	private int index_radio_area;

	int ID_OneButton[] = { R.id.set_general, R.id.set_datetime, R.id.set_wallpaper,
			R.id.set_navigation, R.id.set_video, R.id.set_swc, R.id.set_factory,
			R.id.set_sound_effect, R.id.set_radio_frequency, R.id.set_bt, R.id.set_gps_info,
			R.id.set_wifi, };
	int ID_CheckBox[] = { R.id.set_wifi, };

	HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	MyBroadcast mBroadcastReceiver;

	private void initData() {
		mContext = this;
		mFragmentManager = getFragmentManager();
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		init_RadioPara();
	}

	void init_RadioPara() {

		ArrayList<String> list_radio_area_all;
		String[] stringarray_radio_area_all;
		int[] intarray_radio_area_all;

		stringarray_radio_area_all = getResources().getStringArray(R.array.radio_locale_names);
		list_radio_area_all = XmlParse.getStringArray(XmlParse.support_radio_area);
		intarray_radio_area_all = getResources().getIntArray(R.array.radio_locale_values);

		List<String> list = new ArrayList<String>();
		intarray_radio_area = new ArrayList<Integer>();

		list_radio_area = new ArrayList<String>();
		int i = 0;
		for (String s : stringarray_radio_area_all) {
			if (SystemProperties.getBoolean(Tag.PERSYS_RADIO_AREA_ENABLE[i], true)) {
				list.add(stringarray_radio_area_all[i]);
				list_radio_area.add(list_radio_area_all.get(i));
				intarray_radio_area.add((Integer) intarray_radio_area_all[i]);
			}
			i++;
		}

		int count = list.size();
		stringarray_radio_area = new String[list.size()];
		for (int j = 0; j < count; j++) {
			stringarray_radio_area[j] = list.get(j);
		}

		String default_radio_area = SystemProperties.get(Tag.PERSYS_RADIO_AREA,
				XmlParse.default_radio_area);
		for (int k = 0; k < list_radio_area.size(); k++) {
			if (default_radio_area.equalsIgnoreCase(list_radio_area.get(k))) {
				index_radio_area = k;
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		ApplicationManage.getInstance().addActivity(this);
		setContentView(R.layout.fragment_main);
		initView();
		initBroadcastReceiver();
		
		View view = findViewById(R.id.factory_return);
		if(view != null){
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		}
	}

	private void initView() {

		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			mLayout_OneButton[i] = (HeaderLayout) findViewById(ID_OneButton[i]);
			mLayout_OneButton[i].setOneButtonListener(this);
			i++;
		}

		if (mLayout_OneButton[1] != null) {
			mLayout_OneButton[1].setPromptTitle(tzutil.getCurrentDate(mContext));
			mLayout_OneButton[1].setPromptTitle2(tzutil.getCurrentTime(mContext));
		}

		mLayout_OneButton[8].setPromptTitle(stringarray_radio_area[index_radio_area]);

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[3], true))
			((HeaderLayout) findViewById(R.id.set_swc)).setVisibility(View.GONE);
		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[4], true))
			((HeaderLayout) findViewById(R.id.set_wallpaper)).setVisibility(View.GONE);
		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[5], true))
			((HeaderLayout) findViewById(R.id.set_wifi)).setVisibility(View.GONE);
	}

	private class MyBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.yecon.carseting.close")) {
				Intent intentP = getIntent();
				overridePendingTransition(0, 0);
				intentP.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intentP);
			} else if (action.equals(Intent.ACTION_TIME_TICK)
					|| action.equals(Intent.ACTION_TIME_CHANGED)) {
				if (mLayout_OneButton[1] != null) {
					mLayout_OneButton[1].setPromptTitle(tzutil.getCurrentDate(mContext));
					mLayout_OneButton[1].setPromptTitle2(tzutil.getCurrentTime(mContext));
				}
			} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_UNKNOWN);
				Log.d(TAG, "wifiState: " + wifiState);
				if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
					((HeaderLayout) findViewById(R.id.set_wifi)).getOneCheckBox().setChecked(true);
				} else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
					((HeaderLayout) findViewById(R.id.set_wifi)).getOneCheckBox().setChecked(false);
				}
			} else if (action.equals(WifiManager.WIFI_AP_STATE_CHANGED_ACTION)) {
				int wifiApState = mWifiManager.getWifiApState();
				Log.d(TAG, "2 wifiApState: " + wifiApState);
				if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED) {
					// mTVHotspot.setSelected(true);
				} else if (wifiApState == WifiManager.WIFI_AP_STATE_DISABLED) {
					// mTVHotspot.setSelected(false);
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initBroadcastReceiver() {
		mBroadcastReceiver = new MyBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.yecon.carseting.close");
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.set_general:
			Function.onSet_general(mFragmentManager);
			break;
		case R.id.set_datetime:
			Function.onSet_datetime(mContext);
			break;
		case R.id.set_wallpaper:
			Function.onSet_Wallpaper(mContext);
			break;
		case R.id.set_navigation:
			Function.onSet_navigation(mContext);
			break;
		case R.id.set_video:
			Function.onSet_video(mContext);
			break;
		case R.id.set_swc:
			Function.onSet_LearnSteerWheel(mContext);
			break;
		case R.id.set_factory:
			Function.onSet_factory(mContext, mFragmentManager);
			break;
		case R.id.set_sound_effect:
			Function.onSet_SoundEffect(mContext);
			break;
		case R.id.set_radio_frequency:
			Fragment_List dialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_radio_area, index_radio_area);
			dialog.setOnItemClickListener(this);
			break;
		case R.id.set_bt:
			Function.onSet_bt(mFragmentManager, false);
			break;
		case R.id.set_gps_info:
			Function.onSet_GpsTest(mContext);
			break;
		case R.id.set_wifi:
			if (mWifiManager.getWifiState() != mWifiManager.WIFI_STATE_ENABLING
					&& mWifiManager.getWifiState() != mWifiManager.WIFI_STATE_DISABLING)
				Function.onSet_wifi(mFragmentManager);
			break;

		default:
			break;
		}

	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		if (id == R.id.set_radio_frequency) {
			index_radio_area = value;
			Function.setRadioCountry(mContext, intarray_radio_area.get(value));
			String str = stringarray_radio_area[value];
			mLayout_OneButton[8].setPromptTitle(str);
			SystemProperties.set(Tag.PERSYS_RADIO_AREA, list_radio_area.get(value));
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.set_wifi:
			setWifiEnable();
			break;

		default:
			break;
		}

	}

	void setWifiEnable() {

		if (mWifiManager.getWifiState() == mWifiManager.WIFI_STATE_ENABLING
				|| mWifiManager.getWifiState() == mWifiManager.WIFI_STATE_DISABLING) {
			return;
		} else if (mWifiManager.getWifiState() == mWifiManager.WIFI_STATE_ENABLED) {
			mWifiManager.setWifiEnabled(false);

		} else if (mWifiManager.getWifiState() == mWifiManager.WIFI_STATE_DISABLED
				|| mWifiManager.getWifiState() == mWifiManager.WIFI_STATE_UNKNOWN) {
			mWifiManager.setWifiApEnabled(null, false);
			mWifiManager.setWifiEnabled(true);
		}
	}

}
