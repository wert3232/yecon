package com.can.ui.dz;

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
import com.can.parser.DDef.DrivingData;
import com.can.parser.raise.dz.ReMQB7Protocol;
import com.can.parser.Protocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanDz7DriverData
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-7-7下午4:05:33
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz7DriverData extends CanFrament implements OnClickListener{
	
	private View mObjView = null;
	private TextView[] mObjTextViews = null;
	private TextView mObjTitle = null;
	
	private int miPageId = 0;
	
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
			mObjView = inflater.inflate(R.layout.dz7_drivingdata, container, false);
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
		super.getData(DDef.DRIVING_DATA, miPageId);
	}

	private void init(){
		if (mObjView != null) {
			
			int iIndex = 0;
			mObjTextViews = new TextView[ResDef.mDz7DrivingData.length];
			
			for (int iter : ResDef.mDz7DrivingData) {
				mObjTextViews[iIndex] = (TextView) mObjView.findViewById(iter);
				iIndex++;
			}
			
			mObjTitle = (TextView) mObjView.findViewById(R.id.tx_dz7_driving_title);
			mObjView.findViewById(R.id.btn_dz7_driving_left).setOnClickListener(this);
			mObjView.findViewById(R.id.btn_dz7_driving_right).setOnClickListener(this);
		}
	}
	
	private void setDrivingData(DrivingData data){
		
		int iIndex = 0;
		String strTitle = "";
		StringBuffer strInfo = new StringBuffer();
		
		int    miDistance = 0;
		float  mfAvgfuel = 0;
		float  mfAvgSpeed = 0;
		int    mTravelTime= 0;
		
		if (data.mbyPageId == 0x00) {
			strTitle = getResources().getString(R.string.str_dz7_driving_since_start);
			
			miDistance = data.miDistanceSinceStart;
			mfAvgfuel = data.mfAvgConsSinceStart;
			mfAvgSpeed = data.mfAvgSpeedSinceStart;
			mTravelTime= data.miTravellTimeSinceStart;
			
		}else if (data.mbyPageId == 0x01) {
			strTitle = getResources().getString(R.string.str_dz7_driving_since_refuel);	
			
			miDistance = data.miDistanceSinceRefuel;
			mfAvgfuel = data.mfAvgConsSinceRefuel;
			mfAvgSpeed = data.mfAvgSpeedSinceRefuel;
			mTravelTime = data.miTravellTimeSinceRefuel;
		}else if (data.mbyPageId == 0x02) {
			strTitle = getResources().getString(R.string.str_dz7_driving_long_term);	
			
			miDistance = data.miDistanceLongTerm;
			mfAvgfuel = data.mfAvgConsLongTerm;
			mfAvgSpeed = data.mfAvgSpeedLongTerm;
			mTravelTime = data.miTravellTimeLongTerm;
		}
		
		int iDisUnit = ResDef.mDz7disunit[data.mbyDistanceUnit];
		int iSpeedUnit = ResDef.mDz7speedunit[data.mbyAvgSpeedUnit];
		int iConvsUnit = (data.mbyConvConsumersUnit == 0x00) ? R.string.str_dz7_convs_uint1 :
			R.string.str_dz7_convs_uint2;
		
		int iAvgfuel = 0;
		if (data.mbyConsumptionUnit == 0x00) {
			iAvgfuel = R.string.str_dz7_carset_consum2;
		}else if (data.mbyConsumptionUnit == 0x01) {
			iAvgfuel = R.string.str_dz7_carset_consum4;
		}else if (data.mbyConsumptionUnit == 0x02) {
			iAvgfuel = R.string.str_dz7_carset_consum1;
		}else if (data.mbyConsumptionUnit == 0x03) {
			iAvgfuel = R.string.str_dz7_carset_consum3;
		}
			
		for (@SuppressWarnings("unused") int iter : ResDef.mDz7DrivingData) {
			
			strInfo.delete(0, strInfo.length());
			
			switch (iIndex) {
			case 0:
				strInfo.append(mfAvgfuel);
				strInfo.append(getResources().getString(iAvgfuel));
				break;
			case 1:
				strInfo.append(mfAvgSpeed);
				strInfo.append(getResources().getString(iSpeedUnit));
				break;
			case 2:
				strInfo.append(miDistance);
				strInfo.append(getResources().getString(iDisUnit));
				break;
			case 3:
				strInfo.append(mTravelTime);
				strInfo.append(getResources().getString(R.string.str_dz7_time_min));
				break;
			case 4:
				strInfo.append(getResources().getString(R.string.str_dz7_carset_mfd_curfuel));
				strInfo.append(":");
				strInfo.append("  ");
				strInfo.append(data.mbyInstantFuel);
				strInfo.append(getResources().getString(iAvgfuel));
				break;
			case 5:
				strInfo.append(getResources().getString(R.string.str_dz7_carset_mfd_convs));
				strInfo.append(":");
				strInfo.append("  ");
				strInfo.append(data.miConvConsumers);
				strInfo.append(getResources().getString(iConvsUnit));
				break;
			case 6:
				strInfo.append(getResources().getString(R.string.str_tx_curfuel_cruisingrange));
				strInfo.append(":");
				strInfo.append("  ");
				strInfo.append(data.miRange);
				strInfo.append(getResources().getString(iDisUnit));
				break;
			default:
				break;
			}
			
			if (mObjTextViews[iIndex] != null) {
				mObjTextViews[iIndex].setText(strInfo.toString());
			}
			
			iIndex++;
		}
		
		if (mObjTitle != null) {
			mObjTitle.setText(strTitle);
		}
	}
	
	private Handler mObjHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				Inquiry(msg);
				break;
			case DDef.DRIVING_DATA:
				setDrivingData((DrivingData)msg.obj);
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
		boolean bhandle = false;
		
		switch (arg0.getId()) {
		case R.id.btn_dz7_driving_left:
			bhandle = true;
			miPageId = (miPageId <= 0) ? 2 : (miPageId-1);
			break;
		case R.id.btn_dz7_driving_right:
			bhandle = true;
			miPageId = (miPageId >= 2) ? 0 : (miPageId+1);
			break;
		default:
			break;
		}
		
		if (bhandle) {
			super.getData(DDef.DRIVING_DATA, miPageId);
		}
	}
	
	private void Inquiry(Message msg) {

		byte[] byData1 = { 0x50, 0x10 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData1,
				(Protocol) msg.obj);

		byte[] byData2 = { 0x50, 0x20 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData2,
				(Protocol) msg.obj);
		byte[] byData3 = { 0x50, 0x21 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData3,
				(Protocol) msg.obj);
		byte[] byData4 = { 0x50, 0x22 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData4,
				(Protocol) msg.obj);

		byte[] byData5 = { 0x50, 0x30 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData5,
				(Protocol) msg.obj);
		byte[] byData6 = { 0x50, 0x31 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData6,
				(Protocol) msg.obj);
		byte[] byData7 = { 0x50, 0x32 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData7,
				(Protocol) msg.obj);
		
		byte[] byData8 = { 0x50, 0x40 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData8,
				(Protocol) msg.obj);
		byte[] byData9 = { 0x50, 0x41 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData9,
				(Protocol) msg.obj);
		byte[] byData10 = { 0x50, 0x42 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData10,
				(Protocol) msg.obj);
		
		byte[] byData11 = { 0x50, 0x50 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData11,
				(Protocol) msg.obj);
		byte[] byData12 = { 0x50, 0x51 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData12,
				(Protocol) msg.obj);
		byte[] byData13 = { 0x50, 0x52 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData13,
				(Protocol) msg.obj);
		
		byte[] byData14 = { 0x50, 0x60 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData14,
				(Protocol) msg.obj);
		byte[] byData15 = { 0x50, 0x61 };
		super.sendMsg(ReMQB7Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData15,
				(Protocol) msg.obj);
	}
}
