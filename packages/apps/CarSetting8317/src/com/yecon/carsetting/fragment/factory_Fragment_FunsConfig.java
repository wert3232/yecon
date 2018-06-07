package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_FunsConfig extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener {
	private Context mContext;
	FragmentManager mFragmentManager;
	private final int DVR_USB_ID = 10;
	private final int DVR_UART_ID = 11;

	int ID_OneButton[] = { R.id.factory_funs_other, };
	final int ID_CheckBox[] = { R.id.DVD, R.id.Gps, R.id.FM, R.id.AVIN, R.id.AVIN2, R.id.TV, R.id.IPOD,
			R.id.Music, R.id.Video, R.id.BT, R.id.DVR, R.id.DVR_uart, R.id.Micracast, R.id.Canbus, R.id.Tpms, };
	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	int mEnable[] = { XmlParse.fun_dvd_enable, XmlParse.fun_gps_enable, XmlParse.fun_fm_enable,
			XmlParse.fun_avin_enable, XmlParse.fun_avin2_enable, XmlParse.fun_tv_enable, XmlParse.fun_ipod_enable,
			XmlParse.fun_music_enable, XmlParse.fun_video_enable, XmlParse.fun_bt_enable,
			XmlParse.fun_dvr_enable, XmlParse.fun_dvr_uart_enable, XmlParse.fun_micracast_enable, XmlParse.fun_canbus_enable,
			XmlParse.fun_tpms_enable };

	private static String[] stringArray;

	public factory_Fragment_FunsConfig() {
		super();
	}

	public void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		stringArray = mContext.getResources().getStringArray(R.array.fun_name);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.factory_fragment_funsconfig, container);
		initView(mRootView);
		return mRootView;
	}

	private void initView(View rootView) {

		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			layout = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			layout.setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setSubTitle(stringArray[i]);
			mLayout_CheckBox[i].getOneCheckBox().setChecked(mEnable[i] == 1 ? true : false);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_funs_other:
			Function.onSet_factory_Fragment_FunsConfig_Other(mFragmentManager);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean arg1) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.DVD:
			XmlParse.fun_dvd_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.Gps:
			XmlParse.fun_gps_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.FM:
			XmlParse.fun_fm_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.AVIN:
			XmlParse.fun_avin_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.AVIN2:
			XmlParse.fun_avin2_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.TV:
			XmlParse.fun_tv_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.IPOD:
			XmlParse.fun_ipod_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.Music:
			XmlParse.fun_music_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.Video:
			XmlParse.fun_video_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.BT:
			XmlParse.fun_bt_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.DVR:			
			XmlParse.fun_dvr_enable = (arg1 == true ? 1 : 0);
			if (XmlParse.fun_dvr_enable == 1) {
				XmlParse.fun_dvr_uart_enable = 0;
				
				mLayout_CheckBox[DVR_UART_ID].getOneCheckBox().setChecked(false);
			}
			break;
		case R.id.DVR_uart:			
			XmlParse.fun_dvr_uart_enable = (arg1 == true ? 1 : 0);
			if (XmlParse.fun_dvr_enable == 1) {
				XmlParse.fun_dvr_enable = 0;
				mLayout_CheckBox[DVR_USB_ID].getOneCheckBox().setChecked(false);
			}
			break;
		case R.id.Micracast:
			XmlParse.fun_micracast_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.Canbus:
			XmlParse.fun_canbus_enable = (arg1 == true ? 1 : 0);
			break;
		case R.id.Tpms:
			XmlParse.fun_tpms_enable = (arg1 == true ? 1 : 0);
			break;
		default:
			break;
		}
	}

}
