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
import com.can.parser.Protocol;
import com.can.parser.raise.toyota.ReToyotaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.FuelProgressBar;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanToyotaHisFuel
 * 
 * @function:历史油耗
 * @author Kim
 * @Date: 2016-6-23 下午2:30:45
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaHisFuel extends CanFrament implements OnClickListener {

	private View mObjView = null;
	private Protocol mObjProtocol = null;

	private TextView mObjTextHisBestFuel = null;
	private TextView mObjTextHisFuelUnit = null;
	private TextView[] mObjTextScale = null;

	private FuelProgressBar[] mObjPbarHisFuel = null;

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
			mObjView = inflater.inflate(R.layout.toyota_hisfuel, container,
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
		super.getData(DDef.TRIP_INFO_HISOIL);
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

			mObjTextHisBestFuel = (TextView) mObjView
					.findViewById(R.id.tx_hisfuel_best_val);
			mObjTextHisFuelUnit = (TextView) mObjView
					.findViewById(R.id.tx_hisfuel_min_uitl);

			int iIndex = 0;
			mObjTextScale = new TextView[ResDef.mToyotascale.length];
			for (int iter : ResDef.mToyotascale) {
				mObjTextScale[iIndex] = (TextView) mObjView.findViewById(iter);
				iIndex++;
			}

			iIndex = 0;
			mObjPbarHisFuel = new FuelProgressBar[ResDef.mToyotaHisFuelId.length];
			for (int iter : ResDef.mToyotaHisFuelId) {
				mObjPbarHisFuel[iIndex] = (FuelProgressBar) mObjView
						.findViewById(iter);
				iIndex++;
			}

			mObjView.findViewById(R.id.btn_hisfuel_clear).setOnClickListener(
					this);
			mObjView.findViewById(R.id.btn_hisfuel_2cur).setOnClickListener(
					this);
			mObjView.findViewById(R.id.btn_hisfuel_update).setOnClickListener(
					this);
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

	private void setHisOil(CarInfo objCarInfo) {
		if (this.isVisible()) {
			int iScaleMax = 60;
			int iUnitId = R.string.str_tx_fuelunit_mpg;
			setOilScale(objCarInfo.mHistoryOilUnit);

			if (mObjTextHisFuelUnit != null) {

				if (objCarInfo.mHistoryOilUnit == 1) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_kml;
				} else if (objCarInfo.mHistoryOilUnit == 2) {
					iScaleMax = 30;
					iUnitId = R.string.str_tx_fuelunit_l100km;
				}

				mObjTextHisFuelUnit.setText(getResources().getString(iUnitId));
			}

			float fbestfuel = (objCarInfo.mTripFuel1 + objCarInfo.mTripFuel2
					+ objCarInfo.mTripFuel3 + objCarInfo.mTripFuel4 + objCarInfo.mTripFuel5) / 5;

			if (mObjTextHisBestFuel != null) {
				StringBuffer strbest = new StringBuffer();
				strbest.append(fbestfuel);
				strbest.append(getResources().getString(iUnitId));

				mObjTextHisBestFuel.setText(strbest.toString());
			}

			int iIndex = 0;
			for (@SuppressWarnings("unused")
			int iter : ResDef.mToyotaHisFuelId) {
				int iPorgress = 0;

				switch (iIndex) {
				case 0:
					iPorgress = (int) objCarInfo.mTripFuel1;
					break;
				case 1:
					iPorgress = (int) objCarInfo.mTripFuel2;
					break;
				case 2:
					iPorgress = (int) objCarInfo.mTripFuel3;
					break;
				case 3:
					iPorgress = (int) objCarInfo.mTripFuel4;
					break;
				case 4:
					iPorgress = (int) objCarInfo.mTripFuel5;
					break;
				case 5:
					iPorgress = (int) objCarInfo.mCurrentHistoryOil;
					break;
				}

				if (mObjPbarHisFuel[iIndex] != null) {
					mObjPbarHisFuel[iIndex].setMax(iScaleMax);
					mObjPbarHisFuel[iIndex].setProgress(iPorgress);
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
			case DDef.TRIP_INFO_HISOIL:
				setHisOil((CarInfo) msg.obj);
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
		byte[] bydata = null;

		switch (v.getId()) {
		case R.id.btn_hisfuel_clear:
			bydata = new byte[2];
			bydata[0] = 0x09;
			bydata[1] = 0x00;
			super.sendMsg(ReToyotaProtocol.DATA_TYPE_SETTING_REQUEST, bydata,
					mObjProtocol);
			break;
		case R.id.btn_hisfuel_update:
			bydata = new byte[2];
			bydata[0] = 0x08;
			bydata[1] = 0x00;
			super.sendMsg(ReToyotaProtocol.DATA_TYPE_SETTING_REQUEST, bydata,
					mObjProtocol);
			break;
		case R.id.btn_hisfuel_2cur:
			super.showPage(new CanToyotaCurFuel());
			break;
		default:
			break;
		}
	}
}
