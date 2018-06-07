/**
 * @Title: YeconCANBusService.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月14日 上午11:25:14
 * @version V1.0
 */
package com.yecon.canbus.parse;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.mcu.McuBaseInfo;
import android.mcu.McuExternalConstant;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.yecon.canbus.parse.YeconCANBus;
import com.yecon.canbus.ui.CANBusUI;

/**
 * @ClassName: YeconCANBusService
 * @Description:
 * @author hzGuo
 * @date 2016年4月14日 上午11:25:14
 *
 */
public class YeconCANBusService extends Service implements McuListener {

	private static final int MSG_CANBUS_INFO_CHANGED = 1;
	private static final int MSG_CANBUS_UPDATE_UI = 2;
	private static final int UPDATE_CANBUS_UI_DELAY_TIME = 50;
	
	/*
	 * canbus ui has been bind with this service
	 */
	private List<CANBusUI> mlsCanBusUIManager = new ArrayList<CANBusUI>();

	/*
	 * canbus service
	 */
	private McuManager mCanbusService;

	/*
	 * notify ui to update
	 */
	@SuppressLint("HandlerLeak")
	private Handler mUINotifyHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_CANBUS_INFO_CHANGED:
				mYeconCanbus.LaunchCanbusUI(getApplicationContext());
				mUINotifyHandler.sendEmptyMessageAtTime(MSG_CANBUS_UPDATE_UI,
						UPDATE_CANBUS_UI_DELAY_TIME);
				break;
			case MSG_CANBUS_UPDATE_UI:
				synchronized (this) {
					for (int i = 0; i < mlsCanBusUIManager.size(); i++) {
						((CANBusUI)mlsCanBusUIManager.get(i)).NotifyUpdateUI(mYeconCanbus.getDataCmd());
					}
				}
				break;
			default:
				break;
			}

		}
	};

	/*
	 * YeconCANBus object
	 */
	private YeconCANBus mYeconCanbus = null;
		
	/*
	 * YeconCanbusServiceBinder 
	 */
	private YeconCanbusServiceBinder mBinder = new YeconCanbusServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// create canbus object
		mYeconCanbus = new YeconCANBus(YeconCANBus.ReadCanbusType(getApplicationContext()));
		
		// create mcu canbus listener
		mCanbusService = (McuManager) getSystemService(Context.MCU_SERVICE);
		try {
			mCanbusService.RPC_RequestMcuInfoChangedListener(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try {
			mCanbusService.RPC_RemoveMcuInfoChangedListener(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Title: onMcuInfoChanged description：call back when MCU info has been
	 * changed
	 * 
	 * @param mcuBaseInfo
	 * @param infoType
	 * @see android.mcu.McuListener#onMcuInfoChanged(android.mcu.McuBaseInfo,
	 *      int)
	 */
	@Override
	public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) {
		// TODO Auto-generated method stub
		if (mcuBaseInfo != null) {
			if (infoType == McuExternalConstant.MCU_CANBUS_INFO_TYPE) {
				Log.e("Jincy", "onMcuInfoChanged");

				if (mYeconCanbus.ParsePacket(mcuBaseInfo.getOriginalInfo())) {
					mUINotifyHandler
							.sendEmptyMessage(MSG_CANBUS_INFO_CHANGED);
				}
			}
		}
	}
	
	/*
	 * 注册CANBUS UI到服务
	 */
	public void RegisterCanbusUI(CANBusUI ui)
	{
		synchronized (this) {
			mlsCanBusUIManager.add(ui);
		}
	}
	
	/*
	 * 释放服务中注册的CANBUS UI
	 */
	public void UnRegisterCanbusUI(CANBusUI ui)
	{
		synchronized (this) {
			mlsCanBusUIManager.remove(ui);
		}
	}
	
	/*
	 * 获取CANBUS对象
	 */
	public YeconCANBus getCanBus()
	{
		return mYeconCanbus;
	}
	
	public class YeconCanbusServiceBinder extends Binder {
		public YeconCANBusService getService() {
			return YeconCANBusService.this;
		}
	}
}
