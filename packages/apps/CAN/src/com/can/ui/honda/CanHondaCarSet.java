package com.can.ui.honda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.can.activity.R;
import com.can.parser.DDef.CarSetting;
import com.can.parser.raise.honda.ReHondaProtocol;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.AutoText;

public class CanHondaCarSet extends CanFrament implements OnClickListener{
	private Protocol mProtocol = null;
	private CarSetting mCarSetting = new CarSetting();
	private View mView = null;

	private int[] mTxtID = { R.id.tv_honda_set1, R.id.tv_honda_set2, R.id.tv_honda_set3,
							R.id.tv_honda_set4, R.id.tv_honda_set5, R.id.tv_honda_set6,
							R.id.tv_honda_set7, R.id.tv_honda_set8, R.id.tv_honda_set9,
							R.id.tv_honda_set10, R.id.tv_honda_set11, R.id.tv_honda_set12,
							R.id.tv_honda_set13, R.id.tv_honda_set14, R.id.tv_honda_set15,
							R.id.tv_honda_set16, R.id.tv_honda_set17 };
	
	private int[] mBtnLeftID = { R.id.btn_honda_left_set1, R.id.btn_honda_left_set2, R.id.btn_honda_left_set3,
								R.id.btn_honda_left_set4, R.id.btn_honda_left_set5, R.id.btn_honda_left_set6,
								R.id.btn_honda_left_set7, R.id.btn_honda_left_set8, R.id.btn_honda_left_set9,
								R.id.btn_honda_left_set10, R.id.btn_honda_left_set11, R.id.btn_honda_left_set12,
								R.id.btn_honda_left_set13, R.id.btn_honda_left_set14, R.id.btn_honda_left_set15,
								R.id.btn_honda_left_set16, R.id.btn_honda_left_set17 };
	
	private int[] mBtnRightID = { R.id.btn_honda_right_set1, R.id.btn_honda_right_set2, R.id.btn_honda_right_set3,
								R.id.btn_honda_right_set4, R.id.btn_honda_right_set5, R.id.btn_honda_right_set6,
								R.id.btn_honda_right_set7, R.id.btn_honda_right_set8, R.id.btn_honda_right_set9,
								R.id.btn_honda_right_set10, R.id.btn_honda_right_set11, R.id.btn_honda_right_set12,
								R.id.btn_honda_right_set13, R.id.btn_honda_right_set14, R.id.btn_honda_right_set15,
								R.id.btn_honda_right_set16, R.id.btn_honda_right_set17 };
	
	private int[] mCheckBoxID = { R.id.checkbox_honda_set1, R.id.checkbox_honda_set2, R.id.checkbox_honda_set3,
								R.id.checkbox_honda_set4, R.id.checkbox_honda_set5, R.id.checkbox_honda_set6,
								R.id.checkbox_honda_set7, R.id.checkbox_honda_set8, R.id.checkbox_honda_set9,
								R.id.checkbox_honda_set10, R.id.checkbox_honda_set11, R.id.checkbox_honda_set12,
								R.id.checkbox_honda_set13 };
	
	private int[] mResetID = { R.string.str_refuel, R.string.str_ignite_off, R.string.str_manually };
	private int[] mTimeID = { R.string.str_time_0, R.string.str_time_15, R.string.str_time_30,
								 R.string.str_time_60, R.string.str_time_90 };
	private int[] mSensitivityID = { R.string.str_sensitivity_min, R.string.str_sensitivity_low, R.string.str_sensitivity_mid,
								  R.string.str_sensitivity_high, R.string.str_sensitivity_max };
	private int[] mAutoLockID = { R.string.str_lock_by_speed, R.string.str_lock_by_stalls, R.string.str_lock_close };
	private int[] mAutoUnLockID = { R.string.str_unlock_by_drive_door, R.string.str_unlock_by_stalls, 
									R.string.str_unlock_by_acc, R.string.str_unlock_by_close};
    private int[] mDistanceID = { R.string.str_distance_fra, R.string.str_distance_center, R.string.str_distance_near};
    private int[] mMinorSysSetID = { R.string.str_set_center, R.string.str_set_wide, R.string.str_set_warning_only};
    
    private String[] mStrOutTemp = { "-5", "-4", "-3", "-2", "-1", "0", "+1", "+2", "+3", "+4", "+5"};
    private String[] mStrReset = new String[mResetID.length];
	private String[] mStrTime = new String[mTimeID.length];
	private String[] mStrSensitivity = new String[mSensitivityID.length];
	private String[] mStrAutoLock = new String[mAutoLockID.length];
	private String[] mStrAutoUnlock = new String[mAutoUnLockID.length];
	private String[] mStrDistance = new String[mDistanceID.length];
	private String[] mStrMinorSysSet = new String[mMinorSysSetID.length];
	
	private AutoText[] mTextSet = new AutoText[mTxtID.length];
	private CheckBox[] mChBoxs = new CheckBox[mCheckBoxID.length];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.honda_car_set, null);
			initView();
			initString();
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getData(DDef.CAR_SET_CMD_ID);
	}

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mBtnLeftID.length; i++) {
				View view = mView.findViewById(mBtnLeftID[i]);
				if (view != null) {
					view.setOnClickListener(this);
				}
			}
			
			for (int i = 0; i < mBtnRightID.length; i++) {
				View view = mView.findViewById(mBtnRightID[i]);
				if (view != null) {
					view.setOnClickListener(this);
				}
			}
			
			for (int i = 0; i < mCheckBoxID.length; i++) {
				mChBoxs[i] = (CheckBox) mView.findViewById(mCheckBoxID[i]);
				if (mChBoxs[i] != null) {
					mChBoxs[i].setOnClickListener(this);
				}
			}
			
			for (int i = 0; i < mTxtID.length; i++) {
				mTextSet[i] = (AutoText) mView.findViewById(mTxtID[i]);
			}
		}
	}
	
	private void initString() {
		for (int i = 0; i < mResetID.length; i++) {
			mStrReset[i] = getString(mResetID[i]);
		}
		
		for (int i = 0; i < mTimeID.length; i++) {
			mStrTime[i] = getString(mTimeID[i]);
		}
		
		for (int i = 0; i < mSensitivityID.length; i++) {
			mStrSensitivity[i] = getString(mSensitivityID[i]);
		}
		
		for (int i = 0; i < mAutoLockID.length; i++) {
			mStrAutoLock[i] = getString(mAutoLockID[i]);
		}
		
		for (int i = 0; i < mAutoUnLockID.length; i++) {
			mStrAutoUnlock[i] = getString(mAutoUnLockID[i]);
		}
		
		for (int i = 0; i < mDistanceID.length; i++) {
			mStrDistance[i] = getString(mDistanceID[i]);
		}
		
		for (int i = 0; i < mMinorSysSetID.length; i++) {
			mStrMinorSysSet[i] = getString(mMinorSysSetID[i]);
		}
	}

	@Override
	public void onClick(View v) {
		byte[] data = new byte[2];
		switch (v.getId()) {
		case R.id.btn_honda_left_set1:
			data[0] = 0x02;
			if (mCarSetting.mTripAReset > 0) {
				data[1] = (byte) (mCarSetting.mTripAReset - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set1:
			data[0] = 0x02;
			if (mCarSetting.mTripAReset < 2) {
				data[1] = (byte) (mCarSetting.mTripAReset + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set2:
			data[0] = 0x03;
			if (mCarSetting.mTripBReset > 0) {
				data[1] = (byte) (mCarSetting.mTripBReset - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set2:
			data[0] = 0x03;
			if (mCarSetting.mTripBReset < 2) {
				data[1] = (byte) (mCarSetting.mTripBReset + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set3:
			data[0] = 0x00;
			if (mCarSetting.mOutTemp > 0) {
				data[1] = (byte) (mCarSetting.mOutTemp - 1);
			} else {
				data[1] = 10;
			}
			break;
		case R.id.btn_honda_right_set3:
			data[0] = 0x00;
			if (mCarSetting.mOutTemp < 10) {
				data[1] = (byte) (mCarSetting.mOutTemp + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set4:
			data[0] = 0x06;
			if (mCarSetting.mAutoLightSend > 0) {
				data[1] = (byte) (mCarSetting.mAutoLightSend - 1);
			} else {
				data[1] = 4;
			}
			break;
		case R.id.btn_honda_right_set4:
			data[0] = 0x06;
			if (mCarSetting.mAutoLightSend < 4) {
				data[1] = (byte) (mCarSetting.mAutoLightSend + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set5:
			data[0] = 0x05;
			if (mCarSetting.mHeadLightTime > 0) {
				data[1] = (byte) (mCarSetting.mHeadLightTime - 1);
			} else {
				data[1] = 3;
			}
			break;
		case R.id.btn_honda_right_set5:
			data[0] = 0x05;
			if (mCarSetting.mHeadLightTime < 3) {
				data[1] = (byte) (mCarSetting.mHeadLightTime + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set6:
			data[0] = 0x04;
			if (mCarSetting.mInLightDimTime > 0) {
				data[1] = (byte) (mCarSetting.mInLightDimTime - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set6:
			data[0] = 0x04;
			if (mCarSetting.mInLightDimTime < 2) {
				data[1] = (byte) (mCarSetting.mInLightDimTime + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set7:
		case R.id.btn_honda_right_set7:
			data[0] = 0x09;
			data[1] = (byte) (mCarSetting.mDoorUnLock == 0x00 ? 1 : 0);
			break;
		case R.id.btn_honda_left_set8:
			data[0] = 0x0B;
			if (mCarSetting.mRelockTime > 0) {
				data[1] = (byte) (mCarSetting.mRelockTime - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set8:
			data[0] = 0x0B;
			if (mCarSetting.mRelockTime < 2) {
				data[1] = (byte) (mCarSetting.mRelockTime + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set9:
			data[0] = 0x08;
			if (mCarSetting.mAutoUnLockDoor > 0) {
				data[1] = (byte) (mCarSetting.mAutoUnLockDoor - 1);
			} else {
				data[1] = 3;
			}
			break;
		case R.id.btn_honda_right_set9:
			data[0] = 0x08;
			if (mCarSetting.mAutoUnLockDoor < 3) {
				data[1] = (byte) (mCarSetting.mAutoUnLockDoor + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set10:
			data[0] = 0x07;
			if (mCarSetting.mAutoLockDoor > 0) {
				data[1] = (byte) (mCarSetting.mAutoLockDoor - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set10:
			data[0] = 0x07;
			if (mCarSetting.mAutoLockDoor < 2) {
				data[1] = (byte) (mCarSetting.mAutoLockDoor + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set11:
		case R.id.btn_honda_right_set11:
			data[0] = 0x19;
			data[1] = (byte) (mCarSetting.mDoorLockMode == 0x00 ? 0x01 : 0x00);
			break;
		case R.id.btn_honda_left_set12:
			data[0] = 0x1B;
			if (mCarSetting.mAutoInSend > 0) {
				data[1] = (byte) (mCarSetting.mAutoInSend - 1);
			} else {
				data[1] = 4;
			}
			break;
		case R.id.btn_honda_right_set12:
			data[0] = 0x1B;
			if (mCarSetting.mAutoInSend < 4) {
				data[1] = (byte) (mCarSetting.mAutoInSend + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set13:
			data[0] = 0x12;
			if (mCarSetting.mAdjustAlarm > 0) {
				data[1] = (byte) (mCarSetting.mAdjustAlarm - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set13:
			data[0] = 0x12;
			if (mCarSetting.mAdjustAlarm < 2) {
				data[1] = (byte) (mCarSetting.mAdjustAlarm + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set14:
		case R.id.btn_honda_right_set14:
			data[0] = 0x15;
			data[1] = (byte) (mCarSetting.mSpeedUnit == 0x00 ? 0x01 : 0x00);
			break;
		case R.id.btn_honda_left_set15:
		case R.id.btn_honda_right_set15:
			data[0] = 0x1E;
			data[1] = (byte) (mCarSetting.mAlarmSysVolume == 0x00 ? 0x01 : 0x00);
			break;
		case R.id.btn_honda_left_set16:
			data[0] = 0x1F;
			if (mCarSetting.mWarnDisctance > 0) {
				data[1] = (byte) (mCarSetting.mWarnDisctance - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set16:
			data[0] = 0x1F;
			if (mCarSetting.mWarnDisctance < 2) {
				data[1] = (byte) (mCarSetting.mWarnDisctance + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.btn_honda_left_set17:
			data[0] = 0x22;
			if (mCarSetting.mLaneDeparture > 0) {
				data[1] = (byte) (mCarSetting.mLaneDeparture - 1);
			} else {
				data[1] = 2;
			}
			break;
		case R.id.btn_honda_right_set17:
			data[0] = 0x22;
			if (mCarSetting.mLaneDeparture < 2) {
				data[1] = (byte) (mCarSetting.mLaneDeparture + 1);
			} else {
				data[1] = 0;
			}
			break;
		case R.id.checkbox_honda_set1:
			data[0] = 0x0A;
			data[1] = (byte) (mCarSetting.mKeyLockAnswer ? 0 : 1);
			break;
		case R.id.checkbox_honda_set2:
			data[0] = 0x0D;
			data[1] = (byte) (mCarSetting.mKeylessBeep ? 0 : 1);
			break;
		case R.id.checkbox_honda_set3:
			data[0] = 0x18;
			data[1] = (byte) (mCarSetting.mRemoteSys ? 0 : 1);
			break;
		case R.id.checkbox_honda_set4:
			data[0] = 0x1A;
			data[1] = (byte) (mCarSetting.mKeylessLight ? 0 : 1);
			break;
		case R.id.checkbox_honda_set5:
			data[0] = 0x13;
			data[1] = (byte) (mCarSetting.mFuelBackLight ? 0 : 1);
			break;
		case R.id.checkbox_honda_set6:
			data[0] = 0x14;
			data[1] = (byte) (mCarSetting.mMsgNotify ? 0 : 1);
			break;
		case R.id.checkbox_honda_set7:
			data[0] = 0x16;
			data[1] = (byte) (mCarSetting.mTachometer ? 0 : 1);
			break;
		case R.id.checkbox_honda_set8:
			data[0] = 0x17;
			data[1] = (byte) (mCarSetting.mWalkAwayLock ? 0 : 1);
			break;
		case R.id.checkbox_honda_set9:
			data[0] = 0x1C;
			data[1] = (byte) (mCarSetting.mAutoHeadLight ? 0 : 1);
			break;
		case R.id.checkbox_honda_set10:
			data[0] = 0x1D;
			data[1] = (byte) (mCarSetting.mEngineAutoMatic ? 0 : 1);
			break;
		case R.id.checkbox_honda_set11:
			data[0] = 0x20;
			data[1] = (byte) (mCarSetting.mAccPromptTone ? 0 : 1);
			break;
		case R.id.checkbox_honda_set12:
			data[0] = 0x21;
			data[1] = (byte) (mCarSetting.mPauseLKAS ? 0 : 1);
			break;
		case R.id.checkbox_honda_set13:
			data[0] = 0x23;
			data[1] = (byte) (mCarSetting.mTachmeterSet ? 0 : 1);
			break;
		default:
			return;
		}
		sendMsg(ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST, data, mProtocol);
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol)msg.obj;
				byte[] datas = new byte[1];
				datas[0] = 0x32;
				sendMsg(ReHondaProtocol.DATA_TYPE_CAR_INFO_REQUEST, datas, mProtocol);
				break;
			case DDef.CAR_SET_CMD_ID:
				mCarSetting = (CarSetting)msg.obj;
				setCarSetSts(mCarSetting);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void setCarSetSts(CarSetting carset) {
		if (isVisible()) {
			mChBoxs[0].setChecked(carset.mKeyLockAnswer);
			mChBoxs[1].setChecked(carset.mKeylessBeep);
			mChBoxs[2].setChecked(carset.mRemoteSys);
			mChBoxs[3].setChecked(carset.mKeylessLight);
			mChBoxs[4].setChecked(carset.mFuelBackLight);
			mChBoxs[5].setChecked(carset.mMsgNotify);
			mChBoxs[6].setChecked(carset.mTachometer);
			mChBoxs[7].setChecked(carset.mWalkAwayLock);
			mChBoxs[8].setChecked(carset.mAutoHeadLight);
			mChBoxs[9].setChecked(carset.mEngineAutoMatic);
			mChBoxs[10].setChecked(carset.mAccPromptTone);
			mChBoxs[11].setChecked(carset.mPauseLKAS);
			mChBoxs[12].setChecked(carset.mTachmeterSet);
			mTextSet[0].setText(mStrReset[carset.mTripAReset]);
			mTextSet[1].setText(mStrReset[carset.mTripBReset]);
			mTextSet[2].setText(mStrOutTemp[carset.mOutTemp]);
			mTextSet[3].setText(mStrSensitivity[carset.mAutoLightSend]);
			mTextSet[4].setText(mStrTime[carset.mHeadLightTime]);
			mTextSet[5].setText(mStrTime[carset.mInLightDimTime + 1]);
			mTextSet[6].setText(getString(carset.mDoorUnLock == 0x00 ? R.string.str_unlock_drive_door : R.string.str_unlock_all_door));
			mTextSet[7].setText(mStrTime[carset.mRelockTime + 2]);
			mTextSet[8].setText(mStrAutoUnlock[carset.mAutoUnLockDoor]);
			mTextSet[9].setText(mStrAutoLock[carset.mAutoLockDoor]);
			mTextSet[10].setText(getString(carset.mDoorLockMode == 0x01 ? R.string.str_unlock_drive_door : R.string.str_unlock_all_door));
			mTextSet[11].setText(mStrSensitivity[carset.mAutoInSend]);
			mTextSet[12].setText(mStrSensitivity[3 - carset.mAdjustAlarm]);
			mTextSet[13].setText(carset.mSpeedUnit == 0x00 ? R.string.str_speed_unit_kmh : R.string.str_speed_unit_mph);
			mTextSet[14].setText(carset.mAlarmSysVolume == 0x00 ? R.string.str_volume_min : R.string.str_volume_max);
			mTextSet[15].setText(mStrDistance[carset.mWarnDisctance]);
			mTextSet[16].setText(mStrMinorSysSet[carset.mLaneDeparture]);
		}
	}
}
