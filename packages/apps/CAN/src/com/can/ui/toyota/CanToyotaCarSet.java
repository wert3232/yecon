package com.can.ui.toyota;

import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.SetInfo;
import com.can.parser.DDef.VehicleSettings;
import com.can.parser.Protocol;
import com.can.parser.raise.toyota.ReToyotaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanToyotaCarSet
 * 
 * @function:中控设置
 * @author Kim
 * @Date: 2016-6-23 下午2:30:03
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaCarSet extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;
	private CheckBox[] mObjCheckBoxs = null;
	private SetInfo mObjSetInfo = new SetInfo();
	private RadarInfo mObjRadarInfo = new RadarInfo();
	private VehicleSettings mVehicleSettings = new VehicleSettings();

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
			mObjView = inflater.inflate(R.layout.toyota_carset, container,
					false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		setAttr(mVehicleSettings);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void init() {
		if (mObjView != null) {

			int iIndex = 0;
			mObjCheckBoxs = new CheckBox[ResDef.mToyotaCheckboxId.length];
			for (int iArray : ResDef.mToyotaCheckboxId) {
				mObjCheckBoxs[iIndex] = (CheckBox) mObjView
						.findViewById(iArray);
				mObjCheckBoxs[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : ResDef.mToyotaBtnId) {
				mObjView.findViewById(iter).setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.checkbox_toyota_lockset_speedautolock_onoff:
			setCheckState2Msg(ReToyotaProtocol.AUTOLOCK_BY_SPEED,
					(byte) ((mObjCheckBoxs[0].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_linkageautolock_onoff:
			setCheckState2Msg(ReToyotaProtocol.AUTOLOCK_BY_SHIFT_FROM_P,
					(byte) ((mObjCheckBoxs[1].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_smartcarlock_onoff:
			setCheckState2Msg(
					ReToyotaProtocol.SMART_CAR_LOCK_AND_A_BUTTON_TO_START,
					(byte) ((mObjCheckBoxs[2].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_blocklinkunlock_onoff:
			setCheckState2Msg(ReToyotaProtocol.AUTOUNLOCK_BY_SHIFT_TO_P,
					(byte) ((mObjCheckBoxs[3].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_unlockthekey_onoff:
			setCheckState2Msg(ReToyotaProtocol.REMOTE_2_KEY_UNLOCK,
					(byte) ((mObjCheckBoxs[4].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_responselock_onoff:
			setCheckState2Msg(ReToyotaProtocol.LOCK_UNLOCK_LIGHT_FLASHING,
					(byte) ((mObjCheckBoxs[5].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_2presslock_onoff:
			setCheckState2Msg(ReToyotaProtocol.REMOTE_2_PRESS_UNLOCK,
					(byte) ((mObjCheckBoxs[6].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lockset_drivinglinkunlock_onoff:
			setCheckState2Msg(ReToyotaProtocol.DRIVE_DOOR_LINKAGE_UNLOCK,
					(byte) ((mObjCheckBoxs[7].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_smartdoorunlock_onoff:
			setCheckState2Msg(ReToyotaProtocol.SMART_DOOR_UNLOCK,
					(byte) ((mObjCheckBoxs[8].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_lightset_daytimelight_onoff:
			setCheckState2Msg(ReToyotaProtocol.DAYTIME_RUNNING_LIGHTS,
					(byte) ((mObjCheckBoxs[9].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_airset_autolinkage_onoff:
			setCheckState2Msg(ReToyotaProtocol.AC_AUTO_LINKAGE,
					(byte) ((mObjCheckBoxs[10].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_toyota_airset_inoutgasautolinkage_onoff:
			setCheckState2Msg(ReToyotaProtocol.CHANGE_AIR_LINKAGE,
					(byte) ((mObjCheckBoxs[11].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.btn_toyota_lockset_lockunlockfeedtone_right:
			setMutilSel2Msg(
					ReToyotaProtocol.LOCK_UNLOCK_FEEDBACK_TONE,
					mVehicleSettings.mLockUnLockFeedBackTONE,
					getResources().getString(
							R.string.str_tx_carset_lockset_lockunlockfeedtone),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 0x06, 1, 0, "");
			break;
		case R.id.btn_toyota_lockset_electricdooradjust_right:
			setMutilSel2Msg(
					ReToyotaProtocol.ELECTRIC_DOOR_AdJUST,
					mVehicleSettings.mElectricdooradjust,
					getResources().getString(
							R.string.str_tx_carset_lockset_electricdooradjust),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 0x04, 1, 0, "");
			break;
		case R.id.btn_toyota_lightset_headlampssensitivity_right:
			setMutilSel2Msg(
					ReToyotaProtocol.HEADLAMPS_ON_SENSITIVITY,
					mVehicleSettings.mHeadlampsOnSensitivity,
					getResources()
							.getString(
									R.string.str_tx_carset_lightset_headlampssensitivity),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 0x04, 1, 0, "");
			break;
		case R.id.btn_toyota_lightset_setcarlightofftime_right:
			setMutilSel2Msg(
					ReToyotaProtocol.INCAR_LIGHT_CLOSE_TIME,
					mVehicleSettings.mLightsOffTimer,
					getResources().getString(
							R.string.str_tx_carset_lightset_setcarlightofftime),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_toyota_lightset_setcarlightofftime_right),
					0x00, 0, 0, "");
			break;
		case R.id.btn_toyota_lightset_headlampofftime_right:
			setMutilSel2Msg(
					ReToyotaProtocol.INTERLAMPS_SHUTDOWN_SET,
					mVehicleSettings.mHeadlampsAutoOFFTimer,
					getResources().getString(
							R.string.str_tx_carset_lightset_headlampofftime),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_toyota_lightset_headlampofftime_right),
					0x00, 0, 0, "");
			break;
		case R.id.btn_toyota_airset_autorelocktime_right:
			setMutilSel2Msg(
					ReToyotaProtocol.AUTO_LOCK_TIME,
					mVehicleSettings.mAutoRelockTimer,
					getResources().getString(
							R.string.str_tx_carset_airset_autorelocktime),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_toyota_airset_autorelocktime_right),
					0x00, 0, 0, "");
			break;
		case R.id.btn_toyota_otherset_cameratrack_right:
			setMutilSel2Msg(
					ReToyotaProtocol.RADAR_TRACK_SET,
					mVehicleSettings.mRadarTrack,
					getResources().getString(
							R.string.str_tx_carset_otherset_cameratrack),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_toyota_otherset_cameratrack_right),
					0x00, 0, 0, "");
			break;
		case R.id.btn_toyota_otherset_radarvol_right:
			setMutilSel2Msg(
					ReToyotaProtocol.SET_RADAR_VOL,
					mObjRadarInfo.mVol,
					getResources().getString(
							R.string.str_tx_carset_otherset_radarvol),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 0x04, 1, 1, "");
			break;
		case R.id.btn_toyota_otherset_fueluint_right:
			setMutilSel2Msg(
					ReToyotaProtocol.SET_FUEL_UNIT,
					mObjSetInfo.mbyFuelUnit,
					getResources().getString(
							R.string.str_tx_carset_otherset_fueluint),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_toyota_otherset_fueluint_right), 0x00, 0, 0, "");
			break;
		default:
			break;
		}
	}

	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.btn_toyota_lightset_setcarlightofftime_right:
			for (int iter : ResDef.mToyotasetcarlightofftimeId) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_toyota_lightset_headlampofftime_right:
			for (int iter : ResDef.mToyotaheadlampofftimeId) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_toyota_airset_autorelocktime_right:
			for (int iter : ResDef.mToyotaRelocktimeId) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_toyota_otherset_cameratrack_right:
			for (int iter : ResDef.mToyotacarematrackId) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_toyota_otherset_fueluint_right:
			for (int iter : ResDef.mToyotafuelunit) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		}

		return arrayList;
	}

	private void setCheckState2Msg(byte byCmd, byte bysate) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = bysate;
		super.sendMsg(ReToyotaProtocol.DATA_TYPE_SETTING_REQUEST, bydata,
				mObjProtocol);
	}

	private void setMutilSel2Msg(final byte byCmd, final int istate,
			final String strTitle, final PopDialog.E_POPDIALOG_TYPE eType,
			final ArrayList<String> arrayList, final int iMax,
			final int iStep, final int ioffset, final String string) {
		PopDialog dialog = new PopDialog(strTitle, eType);

		if (eType == E_POPDIALOG_TYPE.ePopDialog_carset_list) {
			dialog.putdata(arrayList, istate);
		} else {
			dialog.setSeekbarVal(iMax, istate, iStep, ioffset, string);
		}

		dialog.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onSelPos(int iPos) {
				// TODO Auto-generated method stub
				setVal(byCmd, iPos);
			}

			@Override
			public void onSeekVal(int iVal) {
				// TODO Auto-generated method stub
				setVal(byCmd, iVal);
			}

			@Override
			public void onConfirm() {
				// TODO Auto-generated method stub

			}
		});

		dialog.show(getFragmentManager(), this.getClass().getName());
	}

	private void setVal(byte byCmd, int iVal) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = (byte) iVal;
		super.sendMsg(ReToyotaProtocol.DATA_TYPE_SETTING_REQUEST, bydata,
				mObjProtocol);
	}

	private void setAttr(VehicleSettings carset) {
		mVehicleSettings = carset;

		int iIndex = 0;
		for (CheckBox checkBox : mObjCheckBoxs) {
			if (checkBox != null) {
				boolean bChecked = false;

				switch (iIndex) {
				case 0:
					bChecked = (carset.mAutoLockBySpeed == 0x01) ? true : false;
					break;
				case 1:
					bChecked = (carset.mAutoLockBySHIFTFORMP == 0x01) ? true
							: false;
					break;
				case 2:
					bChecked = (carset.mLockIntelligentVehicle == 0x01) ? true
							: false;
					break;
				case 3:
					bChecked = (carset.mAutoLockBySHIFTToP == 0x01) ? true
							: false;
					break;
				case 4:
					bChecked = (carset.mLockHandleKeyTwoTimes == 0x01) ? true
							: false;
					break;
				case 5:
					bChecked = (carset.mUnLockFlash == 0x01) ? true : false;
					break;
				case 6:
					bChecked = (carset.mRemote2PressUnlock == 0x01) ? true
							: false;
					break;
				case 7:
					bChecked = (carset.mLockDrivingSeatOpenDoor == 0x01) ? true
							: false;
					break;
				case 8:
					bChecked = (carset.mLockIntelligentDoor == 0x01) ? true
							: false;
					break;
				case 9:
					bChecked = (carset.mDayTimeRunningLights == 0x01) ? true
							: false;
					break;
				case 10:
					bChecked = (carset.mAirSwitchAndAutoLinkage == 0x01) ? true
							: false;
					break;
				case 11:
					bChecked = (carset.mAirCircleAndAutoLinkage == 0x01) ? true
							: false;
					break;
				}

				iIndex++;

				if (checkBox != null) {
					checkBox.setChecked(bChecked);
				}

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
			case DDef.CAR_SET_CMD_ID:
				setAttr((VehicleSettings) msg.obj);
				break;
			case DDef.SET_INFO_CMD_ID:
				mObjSetInfo = (SetInfo) msg.obj;
				break;
			case DDef.RADAR_CMD_ID:
				mObjRadarInfo = (RadarInfo) msg.obj;
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void Inquiry() {
		byte[] bydata = new byte[1];
		bydata[0] = ReToyotaProtocol.DATA_TYPE_VEHICLE_SETTINGS;
		super.sendMsg(ReToyotaProtocol.DATA_TYPE_INQUIRY_REQUEST, bydata,
				mObjProtocol);

	}
}
