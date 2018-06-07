/**
 * @Title: RaiseFordParse.java
 * @Package com.yecon.canbus.raise
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月14日 下午4:42:00
 * @version V1.0
 */
package com.yecon.canbus.raise.ford;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseFordParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月14日 下午4:42:00
 *
 */
public class RaiseFordParse extends CANBusParse {

	public static final int RAISE_FORD_DATACMD_AIR = 0x21;
	public static final int RAISE_FORD_DATACMD_CAR = 0x24;

	/**
	 * @see com.yecon.canbus.parse.CANBusParse#getPacketStructure()
	 */
	@Override
	public PacketStructure getPacketStructure() {
		// TODO Auto-generated method stub
		PacketStructure ps = new PacketStructure();
		ps.mPacketHead = 0x2e;
		ps.mPacketMinLength = 4;
		ps.mDataCmdPos = 1;
		ps.mDataLenPos = 2;
		ps.mDataOffset = 3;
		return ps;
	}

	/**
	 * @param iPacketBuffer
	 * @return
	 * @see com.yecon.canbus.parse.CANBusParse#CheckSum(int[])
	 */
	@Override
	public boolean CheckSum(int[] iPacketBuffer) {
		// TODO Auto-generated method stub
		boolean bRet = false;
		int iCheckSum = 0x00;
		try {
			int i = 1;
			for (; i < iPacketBuffer.length - 1; i++) {
				iCheckSum += iPacketBuffer[i];
				// 由于目前的Packet被MCU封装了一层,
				// MCU_SERVICE未将MCU的CHECKCUM去掉,为了兼容暂时先这样处理
				if (((iCheckSum & 0xFF) ^ 0xFF) == iPacketBuffer[i + 1]) {
					bRet = true;
					break;
				}
			}
			Log.d(this.getClass().getName(), String.format("CheckSum 0x%02x",
					((iCheckSum & 0xFF) ^ 0xFF)));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * @param iDataCmd
	 * @param iDataBuffer
	 * @return
	 * @see com.yecon.canbus.parse.CANBusParse#ParseData(int, int[])
	 */
	@Override
	public boolean ParseData(int iDataCmd, int[] iDataBuffer) {
		// TODO Auto-generated method stub
		boolean bRet = true;
		try {
			switch (iDataCmd) {
			case RAISE_FORD_DATACMD_AIR:
				// data 0
				getAir().setACSwitch(GetBit(iDataBuffer[0], 7)); // 空调开关
				getAir().setAC(GetBit(iDataBuffer[0], 6)); // 制冷开关
				getAir().setInnerLoop(GetBit(iDataBuffer[0], 5)); // 内外循环
				getAir().setAutoWindHigh(GetBit(iDataBuffer[0], 3)); // AUTO
				getAir().setAutoWindLow(GetBit(iDataBuffer[0], 3)); // AUTO
				getAir().setDual(GetBit(iDataBuffer[0], 2)); // 温度双驱控制
				getAir().setMaxFrontLight(GetBit(iDataBuffer[0], 0)); // MAX
				// FRONT
				// data 1
				getAir().setBlowLeftHead(GetBit(iDataBuffer[1], 7)); // 向上送风
				getAir().setBlowLeftHands(GetBit(iDataBuffer[1], 6)); // 平行送风
				getAir().setBlowLeftFeet(GetBit(iDataBuffer[1], 5)); // 向下送风
				getAir().setBlowRightHead(GetBit(iDataBuffer[1], 7)); // 向上送风
				getAir().setBlowRightHands(GetBit(iDataBuffer[1], 6)); // 平行送风
				getAir().setBlowRightFeet(GetBit(iDataBuffer[1], 5)); // 向下送风
				getAir().setShowRequest(GetBit(iDataBuffer[1], 4)); // 空调信息变化请求显示
				getAir().setAirVolume((iDataBuffer[1] & 0x0f) | 0x0700); // 风量大小及最大值
				// data 2
				getAir().setLeftTemp(iDataBuffer[2]); // 左温度值 (实际温度值 = 温度值 *
														// 温度步进)
				// data 3
				getAir().setRightTemp(iDataBuffer[3]); // 右温度值 (实际温度值 = 温度值 *
														// 温度步进)
				// data 4
				getAir().setACMax(GetBit(iDataBuffer[4], 2)); // AC
																// MAX
				getAir().setTempUnit(GetBit(iDataBuffer[4], 6)); // 温度单位
				// data 5
				double OutSideTemp = iDataBuffer[5] & ~(1 << 7);
				OutSideTemp = (GetBit(iDataBuffer[5], 7) > 0) ? (0 - OutSideTemp)
						: OutSideTemp;
				getCar().setOutdoorTemp(OutSideTemp);
				break;
			case RAISE_FORD_DATACMD_CAR:
				// data 0
				getCar().setLeftFrontDoor(GetBit(iDataBuffer[0], 6)); // 左前门
				getCar().setRightFrontDoor(GetBit(iDataBuffer[0], 7)); // 右前门
				getCar().setLeftBackDoor(GetBit(iDataBuffer[0], 4)); // 左后门
				getCar().setRightBackDoor(GetBit(iDataBuffer[0], 5)); // 右后门
				getCar().setRearDoor(GetBit(iDataBuffer[0], 3)); // 后备箱
				// data 1
				getCar().setReverse(GetBit(iDataBuffer[1], 0)); // 倒车状态
				getCar().setHandbrake(GetBit(iDataBuffer[1], 3)); // 手刹状态
				break;
			default:
				bRet = false;
				break;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * @param content
	 * @param iDataCmd
	 * @see com.yecon.canbus.parse.CANBusParse#LaunchCanbusUI(android.content.Context,
	 *      int)
	 */
	@Override
	public void LaunchCanbusUI(Context content, int iDataCmd) {
		// TODO Auto-generated method stub
		if (iDataCmd == RAISE_FORD_DATACMD_AIR) {
			if (!isForeground(content, FordACActivity.class.getName())) {
				Intent intent = new Intent(content, FordACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}
}
