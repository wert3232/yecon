package com.can.ui.honda;

import com.can.activity.R;
import com.can.assist.CanXml;
import com.can.assist.CanXml.CAN_DESCRIBE;
import com.can.parser.DDef;
import com.can.ui.CanFrament;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CanHondaCarInfo extends CanFrament implements OnClickListener{
	public static int sCarType = 0;
	private View mView = null;
	private CanHondaCarSet mFragCarSet = null;
	private CanHondaFuelMil mFragFuelMil = null;
	private CanHondaCompass mFragCompass = null;
	private CanHondaAirSet mFragAirSet = null;

	private RelativeLayout mLayoutCarSet = null;
	private RelativeLayout mLayoutFuelMil = null;
	private RelativeLayout mLayoutCompass = null;
	private RelativeLayout mLayoutAirSet = null;
	private RelativeLayout mLayoutUsbIpod = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CAN_DESCRIBE canDescirbe = CanXml.getCanDescribe();
		sCarType = canDescirbe.iCarTypeID;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_info, null);
			initView();
		}
		return mView;
	}

	private void initView() {
		mFragFuelMil = new CanHondaFuelMil();
		mFragCarSet = new CanHondaCarSet();
		mFragAirSet = new CanHondaAirSet();
		mFragCompass = new CanHondaCompass();
		if (mView != null) {
			mLayoutAirSet = (RelativeLayout) mView.findViewById(R.id.honda_relative_air_set);
			mLayoutCarSet = (RelativeLayout) mView.findViewById(R.id.honda_relative_car_set);
			mLayoutFuelMil = (RelativeLayout) mView.findViewById(R.id.honda_relative_fuel_mil);
			mLayoutCompass = (RelativeLayout) mView.findViewById(R.id.honda_relative_compass);
			mLayoutUsbIpod = (RelativeLayout) mView.findViewById(R.id.honda_relative_usb_ipod);
			
			if (sCarType == DDef.CarHondaType.HONDA_GM) {
				mLayoutUsbIpod.setVisibility(View.GONE);
			} else if (sCarType == DDef.CarHondaType.HONDA_CRV) {
				mLayoutAirSet.setVisibility(View.GONE);
				mLayoutCarSet.setVisibility(View.GONE);
				mLayoutFuelMil.setVisibility(View.GONE);
			}
			
			mView.findViewById(R.id.tv_honda_car_set).setOnClickListener(this);
			mView.findViewById(R.id.tv_honda_fuel_mil).setOnClickListener(this);
			mView.findViewById(R.id.tv_honda_compass).setOnClickListener(this);
			mView.findViewById(R.id.tv_honda_air_set).setOnClickListener(this);
			mView.findViewById(R.id.tv_honda_usb_ipod).setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_honda_fuel_mil:
			super.showPage(mFragFuelMil);
			break;
		case R.id.tv_honda_car_set:
			super.showPage(mFragCarSet);
			break;
		case R.id.tv_honda_compass:
			super.showPage(mFragCompass);
			break;
		case R.id.tv_honda_air_set:
			super.showPage(mFragAirSet);
			break;
		case R.id.tv_honda_usb_ipod:
			showAudioPage();
			break;
		default:
			break;
		}
	}
}
