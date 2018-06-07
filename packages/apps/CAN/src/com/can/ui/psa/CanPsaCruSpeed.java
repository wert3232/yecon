package com.can.ui.psa;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PsaCruSpeed;
import com.can.parser.raise.psa.RePsaProtocol;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;

public class CanPsaCruSpeed extends CanFrament implements OnSeekBarChangeListener, OnClickListener{
	private boolean mIsCruSpeed = true;
	private Protocol mProtocol = null;
	private PsaCruSpeed mPsaCruSpeed = null;
	private View mView = null;

	private int[] mTextId = {
		R.id.psa_tv_cru_speed_value1,
		R.id.psa_tv_cru_speed_value2,
		R.id.psa_tv_cru_speed_value3,
		R.id.psa_tv_cru_speed_value4,
		R.id.psa_tv_cru_speed_value5,
		R.id.psa_tv_cru_speed_value6
	};
	
	private int[] mSeekBarId = {
		R.id.psa_skbar_cru_speed1,
		R.id.psa_skbar_cru_speed2,
		R.id.psa_skbar_cru_speed3,
		R.id.psa_skbar_cru_speed4,
		R.id.psa_skbar_cru_speed5,
		R.id.psa_skbar_cru_speed6
	};
	
	private TextView[] mTextViews = new TextView[mTextId.length];
	private SeekBar[]  mSeekBars = new SeekBar[mSeekBarId.length];
	private TextView mTvCurSpeed = null;
	private TextView mTvLimitSpeed = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_cru_speed, null);
			initView();
		}
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		getData(DDef.PSA_CRU_SPEED);
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mTextId.length; i++) {
				mTextViews[i] = (TextView) mView.findViewById(mTextId[i]);
			}
			
			for (int i = 0; i < mSeekBarId.length; i++) {
				mSeekBars[i] = (SeekBar) mView.findViewById(mSeekBarId[i]);
				mSeekBars[i].setOnSeekBarChangeListener(this);
				mSeekBars[i].setMax(255);
			}
			mView.findViewById(R.id.psa_btn_user_set).setOnClickListener(this);
			mView.findViewById(R.id.psa_btn_reset).setOnClickListener(this);
			
			mTvCurSpeed = (TextView) mView.findViewById(R.id.psa_btn_cru_speed);
			mTvCurSpeed.setOnClickListener(this);
			mTvCurSpeed.setSelected(true);
			mTvLimitSpeed = (TextView) mView.findViewById(R.id.psa_btn_limit_speed);
			mTvLimitSpeed.setOnClickListener(this);
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol)msg.obj;
				getData(DDef.PSA_CRU_SPEED);
				break;
			case DDef.PSA_CRU_SPEED:
				mPsaCruSpeed = (PsaCruSpeed)msg.obj;
				setSpeedInfo();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.psa_btn_cru_speed:
			mIsCruSpeed = true;
			mTvCurSpeed.setSelected(true);
			mTvLimitSpeed.setSelected(false);
			setSpeedInfo();
			break;
		case R.id.psa_btn_limit_speed:
			mIsCruSpeed = false;
			mTvCurSpeed.setSelected(false);
			mTvLimitSpeed.setSelected(true);
			setSpeedInfo();
			break;
		case R.id.psa_btn_user_set:
			sendData(true);
			break;
		case R.id.psa_btn_reset:
			sendData(false);
			break;
		default:
			break;
		}
	}

	
	private void sendData(boolean isUser) {
		byte[] datas = new byte[7];
		datas[0] = (byte) (0x00 | (isUser ? 0x10 : 0x20) | (mIsCruSpeed ? 0x40 : 0x80));
		for (int i = 0; i < mSeekBars.length; i++) {
			datas[i+1] = (byte) mSeekBars[i].getProgress();
		}
		sendMsg(RePsaProtocol.DATA_TYPE_SPEED_REQUEST, datas, mProtocol);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			TextView textView = null;
			switch (seekBar.getId()) {
			
			case R.id.psa_skbar_cru_speed1:
				textView = mTextViews[0];
				break;
			case R.id.psa_skbar_cru_speed2:
				textView = mTextViews[1];
				break;
			case R.id.psa_skbar_cru_speed3:
				textView = mTextViews[2];
				break;
			case R.id.psa_skbar_cru_speed4:
				textView = mTextViews[3];
				break;
			case R.id.psa_skbar_cru_speed5:
				textView = mTextViews[4];
				break;
			case R.id.psa_skbar_cru_speed6:
				textView = mTextViews[5];
				break;
			default:
				break;
			}
			if (textView != null) {
				textView.setText(progress + getString(R.string.str_psa_speed_unit));
			}
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		TextView textView = null;
		switch (seekBar.getId()) {
		
		case R.id.psa_skbar_cru_speed1:
			textView = mTextViews[0];
			break;
		case R.id.psa_skbar_cru_speed2:
			textView = mTextViews[1];
			break;
		case R.id.psa_skbar_cru_speed3:
			textView = mTextViews[2];
			break;
		case R.id.psa_skbar_cru_speed4:
			textView = mTextViews[3];
			break;
		case R.id.psa_skbar_cru_speed5:
			textView = mTextViews[4];
			break;
		case R.id.psa_skbar_cru_speed6:
			textView = mTextViews[5];
			break;
		default:
			break;
		}
		if (textView != null) {
			textView.setText(seekBar.getProgress() + getString(R.string.str_psa_speed_unit));
		}
	}
	
	private void setSpeedInfo() {
		if (mView != null && mPsaCruSpeed != null) {
			for (int i = 0; i < mTextViews.length; i++) {
				int iSpeed = mIsCruSpeed ? mPsaCruSpeed.mCruSpeed[i] : mPsaCruSpeed.mLimitSpeed[i];
				mTextViews[i].setText(String.valueOf(iSpeed) + getString(R.string.str_psa_speed_unit));
				mSeekBars[i].setProgress(iSpeed);
			}
		}
	}
}
