package com.can.ui.domestic;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CQCarSet;
import com.can.parser.Protocol;
import com.can.parser.raise.domestic.ReCQProtol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

public class CanCQCarSet extends CanFrament implements OnClickListener{

	private CQCarSet mCarSet = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private int[] mStrLanId = {
		R.string.str_gm_lan_chinese,
		R.string.str_gm_lan_english
	};
	
	private int[] mStrCycleId = {
		R.string.str_cq_cycle_auto,
		R.string.str_cq_cycle_manu
	};
	
	private int[] mStrCozyId = {
		R.string.str_cq_slow,
		R.string.str_cq_normal,
		R.string.str_cq_fast
	};
	
	private int[] mStrSteerId = {
		R.string.str_cq_movement,
		R.string.str_cq_standard,
		R.string.str_cq_Cozy
	};
	
	private int[] mStrRemoteUnlockId = {
		R.string.str_gm_all_door,
		R.string.str_gm_driver_door
		
	};
	
	private int[] mStrHomeId = {
		R.string.str_cq_close,
		R.string.str_cq_lowbeamlights,
		R.string.str_cq_rear_light
	};
	
	private int[] mStrSendId = {
		R.string.str_gm_low,
		R.string.str_gm_mid,
		R.string.str_gm_high
	};
	
	private int[] mStrSeatLvId = {
		R.string.str_cq_close,
		R.string.str_cq_lv1,
		R.string.str_cq_lv2,
		R.string.str_cq_lv3
	};
	
	private int[] mCheckBoxId = {
		R.id.cq_checkbox_compressor,
		R.id.cq_checkbox_anion_mode,
		R.id.cq_checkbox_leftautoheat,
		R.id.cq_checkbox_rightautoheat,
		R.id.cq_checkbox_welcomefunc,
		R.id.cq_checkbox_autokey,
		R.id.cq_checkbox_lockbyspeed,
		R.id.cq_checkbox_autounlock,
		R.id.cq_checkbox_remotewind,
		R.id.cq_checkbox_frontwiper,
		R.id.cq_checkbox_rearwiper,
		R.id.cq_checkbox_rearviewmirror,
		R.id.cq_checkbox_foglights,
		R.id.cq_checkbox_daylights
	};
	
	private int[] mBtnId = {
		R.id.cq_rl_lan,
		R.id.cq_rl_cyclectrl,
		R.id.cq_rl_cozy,
		R.id.cq_rl_lan,
		R.id.cq_rl_overspeed,
		R.id.cq_rl_alramvol,
		R.id.cq_rl_powertime,
		R.id.cq_rl_starttime,
		R.id.cq_rl_steermode,
		R.id.cq_rl_remoteunlock,
		R.id.cq_rl_followtohome,
		R.id.cq_rl_lightsensitivity
	};
	
	private CheckBox[] mCheckBoxs = new CheckBox[mCheckBoxId.length];
	private TextView mLeftSeat = null;
	private TextView mRightSeat = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.cq_car_set, null);
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
				mCheckBoxs[i] = (CheckBox) mView.findViewById(mCheckBoxId[i]);
				mCheckBoxs[i].setOnClickListener(this);
			}
			
			for (int iter : mBtnId) {
				mView.findViewById(iter).setOnClickListener(this);
			}
			
			mView.findViewById(R.id.cq_btn_reset).setOnClickListener(this);
			mLeftSeat = (TextView) mView.findViewById(R.id.cq_btn_leftheatlv);
			mRightSeat = (TextView) mView.findViewById(R.id.cq_btn_rightheatlv);
			mLeftSeat.setOnClickListener(this);
			mRightSeat.setOnClickListener(this);
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mProtocol = (Protocol) msg.obj;
				getData(DDef.CAR_SET_CMD_ID);
				break;
			case DDef.CAR_SET_CMD_ID:
				mCarSet = (CQCarSet) msg.obj;
				setCarSet();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@Override
	public void onClick(View v) {

		if (mCarSet != null) {
			switch (v.getId()) {
			case R.id.cq_checkbox_compressor:
				setVal((byte) 0x02, mCarSet.mCompressor == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_anion_mode:
				setVal((byte) 0x18, mCarSet.mAnionMode == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_leftautoheat:
				setVal(0x05, mCarSet.mLeftAutoHeat == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_rightautoheat:
				setVal(0x06, mCarSet.mRightAutoHeat == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_welcomefunc:
				setVal(0x19, mCarSet.mWelcomeFunc == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_autokey:
				setVal(0x1A, mCarSet.mAutoKey == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_lockbyspeed:
				setVal(0x0D, mCarSet.mLockBySpeed == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_autounlock:
				setVal(0x0E, mCarSet.mAutoUnlock == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_remotewind:
				setVal(0x0F, mCarSet.mRemoteWind == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_frontwiper:
				setVal(0x10, mCarSet.mFrontWiper == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_rearwiper:
				setVal(0x11, mCarSet.mRearWiper == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_rearviewmirror:
				setVal(0x1B, mCarSet.mRearviewMirror == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_foglights:
				setVal(0x13, mCarSet.mFogLights == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_checkbox_daylights:
				setVal(0x14, mCarSet.mDayLights == 0x01 ? 0x02 : 0x01);
				break;
			case R.id.cq_rl_lan:
				setMutilSel2Msg(0x01,
						(byte) (mCarSet.mLan - 1),
						getString(R.string.str_gm_lan),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_lan), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_cyclectrl:
				setMutilSel2Msg(0x03,
						(byte) (mCarSet.mCycleCtrl - 1),
						getString(R.string.str_cq_cyclectrl),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_cyclectrl), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_cozy:
				setMutilSel2Msg(0x04,
						(byte) (mCarSet.mCozy - 1),
						getString(R.string.str_cq_cozy),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_cozy), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_overspeed:
				setMutilSel2Msg(0x07,
						mCarSet.mOverSpeed,
						getString(R.string.str_cq_overspeed),
						E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
						null, 0x14, 10, 0, "");
				break;
			case R.id.cq_rl_alramvol:
				setMutilSel2Msg(0x08,
						(byte) (mCarSet.mAlramVol - 1),
						getString(R.string.str_cq_alramvol),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_alramvol), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_powertime:
				setMutilSel2Msg(0x09,
						mCarSet.mPowerTime,
						getString(R.string.str_cq_powertime),
						E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
						null, 0x1E, 1, 0, "");
				break;
			case R.id.cq_rl_starttime:
				setMutilSel2Msg(0x0A,
						mCarSet.mStartTime,
						getString(R.string.str_cq_starttime),
						E_POPDIALOG_TYPE.ePopDialog_carset_seekbar,
						null, 0x1E, 1, 0, "");
				break;
			case R.id.cq_rl_steermode:
				setMutilSel2Msg(0x0B,
						(byte) (mCarSet.mSteerMode - 1),
						getString(R.string.str_cq_steermode),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_steermode), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_remoteunlock:
				setMutilSel2Msg(0x0C,
						(byte) (mCarSet.mRemoteUnlock - 1),
						getString(R.string.str_cq_remoteunlock),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_remoteunlock), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_followtohome:
				setMutilSel2Msg(0x12,
						(byte) (mCarSet.mFollowToHome - 1),
						getString(R.string.str_cq_followtohome),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_followtohome), 0x00, 0, 0, "");
				break;
			case R.id.cq_rl_lightsensitivity:
				setMutilSel2Msg(0x15,
						(byte) (mCarSet.mLightSensitivity - 1),
						getString(R.string.str_cq_lightsensitivity),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.cq_rl_lightsensitivity), 0x00, 0, 0, "");
				break;
			case R.id.cq_btn_reset:
				setVal(0x00, 0xFF);
				break;
			case R.id.cq_btn_leftheatlv:
				setVal(0x16, 0x02);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				setVal(0x16, 0x01);
				break;
			case R.id.cq_btn_rightheatlv:
				setVal(0x17, 0x02);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				setVal(0x17, 0x01);
				break;
			default:
				break;
			}
		}
	}
	
	private void setVal(int byCmd, int iVal) {
		byte[] bydata = new byte[2];
		bydata[0] = (byte) byCmd;
		bydata[1] = (byte) iVal;
		if (byCmd == 0x07) {
			bydata[1] = (byte) (iVal/10);
		}
		super.sendMsg(ReCQProtol.DATA_TYPE_CAR_SET_REQUEST, bydata, mProtocol);
	}
	
	private void setCarSet() {
		if (mView != null && mCarSet != null) {
			int iIndex = 0;
			for (CheckBox checkBox : mCheckBoxs) {
				boolean bChecked = false;
				if (checkBox != null) {
					switch (iIndex) {
					case 0:
						bChecked = mCarSet.mCompressor == 0x01 ? true : false;
						break;
					case 1:
						bChecked = mCarSet.mAnionMode == 0x01 ? true : false;
						break;
					case 2:
						bChecked = mCarSet.mLeftAutoHeat == 0x01 ? true : false;
						break;
					case 3:
						bChecked = mCarSet.mRightAutoHeat == 0x01 ? true : false;
						break;
					case 4:
						bChecked = mCarSet.mWelcomeFunc == 0x01 ? true : false;
						break;
					case 5:
						bChecked = mCarSet.mAutoKey == 0x01 ? true : false;
						break;
					case 6:
						bChecked = mCarSet.mLockBySpeed == 0x01 ? true : false;
						break;
					case 7:
						bChecked = mCarSet.mAutoUnlock == 0x01 ? true : false;
						break;
					case 8:
						bChecked = mCarSet.mRemoteWind == 0x01 ? true : false;
						break;
					case 9:
						bChecked = mCarSet.mFrontWiper == 0x01 ? true : false;
						break;
					case 10:
						bChecked = mCarSet.mRearWiper == 0x01 ? true : false;
						break;
					case 11:
						bChecked = mCarSet.mRearviewMirror == 0x01 ? true : false;
						break;
					case 12:
						bChecked = mCarSet.mFogLights == 0x01 ? true : false;
						break;
					case 13:
						bChecked = mCarSet.mDayLights == 0x01 ? true : false;
						break;
					default:
						break;
					}
					checkBox.setChecked(bChecked);
				}
				iIndex++;
			}
			
			mLeftSeat.setText(getString(mStrSeatLvId[mCarSet.mLeftHeatLv]));
			mRightSeat.setText(getString(mStrSeatLvId[mCarSet.mRightHeatLv]));
		}
	}
	
	private void setMutilSel2Msg(final int byCmd, final byte bystate,
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
				setVal(byCmd, iPos + 1);
			}

			@Override
			public void onSeekVal(int iVal) {
				setVal(byCmd, iVal);
			}

			@Override
			public void onConfirm() {

			}
		});

		dialog.show(getFragmentManager(), this.getClass().getName());
	}
	
	private ArrayList<String> getArrayList(int iId) {
		ArrayList<String> arrayList = new ArrayList<String>();

		switch (iId) {
		case R.id.cq_rl_lan:
			for (int iter : mStrLanId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_cyclectrl:
			for (int iter : mStrCycleId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_cozy:
			for (int iter : mStrCozyId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_alramvol:
			for (int iter : mStrSendId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_steermode:
			for (int iter : mStrSteerId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_remoteunlock:
			for (int iter : mStrRemoteUnlockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_followtohome:
			for (int iter : mStrHomeId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.cq_rl_lightsensitivity:
			for (int iter : mStrSendId) {
				arrayList.add(getString(iter));
			}
			break;
		}

		return arrayList;
	}
}
