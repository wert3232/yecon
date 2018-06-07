package com.can.ui.psa;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PsaCarState;
import com.can.parser.Protocol;
import com.can.parser.DDef.PsaTripComputer;
import com.can.parser.raise.psa.RePsaProtocol;
import com.can.ui.CanActivity;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;

public class CanPsaTripCom extends CanFrament implements OnClickListener{
	private CanPsaMemSpeed	mCanPsaMemSpeed = null;
	private PsaCarState		mPsaCarState = null;
	private PsaTripComputer mTripComputer = null;
	private View mView = null;
	private int mShowType = 0;
	private Protocol mProtocol = null;
	private LinearLayout mLayoutPage0 = null;
	private LinearLayout mLayoutPage1 = null;
	private int[] mStrId = {
		R.string.str_psa_invalid,
		R.string.str_psa_l_per_km,
		R.string.str_psa_km,
		R.string.str_psa_km_per_h
	};
	
	private int[] mBtnId = {
		R.id.psa_tv_page0,
		R.id.psa_tv_page1,
		R.id.psa_tv_page2,
		R.id.psa_tv_other
	};
	
	private int[] mTvId = {
		R.id.psa_tv_fuel_consumption,
		R.id.psa_tv_recharge_mileage,
		R.id.psa_tv_destination_miles,
		R.id.psa_tv_start_stop_timing,
		R.id.psa_tv_fuel_average,
		R.id.psa_tv_speed_average,
		R.id.psa_tv_accumulated_miles
	};
	
	private String[] mStrings = new String[mStrId.length];
	private TextView[] mTvMenu = new TextView[mBtnId.length];
	private TextView[] mTvText = new TextView[mTvId.length];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
		mCanPsaMemSpeed = new CanPsaMemSpeed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_trip_com, null);
			initView();
		}
		return mView;
	}

	@Override
	public void onResume() {
		
		if (CanPopWind.sFragmentSw.equals(DDef.MEM_SPEED)) {
			showPopPage(mCanPsaMemSpeed);
			CanPopWind.sFragmentSw = "";
		} else if (CanPopWind.sFragmentSw.equals(DDef.PAGE_SW)) {
			if (mCanPsaMemSpeed.isVisible()) {
				mCanPsaMemSpeed.showPopPage(this);
			}
			CanPopWind.sFragmentSw = "";
		}
		getData(DDef.TRIP_INFO_MINOIL);
		super.onResume();
		
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mStrId.length; i++) {
				mStrings[i] = getString(mStrId[i]);
			}
			
			mLayoutPage0 = (LinearLayout) mView.findViewById(R.id.psa_ll_page0);
			mLayoutPage1 = (LinearLayout) mView.findViewById(R.id.psa_ll_page1);
			for (int i = 0; i < mBtnId.length; i++) {
				mTvMenu[i] = (TextView) mView.findViewById(mBtnId[i]);
				mTvMenu[i].setOnClickListener(this);
			}
			
			for (int i = 0; i < mTvId.length; i++) {
				mTvText[i] = (TextView) mView.findViewById(mTvId[i]);
			}
			
			mTvMenu[0].setSelected(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.psa_tv_page0:
			swithPage(0x00);
			break;
		case R.id.psa_tv_page1:
			swithPage(0x01);
			break;
		case R.id.psa_tv_page2:
			swithPage(0x02);
			break;
		case R.id.psa_tv_other:
			Intent intent = new Intent(getActivity(), CanActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private void swithPage(int iIndex) {
		sendData((byte)iIndex);
		mShowType = iIndex;
		for (int i = 0; i < mTvMenu.length; i++) {
			if (iIndex == i) {
				mTvMenu[i].setSelected(true);
			} else {
				mTvMenu[i].setSelected(false);
			}
		}
		
		if (iIndex == 0) {
			mLayoutPage1.setVisibility(View.GONE);
			mLayoutPage0.setVisibility(View.VISIBLE);
		} else {
			mLayoutPage0.setVisibility(View.GONE);
			mLayoutPage1.setVisibility(View.VISIBLE);
		}
		setTripAttr(mTripComputer);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData(DDef.TRIP_INFO_MINOIL);
				break;
			case DDef.TRIP_INFO_MINOIL:
				mTripComputer = (PsaTripComputer)msg.obj;
				setTripAttr(mTripComputer);
				break;
			case DDef.CAN_FRAGMENT_SW:
				String str = (String)msg.obj;
				if (str != null) {
					if (str.equals(DDef.PAGE_SW) && !isVisible()) {
						mCanPsaMemSpeed.showPopPage(CanPsaTripCom.this);
					} else if (str.equals(DDef.MEM_SPEED) && !mCanPsaMemSpeed.isVisible()) {
						showPopPage(mCanPsaMemSpeed);
					}
				}
				break;
			case DDef.CAR_SET_CMD_ID:
				mPsaCarState = (PsaCarState)msg.obj;
				if (mPsaCarState != null) {
					swithPage(mPsaCarState.mTripEcoPages);
				}
				break;
			default:
				break;
			}
		}
	};
	
	private void setTripAttr(PsaTripComputer tripComputer) {
		if (mView != null && tripComputer != null) {
			if (mShowType == 0x02) {
				mTvText[4].setText(String.valueOf(tripComputer.mFuelAverage2)+mStrings[1]);
				mTvText[5].setText(String.valueOf(tripComputer.mSpeedAverage2)+mStrings[3]);
				mTvText[6].setText(String.valueOf(tripComputer.mAccumulatMileage2)+mStrings[2]);
			} else if(mShowType == 0x01) {
				mTvText[4].setText(String.valueOf(tripComputer.mFuelAverage1)+mStrings[1]);
				mTvText[5].setText(String.valueOf(tripComputer.mSpeedAverage1)+mStrings[3]);
				mTvText[6].setText(String.valueOf(tripComputer.mAccumulatMileage1)+mStrings[2]);
			} else {
				mTvText[0].setText(String.valueOf(tripComputer.mFuelConsumption)+mStrings[1]);
				mTvText[1].setText(String.valueOf(tripComputer.mResidualOilMileage)+mStrings[2]);
				mTvText[2].setText(String.valueOf(tripComputer.mObjectiveTomileage)+mStrings[2]);
				String str = String.format("%02d:%02d:%02d", 
						tripComputer.mTimingH, tripComputer.mTimingM, tripComputer.mTimingS);
				mTvText[3].setText(str);
			}
		}
	}
	
	private void sendData(byte byCmdID) {
		byte[] datas = new byte[3];
		datas[0] = byCmdID;
		sendMsg(RePsaProtocol.DATA_TYPE_CAR_TRIP_REQUEST, datas, mProtocol);
	}
}
