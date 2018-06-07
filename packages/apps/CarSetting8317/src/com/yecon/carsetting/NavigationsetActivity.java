package com.yecon.carsetting;

import static android.constant.YeconConstants.*;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioService;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;

import com.yecon.carsetting.DateBase.MapListDB;
import com.yecon.carsetting.bean.AppInfo;
import com.yecon.carsetting.fragment.Fragment_MapAppSelect;
import com.yecon.carsetting.fragment.Fragment_MapAppSelect.onCheckBoxListener;
import com.yecon.carsetting.unitl.ArraySetting;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoButtonListener;

public class NavigationsetActivity extends Activity implements onCheckBoxListener,
		onOneButtonListener, onOneCheckBoxListener, onTwoButtonListener {

	Context mContext;
	FragmentManager mFragmentManager;
	AudioManager mAudioManager;
	int ID_Button[] = { R.id.set_nav_maps_list };
	int ID_TwoButton[] = { R.id.set_nav_maps_select, R.id.set_nav_volume_media_front,
			R.id.set_nav_volume_media_back, R.id.set_nav_volume_gis, R.id.set_nav_volume_backcar,
			R.id.set_nav_volume_bt };
	int ID_CheckBox[] = { R.id.set_nav_volume_remix, R.id.set_nav_volume_listen, };

	private static final int[] STREAM_TYPE = new int[] { AudioManager.STREAM_MUSIC,
			AudioManager.STREAM_MUSIC, AudioManager.STREAM_GIS, AudioManager.STREAM_BACKCAR,
			AudioManager.STREAM_BLUETOOTH_SCO, };
	int volume_media_values[] = { AudioManager.DEFAULT_STREAM_VOLUME[AudioManager.STREAM_MUSIC],
			AudioManager.DEFAULT_STREAM_VOLUME[AudioManager.STREAM_MUSIC],
			AudioManager.DEFAULT_STREAM_VOLUME[AudioManager.STREAM_GIS],
			AudioManager.DEFAULT_STREAM_VOLUME[AudioManager.STREAM_BACKCAR],
			AudioManager.DEFAULT_STREAM_VOLUME[AudioManager.STREAM_BLUETOOTH_SCO] };
	static final int volumeMax = AudioService.volumeMax;

	private int mapIndex = 0;
	private MapListDB mMapListDB;
	List<AppInfo> mAppList = new ArrayList<AppInfo>();
	boolean mVolumeRemixEnable;
	boolean mVolumeListenEnable;

	HeaderLayout mLayout_Button[] = new HeaderLayout[ID_Button.length];
	HeaderLayout mLayout_TwoButton[] = new HeaderLayout[ID_TwoButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};

	private void initData() {
		mContext = this;
		tzutil.initSharePF(mContext);
		mFragmentManager = getFragmentManager();
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		Fragment_MapAppSelect.setCheckBoxListener(this);
		mMapListDB = new MapListDB(mContext);
		mAppList = mMapListDB.selectAppInfo();
		int tmpInt = tzutil.uiState.getInt(ArraySetting.Nav_map_list_index_tag, 0);
		mapIndex = tmpInt >= mAppList.size() ? mAppList.size() - 1 : tmpInt;

		IntentFilter filter = new IntentFilter(Tag.ACTION_BRIGHTNESS_CHANGED);
		mContext.registerReceiver(mBroadcastReceiver, filter);

		int i = 0;
		for (int value : volume_media_values) {
			volume_media_values[i] = mAudioManager.getStreamVolume(STREAM_TYPE[i]);
			i++;
		}
	}

	private void initView() {
		int i = 0;
		for (HeaderLayout layout : mLayout_Button) {
			mLayout_Button[i] = (HeaderLayout) findViewById(ID_Button[i]);
			mLayout_Button[i].setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_TwoButton) {
			mLayout_TwoButton[i] = (HeaderLayout) findViewById(ID_TwoButton[i]);
			mLayout_TwoButton[i].setTwoButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

		boolean unifiedVolumeAdjust = SystemProperties.getBoolean(
				PROPERTY_KEY_FUNC_UNIFIED_VOLUME_ADJUST, false);

		mLayout_TwoButton[0]
				.setMiddleTitle(isExistMap() ? mAppList.get(mapIndex).getAppName() : null);
		for (int j = 1; j < mLayout_TwoButton.length; j++) {
			mLayout_TwoButton[j].setMiddleTitle(volume_media_values[j - 1] + "");
			if (unifiedVolumeAdjust)
				mLayout_TwoButton[j].setVisibility(View.GONE);
		}

		mVolumeRemixEnable = SystemProperties.getBoolean(Tag.PERSYS_NAVI_REMIX, false);
		mLayout_CheckBox[0].getOneCheckBox().setChecked(mVolumeRemixEnable);
		mVolumeListenEnable = SystemProperties.getBoolean(Tag.PERSYS_NAVI_LISTEN, false);
		mLayout_CheckBox[1].getOneCheckBox().setChecked(mVolumeListenEnable);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_navigationset);
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_Button[0]) {
			Function.onSet_MapList(mContext, mFragmentManager);
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_CheckBox[0]) {
			nav_volume_remix(value);
		} else if (view.getId() == ID_CheckBox[1]) {
			nav_volume_listen(value);
		}
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_TwoButton[0]) {
			if (isExistMap()) {
				nav_maps_select(++mapIndex);
			}
		} else if (view.getId() == ID_TwoButton[1]) {
			set_nav_volume_media(0, --volume_media_values[0]);
		} else if (view.getId() == ID_TwoButton[2]) {
			set_nav_volume_media(1, --volume_media_values[1]);
		} else if (view.getId() == ID_TwoButton[3]) {
			set_nav_volume_media(2, --volume_media_values[2]);
		} else if (view.getId() == ID_TwoButton[4]) {
			set_nav_volume_media(3, --volume_media_values[3]);
		} else if (view.getId() == ID_TwoButton[5]) {
			set_nav_volume_media(4, --volume_media_values[4]);
		}

	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_TwoButton[0]) {
			if (isExistMap()) {
				nav_maps_select(--mapIndex);
			}
		} else if (view.getId() == ID_TwoButton[1]) {
			set_nav_volume_media(0, ++volume_media_values[0]);
		} else if (view.getId() == ID_TwoButton[2]) {
			set_nav_volume_media(1, ++volume_media_values[1]);
		} else if (view.getId() == ID_TwoButton[3]) {
			set_nav_volume_media(2, ++volume_media_values[2]);
		} else if (view.getId() == ID_TwoButton[4]) {
			set_nav_volume_media(3, ++volume_media_values[3]);
		} else if (view.getId() == ID_TwoButton[5]) {
			set_nav_volume_media(4, ++volume_media_values[4]);
		}
	}

	@Override
	public void onCheckedChanged() {
		// TODO Auto-generated method stub
		mAppList = mMapListDB.selectAppInfo();
		if (mAppList.size() > 0) {
			mLayout_TwoButton[0].getMiddleTitle().setVisibility(View.VISIBLE);
			mLayout_TwoButton[0].setMiddleTitle(mAppList.get(0).getAppName());
			mapIndex = 0;
			SystemProperties.set("persist.sys.maps", mAppList.get(mapIndex).packageName + "#"
					+ mAppList.get(mapIndex).className);
		} else {
			SystemProperties.set("persist.sys.maps", "nothing");
			mLayout_TwoButton[0].getMiddleTitle().setVisibility(View.INVISIBLE);
		}
	}

	public boolean isExistMap() {
		return mAppList.size() > 0 ? true : false;
	}

	private void nav_maps_select(int value) {
		mapIndex = value;
		if (mapIndex >= mAppList.size())
			mapIndex = mAppList.size() - 1;
		else if (mapIndex < 0)
			mapIndex = 0;
		SystemProperties.set("persist.sys.maps", mAppList.get(mapIndex).packageName + "#"
				+ mAppList.get(mapIndex).className);
		tzutil.saveIntValue(ArraySetting.Nav_map_list_index_tag, mapIndex);
		mLayout_TwoButton[0].setMiddleTitle(mAppList.get(mapIndex).getAppName());
	}

	private void set_nav_volume_media(int streamType, int streamValue) {
		volume_media_values[streamType] = streamValue;
		if (volume_media_values[streamType] <= 0)
			volume_media_values[streamType] = 0;
		else if (volume_media_values[streamType] >= volumeMax)
			volume_media_values[streamType] = volumeMax;
		mAudioManager.setStreamVolume(STREAM_TYPE[streamType], streamValue, 0);
		mLayout_TwoButton[streamType + 1].setMiddleTitle(streamValue + "");
	}

	private void nav_volume_remix(boolean mValues) {
		mVolumeRemixEnable = mValues;
		SystemProperties.set(Tag.PERSYS_NAVI_REMIX, mVolumeRemixEnable ? "true" : "false");
		if (mValues && mVolumeListenEnable) {
			mVolumeListenEnable = false;
			SystemProperties.set(Tag.PERSYS_NAVI_LISTEN, mVolumeListenEnable ? "true" : "false");
			mLayout_CheckBox[1].getOneCheckBox().setChecked(mVolumeListenEnable);
		}
	}

	private void nav_volume_listen(boolean mValues) {
		mVolumeListenEnable = mValues;
		SystemProperties.set(Tag.PERSYS_NAVI_LISTEN, mValues ? "true" : "false");
		if (mValues && mVolumeRemixEnable) {
			mVolumeRemixEnable = false;
			SystemProperties.set(Tag.PERSYS_NAVI_REMIX, mVolumeRemixEnable ? "true" : "false");
			mLayout_CheckBox[1].getOneCheckBox().setChecked(mVolumeRemixEnable);
		}
	}

}
