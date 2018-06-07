package com.can.ui.honda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.raise.honda.Re12CrvProtocol;
import com.can.parser.raise.honda.ReHondaProtocol;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.Compass;

public class CanHondaCompass extends CanFrament implements OnClickListener{
	private Protocol mProtocol = null;
	private View mView = null;
	
	private SeekBar mSeekBarArea = null;
	private TextView mTvState = null;
	private Compass mClock = null;
	private TextView mBtnCalib = null;
	private boolean mCanSeek = true;
	
	private boolean mCalibration = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_compass, null);
			initView();
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getData(DDef.COMPASS_CMD_ID);
	}

	private void initView() {
		if (mView != null) {
			mTvState = (TextView) mView.findViewById(R.id.compass_tv_state);
			mSeekBarArea = (SeekBar) mView.findViewById(R.id.compass_skbar_area);
			mSeekBarArea.setMax(14);
			mSeekBarArea.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					
					byte[] datas = new byte[2];
					if (CanHondaCarInfo.sCarType == DDef.CarHondaType.HONDA_GM) {
						datas[0] = (byte) 0xC1;
						datas[1] = (byte) (seekBar.getProgress() + 1);
						sendMsg(ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST, datas, mProtocol);
						
					} else if (CanHondaCarInfo.sCarType == DDef.CarHondaType.HONDA_CRV) {
						datas[0] = (byte) 0x02;
						datas[1] = (byte) (seekBar.getProgress() + 1);
						sendMsg(Re12CrvProtocol.DATA_TYPE_COMPASS_REQUEST, datas, mProtocol);
						
					}
					mCanSeek = true;
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					mCanSeek = false;
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
				}
			});
			mClock = (Compass) mView.findViewById(R.id.compass_clock);
			
			mBtnCalib = (TextView) mView.findViewById(R.id.tv_compass_calibration);
			mBtnCalib.setOnClickListener(this);
		}
	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				if (CanHondaCarInfo.sCarType == DDef.CarHondaType.HONDA_GM){
					byte[] datas = new byte[1];
					datas[0] = (byte) 0xd2;
					sendMsg(ReHondaProtocol.DATA_TYPE_CAR_INFO_REQUEST, datas, mProtocol);
				}else {
					getData(DDef.COMPASS_CMD_ID);
				}
				break;
			case DDef.COMPASS_CMD_ID:
				setCompass((CompassInfo) msg.obj);
				break;
			default:
				break;
			}
		}
		
	};
	
	private void setCompass(CompassInfo compassInfo) {
		if (isVisible()) {
			if (mCanSeek) {
				mSeekBarArea.setProgress(compassInfo.Compassarea - 1);
			}
			mClock.setAngle(compassInfo.compassAngle);
			String str = "";
			if (compassInfo.CompassAdjust) {
				str = getString(R.string.str_compass_is_calibration);
			} else {
				str = getString(R.string.str_compass_angle) + compassInfo.compassAngle +"°";
			}
			int  strId = compassInfo.CompassAdjust ? R.string.str_compass_exit_calibration : R.string.str_compass_exe_calibration;
			mBtnCalib.setText(getString(strId));
			mTvState.setText(str);
			mCalibration = compassInfo.CompassAdjust;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_compass_calibration:
			byte[] datas = new byte[2];
			if (CanHondaCarInfo.sCarType == DDef.CarHondaType.HONDA_GM) {
				datas[0] = (byte) 0xc0;
				datas[1] = (byte) (mCalibration ? 0x00 : 0x01);
				sendMsg(ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST, datas, mProtocol);
			} else if (CanHondaCarInfo.sCarType == DDef.CarHondaType.HONDA_CRV) {
				datas[0] = (byte) 0x01;
				datas[1] = (byte) (mCalibration ? 0x00 : 0x01);
				sendMsg(Re12CrvProtocol.DATA_TYPE_COMPASS_REQUEST, datas, mProtocol);
			}
			break;
		default:
			break;
		}
	}
}
