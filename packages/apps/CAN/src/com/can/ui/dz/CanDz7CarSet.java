package com.can.ui.dz;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.Protocol;
import com.can.parser.DDef.CarInfo;
import com.can.parser.raise.dz.ReMQB7Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.ResDef;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

/**
 * ClassName:CanDz7CarSet
 * 
 * @function:车辆设置
 * @author Kim
 * @Date: 2016-7-6上午11:00:49
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7CarSet extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private CheckBox[] mObjCheckBoxs = null;
	private CarInfo mObjCarInfo = new CarInfo();

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
			mObjView = inflater.inflate(R.layout.dz7_carset, container, false);
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
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		super.getData(DDef.CARINFO_CMD_ID);
	}

	private void init() {

		if (mObjView != null) {

			int iIndex = 0;
			mObjCheckBoxs = new CheckBox[ResDef.mDz7CheckboxId.length];
			for (int iArray : ResDef.mDz7CheckboxId) {
				mObjCheckBoxs[iIndex] = (CheckBox) mObjView
						.findViewById(iArray);
				mObjCheckBoxs[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : ResDef.mDz7BtnId) {
				mObjView.findViewById(iter).setOnClickListener(this);
			}
		}
	}

	private void setCarSetAttr(CarInfo objCarInfo) {
		mObjCarInfo = objCarInfo;

		int iIndex = 0;
		for (CheckBox checkBox : mObjCheckBoxs) {
			if (checkBox != null) {
				boolean bChecked = false;

				switch (iIndex) {
				case 0:
					bChecked = (objCarInfo.mbyEscSysState == 0x01) ? true
							: false;
					break;
				case 1:
					bChecked = (objCarInfo.mbySpeedWarn == 0x01) ? true : false;
					break;
				case 2:
					bChecked = (objCarInfo.mbyLaneAssist == 0x01) ? true
							: false;
					break;
				case 3:
					bChecked = (objCarInfo.mbyDriverAlertSys == 0x01) ? true
							: false;
					break;
				case 4:
					bChecked = (objCarInfo.mbyLaneAssistEnable == 0x01) ? true
							: false;
					break;
				case 5:
					bChecked = (objCarInfo.mbyLastDistance == 0x01) ? true
							: false;
					break;
				case 6:
					bChecked = (objCarInfo.mbyFrontAssistActive == 0x01) ? true
							: false;
					break;
				case 7:
					bChecked = (objCarInfo.mbyFrontAssistAdvance == 0x01) ? true
							: false;
					break;
				case 8:
					bChecked = (objCarInfo.mbyFrontAssistDisplay == 0x01) ? true
							: false;
					break;
				case 9:
					bChecked = (objCarInfo.mbyActivateAutoMatically == 0x01) ? true
							: false;
					break;
				case 10:
					bChecked = (objCarInfo.mbyAutoHeadlightCtrlInRain == 0x01) ? true
							: false;
					break;
				case 11:
					bChecked = (objCarInfo.mbyLaneChangeFlash == 0x01) ? true
							: false;
					break;
				case 12:
					bChecked = (objCarInfo.mbyDaytimeRunlight == 0x01) ? true
							: false;
					break;
				case 13:
					bChecked = (objCarInfo.mbyDynclightAssist == 0x01) ? true
							: false;
					break;
				case 14:
					bChecked = (objCarInfo.mbyDyncBiglightAssist == 0x01) ? true
							: false;
					break;
				case 15:
					bChecked = (objCarInfo.mbySynchronousAdjust == 0x01) ? true
							: false;
					break;
				case 16:
					bChecked = (objCarInfo.mbyLowerWhileReverse == 0x01) ? true
							: false;
					break;
				case 17:
					bChecked = (objCarInfo.mbyAutomaticWiperInRain == 0x01) ? true
							: false;
					break;
				case 18:
					bChecked = (objCarInfo.mbyRearWindWiperIngrear == 0x01) ? true
							: false;
					break;
				case 19:
					bChecked = (objCarInfo.mbyFoldAwayAfterParking == 0x01) ? true
							: false;
					break;
				case 20:
					bChecked = (objCarInfo.mbyAutomaticlock == 0x01) ? true
							: false;
					break;
				case 21:
					bChecked = (objCarInfo.mbyCurrentConsumption == 0x01) ? true
							: false;
					break;
				case 22:
					bChecked = (objCarInfo.mbyAverageConsumption == 0x01) ? true
							: false;
					break;
				case 23:
					bChecked = (objCarInfo.mbyConvenienceConsumers == 0x01) ? true
							: false;
					break;
				case 24:
					bChecked = (objCarInfo.mbyEcoTips == 0x01) ? true : false;
					break;
				case 25:
					bChecked = (objCarInfo.mbyTravellingTime == 0x01) ? true
							: false;
					break;
				case 26:
					bChecked = (objCarInfo.mbyDistanceTraveled == 0x01) ? true
							: false;
					break;
				case 27:
					bChecked = (objCarInfo.mbyAverageSpeed == 0x01) ? true
							: false;
					break;
				case 28:
					bChecked = (objCarInfo.mbyDigitalSpeedDisplay == 0x01) ? true
							: false;
					break;
				case 29:
					bChecked = (objCarInfo.mbySpeedWarning == 0x01) ? true
							: false;
					break;
				case 30:
					bChecked = (objCarInfo.mbyOilTemperature == 0x01) ? true
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

	private void setVal(byte byCmd, int iVal) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = (byte) iVal;
		
		if (byCmd == ReMQB7Protocol.LANG_SET) {
			bydata[1] = (byte) ((iVal == 0) ? 0x01 : 0x17);
		}else if (byCmd == ReMQB7Protocol.DRIVER_ACC_DRIVE_PRO) {
			bydata[1] += 1;
		}
		
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_SET, bydata, mObjProtocol);
	}

	private void setCheckState2Msg(byte byCmd, byte bysate) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = bysate;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_SET, bydata, mObjProtocol);
	}

	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.btn_dz7_carset_assist_accpro:
			for (int iter : ResDef.mDz7AccPro) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_assist_accdis:
			for (int iter : ResDef.mDz7AccDis) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_light_swintime:
			for (int iter : ResDef.mDz7SwOnTime) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_light_travelmode:
			for (int iter : ResDef.mDz7TravelMode) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_open2close_convs:
			for (int iter : ResDef.mDz7Convs) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_open2close_doorunlock:
			for (int iter : ResDef.mDz7Doorlock) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_dis:
			for (int iter : ResDef.mDz7disunit) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_speed:
			for (int iter : ResDef.mDz7speedunit) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_temp:
			for (int iter : ResDef.mDz7tempunit) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_vol:
			for (int iter : ResDef.mDz7Vol) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_consumption:
			for (int iter : ResDef.mDz7consum) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_unit_tpms:
			for (int iter : ResDef.mDz7tpms) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_dz7_carset_lang:
			for (int iter : ResDef.mDz7Lang) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		}

		return arrayList;
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

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				Inquiry();
				break;
			case DDef.CARINFO_CMD_ID:
				setCarSetAttr((CarInfo) msg.obj);
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
		case R.id.checkbox_dz7_escsys_onoff:
			setCheckState2Msg(ReMQB7Protocol.ESC_SYSTEM,
					(byte) ((mObjCheckBoxs[0].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_types_speedwarn_onoff:
			setCheckState2Msg(ReMQB7Protocol.TYRES_SPEED_WARN,
					(byte) ((mObjCheckBoxs[1].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_lane_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_LANE_ASSIST,
					(byte) ((mObjCheckBoxs[2].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_alert_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_ALERT_SYSTEM,
					(byte) ((mObjCheckBoxs[3].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_laneaction_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_ASSISTANCE,
					(byte) ((mObjCheckBoxs[4].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_lastdis_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_LAST_DIS,
					(byte) ((mObjCheckBoxs[5].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_frontactive_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_FRONT_ASSIST_ACTIVE,
					(byte) ((mObjCheckBoxs[6].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_frontwarn_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_FRONT_ASSIST_ADVANT,
					(byte) ((mObjCheckBoxs[7].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_assist_frontdisplay_onoff:
			setCheckState2Msg(ReMQB7Protocol.DRIVER_FRONT_ASSIST_DISPLAY,
					(byte) ((mObjCheckBoxs[8].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_park_autoactive_onoff:
			setCheckState2Msg(ReMQB7Protocol.PARK_ACTIVE_AUTO,
					(byte) ((mObjCheckBoxs[9].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_light_autoheadrain_onoff:
			setCheckState2Msg(ReMQB7Protocol.LIGHT_AUTO_HEAD,
					(byte) ((mObjCheckBoxs[10].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_light_lanechangeflash_onoff:
			setCheckState2Msg(ReMQB7Protocol.LIGHT_LANE_FLASH,
					(byte) ((mObjCheckBoxs[11].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_light_daytime_onoff:
			setCheckState2Msg(ReMQB7Protocol.DATTIME_LIGHT,
					(byte) ((mObjCheckBoxs[12].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_light_dymic:
			setCheckState2Msg(ReMQB7Protocol.LIGHT_DYMIC,
					(byte) ((mObjCheckBoxs[13].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_light_motorway:
			setCheckState2Msg(ReMQB7Protocol.LIGHT_MOTORWAY,
					(byte) ((mObjCheckBoxs[14].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mwipers_mirrorssyncadjust:
			setCheckState2Msg(ReMQB7Protocol.WIPER_SYNC_ADJUST,
					(byte) ((mObjCheckBoxs[15].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mwipers_lowerwreverse:
			setCheckState2Msg(ReMQB7Protocol.WIPER_LOWER_REVERSE,
					(byte) ((mObjCheckBoxs[16].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mwipers_autowiper:
			setCheckState2Msg(ReMQB7Protocol.WIPER_AUTO_IN_RAIN,
					(byte) ((mObjCheckBoxs[17].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mwipers_rearwindwiper:
			setCheckState2Msg(ReMQB7Protocol.WIPER_REAR_WIND,
					(byte) ((mObjCheckBoxs[18].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mwipers_parkfoldaway:
			setCheckState2Msg(ReMQB7Protocol.WIPER_FOLD_AWAY,
					(byte) ((mObjCheckBoxs[19].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_open2close_autolock:
			setCheckState2Msg(ReMQB7Protocol.OPNECLSE_AUTO_LOCK,
					(byte) ((mObjCheckBoxs[20].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_curfuel:
			setCheckState2Msg(ReMQB7Protocol.MFD_CURFUEL,
					(byte) ((mObjCheckBoxs[21].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_avgfuel:
			setCheckState2Msg(ReMQB7Protocol.MFD_AVGFUEL,
					(byte) ((mObjCheckBoxs[22].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_convs:
			setCheckState2Msg(ReMQB7Protocol.MFD_CONVS,
					(byte) ((mObjCheckBoxs[23].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_ecotips:
			setCheckState2Msg(ReMQB7Protocol.MFD_ECO,
					(byte) ((mObjCheckBoxs[24].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_traveltime:
			setCheckState2Msg(ReMQB7Protocol.MFD_TRAVELTIME,
					(byte) ((mObjCheckBoxs[25].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_traveldis:
			setCheckState2Msg(ReMQB7Protocol.MFD_DIS,
					(byte) ((mObjCheckBoxs[26].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_avgspeed:
			setCheckState2Msg(ReMQB7Protocol.MFD_AVGSPEED,
					(byte) ((mObjCheckBoxs[27].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_dspeedshow:
			setCheckState2Msg(ReMQB7Protocol.MFD_DIGITAL,
					(byte) ((mObjCheckBoxs[28].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_speedwarn:
			setCheckState2Msg(ReMQB7Protocol.MFD_SPEEDWARN,
					(byte) ((mObjCheckBoxs[29].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_oiltemp:
			setCheckState2Msg(ReMQB7Protocol.MFD_OIL_TEMP,
					(byte) ((mObjCheckBoxs[30].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_since:
			setCheckState2Msg(ReMQB7Protocol.MFD_SINCE_START,
					(byte) ((mObjCheckBoxs[31].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_dz7_carset_mfd_long:
			setCheckState2Msg(ReMQB7Protocol.MFD_LONG_TERM,
					(byte) ((mObjCheckBoxs[32].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.btn_dz7_carset_types_speedVal:
			setMutilSel2Msg(
					ReMQB7Protocol.TYRES_SPEED_VAL,
					mObjCarInfo.miSpeedValue,
					getResources().getString(
							R.string.str_dz7_carset_types_speedVal),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
					null,
					((mObjCarInfo.mbySpeedUnit == 0) ? 24 : 30),
					((mObjCarInfo.mbySpeedUnit == 0) ? 10  : 5), 0,
					((mObjCarInfo.mbySpeedUnit == 0) ? (getResources()
							.getString(R.string.str_dz7_carset_speedunit1))
							: (getResources()
									.getString(R.string.str_dz7_carset_speedunit2))));
			break;
		case R.id.btn_dz7_carset_types_info:
			super.showPage(new CanDz7Tpms());
			break;
		case R.id.btn_dz7_carset_assist_accpro:
			setMutilSel2Msg(
					ReMQB7Protocol.DRIVER_ACC_DRIVE_PRO,
					mObjCarInfo.mbyAccDriveProgram,
					getResources().getString(
							R.string.str_dz7_carset_assist_accpro),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_assist_accpro), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_assist_accdis:
			setMutilSel2Msg(
					ReMQB7Protocol.DRIVER_ACC_DIS,
					mObjCarInfo.mbyAccDistance,
					getResources().getString(
							R.string.str_dz7_carset_assist_accdis),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_assist_accdis), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_park_frontvol:
			setMutilSel2Msg(
					ReMQB7Protocol.PARK_FRONT_VOL,
					mObjCarInfo.mbyFrontVolume,
					getResources().getString(
							R.string.str_dz7_carset_park_frontvol),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 8, 1, 1,"");
			break;
		case R.id.btn_dz7_carset_park_fronttone:
			setMutilSel2Msg(
					ReMQB7Protocol.PARK_FRONT_TONE,
					mObjCarInfo.mbyFrontToneSetting,
					getResources().getString(
							R.string.str_dz7_carset_park_fronttone),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 8, 1, 1, "");
			break;
		case R.id.btn_dz7_carset_park_rearvol:
			setMutilSel2Msg(
					ReMQB7Protocol.PARK_REAR_VOL,
					mObjCarInfo.mbyRearVolume,
					getResources().getString(
							R.string.str_dz7_carset_park_rearvol),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 8, 1, 1, "");
			break;
		case R.id.btn_dz7_carset_park_reartone:
			setMutilSel2Msg(
					ReMQB7Protocol.PARK_REAR_TONE,
					mObjCarInfo.mbyRearToneSetting,
					getResources().getString(
							R.string.str_dz7_carset_park_reartone),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 8, 1, 1, "");
			break;
		case R.id.btn_dz7_carset_light_swintime:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_SW_ONTIME,
					mObjCarInfo.mbySwicthOnTime,
					getResources().getString(
							R.string.str_dz7_carset_light_swintime),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_light_swintime), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_light_instrument:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_INSTRUMENT,
					mObjCarInfo.mbyInstrumentSwlight,
					getResources().getString(
							R.string.str_dz7_carset_light_instrument),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 10, 10, 0,
					getResources()
							.getString(R.string.str_dz7_carset_light_Unit));
			break;
		case R.id.btn_dz7_carset_light_comehome:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_COME_HOME,
					mObjCarInfo.mbyComeHomeFunction,
					getResources().getString(
							R.string.str_dz7_carset_light_comehome),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
					null,
					6,
					5, 0,
					getResources().getString(
							R.string.str_dz7_carset_light_time_unit));
			break;
		case R.id.btn_dz7_carset_light_leavehome:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_LEAVE_HOME,
					mObjCarInfo.mbyLeaveHomeFunction,
					getResources().getString(
							R.string.str_dz7_carset_light_leavehome),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 6,
					5, 0,
					getResources().getString(
							R.string.str_dz7_carset_light_time_unit));
			break;
		case R.id.btn_dz7_carset_light_travelmode:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_TRAVEL_MODE,
					mObjCarInfo.mbyTravlMode,
					getResources().getString(
							R.string.str_dz7_carset_light_travelmode),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_light_travelmode), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_light_doorambient:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_DOOR_AMBIENT,
					mObjCarInfo.mbyDoorAmbientlight,
					getResources().getString(
							R.string.str_dz7_carset_light_doorambient),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 10, 10, 0,
					getResources()
					.getString(R.string.str_dz7_carset_light_Unit));
			break;
		case R.id.btn_dz7_carset_light_footwell:
			setMutilSel2Msg(
					ReMQB7Protocol.LIGHT_FOOTWELL,
					mObjCarInfo.mbyFootwelllight,
					getResources().getString(
							R.string.str_dz7_carset_light_footwell),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 10, 10, 0,
					getResources()
					.getString(R.string.str_dz7_carset_light_Unit));
			break;
		case R.id.btn_dz7_carset_open2close_convs:
			setMutilSel2Msg(
					ReMQB7Protocol.OPNECLSE_CONV,
					mObjCarInfo.mbyConvOpen,
					getResources().getString(
							R.string.str_dz7_carset_open2close_convs),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_open2close_convs), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_open2close_doorunlock:
			setMutilSel2Msg(
					ReMQB7Protocol.OPNECLSE_DOOR_UNLOCK,
					mObjCarInfo.mbyDoorUnlock,
					getResources().getString(
							R.string.str_dz7_carset_open2close_doorunlock),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_open2close_doorunlock), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_dis:
			setMutilSel2Msg(ReMQB7Protocol.UNIT_DISTANCE,
					mObjCarInfo.mbyDistanceUnit,
					getResources().getString(R.string.str_dz7_carset_unit_dis),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_dis), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_speed:
			setMutilSel2Msg(ReMQB7Protocol.UNIT_SPEED,
					mObjCarInfo.mbySpeedUnint,
					getResources()
							.getString(R.string.str_dz7_carset_unit_speed),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_speed), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_temp:
			setMutilSel2Msg(
					ReMQB7Protocol.UNIT_TEMP,
					mObjCarInfo.mbyTemperatureUnit,
					getResources().getString(R.string.str_dz7_carset_unit_temp),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_temp), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_vol:
			setMutilSel2Msg(ReMQB7Protocol.UNIT_VOLUME,
					mObjCarInfo.mbyVolumeUnit,
					getResources().getString(R.string.str_dz7_carset_unit_vol),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_vol), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_consumption:
			setMutilSel2Msg(
					ReMQB7Protocol.UNIT_COMSUMPTION,
					mObjCarInfo.mbyConsumptionUnit,
					getResources().getString(
							R.string.str_dz7_carset_unit_consumption),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_consumption), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_unit_tpms:
			setMutilSel2Msg(
					ReMQB7Protocol.UNIT_PRESSURE,
					mObjCarInfo.mbyPressureUnit,
					getResources().getString(R.string.str_dz7_carset_unit_tpms),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_unit_tpms), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_lang:
			setMutilSel2Msg(ReMQB7Protocol.LANG_SET, mObjCarInfo.mbylangIndex,
					getResources().getString(R.string.str_dz7_carset_lang),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_dz7_carset_lang), 0, 0, 0, "");
			break;
		case R.id.btn_dz7_carset_factory_assist:
			setVal(ReMQB7Protocol.FACTORY_ASSIST, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_park:
			setVal(ReMQB7Protocol.FACTORY_PARK, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_light:
			setVal(ReMQB7Protocol.FACTORY_LIGHT, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_wipers:
			setVal(ReMQB7Protocol.FACTORY_WIPER, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_openclose:
			setVal(ReMQB7Protocol.FACTORY_OPNECLE, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_mfd:
			setVal(ReMQB7Protocol.FACTORY_MFD, 0x01);
			break;
		case R.id.btn_dz7_carset_factory_all:
			setVal(ReMQB7Protocol.FACTORY_ALL, 0x01);
			break;
		default:
			break;
		}
	}

	private void Inquiry() {
		byte[] bydata = new byte[2];
		bydata[0] = ReMQB7Protocol.DATA_TYPE_CAR_STATE_INFO;
		bydata[1] = 0x00;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata,
				mObjProtocol);

	}
}
