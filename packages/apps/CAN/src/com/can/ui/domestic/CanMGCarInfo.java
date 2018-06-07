package com.can.ui.domestic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.can.activity.R;
import com.can.ui.CanFrament;

public class CanMGCarInfo extends CanFrament implements OnClickListener{

	private CanMGAirSet mCanMGAirSet = null;
	private CanMGCarSet mCanMGCarSet = null;
	private View mView = null;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCanMGAirSet = new CanMGAirSet();
		mCanMGCarSet = new CanMGCarSet();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.mg_car_info, null);
			mView.findViewById(R.id.tv_mg_air_set).setOnClickListener(this);
			mView.findViewById(R.id.tv_mg_car_set).setOnClickListener(this);
		}
		return mView;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_mg_air_set:
			showPage(mCanMGAirSet);
			break;
		case R.id.tv_mg_car_set:
			showPage(mCanMGCarSet);
			break;
		default:
			break;
		}
	}

}
