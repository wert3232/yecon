/**
 * @Title: YeconCANBus.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月14日 上午11:53:21
 * @version V1.0
 */
package com.yecon.canbus.parse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.mcu.McuOriginalInfo;

import com.yecon.canbus.YeconCANBusFactory;
import com.yecon.canbus.info.CANBusAir;
import com.yecon.canbus.info.CANBusCar;
import com.yecon.canbus.info.CANBusConstants;
import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: YeconCANBus
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月14日 上午11:53:21
 *
 */
public class YeconCANBus {

	/*
	 * CANBUS类型
	 */
	private int mCanbusType = CANBusConstants.CANBUS_TYPE_UNKOWN;

	/*
	 * 汽车空调信息
	 */
	private CANBusAir mAir = new CANBusAir();

	/*
	 * 汽车车辆信息
	 */
	private CANBusCar mCar = new CANBusCar();

	/*
	 * canbus解析器
	 */
	private CANBusParse mParse = null;

	/*
	 * 构造函数
	 */
	public YeconCANBus(int iCanbusType) {
		// TODO 通过canbus类型构造Canbus对象
		if (mCanbusType != iCanbusType) {
			mCanbusType = iCanbusType;
			mParse = YeconCANBusFactory.CreateCanbusParse(iCanbusType);
			mParse.StartParse(this);
		}
	}

	/*
	 * 获取canbus类型
	 */
	public int getCanbusType() {
		return mCanbusType;
	}
	
	/*
	 * 获取当前packet的命令头
	 */
	public int getDataCmd() {
		int iCmd = 0;
		if (mParse != null) {
			iCmd = mParse.GetDataCmd();
		}
		return iCmd;
	}

	/*
	 * 获取空调对象
	 */
	public CANBusAir getAir() {
		return mAir;
	}

	/*
	 * 获取汽车信息对象
	 */
	public CANBusCar getCar() {
		return mCar;
	}

	/*
	 * 解析数据
	 */
	public boolean ParsePacket(McuOriginalInfo canbusPacket) {
		boolean bRet = false;
		if (mParse != null) {
			bRet = mParse.ParsePacket(canbusPacket);
		}
		return bRet;
	}
	
	/*
	 * 启动Canbus相关的UI
	 */
	public void LaunchCanbusUI(Context content)
	{
		if (mParse != null) {
			mParse.LaunchCanbusUI(content, mParse.GetDataCmd());
		}
	}
	
	public static int ReadCanbusType(Context context)
	{
		int iCanbusType = CANBusConstants.CANBUS_TYPE_UNKOWN;
		SharedPreferences sp = context.getSharedPreferences(CANBusConstants.YECON_CANBUS, Context.MODE_PRIVATE);
		iCanbusType = sp.getInt(CANBusConstants.YECON_CANBUS_TYPE, CANBusConstants.CANBUS_TYPE_VW);
		return iCanbusType;
	}
	
	@SuppressLint("CommitPrefEdits")
	public static int WriteCanbusType(Context context, int iCanbusType)
	{
		SharedPreferences sp = context.getSharedPreferences(CANBusConstants.YECON_CANBUS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(CANBusConstants.YECON_CANBUS_TYPE, iCanbusType);
		editor.commit();
		return iCanbusType;
	}
}
