package com.can.ui.gm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.ui.CanFrament;

public class CanGmCarInfo extends CanFrament implements OnClickListener{
	private View mView = null;

	private CanGmAirSet mFraGmAirSet = null;
	private CanGmCarSet mFraGmCarSet = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.gm_car_info, null);
			initView();
		}
		return mView;
	}

	private void initView() {
		mFraGmAirSet = new CanGmAirSet();
		mFraGmCarSet = new CanGmCarSet();
		if (mView != null) {
			mView.findViewById(R.id.gm_btn_air_set).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_car_set).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_on_star).setOnClickListener(this);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gm_btn_air_set:
			showPage(mFraGmAirSet);
			break;
		case R.id.gm_btn_car_set:
			showPage(mFraGmCarSet);
			break;
		case R.id.gm_btn_on_star:
			showAudioPage();
			break;
		default:
			break;
		}
	}

}
