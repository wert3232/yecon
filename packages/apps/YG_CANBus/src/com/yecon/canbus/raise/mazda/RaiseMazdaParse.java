/**
 * @Title: RaiseMazdaParse.java
 * @Package com.yecon.canbus.raise.mazda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:15:21
 * @version V1.0
 */
package com.yecon.canbus.raise.mazda;

import android.content.Context;
import android.content.Intent;

import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseMazdaParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:15:21
 *
 */
public class RaiseMazdaParse extends CANBusParse {

	public static final int RAISE_MAZDA_DATACMD_AIR = 0x10;
	public static final int RAISE_MAZDA_DATACMD_TEMP = 0x13;

	/**
	 * <p>
	 * Title: getPacketStructure
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @see com.yecon.canbus.parse.CANBusParse#getPacketStructure()
	 */
	@Override
	public PacketStructure getPacketStructure() {
		// TODO Auto-generated method stub
		PacketStructure ps = new PacketStructure();
		ps.mPacketHead = 0xfd;
		ps.mPacketMinLength = 4;
		ps.mDataCmdPos = 2;
		ps.mDataLenPos = 1;
		ps.mDataOffset = 3;
		return ps;
	}

	/**
	 * <p>
	 * Title: CheckSum
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
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
				if ((iCheckSum & 0xFF) == iPacketBuffer[i + 1]) {
					bRet = true;
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bRet;
	}
	
	

	@Override
	public int DataLength(int[] iPacketBuffer) {
		// TODO Auto-generated method stub
		return (super.DataLength(iPacketBuffer) - 3);
	}

	/**
	 * <p>
	 * Title: ParseData
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
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
			case RAISE_MAZDA_DATACMD_AIR:
				// data 0
				getAir().setLeftTemp(iDataBuffer[0]); // 温度整数部分
				// data 3
				getAir().setRightTemp(iDataBuffer[3] & 0x0f); // 温度小数部分

				// data 1
				getAir().setInnerLoop(GetBit(iDataBuffer[1], 7)); // 内外循环
				getAir().setFrontWindowDemisting(GetBit(iDataBuffer[1], 6)); // 前窗除雾
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[1], 5)); // 后窗除雾
				getAir().setBlowLeftHands(GetBit(iDataBuffer[1], 3)); // 平行送风
				getAir().setBlowLeftFeet(GetBit(iDataBuffer[1], 4)); // 向下送风
				getAir().setBlowRightHands(GetBit(iDataBuffer[1], 3)); // 平行送风
				getAir().setBlowRightFeet(GetBit(iDataBuffer[1], 4)); // 向下送风
				getAir().setAutoWindHigh(GetBit(iDataBuffer[1], 2)); // AUTO
				getAir().setAutoWindLow(GetBit(iDataBuffer[1], 2)); // AUTO
				getAir().setDual(GetBit(iDataBuffer[1], 1)); // 温度双驱控制
				getAir().setAC(GetBit(iDataBuffer[1], 0)); // 制冷开关
				// data 2
				getAir().setAirVolume((iDataBuffer[2] & 0x0f) | 0x0F00); // 风量大小及最大值
				break;

			case RAISE_MAZDA_DATACMD_TEMP:
				// data 0
				double OutSideTemp = iDataBuffer[0] & ~(1 << 7);
				OutSideTemp = (GetBit(iDataBuffer[0], 7) > 0) ? (0 - OutSideTemp)
						: OutSideTemp;
				getCar().setOutdoorTemp(OutSideTemp);
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
	 * <p>
	 * Title: LaunchCanbusUI
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param content
	 * @param iDataCmd
	 * @see com.yecon.canbus.parse.CANBusParse#LaunchCanbusUI(android.content.Context,
	 *      int)
	 */
	@Override
	public void LaunchCanbusUI(Context content, int iDataCmd) {
		// TODO Auto-generated method stub
		if (iDataCmd == RAISE_MAZDA_DATACMD_AIR) {
			if (!isForeground(content, MazdaACActivity.class.getName())) {
				Intent intent = new Intent(content, MazdaACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

}
