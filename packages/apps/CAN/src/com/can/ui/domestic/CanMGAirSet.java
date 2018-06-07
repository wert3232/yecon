package com.can.ui.domestic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.DDef.AirInfo;
import com.can.parser.raise.domestic.ReMGProtol;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;
import com.can.ui.draw.FuelSeekBar;

public class CanMGAirSet extends CanFrament implements OnClickListener{

	private AirInfo mAirInfo = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private int[] mBtnId = {
		R.id.mg_air_left_temp_add,
		R.id.mg_air_left_temp_del,
		R.id.mg_air_right_temp_add,
		R.id.mg_air_right_temp_del,
		R.id.mg_btn_air_wind_add,
		R.id.mg_btn_air_wind_del,
		R.id.mg_btn_air_auto,
		R.id.mg_btn_air_ac,
		R.id.mg_btn_air_front_deg,
		R.id.mg_btn_air_rear_deg,
		R.id.mg_btn_air_cycle_in,
		R.id.mg_btn_air_cycle_out,
		R.id.mg_btn_air_wind_mode,
		R.id.mg_btn_air_state
	};

	private int[] mStrWindId = {
		R.string.str_mg_air_par,
		R.string.str_mg_air_par_dn,
		R.string.str_mg_air_dn,
		R.string.str_mg_air_up_dn,
		R.string.str_mg_air_up,
		R.string.str_mg_air_auto
	};
	
	private int[] mStrStateId = {
		R.string.str_mg_air_off,
		R.string.str_mg_air_on
	};
	
	private TextView mTvLeftTemp = null;
	private TextView mTvRightTemp = null;
	private FuelSeekBar mSeekBar = null;
	private TextView[] mTextViews = new TextView[mBtnId.length];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.mg_air_set, null);
			initView();
		}
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		CanPopWind.sIsAirActivityShow = true;
		getData(DDef.AIR_CMD_ID);
	}

	
	@Override
	public void onPause() {
		CanPopWind.sIsAirActivityShow = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData(DDef.AIR_CMD_ID);
				break;
			case DDef.AIR_CMD_ID:
				mAirInfo = (AirInfo)msg.obj;
				setAirInfo(mAirInfo);
				break;
			default:
				break;
			}
		}
	};
	
	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mBtnId.length; i++) {
				mTextViews[i] = (TextView) mView.findViewById(mBtnId[i]);
				mTextViews[i].setOnClickListener(this);
			}
			
			mTvLeftTemp = (TextView) mView.findViewById(R.id.mg_tv_left_temp);
			mTvRightTemp = (TextView) mView.findViewById(R.id.mg_tv_right_temp);
			mSeekBar = (FuelSeekBar) mView.findViewById(R.id.mg_seekbar_wind);
		}
	}
	
	private void setAirInfo(AirInfo airInfo) {
		if (mView != null && airInfo != null) {
			mTextViews[6].setSelected(airInfo.mAutoLight1 == 0x01 ? true : false);
			mTextViews[7].setSelected(airInfo.mAcState == 0x01 ? true : false);
			mTextViews[8].setSelected(airInfo.mFWDefogger == 0x01 ? true : false);
			mTextViews[9].setSelected(airInfo.mRearLight == 0x01 ? true : false);
			mSeekBar.setMax(airInfo.mMaxWindlv);
			mSeekBar.setProgress(airInfo.mWindRate);
			
			boolean bIsCycleIn = airInfo.mCircleState == 0x01 ? true : false;
			mTextViews[10].setVisibility(bIsCycleIn ? View.VISIBLE : View.GONE);
			mTextViews[11].setVisibility(bIsCycleIn ? View.GONE : View.VISIBLE);
			if (airInfo.mWindMode == 0x0F) {
				mTextViews[12].setText(getString(mStrWindId[5]));
			} else if (airInfo.mWindMode < mStrWindId.length) {
				mTextViews[12].setText(getString(mStrWindId[airInfo.mWindMode]));
			}
			mTextViews[13].setText(getString(mStrStateId[airInfo.mAiron]));
			setTempAttr(airInfo);
		}
	}

	private void setTempAttr(AirInfo airInfo) {
		int iTempUint = airInfo.mTempUnit;
		String strTempUnit = getString((iTempUint == 0) ? R.string.ac_temp_unit_c : R.string.ac_temp_unit_f);
			
		if (airInfo.mLeftTemp <= airInfo.mMinTemp) {
			mTvLeftTemp.setText(R.string.ac_temp_lo);	
		} else if (airInfo.mLeftTemp >= airInfo.mMaxTemp) {
			mTvLeftTemp.setText(R.string.ac_temp_hi);
		} else {
			mTvLeftTemp.setText(airInfo.mLeftTemp + strTempUnit);
		}

		if (airInfo.mRightTemp <= airInfo.mMinTemp) {
			mTvRightTemp.setText(R.string.ac_temp_lo);
		} else if (airInfo.mRightTemp >= airInfo.mMaxTemp){
			mTvRightTemp.setText(R.string.ac_temp_hi);
		} else {
			mTvRightTemp.setText(airInfo.mRightTemp + strTempUnit);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mg_air_left_temp_add:
		case R.id.mg_air_right_temp_add:
			sendData(0x01, 0x01);
			sendData(0x01, 0x00);
			break;
		case R.id.mg_air_left_temp_del:
		case R.id.mg_air_right_temp_del:
			sendData(0x01, 0x02);
			sendData(0x01, 0x00);
			break;
		case R.id.mg_btn_air_wind_add:
			sendData(0x04, 0x01);
			sendData(0x04, 0x00);
			break;
		case R.id.mg_btn_air_wind_del:
			sendData(0x04, 0x02);
			sendData(0x04, 0x00);
			break;
		case R.id.mg_btn_air_auto:
			sendData(0x09, 0x01);
			sendData(0x09, 0x00);
			break;
		case R.id.mg_btn_air_ac:
			sendData(0x08, 0x01);
			sendData(0x08, 0x00);
			break;
		case R.id.mg_btn_air_front_deg:
			sendData(0x06, 0x01);
			sendData(0x06, 0x00);
			break;
		case R.id.mg_btn_air_rear_deg:
			sendData(0x07, 0x01);
			sendData(0x07, 0x00);
			break;
		case R.id.mg_btn_air_cycle_in:
		case R.id.mg_btn_air_cycle_out:
			sendData(0x03, 0x01);
			sendData(0x03, 0x00);
			break;
		case R.id.mg_btn_air_wind_mode:
			sendData(0x02, 0x01);
			sendData(0x02, 0x00);
			break;
		case R.id.mg_btn_air_state:
			sendData(0x05, 0x01);
			sendData(0x05, 0x00);
			break;
		default:
			break;
		}
	}
	
	private void sendData(int byCmdId, int byValue) {
		byte[] datas = new byte[2];
		datas[0] = (byte) byCmdId;
		datas[1] = (byte) byValue;
		sendMsg(ReMGProtol.DATA_TYPE_AIR_SET_REQUEST, datas, mProtocol);
	}
}
