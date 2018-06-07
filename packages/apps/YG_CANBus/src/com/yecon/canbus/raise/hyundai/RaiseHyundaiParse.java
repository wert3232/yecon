/**
 * @Title: RaiseHyundaiParse.java
 * @Package com.yecon.canbus.raise.hyundai
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:24:56
 * @version V1.0
 */
package com.yecon.canbus.raise.hyundai;

import android.content.Context;
import android.content.Intent;

import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseHyundaiParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:24:56
 *
 */
public class RaiseHyundaiParse extends CANBusParse {

	public static final int RAISE_HYUNDAI_DATACMD_AIR = 0x03;
	public static final int RAISE_HYUNDAI_DATACMD_TEMP = 0x01;

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
			for (; i < iPacketBuffer.length - 2; i++) {
				iCheckSum += iPacketBuffer[i];
				// 由于目前的Packet被MCU封装了一层,
				// MCU_SERVICE未将MCU的CHECKCUM去掉,为了兼容暂时先这样处理
				if ((iCheckSum & 0xFFFF) == (iPacketBuffer[i + 1] << 8 | iPacketBuffer[i + 2])) {
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
		return super.DataLength(iPacketBuffer) - 4;
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
			case RAISE_HYUNDAI_DATACMD_AIR:
				// data 0
				getAir().setLeftTemp(iDataBuffer[0]); // 左温度值 (实际温度值 = 温度值
														// * 温度步进)
				// data 1
				getAir().setRightTemp(iDataBuffer[1]); // 右温度值 (实际温度值 = 温度值
														// * 温度步进)
				// data 2
				getAir().setAirVolume((iDataBuffer[2] & 0x0f) | 0x0800); // 风量大小及最大值
				// data 3
				getAir().setInnerLoop(GetBit(iDataBuffer[3], 7)); // 内外循环
				getAir().setFrontWindowDemisting(GetBit(iDataBuffer[3], 6)); // 前窗除雾
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[3], 5)); // 后窗除雾
				getAir().setBlowLeftHands(GetBit(iDataBuffer[3], 3)); // 平行送风
				getAir().setBlowLeftFeet(GetBit(iDataBuffer[3], 4)); // 向下送风
				getAir().setBlowRightHands(GetBit(iDataBuffer[3], 3)); // 平行送风
				getAir().setBlowRightFeet(GetBit(iDataBuffer[3], 4)); // 向下送风
				getAir().setAutoWindHigh(GetBit(iDataBuffer[3], 2)); // AUTO
				getAir().setAutoWindLow(GetBit(iDataBuffer[3], 2)); // AUTO
				getAir().setDual(GetBit(iDataBuffer[3], 1)); // 温度双驱控制
				getAir().setAC(GetBit(iDataBuffer[3], 0)); // 制冷开关
				break;
			case RAISE_HYUNDAI_DATACMD_TEMP:

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
		if (iDataCmd == RAISE_HYUNDAI_DATACMD_AIR) {
			if (!isForeground(content, HyundaiACActivity.class.getName())) {
				Intent intent = new Intent(content, HyundaiACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

}
