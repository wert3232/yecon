package com.can.ui.jeep;

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
import com.can.parser.DDef.CarInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.jeep.ReJeepProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanJeepCarSet
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-27下午2:35:48
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJeepCarSet extends CanFrament implements OnClickListener {

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
			mObjView = inflater.inflate(R.layout.jeep_carset, container, false);
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
	}

	private void init() {
		if (mObjView != null) {

			int iIndex = 0;
			mObjCheckBoxs = new CheckBox[ResDef.mJeepCheckboxCarset.length];
			for (int iter : ResDef.mJeepCheckboxCarset) {
				mObjCheckBoxs[iIndex] = (CheckBox) mObjView.findViewById(iter);
				mObjCheckBoxs[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : ResDef.mJeepBtnCarset) {
				mObjView.findViewById(iter).setOnClickListener(this);
			}
		}
	}

	private void setAttr(CarInfo info) {
		if (info != null) {
			mObjCarInfo = info;
			
			int iIndex = 0;
			for (CheckBox checkBox : mObjCheckBoxs) {
				if (checkBox != null) {
					boolean bChecked = false;

					switch (iIndex) {
					case 0:
						bChecked = (info.mbyBackcarTiltMirror == 0x01) ? true
								: false;
						break;
					case 1:
						bChecked = (info.mbyParksenseDynTrack == 0x01) ? true : false;
						break;
					case 2:
						bChecked = (info.mbyRainsensingwipers == 0x01) ? true
								: false;
						break;
					case 3:
						bChecked = (info.mbyRampstartassist == 0x01) ? true
								: false;
						break;
					case 4:
						bChecked = (info.mbyParksensestaTrack == 0x01) ? true
								: false;
						break;
					case 5:
						bChecked = (info.mbyRearRadarparking == 0x01) ? true
								: false;
						break;
					case 6:
						bChecked = (info.mbyAllowautobrakeser == 0x01) ? true
								: false;
						break;
					case 7:
						bChecked = (info.mbyAutomaticparkbrake == 0x01) ? true
								: false;
						break;
					case 8:
						bChecked = (info.mbyAutobrakfrontcwarn == 0x01) ? true
								: false;
						break;
					case 9:
						bChecked = (info.mbyAutoantidazzlelight == 0x01) ? true
								: false;
						break;
					case 10:
						bChecked = (info.mbyDooralarm == 0x01) ? true
								: false;
						break;
					case 11:
						bChecked = (info.mbyBuzzerswitchonoff == 0x01) ? true
								: false;
						break;
					case 12:
						bChecked = (info.mbySautowheadlightwiper == 0x01) ? true
								: false;
						break;
					case 13:
						bChecked = (info.mbyDaytimeRunninglight == 0x01) ? true
								: false;
						break;
					case 14:
						bChecked = (info.mbyLockcarturnlightflash == 0x01) ? true
								: false;
						break;
					case 15:
						bChecked = (info.mbyAutomaticdoorlock == 0x01) ? true
								: false;
						break;
					case 16:
						bChecked = (info.mbyAutounlockgetoff == 0x01) ? true
								: false;
						break;
					case 17:
						bChecked = (info.mbyDrivingautolatch == 0x01) ? true
								: false;
						break;
					case 18:
						bChecked = (info.mbyLockcarprompttone == 0x01) ? true
								: false;
						break;
					case 19:
						bChecked = (info.mbyForfirstunlockcarkeys == 0x01) ? true
								: false;
						break;
					case 20:
						bChecked = (info.mbyNokeyentry == 0x01) ? true
								: false;
						break;
					case 21:
						bChecked = (info.mbySmartkeypersonal == 0x01) ? true
								: false;
						break;
					case 22:
						bChecked = (info.mbyElectrictaildooralarm == 0x01) ? true
								: false;
						break;
					case 23:
						bChecked = (info.mbySeatconvenientinout == 0x01) ? true
								: false;
						break;
					case 24:
						bChecked = (info.mbyUpdownautosuspension == 0x01) ? true : false;
						break;
					case 25:
						bChecked = (info.mbyDispsuspensioninfo == 0x01) ? true
								: false;
						break;
					case 26:
						bChecked = (info.mbyTirejack == 0x01) ? true
								: false;
						break;
					case 27:
						bChecked = (info.mbyTransportmode == 0x01) ? true
								: false;
						break;
					case 28:
						bChecked = (info.mbyTirecalibrationmodel == 0x01) ? true
								: false;
						break;
					case 29:
						bChecked = (info.mbyMirrorlightmirror == 0x01) ? true
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
	}
	
	private void setVal(byte byCmd, int iVal) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = (byte) iVal;
		
		if (byCmd == ReJeepProtocol.headlightoffdelay ||
				byCmd == ReJeepProtocol.nearlightupauto) {
			if (iVal == 1) {
				bydata[1] = 0x1E;
			}else if (iVal == 2) {
				bydata[1] = 0x3C;
			}else if (iVal == 3) {
				bydata[1] = 0x5A;
			}
		}else if (byCmd == ReJeepProtocol.enginepowershutdown) {
			if (iVal == 1) {
				bydata[1] = 0x03;
			}else if (iVal == 2) {
				bydata[1] = 0x14;
			}else if (iVal == 3) {
				bydata[1] = 0x28;
			}
		}else if (byCmd == ReJeepProtocol.langset) {
			if (iVal == 1) {
				bydata[1] = 0x09;
			}
		}else if (byCmd == ReJeepProtocol.autostartdrivingseat) {
			bydata[1] += 1;
		}
		
		super.sendMsg(ReJeepProtocol.DATA_TYPE_CAR_SET, bydata, mObjProtocol);
	}

	private void setCheckState2Msg(byte byCmd, byte bysate) {

		byte[] bydata = new byte[2];
		bydata[0] = byCmd;
		bydata[1] = bysate;
		super.sendMsg(ReJeepProtocol.DATA_TYPE_CAR_SET, bydata, mObjProtocol);
	}

	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.btn_jeep_carset_parksense:
			for (int iter : ResDef.mJeepParksense) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_fparksensevol:
			for (int iter : ResDef.mJeepfparksensevol) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_rparksensevol:
			for (int iter : ResDef.mJeepfparksensevol) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_blindspotwarn:
			for (int iter : ResDef.mJeepblindspotwarn) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_lanedeparturecal:
			for (int iter : ResDef.mJeeplanedeparturecal) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_fcollisionwarn:
			for (int iter : ResDef.mJeepfcollisionwarn) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_autostartdrivingseat:
			for (int iter : ResDef.mJeepautostartdrivingseat) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_nearlightupauto:
			for (int iter : ResDef.mJeepnearlightupauto) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_lanedeparturewarning:
			for (int iter : ResDef.mJeeplanedeparturewarning) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_headlightoffdelay:
			for (int iter : ResDef.mJeepnearlightupauto) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_enginepowershutdown:
			for (int iter : ResDef.mJeepenginepowershutdown) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_unitset:
			for (int iter : ResDef.mJeepunitset) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.btn_jeep_carset_langset:
			for (int iter : ResDef.mJeeplangset) {
				arrayList.add(getString(iter));
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
				setAttr((CarInfo) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.checkbox_jeep_carset_tiltrmirror_onoff:
			setCheckState2Msg(ReJeepProtocol.tiltrmirror,
					(byte) ((mObjCheckBoxs[0].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_affectdyncline_onoff:
			setCheckState2Msg(ReJeepProtocol.affectdyncline,
					(byte) ((mObjCheckBoxs[1].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_jeep_carset_rainfallwiper_onoff:
			setCheckState2Msg(ReJeepProtocol.rainfallwiper,
					(byte) ((mObjCheckBoxs[2].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_rampauxiliary_onoff:
			setCheckState2Msg(ReJeepProtocol.rampauxiliary,
					(byte) ((mObjCheckBoxs[3].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_videoparkfixline_onoff:
			setCheckState2Msg(ReJeepProtocol.videoparkfixline,
					(byte) ((mObjCheckBoxs[4].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_jeep_carset_rradarautoparkaux_onoff:
			setCheckState2Msg(ReJeepProtocol.rradarautoparkaux,
					(byte) ((mObjCheckBoxs[5].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_allowautobrakeservice_onoff:
			setCheckState2Msg(ReJeepProtocol.allowautobrakeservice,
					(byte) ((mObjCheckBoxs[6].isChecked()) ? 0x01 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_autoparkbrake_onoff:
			setCheckState2Msg(ReJeepProtocol.autoparkbrake,
					(byte) ((mObjCheckBoxs[7].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_fcollisionwarnbrake_onoff:
			setCheckState2Msg(ReJeepProtocol.fcollisionwarnbrake,
					(byte) ((mObjCheckBoxs[8].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_antiglareautobeam_onoff:
			setCheckState2Msg(ReJeepProtocol.antiglareautobeam,
					(byte) ((mObjCheckBoxs[9].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_cardoorwarn_onoff:
			setCheckState2Msg(ReJeepProtocol.cardoorwarn,
					(byte) ((mObjCheckBoxs[10].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_buzzerswitch_onoff:
			setCheckState2Msg(ReJeepProtocol.buzzerswitch,
					(byte) ((mObjCheckBoxs[11].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_jeep_carset_wiperautostartlight_onoff:
			setCheckState2Msg(ReJeepProtocol.wiperautostartlight,
					(byte) ((mObjCheckBoxs[12].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_daytimerunlight_onoff:
			setCheckState2Msg(ReJeepProtocol.daytimerunlight,
					(byte) ((mObjCheckBoxs[13].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_lightflash_onoff:
			setCheckState2Msg(ReJeepProtocol.lightflash,
					(byte) ((mObjCheckBoxs[14].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_autodoorlock_onoff:
			setCheckState2Msg(ReJeepProtocol.autodoorlock,
					(byte) ((mObjCheckBoxs[15].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_getoffautounlock_onoff:
			setCheckState2Msg(ReJeepProtocol.getoffautounlock,
					(byte) ((mObjCheckBoxs[16].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_driveautopadlock_onoff:
			setCheckState2Msg(ReJeepProtocol.driveautopadlock,
					(byte) ((mObjCheckBoxs[17].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_lockcarvoice_onoff:
			setCheckState2Msg(ReJeepProtocol.lockcarvoice,
					(byte) ((mObjCheckBoxs[18].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_firsttimunlock_onoff:
			setCheckState2Msg(ReJeepProtocol.firsttimunlock,
					(byte) ((mObjCheckBoxs[19].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_nokeytoenter_onoff:
			setCheckState2Msg(ReJeepProtocol.nokeytoenter,
					(byte) ((mObjCheckBoxs[20].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_intellkeyperset_onoff:
			setCheckState2Msg(ReJeepProtocol.intellkeyperset,
					(byte) ((mObjCheckBoxs[21].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_electricdooralarm_onoff:
			setCheckState2Msg(ReJeepProtocol.electricdooralarm,
					(byte) ((mObjCheckBoxs[22].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_seatconvpassinout_onoff:
			setCheckState2Msg(ReJeepProtocol.seatconvpassinout,
					(byte) ((mObjCheckBoxs[23].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_onoroffadjustsuspen_onoff:
			setCheckState2Msg(ReJeepProtocol.onoroffadjustsuspen,
					(byte) ((mObjCheckBoxs[24].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_showsuspensioninfo_onoff:
			setCheckState2Msg(ReJeepProtocol.showsuspensioninfo,
					(byte) ((mObjCheckBoxs[25].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_tirejack_onoff:
			setCheckState2Msg(ReJeepProtocol.tirejack,
					(byte) ((mObjCheckBoxs[26].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_modeoftransport_onoff:
			setCheckState2Msg(ReJeepProtocol.modeoftransport,
					(byte) ((mObjCheckBoxs[27].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_wheelalignmentmodel_onoff:
			setCheckState2Msg(ReJeepProtocol.wheelalignmentmodel,
					(byte) ((mObjCheckBoxs[28].isChecked()) ? 0x02 : 0x01));
			break;
		case R.id.checkbox_jeep_carset_rearviewadjustlens_onoff:
			setCheckState2Msg(ReJeepProtocol.rearviewadjustlens,
					(byte) ((mObjCheckBoxs[29].isChecked()) ? 0x02 : 0x01));
			break;
			
		case R.id.btn_jeep_carset_parksense:
			setMutilSel2Msg(
					ReJeepProtocol.parksense,
					mObjCarInfo.mbyParksenseRadar,
					getResources().getString(
							R.string.str_tx_jeep_carset_parksense),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_parksense), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_fparksensevol:
			setMutilSel2Msg(
					ReJeepProtocol.fparksensevol,
					mObjCarInfo.mbyFrontParksenseVol,
					getResources().getString(
							R.string.str_tx_jeep_carset_fparksensevol),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_fparksensevol), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_rparksensevol:
			setMutilSel2Msg(
					ReJeepProtocol.rparksensevol,
					mObjCarInfo.mbyAfterParksenseVol,
					getResources().getString(
							R.string.str_tx_jeep_carset_rparksensevol),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_rparksensevol), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_blindspotwarn:
			setMutilSel2Msg(
					ReJeepProtocol.blindspotwarn,
					mObjCarInfo.mbyBusywarning,
					getResources().getString(
							R.string.str_tx_jeep_carset_blindspotwarn),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_blindspotwarn), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_lanedeparturecal:
			setMutilSel2Msg(
					ReJeepProtocol.lanedeparturecal,
					mObjCarInfo.mbyLanedevcorrection,
					getResources().getString(
							R.string.str_tx_jeep_carset_lanedeparturecal),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_lanedeparturecal), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_fcollisionwarn:
			setMutilSel2Msg(
					ReJeepProtocol.fcollisionwarn,
					mObjCarInfo.mbyForwardcollisionwarn,
					getResources().getString(
							R.string.str_tx_jeep_carset_fcollisionwarn),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_fcollisionwarn), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_autostartdrivingseat:
			setMutilSel2Msg(
					ReJeepProtocol.autostartdrivingseat,
					mObjCarInfo.mbyAutodriverseatvehicle,
					getResources().getString(
							R.string.str_tx_jeep_carset_autostartdrivingseat),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_autostartdrivingseat), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_nearlightupauto:
			setMutilSel2Msg(
					ReJeepProtocol.nearlightupauto,
					mObjCarInfo.mbyAutolightuphdlightclose,
					getResources().getString(
							R.string.str_tx_jeep_carset_nearlightupauto),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_nearlightupauto), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_lanedeparturewarning:
			setMutilSel2Msg(
					ReJeepProtocol.lanedeparturewarning,
					mObjCarInfo.mbyLaneDepartureWarn,
					getResources().getString(
							R.string.str_tx_jeep_carset_lanedeparturewarning),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_lanedeparturewarning), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_headlightoffdelay:
			setMutilSel2Msg(
					ReJeepProtocol.headlightoffdelay,
					mObjCarInfo.mbyHeadlightoffdelay,
					getResources().getString(
							R.string.str_tx_jeep_carset_headlightoffdelay),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_headlightoffdelay), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_enginepowershutdown:
			setMutilSel2Msg(
					ReJeepProtocol.enginepowershutdown,
					mObjCarInfo.mbyEnginepoweroffdelay,
					getResources().getString(
							R.string.str_tx_jeep_carset_enginepowershutdown),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_enginepowershutdown), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_unitset:
			setMutilSel2Msg(
					ReJeepProtocol.unitset,
					mObjCarInfo.mbyUnitSetting,
					getResources().getString(
							R.string.str_tx_jeep_carset_unitset),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_unitset), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_langset:
			setMutilSel2Msg(
					ReJeepProtocol.langset,
					mObjCarInfo.mbylangSetting,
					getResources().getString(
							R.string.str_tx_jeep_carset_langset),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_jeep_carset_langset), 0, 0, 0, "");
			break;
		case R.id.btn_jeep_carset_reset:
			setVal(ReJeepProtocol.Reset, 0x01);
			break;
		default:
			break;
		}
	}
	
	private void Inquiry() {
		if (mObjProtocol != null) {
			byte[] bydata = new byte[1];
			bydata[0] = 0x07;
			super.sendMsg(ReJeepProtocol.DATA_TYPE_REQUEST_INFO, bydata,
					mObjProtocol);
		}
	}
}
