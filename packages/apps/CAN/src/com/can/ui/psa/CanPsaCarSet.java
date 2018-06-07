package com.can.ui.psa;

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
import com.can.parser.DDef.PsaCarState;
import com.can.parser.Protocol;
import com.can.parser.raise.psa.RePsaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

public class CanPsaCarSet extends CanFrament implements OnClickListener{
	private PsaCarState mCarState = null;
	private Protocol  mProtocol = null;
	private View	mView = null;
	
	private int[] mPopId = {
		R.id.psa_rl_rear_wiper,	
		R.id.psa_rl_driver_assist,
		R.id.psa_rl_driver_assist1,
		R.id.psa_rl_light_delay,
		R.id.psa_rl_light_atmosphere,
		R.id.psa_rl_light_go_home,
		R.id.psa_rl_guest_light,
		R.id.psa_rl_eq_set,
		R.id.psa_rl_fuel_unit,
		R.id.psa_rl_doors_open_set,
		R.id.psa_rl_lan_set
	};

	private int[] mCheckboxId = {
		R.id.psa_checkbox_light_status,
		R.id.psa_checkbox_auto_run_ill,
		R.id.psa_checkbox_radar_stop,
		R.id.psa_checkbox_tire_pressure,
		R.id.psa_checkbox_blind_area,
		R.id.psa_checkbox_start_stop_func,
		R.id.psa_checkbox_guest_func,
		
		R.id.psa_checkbox_auto_door,
		R.id.psa_checkbox_center_door,
		R.id.psa_checkbox_backcar,
		R.id.psa_checkbox_p_gear,
		R.id.psa_checkbox_small_light
	};
	
	private int[] mStrAbleId = {
		R.string.str_psa_disable,
		R.string.str_psa_enable
	};
	
	private int[] mStrEqId = {
		R.string.str_psa_eq_type1,	
		R.string.str_psa_eq_type2,	
		R.string.str_psa_eq_type3,	
		R.string.str_psa_eq_type4
	};
	
	private int[] mStrTimeId = {
		R.string.str_psa_close,
		R.string.str_psa_15s,
		R.string.str_psa_30s,
		R.string.str_psa_60s
	};
	
	private int[] mStrUnitId = { 
		R.string.str_psa_fuel_unit1,
		R.string.str_psa_fuel_unit2,
//		R.string.str_psa_fuel_unit3
	};
	
	private int[] mStrLanId = {
		R.string.str_psa_english,
		R.string.str_psa_chinese,
//		R.string.str_psa_french
	};
	
	private int[] mStrDoorId = {
		R.string.str_psa_only_driver_door,
		R.string.str_psa_all_door
	};
	
	private CheckBox[] mCheckBoxs = new CheckBox[mCheckboxId.length];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.psa_car_set, null);
			initView();
		}
		return mView;
	}

	@Override
	public void onResume() {
		getData(DDef.CAR_SET_CMD_ID);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.DeInit();
		super.onDestroy();
	}

	private void initView() {
		if (mView != null) {
			for (int i = 0; i < mCheckBoxs.length; i++) {
				mCheckBoxs[i] = (CheckBox) mView.findViewById(mCheckboxId[i]);
				mCheckBoxs[i].setOnClickListener(this);
			}
			mCheckBoxs[9].setClickable(false);
			mCheckBoxs[10].setClickable(false);
			mCheckBoxs[11].setClickable(false);
			for (int i = 0; i < mPopId.length; i++) {
				mView.findViewById(mPopId[i]).setOnClickListener(this);
			}
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				byte[] datas = new byte[1];
				datas[0] = 0x38;
				sendMsg(RePsaProtocol.DATA_TYPE_GET_REQUEST, datas, mProtocol);
				break;
			case DDef.CAR_SET_CMD_ID:
				mCarState = (PsaCarState)msg.obj;
				setCarSetAttr(mCarState);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		if (mCarState != null) {
			switch (v.getId()) {
			case R.id.psa_checkbox_light_status:
				setCarState((byte) 0x03, (byte) (mCheckBoxs[0].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_auto_run_ill:
				setCarState((byte) 0x12, (byte) (mCheckBoxs[1].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_radar_stop:
				setCarState((byte) 0x06, (byte) (mCheckBoxs[2].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_tire_pressure:
				setCarState((byte) 0x10, (byte) (mCheckBoxs[3].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_blind_area:
				setCarState((byte) 0x0C, (byte) (mCheckBoxs[4].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_start_stop_func:
				setCarState((byte) 0x0D, (byte) (mCheckBoxs[5].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_checkbox_guest_func:
				setCarState((byte) 0x0E, (byte) (mCheckBoxs[6].isChecked() ? 0x01 : 0x00));
				break;
			case R.id.psa_rl_rear_wiper:
				setMutilSel2Msg(
						(byte) 0x02, mCarState.mRearWiper,
						getString(R.string.str_psa_rear_wiper),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_rear_wiper), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_driver_assist:
				setMutilSel2Msg(
						(byte) 0x11, mCarState.mDriverAssist3008,
						getString(R.string.str_psa_driver_assist),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_driver_assist), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_driver_assist1:
				setMutilSel2Msg(
						(byte) 0x01, mCarState.mDriverAssistOther,
						getString(R.string.str_psa_driver_assist1),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_driver_assist1), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_light_delay:
				setMutilSel2Msg(
						(byte) 0x04, mCarState.mLightDelaySts,
						getString(R.string.str_psa_light_delay),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_light_delay), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_light_atmosphere:
				setMutilSel2Msg(
						(byte) 0x05, mCarState.mLightAtmosphere,
						getString(R.string.str_psa_light_atmosphere),
						E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
						null, 0x07, 1, 0, "");
				break;
			case R.id.psa_rl_light_go_home:
				setMutilSel2Msg(
						(byte) 0x07, mCarState.mLightGoHome,
						getString(R.string.str_psa_light_go_home),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_light_go_home), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_guest_light:
				setMutilSel2Msg(
						(byte) 0x08, mCarState.mLightHost,
						getString(R.string.str_psa_guest_light),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_guest_light), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_eq_set:
				setMutilSel2Msg(
						(byte) 0x09, mCarState.mEqSet,
						getString(R.string.str_psa_eq_set),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_eq_set), 0x00, 01, 0, "");
				break;
			case R.id.psa_rl_fuel_unit:
				setMutilSel2Msg(
						(byte) 0x0A, mCarState.mFuelSet,
						getString(R.string.str_psa_fuel_unit),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_fuel_unit), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_doors_open_set:
				setMutilSel2Msg(
						(byte) 0x0F, mCarState.mDoorOpenOptions,
						getString(R.string.str_psa_doors_open_set),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_doors_open_set), 0x00, 0, 0, "");
				break;
			case R.id.psa_rl_lan_set:
				setMutilSel2Msg(
						(byte) 0x0B, mCarState.mLauguageSet,
						getString(R.string.str_psa_lan_set),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.psa_rl_lan_set), 0x00, 0, 0, "");
				break;
			default:
				break;
			}
		}
	}
	
	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();
		switch (iId) {
		case R.id.psa_rl_rear_wiper:
		case R.id.psa_rl_driver_assist:
		case R.id.psa_rl_driver_assist1:
			for (int iter : mStrAbleId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.psa_rl_light_delay:
		case R.id.psa_rl_light_atmosphere:
		case R.id.psa_rl_light_go_home:
		case R.id.psa_rl_guest_light:
			for (int iter : mStrTimeId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.psa_rl_eq_set:
			for (int iter : mStrEqId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.psa_rl_lan_set:
			for (int iter : mStrLanId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.psa_rl_doors_open_set:
			for (int iter : mStrDoorId) {
				arrayList.add(getString(iter));
			}	
			break;
		case R.id.psa_rl_fuel_unit:
			for (int iter : mStrUnitId) {
				arrayList.add(getString(iter));
			}
			break;
		}
		return arrayList;
	}
	
	private void setCarSetAttr(PsaCarState carState) {
		if (mView != null && carState != null) {
			for (int i = 0; i < mCheckBoxs.length; i++) {
				boolean bCheck = false;
				switch (i) {
				case 0:
					bCheck = carState.mLightStuats == 0x01 ? true : false;
					break;
				case 1:
					bCheck = carState.mAutoRunILL == 0x01 ? true : false;
					break;
				case 2:
					bCheck = carState.mRadarStop == 0x01 ? true : false;
					break;
				case 4:
					bCheck = carState.mBlindAreaProbe == 0x01 ? true : false;
					break;
				case 5:
					bCheck = carState.mEnginesstopfcdis == 0x01 ? true : false;
					break;
				case 6:
					bCheck = carState.mGuestfunction == 0x01 ? true : false;
					break;
				case 7:
					bCheck = carState.mDoorAutoLock == 0x01 ? true : false;
					break;
				case 8:
					bCheck = carState.mCentDoorLockSts == 0x01 ? true : false;
					break;
				case 9:
					bCheck = carState.mBackCarSts == 0x01 ? true : false;
					break;
				case 10:
					bCheck = carState.mIsPGear == 0x01 ? false : true;
					break;
				case 11:
					bCheck = carState.mSmallLight == 0x01 ? true : false;
					break;
				default:
					break;
				}
				mCheckBoxs[i].setChecked(bCheck);
			}
		}
	}
	
	private void setMutilSel2Msg(final byte byCmd, final byte bystate,
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
				setCarState(byCmd, (byte) iPos);
			}

			@Override
			public void onSeekVal(int iVal) {
				setCarState(byCmd, (byte) iVal);
			}

			@Override
			public void onConfirm() {

			}
		});

		dialog.show(getFragmentManager(), this.getClass().getName());
	}
	
	private void setCarState(byte byCmdId, byte byState) {
		byte[] datas = new byte[2];
		datas[0] = byCmdId;
		datas[1] = (byte) (byCmdId == 0x09 ? (byState + 1) : byState);
		sendMsg(RePsaProtocol.DATA_TYPE_CAR_STATE_REQUEST, datas, mProtocol);
	}
}
