package com.can.ui.psa;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.PsaMemSpeed;
import com.can.parser.raise.psa.RePsaProtocol;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;

public class CanPsaMemSpeed extends CanFrament implements OnSeekBarChangeListener, OnClickListener{
	private PsaMemSpeed mPsaMemSpeed = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private int[] mSpeedId = {
		R.id.psa_tv_mem_speed_value1,
		R.id.psa_tv_mem_speed_value2,
		R.id.psa_tv_mem_speed_value3,
		R.id.psa_tv_mem_speed_value4,
		R.id.psa_tv_mem_speed_value5
	};
	
	private int[] mSeekBarId = {
		R.id.psa_skbar_speed1,
		R.id.psa_skbar_speed2,
		R.id.psa_skbar_speed3,
		R.id.psa_skbar_speed4,
		R.id.psa_skbar_speed5,
	};
	
	private int[] mBoxId = {
		R.id.psa_box_speed1,
		R.id.psa_box_speed2,
		R.id.psa_box_speed3,
		R.id.psa_box_speed4,
		R.id.psa_box_speed5
	};
	
	private TextView[] mTvSpeed = new TextView[mSpeedId.length];
	private SeekBar[] mSeekBars = new SeekBar[mSeekBarId.length];
	private CheckBox[] mBoxs = new CheckBox[mBoxId.length];
	private CheckBox   mCheckBoxSel = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_mem_speed, null);
			initView();
		}
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		getData(DDef.PSA_MEM_SPEED);
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mSpeedId.length; i++) {
				mTvSpeed[i] = (TextView) mView.findViewById(mSpeedId[i]);
			}
			
			for (int i = 0; i < mSeekBarId.length; i++) {
				mSeekBars[i] = (SeekBar) mView.findViewById(mSeekBarId[i]);
				mSeekBars[i].setOnSeekBarChangeListener(this);
				mSeekBars[i].setMax(255);
			}
			
			for (int i = 0; i < mBoxId.length; i++) {
				mBoxs[i] = (CheckBox) mView.findViewById(mBoxId[i]);
				mBoxs[i].setOnClickListener(this);
			}
			mCheckBoxSel = (CheckBox) mView.findViewById(R.id.psa_btn_open_mem_speed);
			mCheckBoxSel.setOnClickListener(this);
			mView.findViewById(R.id.psa_btn_reset_mem_speed).setOnClickListener(this);
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol)msg.obj;
				getData(DDef.PSA_MEM_SPEED);
				break;
			case DDef.PSA_MEM_SPEED:
				mPsaMemSpeed = (PsaMemSpeed) msg.obj;
				setSpeedInfo();
				break;
			default:
				break;
			}
		}
	};

	private void setSpeedInfo() {
		if (mView != null && mPsaMemSpeed != null) {
			for (int i = 0; i < mBoxs.length; i++) {
				mBoxs[i].setChecked(mPsaMemSpeed.mSpeedsSel[i] == 0x01 ? true : false);
				mSeekBars[i].setProgress(mPsaMemSpeed.mSpeeds[i]);
				mSeekBars[i].setEnabled(mBoxs[i].isChecked());
				mTvSpeed[i].setText(mPsaMemSpeed.mSpeeds[i] + getString(R.string.str_psa_speed_unit));
			}
			mCheckBoxSel.setChecked(mPsaMemSpeed.mMemory == 0x01 ? true : false);
			Log.i("LHB","setspeedInfo mPsaMemSpeed.mMemory:"+mPsaMemSpeed.mMemory);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			TextView textView = null;
			switch (seekBar.getId()) {
			case R.id.psa_skbar_speed1:
				textView = mTvSpeed[0];
				break;
			case R.id.psa_skbar_speed2:
				textView = mTvSpeed[1];
				break;
			case R.id.psa_skbar_speed3:
				textView = mTvSpeed[2];
				break;
			case R.id.psa_skbar_speed4:
				textView = mTvSpeed[3];
				break;
			case R.id.psa_skbar_speed5:
				textView = mTvSpeed[4];
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
		case R.id.psa_skbar_speed1:
			textView = mTvSpeed[0];
			break;
		case R.id.psa_skbar_speed2:
			textView = mTvSpeed[1];
			break;
		case R.id.psa_skbar_speed3:
			textView = mTvSpeed[2];
			break;
		case R.id.psa_skbar_speed4:
			textView = mTvSpeed[3];
			break;
		case R.id.psa_skbar_speed5:
			textView = mTvSpeed[4];
			break;
		default:
			break;
		}
		if (textView != null) {
			textView.setText(seekBar.getProgress() + getString(R.string.str_psa_speed_unit));
			sendData(true);
		}
	}

	@Override
	public void onClick(View v) {
		int iIndex = -1;
		switch (v.getId()) {
		case R.id.psa_box_speed1:
			iIndex = 0;
			break;
		case R.id.psa_box_speed2:
			iIndex = 1;
			break;
		case R.id.psa_box_speed3:
			iIndex = 2;
			break;
		case R.id.psa_box_speed4:
			iIndex = 3;
			break;
		case R.id.psa_box_speed5:
			iIndex = 4;
			break;
		case R.id.psa_btn_reset_mem_speed:
			sendData(false);
			break;
		case R.id.psa_btn_open_mem_speed:
			sendData(true);
			break;
		default:
			return;
		}
		if (iIndex != -1) {
			mSeekBars[iIndex].setEnabled(mBoxs[iIndex].isChecked());
			sendData(true);
		}
	}
	
	private void sendData(boolean isUser) {
		byte[] datas = new byte[6];
		if (isUser) {
			datas[0] = (byte) (mCheckBoxSel.isChecked() ? 0x40 : 0x00);
		} else {
			datas[0] = (byte) 0x80;
		}
		
		for (int i = 0; i < mSeekBars.length; i++) {
			byte byData = (byte) (mBoxs[i].isChecked() ? 0x01 : 0x00);
			datas[0] = (byte) (datas[0] | (byData << (5 - i)));
			datas[i+1] = (byte) mSeekBars[i].getProgress();
		}
		sendMsg(RePsaProtocol.DATA_TYPE_REMEM_SPEED_REQUEST, datas, mProtocol);
	}
}
