/**
 * @Title: CANBusUI.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 上午11:39:51
 * @version V1.0
 */
package com.yecon.canbus.ui;

import static com.yecon.canbus.info.CANBusConstants.MSG_UPDATE_UI;

import com.yecon.canbus.info.CANBusAir;
import com.yecon.canbus.info.CANBusCar;
import com.yecon.canbus.parse.YeconCANBusService;
import com.yecon.canbus.parse.YeconCANBusService.YeconCanbusServiceBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

/**
 * @ClassName: CANBusUI
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 上午11:39:51
 *
 */
abstract public class CANBusUI extends Activity implements ServiceConnection {

	/*
	 * 当前界面相关的信息
	 */
	protected CanbusUIConstruct mActivityConstruct = null;
	
	/*
	 * 刷新界面的Handler
	 */
	protected Handler mHandler = null;
	
	/*
	 * CANBUS解析服务
	 */
	protected YeconCANBusService mService = null;
	
	/*
	 * 获取 layout id 和 该界面需要刷新的数据的数据头
	 */
	abstract public CanbusUIConstruct getCanbusUIConstruct();
	
	/*
	 * 刷新界面
	 */
	abstract protected void updateUI();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mActivityConstruct = getCanbusUIConstruct();

		if (mActivityConstruct != null) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), YeconCANBusService.class);
			bindService(intent, this, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		for (int i = 0; i < mActivityConstruct.mRelateCmd.length; i++) {
			mService.UnRegisterCanbusUI(this);
		}
		unbindService(this);
	}

	/*
	 * 通知UI刷新
	 */
	public void NotifyUpdateUI(int iCmd) {
		// TODO Auto-generated method stub
		if (mHandler != null) {
			for (int i = 0; i < mActivityConstruct.mRelateCmd.length; i++)
				if (iCmd == mActivityConstruct.mRelateCmd[i]) {
					mHandler.sendEmptyMessage(MSG_UPDATE_UI);
				}
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		mService = ((YeconCanbusServiceBinder) service).getService();
		for (int i = 0; i < mActivityConstruct.mRelateCmd.length; i++) {
			mService.RegisterCanbusUI(this);
		}
		updateUI();
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		mService = null;
	}
	
	protected CANBusAir getAir()
	{
		return mService.getCanBus().getAir();
	}
	
	protected CANBusCar getCar()
	{
		return mService.getCanBus().getCar();
	}

	protected boolean isConnectedService() {
		return (mService != null);
	}

	/*
	 * CANBUS UI 的基本信息
	 */
	public static class CanbusUIConstruct {
		/*
		 * 该CANBUS UI的layout xml id
		 */
		public int mLayoutID;

		/*
		 * 该CANBUS UI关心的命令头
		 */
		public int[] mRelateCmd;
	}
}
