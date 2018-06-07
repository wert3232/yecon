package com.can.ui.psa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.ui.CanFrament;
import com.can.ui.CanPopActivity;
import com.can.ui.CanPopWind;

public class CanPsaCarInfo extends CanFrament implements OnClickListener{
	public static int sWarnType = 0;
	
	private  CanPsaAirSet mCanPsaAirSet = null;
	private  CanPsaWarnInfo mCanPsaWarnInfo = null;
	private  CanPsaCruSpeed mCanPsaCruSpeed = null;
	private  CanPsaCarSet mCanPsaCarSet = null;
	private View mView = null;
	
	private int[] mSetId = {
		R.id.tv_psa_trip_com,
		R.id.tv_psa_car_set,
		R.id.tv_psa_diog_info,
		R.id.tv_psa_func_info,
		R.id.tv_psa_warn_info,
		R.id.tv_psa_air_set,
		R.id.tv_psa_mem_speed,
		R.id.tv_psa_cru_speed
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_car_info, null);
			initView();
		}
		return mView;
	}

	private void initView() {
		mCanPsaAirSet = new CanPsaAirSet();
		mCanPsaWarnInfo = new CanPsaWarnInfo();
		mCanPsaCruSpeed = new CanPsaCruSpeed();
		mCanPsaCarSet = new CanPsaCarSet();
		if (mView != null) {
			for (int iter: mSetId) {	
				mView.findViewById(iter).setOnClickListener(this);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_psa_trip_com:
			{
				Intent intent = new Intent(getActivity(), CanPopActivity.class);
				CanPopWind.sFragmentSw = DDef.PAGE_SW;
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			break;
		case R.id.tv_psa_car_set:
			showPage(mCanPsaCarSet);
			break;
		case R.id.tv_psa_warn_info:
			sWarnType = 0;
			showPage(mCanPsaWarnInfo);
			break;
		case R.id.tv_psa_func_info:
			sWarnType = 1;
			showPage(mCanPsaWarnInfo);
			break;
		case R.id.tv_psa_diog_info:
			sWarnType = 2;
			showPage(mCanPsaWarnInfo);
			break;
		case R.id.tv_psa_air_set:
			showPage(mCanPsaAirSet);
			break;
		case R.id.tv_psa_mem_speed:
			{
				Intent intent = new Intent(getActivity(), CanPopActivity.class);
				CanPopWind.sFragmentSw = DDef.MEM_SPEED;
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			break;
		case R.id.tv_psa_cru_speed:
			showPage(mCanPsaCruSpeed);
			break;
		default:
			break;
		}
		
	}
}
