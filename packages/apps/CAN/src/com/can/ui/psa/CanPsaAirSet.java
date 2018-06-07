package com.can.ui.psa;

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
import com.can.parser.raise.psa.RePsaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;
import com.can.ui.draw.FuelSeekBar;

public class CanPsaAirSet extends CanFrament implements OnClickListener{

	private AirInfo mAirInfo = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private int[] mBtnId = {
		R.id.psa_air_left_temp_add,
		R.id.psa_air_left_temp_del,
		R.id.psa_air_right_temp_add,
		R.id.psa_air_right_temp_del,
		R.id.psa_btn_air_wind_add,
		R.id.psa_btn_air_wind_del,
		R.id.psa_btn_air_auto,
		R.id.psa_btn_air_dual,
		R.id.psa_btn_air_ac,
		R.id.psa_btn_air_acmax,
		R.id.psa_btn_air_par_wind,
		R.id.psa_btn_air_up_wind,
		R.id.psa_btn_air_down_wind
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
			mView = inflater.inflate(R.layout.psa_air_set, null);
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
			
			mTvLeftTemp = (TextView) mView.findViewById(R.id.psa_tv_left_temp);
			mTvRightTemp = (TextView) mView.findViewById(R.id.psa_tv_right_temp);
			mSeekBar = (FuelSeekBar) mView.findViewById(R.id.psa_seekbar_wind);
		}
	}
	
	private void setAirInfo(AirInfo airInfo) {
		if (mView != null && airInfo != null) {
			mTextViews[6].setSelected(airInfo.mAutoLight1 == 0x01 ? true : false);
			mTextViews[7].setSelected(airInfo.mDaulLight == 0x01 ? true : false);
			mTextViews[8].setSelected(airInfo.mAcState == 0x01 ? true : false);
			mTextViews[9].setSelected(airInfo.mAcMax == 0x01 ? true : false);
			mTextViews[10].setSelected(airInfo.mParallelWind == 0x01 ? true : false);
			mTextViews[11].setSelected(airInfo.mUpwardWind == 0x01 ? true : false);
			mTextViews[12].setSelected(airInfo.mDowmWind == 0x01 ? true : false);
			mSeekBar.setMax(airInfo.mMaxWindlv);
			mSeekBar.setProgress(airInfo.mWindRate);
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
		case R.id.psa_air_left_temp_add:
			sendData((byte)0x04, (byte)0x01);
			break;
		case R.id.psa_air_left_temp_del:
			sendData((byte)0x04, (byte)0x02);
			break;
		case R.id.psa_air_right_temp_add:
			sendData((byte)0x05, (byte)0x01);
			break;
		case R.id.psa_air_right_temp_del:
			sendData((byte)0x05, (byte)0x02);
			break;
		case R.id.psa_btn_air_wind_add:
			sendData((byte)0x0A, (byte)0x01);
			break;
		case R.id.psa_btn_air_wind_del:
			sendData((byte)0x0A, (byte)0x02);
			break;
		case R.id.psa_btn_air_auto:
			sendData((byte)0x01, (byte)(mAirInfo.mAutoLight1 == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_dual:
			sendData((byte)0x0B, (byte)(mAirInfo.mDaulLight == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_ac:
			sendData((byte)0x02, (byte)(mAirInfo.mAcState == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_acmax:
			sendData((byte)0x03, (byte)(mAirInfo.mAcMax == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_par_wind:
			sendData((byte)0x06, (byte)(mAirInfo.mParallelWind == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_up_wind:
			sendData((byte)0x07, (byte)(mAirInfo.mUpwardWind == 0x00 ? 0x01 : 0x00));
			break;
		case R.id.psa_btn_air_down_wind:
			sendData((byte)0x08, (byte)(mAirInfo.mDowmWind == 0x00 ? 0x01 : 0x00));
			break;
		default:
			break;
		}
	}
	
	private void sendData(byte byCmdId, byte byValue) {
		byte[] datas = new byte[2];
		datas[0] = byCmdId;
		datas[1] = byValue;
		sendMsg(RePsaProtocol.DATA_TYPE_AIR_SET_REQUEST, datas, mProtocol);
	}
}
