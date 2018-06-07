/**
 * @Title: CANBusParse.java
 * @Package com.yecon.canbus
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月14日 下午1:59:48
 * @version V1.0
 */
package com.yecon.canbus.parse;

import java.util.List;

import com.yecon.canbus.info.CANBusAir;
import com.yecon.canbus.info.CANBusCar;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.mcu.McuOriginalInfo;
import android.text.TextUtils;
import android.util.Log;
import static com.yecon.canbus.parse.CANBusDebugUtil.*;

/**
 * @ClassName: CANBusParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月14日 下午1:59:48
 *
 */
abstract public class CANBusParse {

	private final String CLASS_NAME = CANBusParse.class.getName() + ":";

	protected int[] mPacketBuffer; // 整包数据
	protected int mDataLength; // 实际数据长度
	protected int mDataCmd; // 数据类型
	protected YeconCANBus mYeconCANBus = null;
	protected PacketStructure mPS = null;
	
	public void StartParse(YeconCANBus yeconCANBus)	{
		mYeconCANBus = yeconCANBus;
	}
	
	/*
	 * 从MCU Service获取数据整个数据包并解析出数据头和所携带的数据
	 */
	public boolean ParsePacket(McuOriginalInfo mcuCanBusInfo) {
		boolean bRet = false;

		// 先构造包信息对象
		if (mPS == null) {
			mPS = getPacketStructure();
		}

		// 解析数据
		if (mPS != null) {
		    int length = mcuCanBusInfo.getDataLength();
		    byte[] data = mcuCanBusInfo.getMcuData();
		    for (int i = 0; i < length; i++) {
		        mPacketBuffer[i] = data[i] & 0xFF;
		    }
			// 从MCU Service获取数据
			//mPacketBuffer = mcuCanBusInfo.getMcuData();
			PrintBuffer(mPacketBuffer);

			// 初步解析判断是否是合法的数据
			if (IsValidPacket(mPacketBuffer)) {
				mDataCmd = DataHead(mPacketBuffer);
				mDataLength = DataLength(mPacketBuffer);
				String logString = String.format(
						"DataCmd 0x%02x, DataLength = 0x%02x ", mDataCmd,
						mDataLength);
				printLog(CLASS_NAME + logString);
				bRet = ParseData(mDataCmd, GetDataBuff(mPacketBuffer));
			} else {
				mDataCmd = 0;
				mDataLength = 0;
				printLog(CLASS_NAME + "ParsePacket Error, invalid Packet !!!");
			}
		}

		return bRet;
	}

	/*
	 * 检测是否是合法的数据包 return true (合法) ， false (非法)
	 */
	protected boolean IsValidPacket(int[] iPacketBuffer) {
		boolean bRet = false;
		if (iPacketBuffer != null) {
			// 判断数据头
			if (iPacketBuffer[0] == mPS.mPacketHead) {
				// 是否符合最小数据包要求
				if (iPacketBuffer.length >= mPS.mPacketMinLength) {
					if (CheckSum(iPacketBuffer)) {
						bRet = true;
					}
				}

			}
		} else {
			printLog(CLASS_NAME + "null buffer");
		}
		return bRet;
	}

	/*
	 * 构造包信息对象
	 */
	abstract public PacketStructure getPacketStructure();

	/*
	 * 数据包检查
	 */
	abstract public boolean CheckSum(int[] iPacketBuffer);

	/*
	 * 解析数据包
	 */
	abstract public boolean ParseData(int iDataCmd, int[] iDataBuffer);
	
	/*
	 * 激活要显示的界面
	 */
	abstract public void LaunchCanbusUI(Context content, int iDataCmd);

	/*
	 * 获取数据位长度 实际数据位长度 = 数据长度 + 数据头 - 最小数据包
	 */
	public int DataLength(int[] iPacketBuffer) {
		return iPacketBuffer[mPS.mDataLenPos];
	}

	/*
	 * 获取数据头
	 */
	public int DataHead(int[] iPacketBuffer) {
		return iPacketBuffer[mPS.mDataCmdPos];
	}

	/*
	 * 获取SubCmd所携带的数据
	 */
	public int[] GetDataBuff(int[] iPacketBuffer) {
		int[] iDataBuffer = new int[mDataLength + 1];
		for (int i = 0; i < mDataLength; i++) {
			iDataBuffer[i] = iPacketBuffer[i + mPS.mDataOffset];
		}
		iDataBuffer[mDataLength] = 0;
		return iDataBuffer;
	}
	
	/*
	 * 获取当前包的命令头
	 */
	public int GetDataCmd() {
		return mDataCmd;
	}

	/*
	 * 获取指定BIT位的数据值
	 * 
	 * @param iData 数据源， iBitNum 指定的位
	 */
	protected int GetBit(int iData, int iBitPos) {
		return ((iData >> iBitPos) & 0x01);
	}

	/*
	 * 输出整个buffer
	 * 
	 * @param idatabuff
	 */
	protected void PrintBuffer(int[] iBuffer) {
		String strlog = "PrintBuffer ";
		for (int i = 0; i < iBuffer.length; i++) {
			strlog += String.format(" 0x%02x", iBuffer[i]);
		}
		Log.d(CLASS_NAME, strlog);
	}

	protected CANBusAir getAir()
	{
		return mYeconCANBus.getAir();
	}
	
	protected CANBusCar getCar()
	{
		return mYeconCANBus.getCar();
	}
	
	 /**
     * 判断某个界面是否在前台
     * @param content
     * @param className
     *  某个界面名称
     */
    public static boolean isForeground(Context content, String className) {
        if (content == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) content.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

	/*
	 * 该车型的协议BUFFER的结构
	 */
	public class PacketStructure {
		// 包头
		public int mPacketHead;

		// 最小数据包长度
		public int mPacketMinLength;

		// 数据头的位置
		public int mDataCmdPos;

		// 数据长度的位置
		public int mDataLenPos;

		// 数据偏移
		public int mDataOffset;
	}
}
