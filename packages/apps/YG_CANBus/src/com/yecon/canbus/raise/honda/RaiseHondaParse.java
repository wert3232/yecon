/**
 * @Title: RaiseHondaParse.java
 * @Package com.yecon.canbus.raise.honda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:42:18
 * @version V1.0
 */
package com.yecon.canbus.raise.honda;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseHondaParse
 * @Description: 此CANBUS分析器 适用于凌派&杰德
 * @author hzGuo
 * @date 2016年4月15日 下午4:42:18
 *
 */
public class RaiseHondaParse extends CANBusParse {

	// 空调数据头
	public static final int RAISE_HONDA_DATACMD_AIR = 0x21;
	// 形成电脑数据头
	public static final int RAISE_HONDA_DATACMD_CAR = 0x24;
	
	
	/**
	 * <p>Title: getPacketStructure</p>
	 * <p>Description: </p>
	 * @return
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
	 * <p>Title: CheckSum</p>
	 * <p>Description: </p>
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
			Log.d("Jincy", String.format("CheckSum 0x%02x", ((iCheckSum & 0xFF) ^ 0xFF)));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * <p>Title: ParseData</p>
	 * <p>Description: </p>
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
			case RAISE_HONDA_DATACMD_AIR:
				// data 0
				getAir().setACSwitch(GetBit(iDataBuffer[0], 7)); // 空调开关
				getAir().setAC(GetBit(iDataBuffer[0], 6)); // 制冷开关
				getAir().setInnerLoop(GetBit(iDataBuffer[0], 5)); // 内外循环
				getAir().setAutoWindHigh(GetBit(iDataBuffer[0], 3)); // AUTO
				getAir().setAutoWindLow(GetBit(iDataBuffer[0], 3)); // AUTO
				getAir().setDual(GetBit(iDataBuffer[0], 2)); // 温度双驱控制
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[0], 0)); // rear
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
				getAir().setLeftTemp(iDataBuffer[2]); // 左温度值 (实际温度值 = 温度值
															// *
															// 温度步进)
				// data 3
				getAir().setRightTemp(iDataBuffer[3]); // 右温度值 (实际温度值 = 温度值
															// *
															// 温度步进)
				// data 4
				getAir().setFrontWindowDemisting(GetBit(iDataBuffer[4], 7)); // AC MAX
				getAir().setTempUnit(GetBit(iDataBuffer[4], 0)); // 温度单位
				break;
			case RAISE_HONDA_DATACMD_CAR:
				// data 0
				getCar().setLeftFrontDoor(GetBit(iDataBuffer[0], 6)); // 左前门
				getCar().setRightFrontDoor(GetBit(iDataBuffer[0], 7)); // 右前门
				getCar().setLeftBackDoor(GetBit(iDataBuffer[0], 4)); // 左后门
				getCar().setRightBackDoor(GetBit(iDataBuffer[0], 5)); // 右后门
				getCar().setRearDoor(GetBit(iDataBuffer[0], 3)); // 后备箱
				getCar().setBonnet(GetBit(iDataBuffer[0], 2)); // 引擎盖
				// data 1
				getCar().setReverse(GetBit(iDataBuffer[1], 0)); // 倒车状态
				getCar().setParkOrBrake(GetBit(iDataBuffer[1], 1)); // P档/手刹
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
	 * <p>Title: LaunchCanbusUI</p>
	 * <p>Description: </p>
	 * @param content
	 * @param iDataCmd
	 * @see com.yecon.canbus.parse.CANBusParse#LaunchCanbusUI(android.content.Context, int)
	 */
	@Override
	public void LaunchCanbusUI(Context content, int iDataCmd) {
		// TODO Auto-generated method stub
		if (iDataCmd == RAISE_HONDA_DATACMD_AIR) {
			if (!isForeground(content, HondaACActivity.class.getName())) {
				Intent intent = new Intent(content, HondaACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

}
