package com.can.ui.jeep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.ui.CanFrament;

/**
 * ClassName:CanJeepCarInfo
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-7-14下午4:54:31
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJeepCarInfo extends CanFrament implements OnClickListener{

	private View mObjView = null;
	private CanJeepCarSet  mObjJeepCarSet  = null;
	private CanJeepCompass mObjJeepCompass = null;
	private CanJeepAirSet  mObjJeepAirSet  = null;
	private CanJeepDspInfo mObjJeepDspInfo = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater.inflate(R.layout.jeep_info, container, false);
			init();
		}

		return mObjView;
	}
	
	private void init(){
		if (mObjView != null) {
			
			mObjJeepCarSet  = new CanJeepCarSet();
			mObjJeepCompass = new CanJeepCompass();
			mObjJeepAirSet  = new CanJeepAirSet();
			mObjJeepDspInfo = new CanJeepDspInfo();
			
			mObjView.findViewById(R.id.btn_jeep_menu_carset).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_jeep_menu_cdinfo).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_jeep_menu_compass).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_jeep_menu_airset).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_menu_dsp).setOnClickListener(this);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_jeep_menu_carset:
			super.showPage(mObjJeepCarSet);
			break;
		case R.id.btn_jeep_menu_cdinfo:
			super.showAudioPage();
			break;
		case R.id.btn_jeep_menu_compass:
			super.showPage(mObjJeepCompass);
			break;
		case R.id.btn_jeep_menu_airset:
			super.showPage(mObjJeepAirSet);
			break;
		case R.id.btn_menu_dsp:
			super.showPage(mObjJeepDspInfo);
			break;
		default:
			break;
		}
	}
}
