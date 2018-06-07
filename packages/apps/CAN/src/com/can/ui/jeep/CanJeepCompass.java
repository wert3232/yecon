package com.can.ui.jeep;

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
import com.can.parser.DDef.CompassInfo;
import com.can.parser.raise.jeep.ReJeepProtocol;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.Compass;

/**
 * ClassName:CanJeepCompass
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-14下午5:14:36
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJeepCompass extends CanFrament implements OnClickListener,
		OnSeekBarChangeListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private boolean mbCalibration = false;
	private Compass mObjCompass = null;
	private TextView mObjBtnAction = null;
	private TextView mObjCompassState = null;
	private SeekBar mObjCompassoffset = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {
			mObjView = inflater
					.inflate(R.layout.jeep_compass, container, false);
			init();
		}

		return mObjView;
	}

	private void init() {
		if (mObjView != null) {
			mObjCompass = (Compass) mObjView
					.findViewById(R.id.jeep_compass_clock);
			mObjCompassState = (TextView) mObjView
					.findViewById(R.id.compass_tv_state);
			mObjCompassoffset = (SeekBar) mObjView
					.findViewById(R.id.jeep_compass_skbar_area);
			mObjCompassoffset.setOnSeekBarChangeListener(this);

			mObjBtnAction = (TextView) mObjView
					.findViewById(R.id.tv_compass_calibration);
			mObjBtnAction.setOnClickListener(this);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	private void setAttr(CompassInfo objCompassInfo) {
		if (objCompassInfo != null) {
			boolean bAngle = false;
			String strInfo = getString(R.string.str_compass_f_calibration);

			if (objCompassInfo.mbyCompassState == 0) {
				mbCalibration = false;
				strInfo = getString(R.string.str_compass_f_calibration);
			} else if (objCompassInfo.mbyCompassState == 1) {
				bAngle = true;
				mbCalibration = true;
				strInfo = getString(R.string.str_compass_ing_calibration);
			} else if (objCompassInfo.mbyCompassState == 2) {
				mbCalibration = false;
				strInfo = getString(R.string.str_compass_fa_calibration);
			}

			if (mObjCompassState != null) {
				if (bAngle) {
					mObjCompassState.setText(strInfo + " "
							+ objCompassInfo.Compassarea);
				} else {
					mObjCompassState.setText(strInfo);
				}
			}

			if (mObjCompassoffset != null) {
				mObjCompassoffset.setMax(15);
				mObjCompassoffset.setProgress(objCompassInfo.Compassarea - 1);
			}

			if (mObjCompass != null) {
				int iAngle = 0;
				switch (objCompassInfo.mbyCompassDir) {
				case 0x10:
					iAngle = 0;
					break;
				case 0x11:
					iAngle = 45;
					break;
				case 0x12:
					iAngle = 90;
					break;
				case 0x13:
					iAngle = 135;
					break;
				case 0x14:
					iAngle = 180;
					break;
				case 0x15:
					iAngle = 225;
					break;
				case 0x16:
					iAngle = 270;
					break;
				case 0x17:
					iAngle = 315;
					break;
				}

				mObjCompass.setAngle(iAngle);
			}
		}
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				Inquiry();
				break;
			case DDef.COMPASS_CMD_ID:
				setAttr((CompassInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_compass_calibration:
			if (mObjProtocol != null) {
				byte[] bydata = new byte[2];
				bydata[0] = 0x56;
				bydata[1] = (byte) (mbCalibration ? 0x00 : 0x01);
				super.sendMsg(ReJeepProtocol.DATA_TYPE_CAR_SET, bydata,
						mObjProtocol);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		if (mObjProtocol != null) {
			byte[] bydata = new byte[3];
			bydata[0] = 0x57;
			bydata[1] = (byte) (arg0.getProgress() + 1);
			super.sendMsg(ReJeepProtocol.DATA_TYPE_CAR_SET, bydata,
					mObjProtocol);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	private void Inquiry() {
		if (mObjProtocol != null) {
			byte[] bydata = new byte[1];
			bydata[0] = 0x0b;
			super.sendMsg(ReJeepProtocol.DATA_TYPE_REQUEST_INFO, bydata,
					mObjProtocol);
		}
	}
}
