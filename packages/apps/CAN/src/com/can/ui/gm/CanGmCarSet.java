package com.can.ui.gm;

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
import com.can.parser.Protocol;
import com.can.parser.DDef.GmCarSet;
import com.can.parser.raise.gm.ReGmProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

public class CanGmCarSet extends CanFrament implements OnClickListener {
	private View mView = null;
	private Protocol mObjProtocol = null;
	private CheckBox[] mCheckBoxs = null;
	private GmCarSet mGmCarSet = new GmCarSet();

	public static int[] mCheckboxId = {
		R.id.gm_checkbox_seek_headlight,
		R.id.gm_checkbox_prevent_lock,
		R.id.gm_checkbox_start_lock,
		R.id.gm_checkbox_delay_lock,
		R.id.gm_checkbox_remote_unlock,
		R.id.gm_checkbox_start_wiper_by_back,
		R.id.gm_checkbox_remote_then_lock,
		R.id.gm_checkbox_remote_start_car,
		R.id.gm_checkbox_froget_key,
		R.id.gm_checkbox_person_by_driver,
		R.id.gm_checkbox_auto_relock,
		R.id.gm_checkbox_flank_warning,
		R.id.gm_checkbox_car_state,
		R.id.gm_checkbox_auto_wiper,
		R.id.gm_checkbox_remote_wind,
		R.id.gm_checkbox_auto_defog_r,
		R.id.gm_checkbox_auto_defog_f,
		R.id.gm_checkbox_remote_seat_heat,
		R.id.gm_checkbox_radar_state
	};
	
	public static int[] mBtnId = {
		R.id.gm_rl_auto_wind_set,
		R.id.gm_rl_air_quality_sensor,
		R.id.gm_rl_part_temp,
		R.id.gm_rl_start_mode,
		R.id.gm_rl_lock_headlight_delay,
		R.id.gm_rl_park_lock,
		R.id.gm_rl_remote_lock,
		R.id.gm_rl_remote_unlock_set,
		/*R.id.gm_rl_car_body_ctrl,*/
		R.id.gm_rl_near_unlock,
		R.id.gm_rl_away_lock,
		R.id.gm_rl_auto_collision,
		R.id.gm_rl_car_speed,
		R.id.gm_rl_lan,
		R.id.gm_rl_waring_vol,
		R.id.gm_btn_reset
	};
	
	public static int[] mAutoCollisionId = {
		R.string.str_gm_close,
		R.string.str_gm_alram,
		R.string.str_gm_alram_brake
	};
	
	public static int[] mAwaylockId = {
		R.string.str_gm_close,
		R.string.str_gm_open,
		R.string.str_gm_hroning
	};
	
	public static int[] mNearUnlockId = {
		R.string.str_gm_all_door,
		R.string.str_gm_driver_door
	};
	
	public static int[] mCarBodyId = {
		R.string.str_gm_cant_operate,
		R.string.str_gm_operate
	};
	
	public static int[] mRemoteUnlockId = {
		R.string.str_gm_driver_door,
		R.string.str_gm_all_door
	};
	
	public static int[] mRemoteLockId = {
		R.string.str_gm_only_light,
		R.string.str_gm_light_horn,
		R.string.str_gm_only_horn,
		R.string.str_gm_close
	};
	
	public static int[] mParkLockId = {
		R.string.str_gm_close,
		R.string.str_gm_driver_door,
		R.string.str_gm_all_door
	};
	
	public static int[] mLockHeadLightId = {
		R.string.str_gm_time_close,
		R.string.str_gm_time_30s,
		R.string.str_gm_time_1minute,
		R.string.str_gm_time_2minute
	};
	
	public static int[] mAutoWindSetId = {
		R.string.str_gm_low,
		R.string.str_gm_mid,
		R.string.str_gm_high
	};
	
	public static int[] mAirQualityId = {
		R.string.str_gm_close,
		R.string.str_gm_low_send,
		R.string.str_gm_high_send
	};
	
	public static int[] mPartTemp = {
		R.string.str_gm_unified_set,
		R.string.str_gm_part_setup,
		R.string.str_gm_last_set
	};
	
	public static int[] mRemoteAirId = {
		R.string.str_gm_close,
		R.string.str_gm_open,
		R.string.str_gm_last_set
	};
	
	public static int[] mLanId = {
		R.string.str_gm_lan_chinese,
		R.string.str_gm_lan_english
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mObjHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.gm_car_set, null);
			init();
		}
		return mView;
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		setAttr(mGmCarSet);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		getData(DDef.CAR_SET_CMD_ID);
	}

	private void init() {
		if (mView != null) {

			int iIndex = 0;
			mCheckBoxs = new CheckBox[mCheckboxId.length];
			for (int iArray : mCheckboxId) {
				mCheckBoxs[iIndex] = (CheckBox) mView
						.findViewById(iArray);
				mCheckBoxs[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : mBtnId) {
				mView.findViewById(iter).setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gm_checkbox_seek_headlight:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x00,
					(byte) ((mCheckBoxs[0].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_prevent_lock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x02,
					(byte) ((mCheckBoxs[1].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_start_lock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x03,
					(byte) ((mCheckBoxs[2].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_delay_lock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x05,
					(byte) ((mCheckBoxs[3].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_remote_unlock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x06,
					(byte) ((mCheckBoxs[4].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_start_wiper_by_back:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x09,
					(byte) ((mCheckBoxs[5].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_remote_then_lock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0A,
					(byte) ((mCheckBoxs[6].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_remote_start_car:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0B,
					(byte) ((mCheckBoxs[7].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_froget_key:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0D,
					(byte) ((mCheckBoxs[8].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_person_by_driver:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0E,
					(byte) ((mCheckBoxs[9].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_auto_relock:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0F,
					(byte) ((mCheckBoxs[10].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_flank_warning:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x16,
					(byte) ((mCheckBoxs[11].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_car_state:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x19,
					(byte) ((mCheckBoxs[12].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_auto_wiper:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x1A,
					(byte) ((mCheckBoxs[13].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_remote_wind:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x1B,
					(byte) ((mCheckBoxs[14].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_auto_defog_r:
			setVal(ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x03,
					(byte) ((mCheckBoxs[15].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_auto_defog_f:
			setVal(ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x04,
					(byte) ((mCheckBoxs[16].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_remote_seat_heat:
			setVal(ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x05,
					(byte) ((mCheckBoxs[17].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.gm_checkbox_radar_state:
			setVal(ReGmProtocol.DATA_TYPE_RADIO_ON_OFF_REQUEST, (byte) 0x00,
					((mCheckBoxs[18].isChecked()) ? 0x01 : 0x00));
			break;

		case R.id.gm_rl_auto_wind_set:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x00,
					mGmCarSet.mAutoWindSet,
					getString(R.string.str_gm_auto_wind_set),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_auto_wind_set), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_air_quality_sensor:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x01,
					mGmCarSet.mAirQualitySensor,
					getString(R.string.str_gm_air_quality_sensor),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_air_quality_sensor), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_part_temp:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x02,
					mGmCarSet.mPartTemp,
					getString(R.string.str_gm_part_temp),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_part_temp), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_start_mode:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_AIR_SET_REQUEST, (byte) 0x06,
					mGmCarSet.mStartMode,
					getString(R.string.str_gm_remote_seat_heat),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_start_mode), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_lock_headlight_delay:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x01,
					mGmCarSet.mHeadlampDelaylock,
					getString(R.string.str_gm_lock_headlight_delay),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_lock_headlight_delay), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_park_lock:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x04,
					mGmCarSet.mAUtoUnlockByPark,
					getString(R.string.str_gm_park_lock),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_park_lock), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_remote_lock:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x07,
					mGmCarSet.mRemotelockLight,
					getString(R.string.str_gm_remote_lock),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_remote_lock), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_remote_unlock_set:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x08,
					mGmCarSet.mRemoteUnlock,
					getString(R.string.str_gm_remote_unlock_set),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_remote_unlock_set), 0x00, 0, 0, "");
			break;
//		case R.id.gm_rl_car_body_ctrl:
//			setMutilSel2Msg(
//					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0C,
//					mGmCarSet.mCarBodyCtrl,
//					getString(R.string.str_gm_car_body_ctrl),
//					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, 
//					getArrayList(R.id.gm_rl_car_body_ctrl),
//					0x00);
//			break;
		case R.id.gm_rl_near_unlock:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x0C,
					mGmCarSet.mNearCarUnlock,
					getString(R.string.str_gm_near_unlock),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_near_unlock), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_away_lock:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x17,
					mGmCarSet.mAutolockByAway,
					getString(R.string.str_gm_away_lock),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_away_lock), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_auto_collision:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x18,
					mGmCarSet.mAutoCollision,
					getString(R.string.str_gm_auto_collision),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_auto_collision), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_lan:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_LAN_REQUEST, (byte) 0x00,
					mGmCarSet.mLan,
					getString(R.string.str_gm_lan),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.gm_rl_lan), 0x00, 0, 0, "");
			break;
		case R.id.gm_rl_waring_vol:
			setMutilSel2Msg(
					ReGmProtocol.DATA_TYPE_WARNING_VOL_REQUEST, (byte) 0x00,
					mGmCarSet.mWarnVol,
					getString(R.string.str_gm_warning_vol),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
					null, 0x0F, 1, 0, "");
			break;
		case R.id.gm_btn_reset:
			setVal(ReGmProtocol.DATA_TYPE_CAR_SET_REQUEST, (byte) 0x80, 0x01);
			break;
		default:
			break;
		}
	}

	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.gm_rl_auto_wind_set:
			for (int iter : mAutoWindSetId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_air_quality_sensor:
			for (int iter : mAirQualityId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_part_temp:
			for (int iter : mPartTemp) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_start_mode:
			for (int iter : mRemoteAirId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_lock_headlight_delay:
			for (int iter : mLockHeadLightId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_park_lock:
			for (int iter : mParkLockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_remote_lock:
			for (int iter : mRemoteLockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_remote_unlock_set:
			for (int iter : mRemoteUnlockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_car_body_ctrl:
			for (int iter : mCarBodyId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_near_unlock:
			for (int iter : mNearUnlockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_away_lock:
			for (int iter : mAwaylockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_auto_collision:
			for (int iter : mAutoCollisionId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_car_speed:
			break;
		case R.id.gm_rl_lan:
			for (int iter : mLanId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.gm_rl_waring_vol:
			break;
		}

		return arrayList;
	}

	private void setVal(byte byHead, byte byCmd, int iVal) {
		byte[] bydata;
		if (byHead == ReGmProtocol.DATA_TYPE_LAN_REQUEST ||
			byHead == ReGmProtocol.DATA_TYPE_WARNING_VOL_REQUEST ||
			byHead == ReGmProtocol.DATA_TYPE_RADIO_ON_OFF_REQUEST) {
			bydata = new byte[1];
			bydata[0] = (byte) iVal;
		} else {
			bydata = new byte[2];
			bydata[0] = byCmd;
			bydata[1] = (byte) iVal;
		}
		super.sendMsg(byHead, bydata, mObjProtocol);
	}
	
	private void setMutilSel2Msg(final byte byHead, final byte byCmd, final byte bystate,
			final String strTitle, final PopDialog.E_POPDIALOG_TYPE eType,
			final ArrayList<String> arrayList, final int iMax,
			final int iStep, final int ioffset, final String string) {
		PopDialog dialog = new PopDialog(strTitle, eType);

		if (eType == E_POPDIALOG_TYPE.ePopDialog_carset_list) {
			dialog.putdata(arrayList, bystate);
		} else {
			dialog.setSeekbarVal(iMax, bystate, iStep, ioffset, string);
		}

		dialog.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onSelPos(int iPos) {
				setVal(byHead, byCmd, iPos);
			}

			@Override
			public void onSeekVal(int iVal) {
				setVal(byHead ,byCmd, iVal);
			}

			@Override
			public void onConfirm() {

			}
		});

		dialog.show(getFragmentManager(), this.getClass().getName());
	}


	private void setAttr(GmCarSet carset) {
		mGmCarSet = carset;

		int iIndex = 0;
		for (CheckBox checkBox : mCheckBoxs) {
			if (checkBox != null) {
				boolean bChecked = false;

				switch (iIndex) {
				case 0:
					bChecked = carset.mLookForLights == 0x01 ? true : false;
					break;
				case 1:
					bChecked = carset.mPreventDoorlock == 0x01 ? true : false;
					break;
				case 2:
					bChecked = carset.mAutolockByStart == 0x01 ? true : false;
					break;
				case 3:
					bChecked = carset.mDelayLock == 0x01 ? true : false;
					break;
				case 4:
					bChecked = carset.mRemoteUnlockLight == 0x01 ? true : false;
					break;
				case 5:
					bChecked = carset.mBackWiper == 0x01 ? true : false;
					break;
				case 6:
					bChecked = carset.mRemoteThenLock == 0x01 ? true : false;
					break;
				case 7:
					bChecked = carset.mRemoteStart == 0x01 ? true : false;
					break;
				case 8:
					bChecked = carset.mForgetKey == 0x01 ? true : false;
					break;
				case 9:
					bChecked = carset.mPresonByDriver == 0x01 ? true : false;
					break;
				case 10:
					bChecked = carset.mAutoRelockDoor == 0x01 ? true : false;
					break;
				case 11:
					bChecked = carset.mFlankWarn == 0x01 ? true : false;
					break;
				case 12:
					bChecked = carset.mCarState == 0x01 ? true : false;
					break;
				case 13:
					bChecked = carset.mAutoWiper == 0x01 ? true : false;
					break;
				case 14:
					bChecked = carset.mRemoteWind == 0x01 ? true : false;
					break;
				case 15:
					bChecked = carset.mAutoDefogR == 0x01 ? true : false;
					break;
				case 16:
					bChecked = carset.mAutoDefogF == 0x01 ? true : false;
					break;
				case 17:
					bChecked = carset.mRemoteSeatHeat == 0x01 ? true : false;
					break;
				case 18:
					bChecked = carset.mRadar == 0x01 ? true : false;
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
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				Inquiry();
				break;
			case DDef.CAR_SET_CMD_ID:
				setAttr((GmCarSet) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void Inquiry() {
		byte[] bydata = new byte[1];
		bydata[0] = 0x06;
		super.sendMsg(ReGmProtocol.DATA_TYPE_MEDIA_REQUEST, bydata,
				mObjProtocol);

	}
}
