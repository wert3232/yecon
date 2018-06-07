package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_BackCar extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, OnListDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	private String[] stringarray_backcar_mirror;

	int ID_OneButton[] = { R.id.factory_backcar_mirror };
	int ID_OneCheck[] = { R.id.factory_backcar_enable, R.id.factory_backcar_track,
			R.id.factory_backcar_radar, R.id.factory_backcar_mute, };

	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	private HeaderLayout mLayout_OneCheck[] = new HeaderLayout[ID_OneCheck.length];

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
		stringarray_backcar_mirror = getResources().getStringArray(R.array.mirror_flip_names);
	}

	public factory_Fragment_BackCar() {

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
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
		mRootView = inflater.inflate(R.layout.factory_fragment_backcar, container, false);
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
		for (HeaderLayout layout : mLayout_OneButton) {
			mLayout_OneButton[i] = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			mLayout_OneButton[i].setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_OneCheck) {
			mLayout_OneCheck[i] = (HeaderLayout) rootView.findViewById(ID_OneCheck[i]);
			mLayout_OneCheck[i].setOneCheckBoxListener(this);
			i++;
		}

		((HeaderLayout) rootView.findViewById(R.id.factory_backcar_mirror))
				.setPromptTitle(stringarray_backcar_mirror[XmlParse.backcar_mirror]);

		((HeaderLayout) rootView.findViewById(R.id.factory_backcar_enable)).getOneCheckBox()
				.setChecked(XmlParse.backcar_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_backcar_track)).getOneCheckBox()
				.setChecked(XmlParse.backcar_guidelines == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_backcar_radar)).getOneCheckBox()
				.setChecked(XmlParse.backcar_radar == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_backcar_mute)).getOneCheckBox()
				.setChecked(XmlParse.backcar_mute == 1 ? true : false);
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_backcar_mirror:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_backcar_mirror, XmlParse.backcar_mirror);
			mDialog.setOnItemClickListener(this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_backcar_enable:
			XmlParse.backcar_enable = (value == true ? 1 : 0);
			break;
		case R.id.factory_backcar_track:
			XmlParse.backcar_guidelines = (value == true ? 1 : 0);
			break;
		case R.id.factory_backcar_radar:
			XmlParse.backcar_radar = (value == true ? 1 : 0);
			break;
		case R.id.factory_backcar_mute:
			XmlParse.backcar_mute = (value == true ? 1 : 0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {

		case R.id.factory_backcar_mirror:
			((HeaderLayout) mRootView.findViewById(R.id.factory_backcar_mirror))
					.setPromptTitle(stringarray_backcar_mirror[value]);
			XmlParse.backcar_mirror = value;
			setMcuSystemPara();
			break;
		default:
			break;

		}
	}

	private void setMcuSystemPara() {
		McuMethodManager.SystemParam para = new McuMethodManager.SystemParam();
		para.brightness = XmlParse.rgb_video[0][0];
		para.contrast = XmlParse.rgb_video[0][1];
		para.hue = XmlParse.rgb_video[0][2];
		para.saturation = XmlParse.rgb_video[0][3];
		para.backlight = SystemProperties.getInt(Tag.PERSYS_BKLIGHT[0],
				XmlParse.backlight[0] * 255 / 100);
		para.backcarMirror = XmlParse.backcar_mirror;
		McuMethodManager.getInstance(mContext).setMcuSystemParam(para);
	}

}