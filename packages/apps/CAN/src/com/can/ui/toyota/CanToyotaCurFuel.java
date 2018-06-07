package com.can.ui.toyota;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CurOilInfo;
import com.can.parser.DDef.IntantOilInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.toyota.ReToyotaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.FuelProgressBar;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanToyotaCurFuel
 * 
 * @function:实时油耗
 * @author Kim
 * @Date: 2016-6-23 下午2:30:17
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaCurFuel extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private TextView mObjTextAverSpeed = null;
	private TextView mObjTextElapsedTime = null;
	private TextView mObjTextCruisingRange = null;

	private TextView mObjTextCurFuelUnit = null;
	private FuelProgressBar mObjPbarCurFuel = null;

	private TextView[] mObjTextScale = null;
	private FuelProgressBar[] mObjPbarMinFuel = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.Init(mObjhHandler, this.getClass().getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mObjView == null) {

			mObjView = inflater.inflate(R.layout.toyota_curfuel, container,
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
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		super.getData(DDef.TRIP_INFO_MINOIL);
		super.getData(DDef.INTANTOIL_CMD_ID);
		super.getData(DDef.CUROIL_CMD_ID);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	private void init() {
		if (mObjView != null) {
			mObjTextAverSpeed = (TextView) mObjView
					.findViewById(R.id.tx_curfuel_speed_val);
			mObjTextElapsedTime = (TextView) mObjView
					.findViewById(R.id.tx_curfuel_elapsedtime_val);
			mObjTextCruisingRange = (TextView) mObjView
					.findViewById(R.id.tx_curfuel_crutange_val);

			int iIndex = 0;
			mObjTextScale = new TextView[ResDef.mToyotascale.length];
			for (int iter : ResDef.mToyotascale) {
				mObjTextScale[iIndex] = (TextView) mObjView.findViewById(iter);
				iIndex++;
			}

			mObjTextCurFuelUnit = (TextView) mObjView
					.findViewById(R.id.tx_curfuel_min_uitl);
			mObjPbarCurFuel = (FuelProgressBar) mObjView
					.findViewById(R.id.pb_curfuel_progress_bar_cur);

			iIndex = 0;
			mObjPbarMinFuel = new FuelProgressBar[ResDef.mToyotaMinFuelId.length];
			for (int iter : ResDef.mToyotaMinFuelId) {
				mObjPbarMinFuel[iIndex] = (FuelProgressBar) mObjView
						.findViewById(iter);
				iIndex++;
			}

			mObjView.findViewById(R.id.btn_curfuel_2his).setOnClickListener(
					this);
			mObjView.findViewById(R.id.btn_curfuel_clear).setOnClickListener(
					this);
		}
	}

	private void setTripInfo(CarInfo objcarInfo) {

		if (this.isVisible()) {
			StringBuffer strInfo = new StringBuffer();

			int iUintId1 = R.string.str_tx_unit_no;
			int iUintId2 = R.string.str_tx_unit_no;

			if (objcarInfo.mUintMin == 1) {
				iUintId1 = R.string.str_tx_unit_mile;
				iUintId2 = R.string.str_tx_unit_mileh;
			} else if (objcarInfo.mUintMin == 2) {
				iUintId1 = R.string.str_tx_unit_KM;
				iUintId2 = R.string.str_tx_unit_KMh;
			}

			String strUnit1 = getResources().getString(iUintId1);
			String strUnit2 = getResources().getString(iUintId2);

			if (iUintId1 == R.string.str_tx_unit_no) {
				strInfo.append(getResources().getString(iUintId1));

				if (mObjTextAverSpeed != null && mObjTextCruisingRange != null) {
					mObjTextAverSpeed.setText(strInfo.toString());
					mObjTextCruisingRange.setText(strInfo.toString());
				}
			} else {
				strInfo.append(objcarInfo.mAverageSpeedMin);
				strInfo.append(" ");
				strInfo.append(strUnit2);

				if (mObjTextAverSpeed != null) {
					mObjTextAverSpeed.setText(strInfo.toString());
				}

				strInfo.delete(0, strInfo.length());

				strInfo.append(objcarInfo.mCruisingRangeMin);
				strInfo.append(" ");
				strInfo.append(strUnit1);

				if (mObjTextCruisingRange != null) {
					mObjTextCruisingRange.setText(strInfo.toString());
				}
			}

			strInfo.delete(0, strInfo.length());

			strInfo.append(objcarInfo.mElapsedTimeMin / 60);
			strInfo.append(":");
			strInfo.append(objcarInfo.mElapsedTimeMin % 60);

			if (mObjTextElapsedTime != null) {
				mObjTextElapsedTime.setText(strInfo.toString());
			}
		}
	}

	private void setOilScale(byte byStyle) {
		int iIndex = 0;
		if (byStyle == 0) {
			for (int iter : ResDef.mToyotaOilscaleStyle2) {

				if (mObjTextScale[iIndex] != null) {
					mObjTextScale[iIndex].setText(getResources()
							.getString(iter));
				}
				iIndex++;
			}

		} else {
			for (int iter : ResDef.mToyotaOilscaleStyle1) {

				if (mObjTextScale[iIndex] != null) {
					mObjTextScale[iIndex].setText(getResources()
							.getString(iter));
				}
				iIndex++;
			}
		}
	}

	private void setIntantoil(IntantOilInfo objOilInfo) {

		if (this.isVisible()) {
			int iScaleMax = 60;
			setOilScale(objOilInfo.mIntantConsumeOilUint);

			if (mObjTextCurFuelUnit != null) {
				int iUnitId = R.string.str_tx_fuelunit_mpg;

				if (objOilInfo.mIntantConsumeOilUint == 1) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_kml;
				} else if (objOilInfo.mIntantConsumeOilUint == 2) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_l100km;
				}

				mObjTextCurFuelUnit.setText(getResources().getString(iUnitId));
			}

			if (mObjPbarCurFuel != null) {
				mObjPbarCurFuel.setMax(iScaleMax);
				mObjPbarCurFuel.setProgress((int) objOilInfo.mIntantConsumeOil);
			}
		}
	}

	private void set15MinOil(CurOilInfo objCurOilInfo) {
		if (this.isVisible()) {
			int iScaleMax = 60;
			setOilScale(objCurOilInfo.m15minOilFuelUint);

			if (mObjTextCurFuelUnit != null) {
				int iUnitId = R.string.str_tx_fuelunit_mpg;

				if (objCurOilInfo.m15minOilFuelUint == 1) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_kml;
				} else if (objCurOilInfo.m15minOilFuelUint == 2) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_l100km;
				}

				mObjTextCurFuelUnit.setText(getResources().getString(iUnitId));
			}

			int iIndex = 0;
			for (@SuppressWarnings("unused")
			int iter : ResDef.mToyotaMinFuelId) {
				int iProgress = 0;

				switch (iIndex) {
				case 0:
					iProgress = (int) objCurOilInfo.m15minOilFuel_15;
					break;
				case 1:
					iProgress = (int) objCurOilInfo.m15minOilFuel_14;
					break;
				case 2:
					iProgress = (int) objCurOilInfo.m15minOilFuel_13;
					break;
				case 3:
					iProgress = (int) objCurOilInfo.m15minOilFuel_12;
					break;
				case 4:
					iProgress = (int) objCurOilInfo.m15minOilFuel_11;
					break;
				case 5:
					iProgress = (int) objCurOilInfo.m15minOilFuel_10;
					break;
				case 6:
					iProgress = (int) objCurOilInfo.m15minOilFuel_9;
					break;
				case 7:
					iProgress = (int) objCurOilInfo.m15minOilFuel_8;
					break;
				case 8:
					iProgress = (int) objCurOilInfo.m15minOilFuel_7;
					break;
				case 9:
					iProgress = (int) objCurOilInfo.m15minOilFuel_6;
					break;
				case 10:
					iProgress = (int) objCurOilInfo.m15minOilFuel_5;
					break;
				case 11:
					iProgress = (int) objCurOilInfo.m15minOilFuel_4;
					break;
				case 12:
					iProgress = (int) objCurOilInfo.m15minOilFuel_3;
					break;
				case 13:
					iProgress = (int) objCurOilInfo.m15minOilFuel_2;
					break;
				case 14:
					iProgress = (int) objCurOilInfo.m15minOilFuel_1;
					break;
				default:
					break;
				}

				if (mObjPbarMinFuel[iIndex] != null) {
					mObjPbarMinFuel[iIndex].setMax(iScaleMax);
					mObjPbarMinFuel[iIndex].setProgress(iProgress);
				}

				iIndex++;
			}
		}
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				mObjProtocol = (Protocol) msg.obj;
				break;
			case DDef.TRIP_INFO_MINOIL:
				setTripInfo((CarInfo) msg.obj);
				break;
			case DDef.INTANTOIL_CMD_ID:
				setIntantoil((IntantOilInfo) msg.obj);
				break;
			case DDef.CUROIL_CMD_ID:
				set15MinOil((CurOilInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_curfuel_2his:
			super.showPage(new CanToyotaHisFuel());
			break;
		case R.id.btn_curfuel_clear:
			byte[] bydata = new byte[2];
			bydata[0] = 0x0A;
			bydata[1] = 0x00;
			super.sendMsg(ReToyotaProtocol.DATA_TYPE_SETTING_REQUEST, bydata,
					mObjProtocol);
			break;
		default:
			break;
		}
	}
}
