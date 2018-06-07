package com.can.ui.dz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.ui.CanFrament;

/**
 * ClassName:CanDz7CarInfo
 * 
 * @function:大众7代UI
 * @author Kim
 * @Date: 2016-7-5下午4:14:21
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7CarInfo extends CanFrament implements OnClickListener{

	private View mObjView = null;
	
	private CanDz7Convs  mObjCanDz7Convs  = null;
	private CanDz7CarSet mObjCanDz7CarSet = null;
	private CanDz7Serice mObjCanDz7Serice = null;
	private CanDz7DriverData mObjCanDz7DriverData = null;

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
			mObjView = inflater.inflate(R.layout.dz7_info, container, false);
			init();
		}

		return mObjView;
	}
	
	public void init(){
		
		if (mObjView != null) {
			
			mObjCanDz7Convs  = new CanDz7Convs();
			mObjCanDz7CarSet = new CanDz7CarSet();
			mObjCanDz7Serice = new CanDz7Serice();
			mObjCanDz7DriverData = new CanDz7DriverData();
			
			mObjView.findViewById(R.id.btn_dz7_menu_carset).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_dz7_menu_convs).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_dz7_menu_serivces).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_dz7_menu_startstop).setOnClickListener(this);
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
		case R.id.btn_dz7_menu_carset:
			super.showPage(mObjCanDz7CarSet);
			break;
		case R.id.btn_dz7_menu_convs:
			super.showPage(mObjCanDz7DriverData);
			break;
		case R.id.btn_dz7_menu_serivces:
			super.showPage(mObjCanDz7Serice);
			break;
		case R.id.btn_dz7_menu_startstop:
			super.showPage(mObjCanDz7Convs);
			break;
		default:
			break;
		}
	}

}
