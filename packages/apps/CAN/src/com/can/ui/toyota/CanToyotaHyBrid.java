package com.can.ui.toyota;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.OilVatteryInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.toyota.ReToyotaProtocol;
import com.can.ui.CanFrament;
import com.can.ui.draw.ResDef;

/**
 * ClassName:CanToyotaHyBrid
 * 
 * @function:混合动力
 * @author Kim
 * @Date: 2016-6-23 下午2:31:00
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanToyotaHyBrid extends CanFrament {

	private View mObjView = null;
	private RelativeLayout mObjHybridDevlayout = null;
	private LinearLayout mObjHybridInfolayout = null;

	private ImageView mObjImageHybridBattery = null;
	private ImageView mObjImageHybridGleft = null;
	private ImageView mObjImageHybridGRight = null;
	private ImageView mObjImageHybridGcenter = null;
	private ImageView mObjImageHybridRRight = null;
	private ImageView mObjImageHybridRcenter = null;

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

			mObjView = inflater.inflate(R.layout.toyota_hybrid, container,
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
		super.getData(DDef.SYSINFO_CMD_ID);
	}

	private void init() {
		if (mObjView != null) {
			mObjHybridDevlayout = (RelativeLayout) mObjView
					.findViewById(R.id.layout_hybrid_dev);
			mObjHybridInfolayout = (LinearLayout) mObjView
					.findViewById(R.id.layout_hybrid_info);

			mObjImageHybridBattery = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_battery);
			mObjImageHybridGleft = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_grend_left);
			mObjImageHybridGRight = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_grend_right);
			mObjImageHybridGcenter = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_grend_ver);
			mObjImageHybridRRight = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_red_right);
			mObjImageHybridRcenter = (ImageView) mObjView
					.findViewById(R.id.toyota_hybrid_red_v);
		}
	}

	private void setHybrid(OilVatteryInfo objVatteryInfo) {
		if (this.isVisible()) {
			if (objVatteryInfo.mHybridEleVehicle == 0) {
				if (mObjHybridDevlayout != null && mObjHybridInfolayout != null) {
					mObjHybridDevlayout.setVisibility(View.VISIBLE);
					mObjHybridInfolayout.setVisibility(View.INVISIBLE);
				}
			} else {
				if (mObjHybridDevlayout != null && mObjHybridInfolayout != null) {
					mObjHybridDevlayout.setVisibility(View.INVISIBLE);
					mObjHybridInfolayout.setVisibility(View.VISIBLE);
				}

				if (mObjImageHybridBattery != null) {
					mObjImageHybridBattery
							.setImageResource(ResDef.mToyotaHybridBat[objVatteryInfo.mBatteryVoltage]);
				}

				if (mObjImageHybridGleft != null) {
					mObjImageHybridGleft
							.setVisibility((objVatteryInfo.mVatteryDriveMotor == 1) ? View.VISIBLE
									: View.INVISIBLE);
				}

				if (mObjImageHybridGRight != null) {
					mObjImageHybridGRight
							.setVisibility((objVatteryInfo.mMotorDriveVattery == 1) ? View.VISIBLE
									: View.INVISIBLE);
				}

				if (mObjImageHybridGcenter != null) {
					mObjImageHybridGcenter
							.setVisibility((objVatteryInfo.mMotorDriveWheel == 1) ? View.VISIBLE
									: View.INVISIBLE);
				}

				if (mObjImageHybridRRight != null) {
					mObjImageHybridRRight
							.setVisibility((objVatteryInfo.mEngineDriveMotor == 1) ? View.VISIBLE
									: View.INVISIBLE);
				}

				if (mObjImageHybridRcenter != null) {
					mObjImageHybridRcenter
							.setVisibility((objVatteryInfo.mEngineDriveWheel == 1) ? View.VISIBLE
									: View.INVISIBLE);
				}
			}
		}
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				Inquiry(msg);
				break;
			case DDef.SYSINFO_CMD_ID:
				setHybrid((OilVatteryInfo) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void Inquiry(Message msg) {
		byte[] bydata = new byte[2];
		bydata[0] = 0x1F;
		bydata[1] = 0x00;
		super.sendMsg(ReToyotaProtocol.DATA_TYPE_INQUIRY_REQUEST, bydata,
				(Protocol) msg.obj);
	}
}
