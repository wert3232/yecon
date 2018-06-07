package com.can.ui.dz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CarService;
import com.can.parser.Protocol;
import com.can.parser.raise.dz.ReMQB7Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanDz7Serice
 * 
 * @function:TODO
 * @author Kim
 * @Date: 2016-7-7下午4:05:55
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7Serice extends CanFrament {

	private View mObjView = null;
	private TextView[] mObjTextViews = null;

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
			mObjView = inflater
					.inflate(R.layout.dz7_services, container, false);
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
			mObjTextViews = new TextView[ResDef.mDz7ServicesText.length];
			for (int iter : ResDef.mDz7ServicesText) {
				mObjTextViews[iIndex] = (TextView) mObjView.findViewById(iter);
				iIndex++;
			}
		}
	}

	private void setAttr(CarService data) {

		StringBuffer strInfo = new StringBuffer();

		int iIndex = 0;
		int iDisUint = ResDef.mDz7disunit[data.mbyVolksWagenDisUnit];

		for (@SuppressWarnings("unused")
		int iter : ResDef.mDz7ServicesText) {

			strInfo.delete(0, strInfo.length());

			switch (iIndex) {
			case 0:
				strInfo.append(data.mStrVehicleNo);
				break;
			case 1:
				if (data.mbyVolksWagenDaysType == 0x00) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_valid));
				} else if (data.mbyVolksWagenDaysType == 0x01) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_days_leave));
					strInfo.append(data.miVolksWagenDays);
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_daysuint));
				} else if (data.mbyVolksWagenDaysType == 0x02) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_days_timout));
					strInfo.append(data.miVolksWagenDays);
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_daysuint));
				}
				break;
			case 2:
				if (data.mbyVolksWagenDisType == 0x00) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_valid));
				} else if (data.mbyVolksWagenDisType == 0x01) {
					strInfo.append(data.miVolksWagenDistance);
					strInfo.append(getResources().getString(iDisUint));
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_volksdis_leave));
				} else if (data.mbyVolksWagenDisType == 0x02) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_volksdis_timout));
					strInfo.append(data.miVolksWagenDistance);
					strInfo.append(getResources().getString(iDisUint));
				}
				break;
			case 3:
				if (data.mbyOilChangeSerivesDayType == 0x00) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_valid));
				} else if (data.mbyOilChangeSerivesDayType == 0x01) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_oilchangedays_leave));
					strInfo.append(data.miOilChangeSerivesDays);
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_daysuint));
				} else if (data.mbyOilChangeSerivesDayType == 0x02) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_oilchangedays_timout));
					strInfo.append(data.miOilChangeSerivesDays);
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_daysuint));
				}
				break;
			case 4:
				if (data.mbyOilChangeServiceDisType == 0x00) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_valid));
				} else if (data.mbyOilChangeServiceDisType == 0x01) {
					strInfo.append(data.miOilChangeServiceDistance);
					strInfo.append(getResources().getString(iDisUint));
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_oilchangedis_leave));
				} else if (data.mbyOilChangeServiceDisType == 0x02) {
					strInfo.append(getResources().getString(
							R.string.str_dz7_serives_oilchangedis_timout));
					strInfo.append(data.miOilChangeServiceDistance);
					strInfo.append(getResources().getString(iDisUint));
				}
				break;
			}

			if (mObjTextViews[iIndex] != null) {
				mObjTextViews[iIndex].setText(strInfo.toString());
			}

			iIndex++;
		}
	}

	private Handler mObjHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				Inquiry(msg);
				break;
			case DDef.CAR_SERVICE:
				setAttr((CarService) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void Inquiry(Message msg) {
		byte[] bydata1 = new byte[2];
		bydata1[0] = ReMQB7Protocol.DATA_TYPE_SERVICES;
		bydata1[1] = 0x00;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata1,
				(Protocol) msg.obj);

		byte[] bydata2 = new byte[2];
		bydata2[0] = ReMQB7Protocol.DATA_TYPE_SERVICES;
		bydata2[1] = 0x10;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata2,
				(Protocol) msg.obj);

		byte[] bydata3 = new byte[2];
		bydata3[0] = ReMQB7Protocol.DATA_TYPE_SERVICES;
		bydata3[1] = 0x11;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata3,
				(Protocol) msg.obj);

		byte[] bydata4 = new byte[2];
		bydata4[0] = ReMQB7Protocol.DATA_TYPE_SERVICES;
		bydata4[1] = 0x20;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata4,
				(Protocol) msg.obj);

		byte[] bydata5 = new byte[2];
		bydata5[0] = ReMQB7Protocol.DATA_TYPE_SERVICES;
		bydata5[1] = 0x21;
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, bydata5,
				(Protocol) msg.obj);
	}
}
