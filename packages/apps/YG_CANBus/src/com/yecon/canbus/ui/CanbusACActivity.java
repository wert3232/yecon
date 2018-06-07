package com.yecon.canbus.ui;

import static com.yecon.canbus.info.CANBusConstants.ACTIVITY_AUTO_FINISH_DELAY_TIME;
import static com.yecon.canbus.info.CANBusConstants.CANBUS_DATA_OFF;
import static com.yecon.canbus.info.CANBusConstants.MSG_EXIT_ACTIVITY;
import static com.yecon.canbus.info.CANBusConstants.MSG_UPDATE_UI;
import static com.yecon.canbus.info.CANBusConstants.TEMP_UNIT_C;

import com.yecon.canbus.R;
import com.yecon.canbus.view.CANBusWindLevelView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

abstract public class CanbusACActivity extends CANBusUI {
	
	protected TextView mTVDual;
	protected TextView mTVAutoWind;
	protected TextView mTVAC;

	protected ImageView mIVInnerLoop;
	protected ImageView mIVFrontGlass;
	protected ImageView mIVRearGlass;

	protected ImageView mIVBlowHeadLeft;
	protected ImageView mIVBlowHandsLeft;
	protected ImageView mIVBlowFeetLeft;
	protected ImageView mIVHeatSeatLeft;

	protected ImageView mIVBlowHeadRight;
	protected ImageView mIVBlowHandsRight;
	protected ImageView mIVBlowFeetRight;
	protected ImageView mIVHeatSeatRight;

	protected TextView mTVTempLeft;
	protected TextView mTVTempRight;

	protected CANBusWindLevelView mCANBusWindLevelView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mActivityConstruct.mLayoutID);
		initData();
		initUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();

		mHandler.removeMessages(MSG_EXIT_ACTIVITY);
		mHandler.sendEmptyMessageDelayed(MSG_EXIT_ACTIVITY,
				ACTIVITY_AUTO_FINISH_DELAY_TIME);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(MSG_EXIT_ACTIVITY);
	}

	@SuppressLint("HandlerLeak")
	private void initData() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case MSG_UPDATE_UI:
					updateUI();

					mHandler.removeMessages(MSG_EXIT_ACTIVITY);
					mHandler.sendEmptyMessageDelayed(MSG_EXIT_ACTIVITY,
							ACTIVITY_AUTO_FINISH_DELAY_TIME);
					break;

				case MSG_EXIT_ACTIVITY:
					CanbusACActivity.this.finish();
					break;
				}
			}
		};
	}

	private void initUI() {
		mTVDual = (TextView) findViewById(R.id.tv_dual);
		mTVAutoWind = (TextView) findViewById(R.id.tv_auto_wind);
		mTVAC = (TextView) findViewById(R.id.tv_ac);

		mIVInnerLoop = (ImageView) findViewById(R.id.iv_inner_loop);
		mIVFrontGlass = (ImageView) findViewById(R.id.iv_front_glass);
		mIVRearGlass = (ImageView) findViewById(R.id.iv_rear_glass);

		mIVBlowHeadLeft = (ImageView) findViewById(R.id.iv_blow_head_l);
		mIVBlowHandsLeft = (ImageView) findViewById(R.id.iv_blow_hands_l);
		mIVBlowFeetLeft = (ImageView) findViewById(R.id.iv_blow_feet_l);
		mIVHeatSeatLeft = (ImageView) findViewById(R.id.iv_heat_seat_l);

		mIVBlowHeadRight = (ImageView) findViewById(R.id.iv_blow_head_r);
		mIVBlowHandsRight = (ImageView) findViewById(R.id.iv_blow_hands_r);
		mIVBlowFeetRight = (ImageView) findViewById(R.id.iv_blow_feet_r);
		mIVHeatSeatRight = (ImageView) findViewById(R.id.iv_heat_seat_r);

		mTVTempLeft = (TextView) findViewById(R.id.tv_temp_l);
		mTVTempRight = (TextView) findViewById(R.id.tv_temp_r);

		mCANBusWindLevelView = (CANBusWindLevelView) findViewById(R.id.cwv_wind_speed);
	}

	protected void updateUI() {
		if (isConnectedService()) {
			
			Log.d("UIACActivity", "updateUI");

			updateAC();

			updateAutoWindUI();

			updateGlassUI();

			updateBlowUI();

			updateHeatSeatUI();

			updateTempUI();

			updateWindSpeedUI();
		}
	}

	protected void updateAC() {
		if (getAir().getDual() != CANBUS_DATA_OFF) {
			mTVDual.setVisibility(View.VISIBLE);
		} else {
			mTVDual.setVisibility(View.INVISIBLE);
		}

		if (getAir().getAC() != CANBUS_DATA_OFF) {
			mTVAC.setVisibility(View.VISIBLE);
		} else {
			mTVAC.setVisibility(View.INVISIBLE);
		}

		if (getAir().getInnerLoop() != CANBUS_DATA_OFF) {
			mIVInnerLoop.setImageResource(R.drawable.ac_inner_loop);
		} else {
			mIVInnerLoop.setImageResource(R.drawable.ac_outer_loop);
		}
	}

	protected void updateAutoWindUI() {
		if (getAir().getAutoWindLow() == CANBUS_DATA_OFF
				&& getAir().getAutoWindHigh() == CANBUS_DATA_OFF) {
			mTVAutoWind.setVisibility(View.INVISIBLE);
		} else if (getAir().getAutoWindLow() != CANBUS_DATA_OFF) {
			mTVAutoWind.setVisibility(View.VISIBLE);

			mTVAutoWind.setSelected(false);
		} else if (getAir().getAutoWindHigh() != CANBUS_DATA_OFF) {
			mTVAutoWind.setVisibility(View.VISIBLE);

			mTVAutoWind.setSelected(true);
		}
	}

	protected void updateGlassUI() {
		if (getAir().getFrontWindowDemisting() != CANBUS_DATA_OFF) {
			mIVFrontGlass.setVisibility(View.VISIBLE);
		} else {
			mIVFrontGlass.setVisibility(View.INVISIBLE);
		}

		if (getAir().getRearWindowDemisting() != CANBUS_DATA_OFF) {
			mIVRearGlass.setVisibility(View.VISIBLE);
		} else {
			mIVRearGlass.setVisibility(View.INVISIBLE);
		}
	}

	protected void updateBlowUI() {
		if (getAir().getBlowLeftHead() != CANBUS_DATA_OFF) {
			mIVBlowHeadLeft.setVisibility(View.VISIBLE);
		} else {
			mIVBlowHeadLeft.setVisibility(View.INVISIBLE);
		}

		if (getAir().getBlowLeftHands() != CANBUS_DATA_OFF) {
			mIVBlowHandsLeft.setVisibility(View.VISIBLE);
		} else {
			mIVBlowHandsLeft.setVisibility(View.INVISIBLE);
		}

		if (getAir().getBlowLeftFeet() != CANBUS_DATA_OFF) {
			mIVBlowFeetLeft.setVisibility(View.VISIBLE);
		} else {
			mIVBlowFeetLeft.setVisibility(View.INVISIBLE);
		}

		if (getAir().getBlowRightHead() != CANBUS_DATA_OFF) {
			mIVBlowHeadRight.setVisibility(View.VISIBLE);
		} else {
			mIVBlowHeadRight.setVisibility(View.INVISIBLE);
		}

		if (getAir().getBlowRightHands() != CANBUS_DATA_OFF) {
			mIVBlowHandsRight.setVisibility(View.VISIBLE);
		} else {
			mIVBlowHandsRight.setVisibility(View.INVISIBLE);
		}

		if (getAir().getBlowRightFeet() != CANBUS_DATA_OFF) {
			mIVBlowFeetRight.setVisibility(View.VISIBLE);
		} else {
			mIVBlowFeetRight.setVisibility(View.INVISIBLE);
		}
	}

	protected void updateHeatSeatUI() {
		int heatLeftSeat = getAir().getHeatLeftSeat();
		switch (heatLeftSeat) {
		case 0x00:
			mIVHeatSeatLeft.setImageResource(R.drawable.ac_heat_seat_l1);
			break;

		case 0x10:
			mIVHeatSeatLeft.setImageResource(R.drawable.ac_heat_seat_l2);
			break;

		case 0x20:
			mIVHeatSeatLeft.setImageResource(R.drawable.ac_heat_seat_l3);
			break;

		case 0x30:
			mIVHeatSeatLeft.setImageResource(R.drawable.ac_heat_seat_l4);
			break;
		}

		int heatRightSeat = getAir().getHeatRightSeat();
		switch (heatRightSeat) {
		case 0x00:
			mIVHeatSeatRight.setImageResource(R.drawable.ac_heat_seat_r1);
			break;

		case 0x01:
			mIVHeatSeatRight.setImageResource(R.drawable.ac_heat_seat_r2);
			break;

		case 0x02:
			mIVHeatSeatRight.setImageResource(R.drawable.ac_heat_seat_r3);
			break;

		case 0x03:
			mIVHeatSeatRight.setImageResource(R.drawable.ac_heat_seat_r4);
			break;
		}
	}

	protected void updateTempUI() {
		int tempUnit = getAir().getTempUnit();
		String tempUnitStr = getString((tempUnit == TEMP_UNIT_C) ? R.string.ac_temp_unit_c
				: R.string.ac_temp_unit_f);
		float dbStep = (tempUnit == TEMP_UNIT_C) ? 0.5f : 0.5f * 1.8f;
		int iTempLine = (tempUnit == TEMP_UNIT_C) ? 0 : 32;

		int leftTemp = getAir().getLeftTemp();
		if (leftTemp == 0x00) {
			mTVTempLeft.setText(R.string.ac_temp_lo);
		} else if (leftTemp >= 0x7F) {
			mTVTempLeft.setText(R.string.ac_temp_hi);
		} else {
			float temp = 0.0f;
			temp = leftTemp * dbStep + iTempLine;
			mTVTempLeft.setText(temp + tempUnitStr);
		}

		int rightTemp = getAir().getRightTemp();
		if (rightTemp == 0x00) {
			mTVTempRight.setText(R.string.ac_temp_lo);
		} else if (rightTemp >= 0x7F) {
			mTVTempRight.setText(R.string.ac_temp_hi);
		} else {
			float temp = 0.0f;
			temp = rightTemp * dbStep + iTempLine;
			mTVTempRight.setText(temp + tempUnitStr);
		}
	}

	protected void updateWindSpeedUI() {
		int windSpeed = getAir().getAirVolume() & 0x0f;
		int windMax = (getAir().getAirVolume() & 0xff00) >> 8;
		mCANBusWindLevelView.setMaxLevel(windMax);
		mCANBusWindLevelView.setCurLevel(windSpeed);
		mCANBusWindLevelView.invalidate();
	}
}
