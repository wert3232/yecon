package com.can.ui.jeep;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.raise.jeep.ReJeepProtocol;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.CanPopWind;
import com.can.ui.draw.FuelSeekBar;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanJeepAirSet
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-14下午5:24:38
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanJeepAirSet extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol  = null;
	private Button[] mObjBtnAirset = null;
	private FuelSeekBar mObjBarWindlv  = null;
	private TextView mObjTextLeftTemp  = null;
	private TextView mObjTextRightTemp = null;

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
			mObjView = inflater.inflate(R.layout.jeep_airset, container, false);
			init();
		}

		return mObjView;
	}

	private void init() {
		if (mObjView != null) {

			int iIndex = 0;
			mObjBtnAirset = new Button[ResDef.mJeepAirset.length];
			for (int iter : ResDef.mJeepAirset) {
				mObjBtnAirset[iIndex] = (Button) mObjView.findViewById(iter);
				mObjBtnAirset[iIndex].setOnClickListener(this);
				iIndex++;
			}

			mObjBarWindlv = (FuelSeekBar) mObjView
					.findViewById(R.id.jeep_seekbar_wind);
			mObjTextLeftTemp = (TextView) mObjView
					.findViewById(R.id.jeep_tv_left_temp);
			mObjTextRightTemp = (TextView) mObjView
					.findViewById(R.id.jeep_tv_right_temp);
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
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		CanPopWind.sIsAirActivityShow = false;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CanPopWind.sIsAirActivityShow = true;
	}

	private void setAirAttr(AirInfo airinfo) {
		if (airinfo != null) {

			int iIndex = 0;
			for (Button iter : mObjBtnAirset) {
				byte byType = 0;
				boolean selected = false;
				String strInfo = "";

				switch (iIndex) {
				case 15:
					byType = 1;
					strInfo = (airinfo.mAiron == 0) ? getString(R.string.str_air_on)
							: getString(R.string.str_air_off);
					break;
				case 16:
					selected = (airinfo.mAcState == 0) ? false : true;
					break;
				case 6:
					selected = (airinfo.mAutoLight2 == 0) ? false : true;
					break;
				case 7:
					selected = (airinfo.mAcMax == 0) ? false : true;
					break;
				case 8:
					selected = (airinfo.mFWDefogger == 0) ? false : true;
					break;
				case 9:
					byType = 2;
					selected = (airinfo.mCircleState >= 1) ? false : true;
					break;
				case 10:
					byType = 2;
					selected = (airinfo.mCircleState == 0) ? false : true;
					break;
				case 11:
					byType = 1;
					strInfo = getString(R.string.str_air_lhot)
							+ airinfo.mLeftHotSeatTemp;
					break;
				case 12:
					selected = (airinfo.mRearLight == 0) ? false : true;
					break;
				case 13:
					selected = (airinfo.mSync == 0) ? false : true;
					break;
				case 14:
					byType = 1;
					strInfo = getString(R.string.str_air_rhot)
							+ airinfo.mRightHotSeatTemp;
					break;
				case 17:
					if (airinfo.mUpwardWind == 0 && airinfo.mParallelWind == 1
							&& airinfo.mDowmWind == 0) {
						selected = true;
					} else {
						selected = false;
					}
					break;
				case 18:
					if (airinfo.mUpwardWind == 0 && airinfo.mParallelWind == 1
							&& airinfo.mDowmWind == 1) {
						selected = true;
					} else {
						selected = false;
					}
					break;
				case 19:
					if (airinfo.mUpwardWind == 1 && airinfo.mParallelWind == 0
							&& airinfo.mDowmWind == 1) {
						selected = true;
					} else {
						selected = false;
					}
					break;
				case 20:
					if (airinfo.mUpwardWind == 0 && airinfo.mParallelWind == 0
							&& airinfo.mDowmWind == 1) {
						selected = true;
					} else {
						selected = false;
					}
					break;
				}

				if (iter != null) {
					if (byType == 1) {
						iter.setText(strInfo);
					} else if (byType == 2) {
						iter.setVisibility(selected ? View.VISIBLE : View.GONE);
					}else {
						iter.setSelected(selected);
					}
				}

				iIndex++;
			}
			
			if (mObjTextLeftTemp != null && mObjTextRightTemp != null) {
				int iTempUint = airinfo.mTempUnit;
				String strTempUnit = getString((iTempUint == 0) ? R.string.ac_temp_unit_c : R.string.ac_temp_unit_f);
					
				if (airinfo.mLeftTemp <= airinfo.mMinTemp) {
					mObjTextLeftTemp.setText(R.string.ac_temp_lo);	
				} else if (airinfo.mLeftTemp >= airinfo.mMaxTemp) {
					mObjTextLeftTemp.setText(R.string.ac_temp_hi);
				} else {
					mObjTextLeftTemp.setText(airinfo.mLeftTemp + strTempUnit);
				}

				if (airinfo.mRightTemp <= airinfo.mMinTemp) {
					mObjTextRightTemp.setText(R.string.ac_temp_lo);
				} else if (airinfo.mRightTemp >= airinfo.mMaxTemp){
					mObjTextRightTemp.setText(R.string.ac_temp_hi);
				} else {
					mObjTextRightTemp.setText(airinfo.mRightTemp + strTempUnit);
				}
			}
			
			if (mObjBarWindlv != null) {
				mObjBarWindlv.setMax(airinfo.mMaxWindlv);
				mObjBarWindlv.setProgress(airinfo.mWindRate);
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
			case DDef.AIR_CMD_ID:
				setAirAttr((AirInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};
	
	private void onhandle(int iId){
		int icmd = -1;
		switch (iId) {
		case R.id.jeep_air_left_temp_add:
			icmd = 0x1e;
			break;
		case R.id.jeep_air_left_temp_del:
			icmd = 0x1f;
			break;
		case R.id.jeep_air_right_temp_add:
			icmd = 0x20;
			break;
		case R.id.jeep_air_right_temp_del:
			icmd = 0x21;
			break;
		case R.id.jeep_btn_air_wind_del:
			icmd = 0x1c;
			break;
		case R.id.jeep_btn_air_wind_add:
			icmd = 0x1d;
			break;
		case R.id.jeep_btn_air_auto:
			icmd = 0x14;
			break;
		case R.id.jeep_btn_air_acmax:
			icmd = 0x12;
			break;
		case R.id.jeep_btn_air_front_deg:
			icmd = 0x15;
			break;
		case R.id.jeep_btn_air_cycle_in:
		case R.id.jeep_btn_air_cycle_out:
			icmd = 0x13;
			break;
		case R.id.jeep_btn_air_lhot:
			icmd = 0x30;
			break;
		case R.id.jeep_btn_air_rear:
			icmd = 0x16;
			break;
		case R.id.jeep_btn_air_sync:
			icmd = 0x17;
			break;
		case R.id.jeep_btn_air_rhot:
			icmd = 0x32;
			break;
		case R.id.jeep_btn_air_on:
			icmd = 0x09;
			break;
		case R.id.jeep_btn_air_ac:
			icmd = 0x11;
			break;
		case R.id.jeep_btn_air_par_wind:
			icmd = 0x18;
			break;
		case R.id.jeep_btn_air_down_par_wind:
			icmd = 0x19;
			break;
		case R.id.jeep_btn_air_down_up_wind:
			icmd = 0x1b;
			break;
		case R.id.jeep_btn_air_down_wind:
			icmd = 0x1a;
			break;
		default:
			break;
		}
		
		if (icmd != -1 && mObjProtocol != null) {
			byte[] bydata = new byte[2];
			bydata[0] = (byte) icmd;
			bydata[1] = 0x01;
			
			super.sendMsg(ReJeepProtocol.DATA_TYPE_AIR_SET, bydata, mObjProtocol);

			try {
				Thread.sleep(100);
				bydata[1] = 0x00;
				super.sendMsg(ReJeepProtocol.DATA_TYPE_AIR_SET, bydata, mObjProtocol);			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		onhandle(arg0.getId());
	}
	
	private void Inquiry(){
		if (mObjProtocol != null) {
			byte[] bydata = new byte[1];
			bydata[0] = 0x05;
			super.sendMsg(ReJeepProtocol.DATA_TYPE_REQUEST_INFO, bydata,
					mObjProtocol);
		}
	}
}
