package com.can.ui.ford;

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
import com.can.parser.DDef.BaseInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.ford.ReFordProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.ResDef;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

/**
 * ClassName:CanFordCarSet
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-12上午10:42:46
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanFordCarSet extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private CheckBox[] mObjCheckBoxs = null;

	private Protocol mObjProtocol = null;
	private BaseInfo mObjBaseInfo = new BaseInfo();

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
			mObjView = inflater.inflate(R.layout.ford_carset, container, false);
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
		super.getData(DDef.BASE_CMD_ID);
	}

	private void init() {
		if (mObjView != null) {

			int iIndex = 0;
			mObjCheckBoxs = new CheckBox[ResDef.mFordCheckboxId.length];
			for (int iter : ResDef.mFordCheckboxId) {
				mObjCheckBoxs[iIndex] = (CheckBox) mObjView.findViewById(iter);
				mObjCheckBoxs[iIndex].setOnClickListener(this);
				iIndex++;
			}

			for (int iter : ResDef.mFordBtnCmdId) {
				mObjView.findViewById(iter).setOnClickListener(this);
			}
		}
	}

	private void setVal(byte byCmd, int iVal) {

		boolean bsend = true;
		byte[] bydata = new byte[2];

		if (byCmd == ReFordProtocol.TrunLightonce) {

			bydata[0] = ReFordProtocol.CAR_SET;
			bydata[1] = (byte) (iVal+0x03);

		} else if (byCmd == ReFordProtocol.ToneType) {
			
			bydata[0] = ReFordProtocol.CAR_SET;
			bydata[1] = (byte) (iVal+0x09);
			
		} else if (byCmd == ReFordProtocol.Mileunit) {
			
			bydata[0] = ReFordProtocol.CAR_SET;
			bydata[1] = (byte) (iVal+0x0D);

		} else if (byCmd == ReFordProtocol.Autobright) {
			
			bydata[0] = ReFordProtocol.CAR_SET;
			bydata[1] = (byte) (iVal+0x10);
		} else {
			bsend = false;
		}

		if (bsend) {
			super.sendMsg(ReFordProtocol.DATA_TYPE_CTRL_CMD, bydata,
					mObjProtocol);
		}
	}

	private void setCheckState2Msg(byte byCmd, byte bysate) {

		byte[] bydata = new byte[2];

		if (byCmd == ReFordProtocol.Rainsensor
				|| byCmd == ReFordProtocol.Interiorlight
				|| byCmd == ReFordProtocol.Parklockctrl) {
			bydata[0] = byCmd;
		} else {
			bydata[0] = ReFordProtocol.CAR_SET;
		}

		if (byCmd == ReFordProtocol.TRACTIONCTRL) {
			bydata[1] = (byte) ((bysate == 0) ? 0x02 : 0x01);
		} else if (byCmd == ReFordProtocol.Msgtoneon) {
			bydata[1] = (byte) ((bysate == 0) ? 0x05 : 0x06);
		} else if (byCmd == ReFordProtocol.Warntoneon) {
			bydata[1] = (byte) ((bysate == 0) ? 0x07 : 0x08);
		} else if (byCmd == ReFordProtocol.Rainsensor
				|| byCmd == ReFordProtocol.Interiorlight
				|| byCmd == ReFordProtocol.Parklockctrl) {
			bydata[1] = bysate;
		}

		super.sendMsg(ReFordProtocol.DATA_TYPE_CTRL_CMD, bydata, mObjProtocol);
	}

	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.btn_ford_carset_trunLightonce:
			for (int iter : ResDef.mFordTrunLightonce) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_ford_carset_tonetype:
			for (int iter : ResDef.mFordTonetype) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_ford_carset_mileunit:
			for (int iter : ResDef.mFordMileUnit) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_ford_carset_autobright:
			for (int iter : ResDef.mFordAutobright) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		case R.id.btn_ford_carset_showenginehot:
			for (int iter : ResDef.mFordShowenginehot) {
				arrayList.add(getResources().getString(iter));
			}
			break;
		}

		return arrayList;
	}

	private void setMutilSel2Msg(final byte byCmd, final int istate,
			final String strTitle, final PopDialog.E_POPDIALOG_TYPE eType,
			final ArrayList<String> arrayList, final int iMax, final int iStep,
			final int ioffset, final String string) {
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

	private void setAttr(BaseInfo data) {
		if (data != null) {

			mObjBaseInfo = data;

			int iIndex = 0;
			for (CheckBox checkBox : mObjCheckBoxs) {

				boolean bchecked = false;

				switch (iIndex) {
				case 0:
					bchecked = (data.mbyTractionCtrl == 0x01) ? true : false;
					break;
				case 1:
					bchecked = (data.mbyMsgToneOn == 0x01) ? true : false;
					break;
				case 2:
					bchecked = (data.mbyWarnToneOn == 0x01) ? true : false;
					break;
				case 3:
					bchecked = (data.mbyRainSensor == 0x01) ? true : false;
					break;
				case 4:
					bchecked = (data.mbyInteriorlight == 0x01) ? true : false;
					break;
				case 5:
					bchecked = (data.mbyParklockCtrl == 0x01) ? true : false;
					break;
				default:
					break;
				}

				if (checkBox != null) {
					checkBox.setChecked(bchecked);
				}

				iIndex++;
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
				break;
			case DDef.BASE_CMD_ID:
				setAttr((BaseInfo) msg.obj);
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
		case R.id.checkbox_ford_carset_tractionctrl_onoff:
			setCheckState2Msg(ReFordProtocol.TRACTIONCTRL,
					(byte) ((mObjCheckBoxs[0].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_ford_carset_msgtoneon_onoff:
			setCheckState2Msg(ReFordProtocol.Msgtoneon,
					(byte) ((mObjCheckBoxs[1].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_ford_carset_warntoneon_onoff:
			setCheckState2Msg(ReFordProtocol.Warntoneon,
					(byte) ((mObjCheckBoxs[2].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_ford_carset_rainsensor_onoff:
			setCheckState2Msg(ReFordProtocol.Rainsensor,
					(byte) ((mObjCheckBoxs[3].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_ford_carset_interiorlight_onoff:
			setCheckState2Msg(ReFordProtocol.Interiorlight,
					(byte) ((mObjCheckBoxs[4].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.checkbox_ford_carset_parklockctrl_onoff:
			setCheckState2Msg(ReFordProtocol.Parklockctrl,
					(byte) ((mObjCheckBoxs[5].isChecked()) ? 0x01 : 0x00));
			break;
		case R.id.btn_ford_carset_trunLightonce:
			setMutilSel2Msg(
					ReFordProtocol.TrunLightonce,
					mObjBaseInfo.mbyTrunLightOnce,
					getResources().getString(
							R.string.str_ford_carset_trunLightonce),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_ford_carset_trunLightonce), 0, 0, 0,
					"");
			break;
		case R.id.btn_ford_carset_tonetype:
			setMutilSel2Msg(
					ReFordProtocol.ToneType,
					mObjBaseInfo.mbyToneType,
					getResources().getString(R.string.str_ford_carset_tonetype),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_ford_carset_tonetype), 0, 0, 0, "");
			break;
		case R.id.btn_ford_carset_mileunit:
			setMutilSel2Msg(
					ReFordProtocol.Mileunit,
					mObjBaseInfo.mbyMileUnit,
					getResources().getString(R.string.str_ford_carset_mileunit),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_ford_carset_mileunit), 0, 0, 0, "");
			break;
		case R.id.btn_ford_carset_travelplan:
			setMutilSel2Msg(
					ReFordProtocol.NOT_SEND,
					mObjBaseInfo.mbyPlan,
					getResources().getString(
							R.string.str_ford_carset_travelplan),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 5, 1, 0,
					"");
			break;
		case R.id.btn_ford_carset_travelspeed:
			setMutilSel2Msg(
					ReFordProtocol.NOT_SEND,
					mObjBaseInfo.mbySpeed,
					getResources().getString(
							R.string.str_ford_carset_travelspeed),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 5, 1, 0,
					"");
			break;
		case R.id.btn_ford_carset_autobright:
			setMutilSel2Msg(
					ReFordProtocol.Autobright,
					mObjBaseInfo.mbyAotuBright,
					getResources().getString(
							R.string.str_ford_carset_autobright),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_ford_carset_autobright), 0, 0, 0, "");
			break;
		case R.id.btn_ford_carset_enginehotper:
			setMutilSel2Msg(
					ReFordProtocol.NOT_SEND,
					mObjBaseInfo.mbyEngineHotPer,
					getResources().getString(
							R.string.str_ford_carset_enginehotper),
					E_POPDIALOG_TYPE.ePopDialog_carset_seekbar, null, 100, 1,
					0,
					getResources()
							.getString(R.string.str_dz7_carset_light_Unit));
			break;
		case R.id.btn_ford_carset_showenginehot:
			setMutilSel2Msg(
					ReFordProtocol.NOT_SEND,
					mObjBaseInfo.mbyShowEngineHot,
					getResources().getString(
							R.string.str_ford_carset_showenginehot),
					E_POPDIALOG_TYPE.ePopDialog_carset_list,
					getArrayList(R.id.btn_ford_carset_showenginehot), 0, 0, 0,
					"");
			break;
		default:
			break;
		}
	}
}
