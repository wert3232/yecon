package com.yecon.carsetting.fragment;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoRadioButtonListener;

public class factory_Fragment_LcdConfig extends DialogFragment implements onOneButtonListener,onTwoRadioButtonListener, OnListDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	int ID_OneButton[] = { R.id.factory_lcd_resolution, R.id.factory_tft_drive_current, 
			R.id.factory_lvds_drive_current, R.id.factory_dithering_output, };
	
	int ID_TwoRadio[] = { R.id.factory_lcd_type, R.id.factory_lvds_bit,};
	
	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	private HeaderLayout mLayout_TwoRadio[] = new HeaderLayout[ID_TwoRadio.length];

	ArrayList<String> list_lcd_resolution;
	ArrayList<String> list_ttf_drive_current;
	ArrayList<String> list_lvds_drive_current;
	ArrayList<String> list_dithering_output;

	private String[] stringarray_lcd_resolution;
	private String[] stringarray_tft_drive_current;
	private String[] stringarray_lvds_drive_current;
	private String[] stringarray_dithering_output;
	
	private int index_lcd_resolution;
	private int index_tft_drive_current;
	private int index_lvds_drive_current;
	private int index_dithering_output;

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

		//获取LCD分辨率
		stringarray_lcd_resolution = getResources().getStringArray(R.array.Lcd_Resolution);
		list_lcd_resolution = XmlParse.getStringArray(XmlParse.support_lcd_resolution);
		for (int i = 0; i < list_lcd_resolution.size(); i++) {
			if (XmlParse.default_lcd_resolution.equalsIgnoreCase(list_lcd_resolution.get(i))) {
				index_lcd_resolution = i;
				break;
			}
		}
		
		//tft驱动电流
		stringarray_tft_drive_current = getResources().getStringArray(R.array.tft_drive_current);
		list_ttf_drive_current = XmlParse.getStringArray(XmlParse.support_tft_drive_current);
		for (int i = 0; i < list_ttf_drive_current.size(); i++) {
			if (XmlParse.default_tft_drive_current.equalsIgnoreCase(list_ttf_drive_current.get(i))) {
				index_tft_drive_current = i;
				break;
			}
		}
		
		//lvds驱动电流
		stringarray_lvds_drive_current = getResources().getStringArray(R.array.lvds_drive_current);
		list_lvds_drive_current = XmlParse.getStringArray(XmlParse.support_lvds_drive_current);
		for (int i = 0; i < list_lvds_drive_current.size(); i++) {
			if (XmlParse.default_lvds_drive_current.equalsIgnoreCase(list_lvds_drive_current.get(i))) {
				index_tft_drive_current = i;
				break;
			}
		}
		
		//dithering输出控制
		stringarray_dithering_output = getResources().getStringArray(R.array.dithering_output);
		list_dithering_output = XmlParse.getStringArray(XmlParse.support_dithering_output);
		for (int i = 0; i < list_dithering_output.size(); i++) {
			if (XmlParse.default_dithering_output.equalsIgnoreCase(list_dithering_output.get(i))) {
				index_tft_drive_current = i;
				break;
			}
		}
	}

	public factory_Fragment_LcdConfig() {

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
		mRootView = inflater.inflate(R.layout.factory_fragment_lcd, container, false);
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

	/**
	 * 根据当前是TTL还是LVDS，来决定tft驱动电流还是lvds驱动电流
	 */
	void enableDriveCurrent() {
		if (XmlParse.default_lcd_type.equalsIgnoreCase("ttl")) {
			((HeaderLayout) mRootView.findViewById(R.id.factory_lvds_drive_current))
			.setEnabled(false);
			((HeaderLayout) mRootView.findViewById(R.id.factory_tft_drive_current))
			.setEnabled(true);
			
			((HeaderLayout) mRootView.findViewById(R.id.factory_lvds_bit))
			.setEnabled(false);
		}else if (XmlParse.default_lcd_type.equalsIgnoreCase("lvds")) {
			((HeaderLayout) mRootView.findViewById(R.id.factory_lvds_drive_current))
			.setEnabled(true);
			((HeaderLayout) mRootView.findViewById(R.id.factory_tft_drive_current))
			.setEnabled(false);
			
			((HeaderLayout) mRootView.findViewById(R.id.factory_lvds_bit))
			.setEnabled(true);
		}
	}
	private void initView(View rootView) {

		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			layout = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			layout.setOneButtonListener(this);
			i++;
		}
		
		i = 0;
		for (HeaderLayout layout : mLayout_TwoRadio) {
			layout = (HeaderLayout) rootView.findViewById(ID_TwoRadio[i]);
			layout.setTwoRadioButtonListener(this);
			i++;
		}

		//设置当前的显示值，根据index
		((HeaderLayout) rootView.findViewById(R.id.factory_lcd_resolution))
				.setPromptTitle(stringarray_lcd_resolution[index_lcd_resolution]);
		((HeaderLayout) rootView.findViewById(R.id.factory_tft_drive_current))
				.setPromptTitle(stringarray_tft_drive_current[index_tft_drive_current]);
		((HeaderLayout) rootView.findViewById(R.id.factory_lvds_drive_current))
				.setPromptTitle(stringarray_lvds_drive_current[index_lvds_drive_current]);
		((HeaderLayout) rootView.findViewById(R.id.factory_dithering_output))
				.setPromptTitle(stringarray_dithering_output[index_dithering_output]);

		// //////////////////////////////////////////////////////////////////////////////////////////
		((HeaderLayout) rootView.findViewById(R.id.factory_lcd_type))
				.setRadioCheck(XmlParse.default_lcd_type.equalsIgnoreCase("lvds") ? 2 : 1);
		((HeaderLayout) rootView.findViewById(R.id.factory_lvds_bit))
				.setRadioCheck(XmlParse.default_lvds_bit.equalsIgnoreCase("8") ? 2 : 1);		
		
		enableDriveCurrent();
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_lcd_resolution:				//lcd分辨率
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_lcd_resolution,
					index_lcd_resolution);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_tft_drive_current:			//tft驱动电流
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_tft_drive_current,
					index_tft_drive_current);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_lvds_drive_current:		//lvds驱动电流
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_lvds_drive_current,
					index_lvds_drive_current);
			mDialog.setOnItemClickListener(this);
			break;
			
		case R.id.factory_dithering_output:		//dithering输出控制
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_dithering_output,
					index_dithering_output);
			mDialog.setOnItemClickListener(this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onLeftRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_lcd_type:	//lcd接口类型  ttf lvds
			XmlParse.default_lcd_type = "ttl";
			enableDriveCurrent();
			break;
		case R.id.factory_lvds_bit:	//lvds数据位宽
			XmlParse.default_lvds_bit = "6";
			break;		
		default:
			break;
		}
	}

	@Override
	public void onRightRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_lcd_type:	//lcd接口类型  ttf lvds
			XmlParse.default_lcd_type = "lvds";
			enableDriveCurrent();
			break;
		case R.id.factory_lvds_bit:	//lvds数据位宽
			XmlParse.default_lvds_bit = "8";
			break;
		default:
			break;
		}
	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {

		/*
		 * case R.id.factory_car_type: ((HeaderLayout)
		 * mRootView.findViewById(R.id.factory_car_type))
		 * .setPromptTitle(stringarray_car_type[value]);
		 * XmlParse.default_car_type = list_car_type.get(value); index_car_type
		 * = value;
		 * McuMethodManager.getInstance(mContext).setMcuParam_cantype(index_car_type
		 * ); break;
		 */

		case R.id.factory_lcd_resolution:
			((HeaderLayout) mRootView.findViewById(R.id.factory_lcd_resolution))
					.setPromptTitle(stringarray_lcd_resolution[value]);
			XmlParse.default_lcd_resolution = list_lcd_resolution.get(value);
			index_lcd_resolution = value;
			break;

		case R.id.factory_tft_drive_current:
			((HeaderLayout) mRootView.findViewById(R.id.factory_tft_drive_current))
					.setPromptTitle(stringarray_tft_drive_current[value]);
			XmlParse.default_tft_drive_current = list_ttf_drive_current.get(value);
			index_tft_drive_current = value;
			break;

		case R.id.factory_lvds_drive_current:
			((HeaderLayout) mRootView.findViewById(R.id.factory_lvds_drive_current))
					.setPromptTitle(stringarray_lvds_drive_current[value]);
			XmlParse.default_lvds_drive_current =  list_lvds_drive_current.get(value);
			index_lvds_drive_current = value;
			break;

		case R.id.factory_dithering_output:
			((HeaderLayout) mRootView.findViewById(R.id.factory_dithering_output))
					.setPromptTitle(stringarray_dithering_output[value]);
			XmlParse.default_dithering_output = list_dithering_output.get(value);
			index_dithering_output = value;
			break;
		default:
			break;
		}
	}

	private void buildProgressDialog(int id, String title, int progress, int pos) {
		Fragment_Progress Dialog = new Fragment_Progress();
		Dialog.initData(id, title, progress, pos);
		Dialog.show(mFragmentManager, title);
	}

}