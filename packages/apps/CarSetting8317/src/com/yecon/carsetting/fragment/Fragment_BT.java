package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.autochips.bluetooth.LocalBluetoothManager;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_Keyboard_all.OnKeyboardAllListener;
import com.yecon.carsetting.fragment.Fragment_Keyboard_num.OnKeyboardListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.XmlRW;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class Fragment_BT extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, OnKeyboardListener {

	private Context mContext;
	FragmentManager mFragmentManager;
	private LocalBluetoothManager mLocalManager;

	int ID_TextView[] = { R.id.set_bt_device, R.id.set_bt_pair, };
	int ID_CheckBox[] = { R.id.set_bt_auto_connect, R.id.set_bt_auto_answer, R.id.factory_bt_noice };

	HeaderLayout mLayout_TextView[] = new HeaderLayout[ID_TextView.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	String mStr_bt_device = XmlParse.bt_device;
	String mStr_bt_pair = XmlParse.bt_pair;
	boolean mEnable[] = new boolean[ID_CheckBox.length];
	boolean mIsFactory = false;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("")) {
			}
		}
	};

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		IntentFilter filter = new IntentFilter();
		mContext.registerReceiver(mBroadcastReceiver, filter);

		mLocalManager = LocalBluetoothManager.getInstance(mContext);
		if (mIsFactory) {
			mStr_bt_device = XmlParse.bt_device;
			mStr_bt_pair = XmlParse.bt_pair;
			mEnable[0] = XmlParse.bt_auto_connect == 1 ? true : false;
			mEnable[1] = XmlParse.bt_auto_answer == 1 ? true : false;
			mEnable[2] = XmlParse.bt_noice_enable == 1 ? true : false;
		} else {
			mStr_bt_device = mLocalManager.getBluetoothAdapter().getName();
			//mStr_bt_device = SystemProperties.get(Tag.PERSYS_BT_DEVICE, mStr_bt_device);
			mStr_bt_pair = SystemProperties.get(Tag.PERSYS_BT_PAIR, mStr_bt_pair);
			mEnable[0] = SystemProperties.getBoolean(Tag.PERSYS_BT_AUTO_CONNECT, false);
			mEnable[1] = SystemProperties.getBoolean(Tag.PERSYS_BT_AUTO_ANSWER, false);
			mEnable[2] = SystemProperties.getBoolean(Tag.PERSYS_BT_NOICE, false);
		}
	}

	public Fragment_BT(boolean isFactorySet) {
		mIsFactory = isFactorySet;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_bt, container, false);
		initView(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initView(View rootView) {

		int i = 0;
		String str[] = { mStr_bt_device, mStr_bt_pair };
		for (HeaderLayout layout : mLayout_TextView) {
			mLayout_TextView[i] = (HeaderLayout) rootView.findViewById(ID_TextView[i]);
			mLayout_TextView[i].setRightTitle(str[i]);
			mLayout_TextView[i].setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].getOneCheckBox().setChecked(mEnable[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_bt_noice:
			if (mIsFactory) {
				XmlParse.bt_noice_enable = value ? 1 : 0;
			} else {
				SystemProperties.set(Tag.PERSYS_BT_NOICE, value ? "true" : "false");
			}
			break;
		case R.id.set_bt_auto_connect:
			if (mIsFactory) {
				XmlParse.bt_auto_connect = value ? 1 : 0;
			} else {
				SystemProperties.set(Tag.PERSYS_BT_AUTO_CONNECT, value ? "true" : "false");
			}
			break;
		case R.id.set_bt_auto_answer:
			if (mIsFactory) {
				XmlParse.bt_auto_answer = value ? 1 : 0;
			} else {
				SystemProperties.set(Tag.PERSYS_BT_AUTO_ANSWER, value ? "true" : "false");
			}
			break;

		default:
			break;
		}
	}
	
	public static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (Tag.HANDLER_WRITE_XMLFILE == msg.what) {
				XmlRW.writeXMLFile(Tag.XML_PATH_USER);
			}
		}
	};

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_TextView[0]) {
			Fragment_Keyboard_all dialog = Function.onSet_keyboard_all(mFragmentManager);
			dialog.setOnKeyboardAllListener(new OnKeyboardAllListener() {
				@Override
				public void back(int id, String str) {
					// TODO Auto-generated method stub
					//if (mIsFactory) {
						XmlParse.bt_device = str;
					//} else {
						if (!mLocalManager.getBluetoothAdapter().setName(str))
							Log.e("carsetting_bt", "set local bluetooth adapter name fail");
						SystemProperties.set(Tag.PERSYS_BT_DEVICE, str);
						mHandler.sendEmptyMessage(Tag.HANDLER_WRITE_XMLFILE);
					//}
					mLayout_TextView[0].setRightTitle(str);
				}
			});
		} else if (view.getId() == ID_TextView[1]) {
			Fragment_Keyboard_num dialog = Function.onSet_keyboard_num(mFragmentManager);
			dialog.setOnKeyboardListener(this);
		}
	}

	@Override
	public void back(int id, String str) {
		// TODO Auto-generated method stub
		mLayout_TextView[1].setRightTitle(str);
		if (mIsFactory) {
			XmlParse.bt_pair = str;
		} else {
			SystemProperties.set(Tag.PERSYS_BT_PAIR, str);
		}
	}
}