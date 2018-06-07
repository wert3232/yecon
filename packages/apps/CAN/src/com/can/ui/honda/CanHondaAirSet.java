package com.can.ui.honda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.raise.honda.ReHondaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;
import com.can.ui.draw.FuelSeekBar;

public class CanHondaAirSet extends CanFrament implements OnClickListener, OnSeekBarChangeListener{
	private View mView = null;
	private Protocol mProtocol = null;
	private ImageView[] mAcMode = new ImageView[2];
	private ImageView[] mWinMode = new ImageView[4];
	private FuelSeekBar mSeekBar = null;
	private TextView mLeftTemp = null;
	private TextView mRightTemp = null;
	private int[] mWinModeId = { R.id.honda_btn_win_para, R.id.honda_btn_win_para_dn,
								 R.id.honda_btn_win_dn, R.id.honda_btn_win_up_dn };
	
	private boolean mCanSeek = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_air_set, null);
			initView();
		}
		return mView;
	}

	
	@Override
	public void onStart() {
		super.onStart();
		getData(DDef.AIR_CMD_ID);
		CanPopWind.sIsAirActivityShow = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		CanPopWind.sIsAirActivityShow = false;
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			mLeftTemp = (TextView) mView.findViewById(R.id.honda_left_temp);
			mRightTemp = (TextView) mView.findViewById(R.id.honda_right_temp);
			mAcMode[0] = (ImageView) mView.findViewById(R.id.honda_btn_ac_on);
			mAcMode[0].setOnClickListener(this);
			mAcMode[1] = (ImageView) mView.findViewById(R.id.honda_btn_ac_off);
			mAcMode[1].setOnClickListener(this);
			for (int i = 0; i < mWinModeId.length; i++) {
				mWinMode[i] = (ImageView) mView.findViewById(mWinModeId[i]); 
				if (mWinMode[i] != null) {
					mWinMode[i].setOnClickListener(this);
				}
			}
			mSeekBar = (FuelSeekBar) mView.findViewById(R.id.honda_seekbar_air_win);
			mSeekBar.setOnSeekBarChangeListener(this);
			mSeekBar.setMax(7);
		}
		
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData(DDef.AIR_CMD_ID);
				break;
			case DDef.AIR_CMD_ID:
				setAirInfo((AirInfo) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private void setAirInfo(AirInfo airInfo){
		if (isVisible() && airInfo != null) {
			if (airInfo.mTempUnit == 0x01) {
				mLeftTemp.setText(String.valueOf(airInfo.mLeftTemp)+getString(R.string.ac_temp_unit_c));
				mRightTemp.setText(String.valueOf(airInfo.mRightTemp)+getString(R.string.ac_temp_unit_c));
			} else {
				mLeftTemp.setText(String.valueOf(airInfo.mLeftTemp)+getString(R.string.ac_temp_unit_f));
				mRightTemp.setText(String.valueOf(airInfo.mRightTemp)+getString(R.string.ac_temp_unit_f));
			}
			mAcMode[0].setSelected(airInfo.mAcState == 1 ? true : false);
			mAcMode[1].setSelected(airInfo.mAcState == 0 ? true : false);
			
			int iWind = -1;
			if(airInfo.mUpwardWind == 0x01 && airInfo.mDowmWind == 0x01 && airInfo.mParallelWind == 0x00) {
				iWind = 0x03;
			} else if(airInfo.mUpwardWind == 0x00 && airInfo.mDowmWind == 0x01 && airInfo.mParallelWind == 0x01) {
				iWind = 0x01;
			} else if (airInfo.mUpwardWind == 0x00 && airInfo.mDowmWind == 0x01 && airInfo.mParallelWind == 0x00) {
				iWind = 0x02;
			} else if (airInfo.mUpwardWind == 0x00 && airInfo.mDowmWind == 0x00 && airInfo.mParallelWind == 0x01) {
				iWind = 0x00;
			}
			
			for (int i = 0; i < mWinMode.length; i++) {
				if (i == iWind) {
					mWinMode[i].setSelected(true);
				} else {
					mWinMode[i].setSelected(false);
				}
			}
			
			if (mCanSeek) {
				mSeekBar.setProgress(airInfo.mWindRate);
			}
		}
	}

	@Override
	public void onClick(View v) {
		byte[] datas = new byte[2];
		datas[0] = (byte) 0xAC; 
		switch (v.getId()) {
		case R.id.honda_btn_ac_on:
			datas[1] = 0x01; 
			break;
		case R.id.honda_btn_ac_off:
			datas[1] = 0x02; 
			break;
		case R.id.honda_btn_win_para:
			datas[1] = 0x03;
			break;
		case R.id.honda_btn_win_dn:
			datas[1] = 0x05;
			break;
		case R.id.honda_btn_win_para_dn:
			datas[1] = 0x04;
			break;
		case R.id.honda_btn_win_up_dn:
			datas[1] = 0x06;
			break;
		default:
			datas = null;
			break;
		}
		if (datas != null) {
			sendMsg((byte) ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST, datas, mProtocol);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser) {
			byte[] datas = new byte[2];
			datas[0] = (byte) 0xAD; 
			datas[1] = (byte) progress;
			sendMsg((byte) ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST, datas, mProtocol);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mCanSeek = false;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mCanSeek = true;
	}
}
