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

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.MGCarSet;
import com.can.parser.Protocol;
import com.can.parser.raise.domestic.ReMGProtol;
import com.can.ui.CanFrament;
import com.can.ui.draw.PopDialog;
import com.can.ui.draw.PopDialog.E_POPDIALOG_TYPE;
import com.can.ui.draw.PopDialog.OnConfirmListener;

public class CanMGCarSet extends CanFrament implements OnClickListener{

	private MGCarSet mCarSet = null;
	private Protocol mProtocol = null;
	private View mView = null;
	
	private int[] mStrUnlockId = {
		R.string.str_gm_all_door,
		R.string.str_gm_driver_door
	};
	
	private int[] mStrTimeId = {
		R.string.str_mg_time_00,
		R.string.str_mg_time_01,
		R.string.str_mg_time_02,
		R.string.str_mg_time_03,
		R.string.str_mg_time_04,
		R.string.str_mg_time_05,
		R.string.str_mg_time_06,
		R.string.str_mg_time_07,
		R.string.str_mg_time_08,
		R.string.str_mg_time_09,
	};
	
	private int[] mCheckBoxId = {
		R.id.mg_checkbox_driving_latch,
		R.id.mg_checkbox_unlock,
		R.id.mg_checkbox_revers_lights,
		R.id.mg_checkbox_near_lights,
		R.id.mg_checkbox_fog_lights,
		R.id.mg_checkbox_f_revers_lights,
		R.id.mg_checkbox_f_near_lights,
		R.id.mg_checkbox_f_fog_lights		
	};
	
	private int[] mBtnId = {
		R.id.mg_rl_unlock_mode,
		R.id.mg_rl_near_unlock,
		R.id.mg_rl_gohome_time,
		R.id.mg_rl_f_gohome_time
	};
	
	private CheckBox[] mCheckBoxs = new CheckBox[mCheckBoxId.length];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.Init(mHandler, getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.mg_car_set, null);
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
				mCarSet = (MGCarSet) msg.obj;
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
			case R.id.mg_checkbox_driving_latch:
				setVal(0x01, 0x01, mCarSet.mDrivingLatch == 0x01 ? 0x00 : 0x01);
				break;
			case R.id.mg_checkbox_unlock:
				setVal(0x01, 0x02, mCarSet.mUnlock == 0x01 ? 0x00 : 0x01);
				break;
			case R.id.mg_checkbox_revers_lights:
				{
					int iValue = getBitVal((byte)(mCarSet.mReversLights == 0x01 ? 0x00 : 0x01), 
							mCarSet.mNearLights,mCarSet.mFogLights);
					setVal(0x02, 0x01, iValue);
				}
				break;
			case R.id.mg_checkbox_near_lights:
				{
					int iValue = getBitVal(mCarSet.mReversLights, 
							(byte)(mCarSet.mNearLights == 0x01 ? 0x00 : 0x01),mCarSet.mFogLights);
					setVal(0x02, 0x01, iValue);
				}
				break;
			case R.id.mg_checkbox_fog_lights:
				{
					int iValue = getBitVal(mCarSet.mReversLights, mCarSet.mNearLights,
							(byte)(mCarSet.mFogLights == 0x01 ? 0x00 : 0x01));
					setVal(0x02, 0x01, iValue);
				}
				break;
			case R.id.mg_checkbox_f_revers_lights:
				{
					int iValue = getBitVal((byte)(mCarSet.mCarReversLights == 0x01 ? 0x00 : 0x01), 
							mCarSet.mCarNearLights,mCarSet.mCarFogLights);
					setVal(0x02, 0x03, iValue);
				}
				break;
			case R.id.mg_checkbox_f_near_lights:
				{
					int iValue = getBitVal(mCarSet.mCarReversLights, 
							(byte)(mCarSet.mCarNearLights == 0x01 ? 0x00 : 0x01),mCarSet.mCarFogLights);
					setVal(0x02, 0x03, iValue);
				}	
				break;
			case R.id.mg_checkbox_f_fog_lights:
				{
					int iValue = getBitVal(mCarSet.mCarReversLights, mCarSet.mCarNearLights,
							(byte)(mCarSet.mCarFogLights == 0x01 ? 0x00 : 0x01));
					setVal(0x02, 0x03, iValue);
				}
				break;
			case R.id.mg_rl_unlock_mode:
				setMutilSel2Msg(0x01, 0x03,
						mCarSet.mUnlockMode,
						getString(R.string.str_mg_unlock_mode),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.mg_rl_unlock_mode), 0x00, 0, 0, "");
				break;
			case R.id.mg_rl_near_unlock:
				setMutilSel2Msg(0x01, 0x04,
						mCarSet.mNearUnlock,
						getString(R.string.str_mg_near_unlock),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.mg_rl_near_unlock), 0x00, 0, 0, "");
				break;
			case R.id.mg_rl_gohome_time:
				setMutilSel2Msg(0x02, 0x02,
						mCarSet.mTime,
						getString(R.string.str_mg_gohome_time),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.mg_rl_gohome_time), 0x00, 0, 0, "");
				break;
			case R.id.mg_rl_f_gohome_time:
				setMutilSel2Msg(0x02, 0x04,
						mCarSet.mCarTime,
						getString(R.string.str_mg_f_gohome_time),
						E_POPDIALOG_TYPE.ePopDialog_carset_list,
						getArrayList(R.id.mg_rl_f_gohome_time), 0x00, 0, 0, "");
				break;
			default:
				break;
			}
		}
	}
	
	private int getBitVal(byte byData0, byte byData1, byte byData2) {
		return  (0x00 | (byData0 << 7) | (byData1 << 6) | (byData2 << 5));
	}
	
	private void setVal(int byHeadID, int byCmd, int iVal) {
		byte[] bydata = new byte[3];
		bydata[0] = (byte) byHeadID;
		bydata[1] = (byte) byCmd;
		bydata[2] = (byte) iVal;
		super.sendMsg(ReMGProtol.DATA_TYPE_CAR_SET_REQUEST, bydata, mProtocol);
	}
	
	private void setCarSet() {
		if (mView != null && mCarSet != null) {
			int iIndex = 0;
			for (CheckBox checkBox : mCheckBoxs) {
				boolean bChecked = false;
				if (checkBox != null) {
					switch (iIndex) {
					case 0:
						bChecked = mCarSet.mDrivingLatch == 0x01 ? true : false;
						break;
					case 1:
						bChecked = mCarSet.mUnlock == 0x01 ? true : false;
						break;
					case 2:
						bChecked = mCarSet.mReversLights == 0x01 ? true : false;
						break;
					case 3:
						bChecked = mCarSet.mNearLights == 0x01 ? true : false;
						break;
					case 4:
						bChecked = mCarSet.mFogLights == 0x01 ? true : false;
						break;
					case 5:
						bChecked = mCarSet.mCarReversLights == 0x01 ? true : false;
						break;
					case 6:
						bChecked = mCarSet.mCarNearLights == 0x01 ? true : false;
						break;
					case 7:
						bChecked = mCarSet.mCarFogLights == 0x01 ? true : false;
						break;
					default:
						break;
					}
					checkBox.setChecked(bChecked);
				}
				iIndex++;
			}
		}
	}
	
	private void setMutilSel2Msg(final int byHead, final int byCmd, final byte bystate,
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
				setVal(byHead, byCmd, iVal);
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
		case R.id.mg_rl_near_unlock:
		case R.id.mg_rl_unlock_mode:
			for (int iter : mStrUnlockId) {
				arrayList.add(getString(iter));
			}
			break;
		case R.id.mg_rl_gohome_time:
		case R.id.mg_rl_f_gohome_time:
			for (int iter : mStrTimeId) {
				arrayList.add(getString(iter));
			}
			break;
		}
		return arrayList;
	}
}
