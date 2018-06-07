package com.can.ui.dz;

import com.can.activity.R;
import com.can.parser.DDef;
import com.can.parser.DDef.CarInfo;
import com.can.parser.Protocol;
import com.can.parser.raise.dz.ReMQB6Protocol;
import com.can.ui.CanFrament;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ClassName:CanDzCarInfo
 * 
 * @function:大众原车信息
 * @author Kim
 * @Date: 2016-6-1 下午3:37:56
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class CanDz6CarInfo extends CanFrament {

	private View mObjView = null;

	private TextView mTextOilSize = null;
	private TextView mTextOutTemp = null;
	private TextView mTextBatteryLv = null;
	private TextView mTextSeatBelts = null;
	private TextView mTextRDoorOpen = null;
	private TextView mTextHandbrake = null;
	private TextView mTextCleanFluid = null;
	private TextView mTextEngineSpeed = null;
	private TextView mTextTravelspeed = null;
	private TextView mTextDrivenDis = null;
	private TextView mTextDoorOpenWarn = null;

	private ImageView mImageLFDoor = null;
	private ImageView mImageRFDoor = null;
	private ImageView mImageLBDoor = null;
	private ImageView mImageRBDoor = null;
	private ImageView mImageFCOpen = null;
	private ImageView mImageRDOpen = null;

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

			mObjView = inflater.inflate(R.layout.dz6_info, container, false);
			init();
		}
		return mObjView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private Handler mObjhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case DDef.FINISH_BIND:
				Inquiry(msg);
				break;
			case DDef.CARINFO_CMD_ID:
				setCarInfoAttr((CarInfo) msg.obj);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	private void init() {

		mTextOilSize = (TextView) mObjView.findViewById(R.id.tv_oil_left);
		mTextOutTemp = (TextView) mObjView.findViewById(R.id.tv_outdoor_temp);
		mTextBatteryLv = (TextView) mObjView
				.findViewById(R.id.tv_battery_voltage);
		mTextSeatBelts = (TextView) mObjView.findViewById(R.id.tv_seat_belts);
		mTextRDoorOpen = (TextView) mObjView
				.findViewById(R.id.tv_rear_door_open);
		mTextHandbrake = (TextView) mObjView.findViewById(R.id.tv_handbrake);
		mTextCleanFluid = (TextView) mObjView
				.findViewById(R.id.tv_cleaning_fluid);
		mTextEngineSpeed = (TextView) mObjView
				.findViewById(R.id.tv_engine_speed);
		mTextTravelspeed = (TextView) mObjView
				.findViewById(R.id.tv_traveling_speed);
		mTextDrivenDis = (TextView) mObjView
				.findViewById(R.id.tv_driven_distance);
		mTextDoorOpenWarn = (TextView) mObjView
				.findViewById(R.id.tv_door_open_warning);

		mImageLFDoor = (ImageView) mObjView
				.findViewById(R.id.iv_left_front_door);
		mImageRFDoor = (ImageView) mObjView
				.findViewById(R.id.iv_right_front_door);
		mImageLBDoor = (ImageView) mObjView
				.findViewById(R.id.iv_left_back_door);
		mImageRBDoor = (ImageView) mObjView
				.findViewById(R.id.iv_right_back_door);
		mImageFCOpen = (ImageView) mObjView
				.findViewById(R.id.iv_front_cover_open);
		mImageRDOpen = (ImageView) mObjView
				.findViewById(R.id.iv_rear_door_open);
	}

	private void setCarInfoAttr(CarInfo carInfo) {

		if (this.isVisible()) {
			StringBuffer strBuffer = new StringBuffer();
			// 燃油量
			strBuffer.append(getResources().getString(R.string.ci_oil_left));
			strBuffer.append("\n");

			if (carInfo.mLowOilWarn == 0x01) {
				strBuffer.append(getResources().getString(
						R.string.ci_status_low));

				mTextOilSize.setSelected(true);
			} else {
				strBuffer.append(carInfo.mSurplusmOil);
				strBuffer.append(" L");

				mTextOilSize.setSelected(false);
			}

			mTextOilSize.setText(strBuffer.toString());

			// 室外温度
			strBuffer.delete(0, strBuffer.length());
			strBuffer
					.append(getResources().getString(R.string.ci_outdoor_temp));
			strBuffer.append("\n");
			strBuffer.append(carInfo.mOutCarTemp);
			strBuffer.append(" ℃");
			mTextOutTemp.setText(strBuffer.toString());

			// 电池电压
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(
					R.string.ci_battery_voltage));
			strBuffer.append("\n");

			if (carInfo.mLowBatVolWarn == 0) {
				strBuffer.append(carInfo.mBatteryVoltage);
				strBuffer.append(" V");

				mTextBatteryLv.setSelected(false);
			} else {
				strBuffer.append(getResources().getString(
						R.string.ci_status_low));

				mTextBatteryLv.setSelected(true);
			}

			mTextBatteryLv.setText(strBuffer.toString());

			// 安全带
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(R.string.ci_seat_belts));
			strBuffer.append("\n");
			if (carInfo.mSafetyBelt == 0) {

				strBuffer.append(getResources().getString(
						R.string.ci_status_normal));
				mTextSeatBelts.setSelected(false);
			} else {

				strBuffer.append(getResources().getString(
						R.string.ci_status_no_line));
				mTextSeatBelts.setSelected(true);
			}
			mTextSeatBelts.setText(strBuffer.toString());

			// 尾箱门
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(R.string.ci_rear_door));
			strBuffer.append("\n");

			if (carInfo.mTailBoxDoor == 0) {
				strBuffer.append(getResources().getString(
						R.string.ci_status_normal));

				mTextRDoorOpen.setSelected(false);
				mTextRDoorOpen.setVisibility(View.GONE);
			} else {
				strBuffer.append(getResources().getString(
						R.string.ci_status_no_close));

				mTextRDoorOpen.setSelected(true);
				mTextRDoorOpen.setVisibility(View.VISIBLE);
			}
			mTextRDoorOpen.setText(strBuffer.toString());

			// 手刹状态
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(R.string.ci_handbrake));
			strBuffer.append("\n");
			if (carInfo.mHandbrake == 1) {
				strBuffer.append(getResources().getString(
						R.string.ci_status_normal));

				mTextHandbrake.setSelected(false);
			} else {
				strBuffer.append(getResources().getString(
						R.string.ci_status_no_release));

				mTextHandbrake.setSelected(true);
			}
			mTextHandbrake.setText(strBuffer.toString());

			// 清洗液状态
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(
					R.string.ci_cleaning_fluid));
			strBuffer.append("\n");
			if (carInfo.mWashLiquid == 0) {
				strBuffer.append(getResources().getString(
						R.string.ci_status_normal));

				mTextCleanFluid.setSelected(false);
			} else {
				strBuffer.append(getResources().getString(
						R.string.ci_status_low));

				mTextCleanFluid.setSelected(true);
			}
			mTextCleanFluid.setText(strBuffer.toString());

			// 发动机状态
			strBuffer.delete(0, strBuffer.length());
			strBuffer
					.append(getResources().getString(R.string.ci_engine_speed));
			strBuffer.append("\n");
			strBuffer.append(carInfo.mRotationlSpeed);
			strBuffer.append(" Rpm");
			mTextEngineSpeed.setText(strBuffer.toString());
			// 瞬时速度
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(
					R.string.ci_traveling_speed));
			strBuffer.append("\n");
			strBuffer.append(carInfo.mCurrentSpeed);
			strBuffer.append(" Km/h");
			mTextTravelspeed.setText(strBuffer.toString());
			// 行驶里程
			strBuffer.delete(0, strBuffer.length());
			strBuffer.append(getResources().getString(
					R.string.ci_driven_distance));
			strBuffer.append("\n");
			strBuffer.append(carInfo.mTravelMileage);
			strBuffer.append(" Km");
			mTextDrivenDis.setText(strBuffer.toString());

			// 车门状态
			if (carInfo.mHoodBoxDoor == 0) {
				mImageFCOpen.setSelected(false);

				mImageFCOpen.setVisibility(View.GONE);
			} else {

				mImageFCOpen.setSelected(true);
				mImageFCOpen.setVisibility(View.VISIBLE);
			}

			if (carInfo.mLeftFrontDoor == 0) {
				mImageLFDoor.setVisibility(View.GONE);
			} else {

				mImageLFDoor.setVisibility(View.VISIBLE);
				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_left_front_door_open));
			}

			if (carInfo.mRightFrontDoor == 0) {
				mImageRFDoor.setVisibility(View.GONE);
			} else {

				mImageRFDoor.setVisibility(View.VISIBLE);
				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_right_front_door_open));
			}

			if (carInfo.mLeftBackDoor == 0) {
				mImageLBDoor.setVisibility(View.GONE);
			} else {

				mImageLBDoor.setVisibility(View.VISIBLE);
				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_left_back_door_open));
			}

			if (carInfo.mRightBackDoor == 0) {
				mImageRBDoor.setVisibility(View.GONE);
			} else {

				mImageRBDoor.setVisibility(View.VISIBLE);
				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_right_back_door_open));
			}

			if (carInfo.mTailBoxDoor == 0) {
				mImageRDOpen.setVisibility(View.GONE);
			} else {
				mImageRDOpen.setVisibility(View.VISIBLE);
				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_rear_door_open));
			}

			if ((carInfo.mTailBoxDoor == 0) && (carInfo.mRightBackDoor == 0)
					&& (carInfo.mRightFrontDoor == 0)
					&& (carInfo.mLeftFrontDoor == 0)
					&& (carInfo.mHoodBoxDoor == 0)
					&& (carInfo.mTailBoxDoor == 0)) {

				mTextDoorOpenWarn.setText(getResources().getString(
						R.string.ci_status_normal));
			}
		}
	}

	private void Inquiry(Message msg) {

		byte[] byData1 = { 0x41, 0x01 };
		super.sendMsg(ReMQB6Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData1,
				(Protocol) msg.obj);

		byte[] byData2 = { 0x41, 0x02 };
		super.sendMsg(ReMQB6Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData2,
				(Protocol) msg.obj);

		byte[] byData3 = { 0x41, 0x03 };
		super.sendMsg(ReMQB6Protocol.DATA_TYPE_CAR_INFO_REQUEST, byData3,
				(Protocol) msg.obj);
	}
}
