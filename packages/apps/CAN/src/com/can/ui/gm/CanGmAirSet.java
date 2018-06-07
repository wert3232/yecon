package com.can.ui.gm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.gm.ReGmProtocol;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;
import com.can.ui.draw.FuelSeekBar;

public class CanGmAirSet extends CanFrament implements OnClickListener{
	private AirInfo mAirInfo = null;
	private Protocol mProtocol = null;
	private View mView = null;

	private TextView mTvLeftTemp = null;
	private TextView mTvRightTemp = null;
	
	private FuelSeekBar mSeekBar = null;
	
	private int[] mBtnId = {
		R.id.gm_btn_air_par_wind,
		R.id.gm_btn_air_down_par_wind,
		R.id.gm_btn_air_down_up_wind,
		R.id.gm_btn_air_down_wind,
		R.id.gm_btn_air_ac,
		R.id.gm_btn_air_auto,
		R.id.gm_btn_air_dual,
		R.id.gm_btn_air_front_deg,
		R.id.gm_btn_air_cycle_in,
		R.id.gm_btn_air_cycle_out
	};
	
	private Button[] mButtons = new Button[mBtnId.length];
 	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.gm_air_set, null);
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

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mBtnId.length; i++) {
				mButtons[i] = (Button)mView.findViewById(mBtnId[i]);
				if (mButtons[i] != null) {
					mButtons[i].setOnClickListener(this);
				}
			}
			mView.findViewById(R.id.gm_btn_air_wind_add).setOnClickListener(this);
			mView.findViewById(R.id.gm_btn_air_wind_del).setOnClickListener(this);
			mView.findViewById(R.id.gm_air_right_temp_add).setOnClickListener(this);
			mView.findViewById(R.id.gm_air_left_temp_add).setOnClickListener(this);
			mView.findViewById(R.id.gm_air_right_temp_del).setOnClickListener(this);
			mView.findViewById(R.id.gm_air_left_temp_del).setOnClickListener(this);
			mTvLeftTemp = (TextView) mView.findViewById(R.id.gm_tv_left_temp);
			mTvRightTemp = (TextView) mView.findViewById(R.id.gm_tv_right_temp);
			
			mSeekBar = (FuelSeekBar) mView.findViewById(R.id.gm_seekbar_wind);
		}
	}

	@Override
	public void onClick(View v) {
		if (mAirInfo != null) {
			switch (v.getId()) {
			case R.id.gm_btn_air_par_wind:
				sendData((byte)0x08);
				break;
			case R.id.gm_btn_air_down_par_wind:
				sendData((byte)0x09);
				break;
			case R.id.gm_btn_air_down_up_wind:
				sendData((byte)0x0A);
				break;
			case R.id.gm_btn_air_down_wind:
				sendData((byte)0x0B);
				break;
			case R.id.gm_btn_air_ac:
				sendData((byte)0x01);
				break;
			case R.id.gm_btn_air_auto:
				sendData((byte)0x02);
				break;
			case R.id.gm_btn_air_dual:
				sendData((byte)0x0D);
				break;
			case R.id.gm_btn_air_front_deg:
				sendData((byte)0x0C);
				break;
			case R.id.gm_btn_air_cycle_in:
			case R.id.gm_btn_air_cycle_out:
				sendData((byte)0x03);
				break;
			case R.id.gm_btn_air_wind_add:
				sendData((byte)0x06);
				break;
			case R.id.gm_btn_air_wind_del:
				sendData((byte)0x07);
				break;
			case R.id.gm_air_left_temp_add:
				sendData((byte)0x04);
				break;
			case R.id.gm_air_left_temp_del:
				sendData((byte) 0x05);
				break;
			case R.id.gm_air_right_temp_add:
				if (mAirInfo.mDaulLight == 0x00) {
					sendData((byte)0x14);
				} else {
					sendData((byte)0x04);
				}
				break;
			case R.id.gm_air_right_temp_del:
				if (mAirInfo.mDaulLight == 0x00) {
					sendData((byte)0x15);
				} else {
					sendData((byte)0x05);
				}
				break;
			default:
				break;
			}
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData(DDef.AIR_CMD_ID);
				break;
			case DDef.AIR_CMD_ID:
				mAirInfo = (AirInfo)msg.obj;
				setAirAttr(mAirInfo);
				break;
			default:
				break;
			}
		}
	};
	
	private void setAirAttr(AirInfo airInfo) {
		if (mView != null && airInfo != null) {
			mButtons[4].setSelected(airInfo.mAcState == 0x01 ? true : false);
			mButtons[5].setSelected(airInfo.mAutoLight1 == 0x01 ? true : false);
			mButtons[6].setSelected(airInfo.mDaulLight == 0x01 ? true : false);
			mButtons[7].setSelected(airInfo.mWindMode == 0x02 ? true : false);
			mButtons[8].setVisibility(airInfo.mCircleState == 0x01 ? View.VISIBLE : View.GONE);
			mButtons[9].setVisibility(airInfo.mCircleState == 0x00 ? View.VISIBLE : View.GONE);
			
			int iIndex = -1;
			if (airInfo.mWindMode == 0x05) {
				iIndex = 0x00;
			} else if (airInfo.mWindMode == 0x04) {
				iIndex = 0x01;
			} else if (airInfo.mWindMode == 0x08) {
				iIndex = 0x02;
			} else if (airInfo.mWindMode == 0x03) {
				iIndex = 0x03;
			}
			
			for (int i = 0; i < 4; i++) {
				mButtons[i].setSelected(iIndex == i ? true : false);
			}
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
	
	private void sendData(byte byCmdId) {
		byte[] datas = new byte[2];
		datas[0] = 0x07;
		datas[1] = byCmdId;
		sendMsg(ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, datas, mProtocol);
		
		if (byCmdId != 0x00) {
			try {
				Thread.sleep(50);
				sendData((byte) 0x00);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
