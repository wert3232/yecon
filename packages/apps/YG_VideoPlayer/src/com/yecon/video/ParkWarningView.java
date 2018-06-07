package com.yecon.video;

import static android.mcu.McuExternalConstant.INTENT_EXTRA_BRAKE_STATUS;
import static android.mcu.McuExternalConstant.MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ParkWarningView extends LinearLayout {

	private static final String TAG = "ParkWarningView";
	private boolean mbParkingEnable; 	// 行车禁止视频
	private boolean mbUnderParking;		// 驻车状态
	private Context mContext;
	private BroadcastReceiver mParkingListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS)) {
				int brakeStatus = intent.getIntExtra(INTENT_EXTRA_BRAKE_STATUS, 0);
				mbUnderParking = (brakeStatus == 0) ? true : false;
                displayParking();  
			}else if("com.wesail.action.speed.info".equals(action)){
				mbParkingEnable = true;
				int speed = intent.getIntExtra("speed", 0);
				mbUnderParking = (speed < 20);
                displayParking();  
			}
		}
	};
	
	public ParkWarningView(Context context) {
		this(context, null);
	}
	
	public ParkWarningView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ParkWarningView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public void updateParkProperty() {
        mbParkingEnable = SystemProperties.getBoolean("persist.sys.parking_enable", false);
        mbUnderParking = SystemProperties.getBoolean("persist.sys.mcu_parking", false);
        Log.e(TAG, "parking_enable:" + mbParkingEnable + " mbUnderParking:" + mbUnderParking);
        displayParking();
	}
	
	private void displayParking() {
		if (mbParkingEnable && !mbUnderParking) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			updateParkProperty();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter();
        filter.addAction(MCU_ACTION_HEADLIGHTS_AND_BRAKE_STATUS);
        filter.addAction("com.wesail.action.speed.info");
        mContext.registerReceiver(mParkingListener, filter);
        Log.e(TAG, "registerReceiver:mParkingListener");
        updateParkProperty();
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mContext.unregisterReceiver(mParkingListener);
	}
}
