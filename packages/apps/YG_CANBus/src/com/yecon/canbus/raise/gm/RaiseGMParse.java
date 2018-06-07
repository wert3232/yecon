/**
 * @Title: RaiseGMParse.java
 * @Package com.yecon.canbus.raise.gm
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午4:54:10
 * @version V1.0
 */
package com.yecon.canbus.raise.gm;

import android.content.Context;
import android.content.Intent;

import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseGMParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午4:54:10
 *
 */
public class RaiseGMParse extends CANBusParse {

	public static final int RAISE_GM_DATACMD_AIR = 0x03; // 空调信息
	public static final int RAISE_GM_DATACMD_CAR = 0x24; // 车速信息
	public static final int RAISE_GM_DATACMD_SPEED = 0x0B; // 车速信息

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
		ps.mPacketHead = 0x2e;
		ps.mPacketMinLength = 4;
		ps.mDataCmdPos = 1;
		ps.mDataLenPos = 2;
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
				if (((iCheckSum & 0xFF) ^ 0xFF) == iPacketBuffer[i + 1]) {
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
			case RAISE_GM_DATACMD_AIR:
				// data 0
				getAir().setACSwitch(GetBit(iDataBuffer[0], 7)); // 空调开关
				getAir().setAC(GetBit(iDataBuffer[0], 6)); // 制冷开关
				getAir().setInnerLoop(GetBit(iDataBuffer[0], 5)); // 内外循环
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[0], 4)); // 后窗除雾
				getAir().setAirVolume(
						(GetBit(iDataBuffer[1], 4) << 3 | (iDataBuffer[0] & 0x07)) | 0x0800); // 风量大小及最大值
				// data 1
				switch (iDataBuffer[1] & 0x0f) {
				case 1:
					getAir().setAutoWindHigh(1); // AUTO
					getAir().setAutoWindLow(1); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(0); // 向上送风
					getAir().setBlowLeftHands(0); // 平行送风
					getAir().setBlowLeftFeet(0); // 向下送风
					getAir().setBlowRightHead(0); // 向上送风
					getAir().setBlowRightHands(0); // 平行送风
					getAir().setBlowRightFeet(0); // 向下送风
					break;
				case 2:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(1); // 前窗除雾
					getAir().setBlowLeftHead(0); // 向上送风
					getAir().setBlowLeftHands(0); // 平行送风
					getAir().setBlowLeftFeet(0); // 向下送风
					getAir().setBlowRightHead(0); // 向上送风
					getAir().setBlowRightHands(0); // 平行送风
					getAir().setBlowRightFeet(0); // 向下送风
					break;
				case 3:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(0); // 向上送风
					getAir().setBlowLeftHands(0); // 平行送风
					getAir().setBlowLeftFeet(1); // 向下送风
					getAir().setBlowRightHead(0); // 向上送风
					getAir().setBlowRightHands(0); // 平行送风
					getAir().setBlowRightFeet(1); // 向下送风
					break;
				case 4:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(0); // 向上送风
					getAir().setBlowLeftHands(1); // 平行送风
					getAir().setBlowLeftFeet(1); // 向下送风
					getAir().setBlowRightHead(0); // 向上送风
					getAir().setBlowRightHands(1); // 平行送风
					getAir().setBlowRightFeet(1); // 向下送风
					break;
				case 5:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(0); // 向上送风
					getAir().setBlowLeftHands(1); // 平行送风
					getAir().setBlowLeftFeet(0); // 向下送风
					getAir().setBlowRightHead(0); // 向上送风
					getAir().setBlowRightHands(1); // 平行送风
					getAir().setBlowRightFeet(0); // 向下送风
					break;
				case 6:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(1); // 向上送风
					getAir().setBlowLeftHands(1); // 平行送风
					getAir().setBlowLeftFeet(0); // 向下送风
					getAir().setBlowRightHead(1); // 向上送风
					getAir().setBlowRightHands(1); // 平行送风
					getAir().setBlowRightFeet(0); // 向下送风
					break;
				case 7:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(1); // 向上送风
					getAir().setBlowLeftHands(0); // 平行送风
					getAir().setBlowLeftFeet(0); // 向下送风
					getAir().setBlowRightHead(1); // 向上送风
					getAir().setBlowRightHands(0); // 平行送风
					getAir().setBlowRightFeet(0); // 向下送风
					break;
				case 8:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(1); // 向上送风
					getAir().setBlowLeftHands(0); // 平行送风
					getAir().setBlowLeftFeet(1); // 向下送风
					getAir().setBlowRightHead(1); // 向上送风
					getAir().setBlowRightHands(0); // 平行送风
					getAir().setBlowRightFeet(1); // 向下送风
					break;
				case 9:
					getAir().setAutoWindHigh(0); // AUTO
					getAir().setAutoWindLow(0); // AUTO
					getAir().setFrontWindowDemisting(0); // 前窗除雾
					getAir().setBlowLeftHead(1); // 向上送风
					getAir().setBlowLeftHands(1); // 平行送风
					getAir().setBlowLeftFeet(1); // 向下送风
					getAir().setBlowRightHead(1); // 向上送风
					getAir().setBlowRightHands(1); // 平行送风
					getAir().setBlowRightFeet(1); // 向下送风
					break;
				default:
					break;
				}
				// data 2
				getAir().setLeftTemp(iDataBuffer[2]); // 左温度值 (实际温度值 = 温度值
														// *
														// 温度步进)
				// data 3
				getAir().setRightTemp(iDataBuffer[3]); // 右温度值 (实际温度值 = 温度值
														// *
														// 温度步进)
				// data 4
				getAir().setHeatLeftSeat(iDataBuffer[4] & 0xf0);
				getAir().setHeatRightSeat(iDataBuffer[4] & 0x0f);
				// data 5
				double OutSideTemp = iDataBuffer[5] & ~(1 << 7);
				OutSideTemp = (GetBit(iDataBuffer[5], 7) > 0) ? (0 - OutSideTemp)
						: OutSideTemp;
				getCar().setOutdoorTemp(OutSideTemp);
				break;
			case RAISE_GM_DATACMD_CAR:
				// data 0
				getCar().setLeftFrontDoor(GetBit(iDataBuffer[0], 6)); // 左前门
				getCar().setRightFrontDoor(GetBit(iDataBuffer[0], 7)); // 右前门
				getCar().setLeftBackDoor(GetBit(iDataBuffer[0], 4)); // 左后门
				getCar().setRightBackDoor(GetBit(iDataBuffer[0], 5)); // 右后门
				getCar().setRearDoor(GetBit(iDataBuffer[0], 3)); // 后备箱
				break;
			case RAISE_GM_DATACMD_SPEED:
				getCar().setTravelingSpeed(
						((double) (iDataBuffer[0] << 8 | iDataBuffer[1])) / 100);
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
		if (iDataCmd == RAISE_GM_DATACMD_AIR) {
			if (!isForeground(content, GMACActivity.class.getName())) {
				Intent intent = new Intent(content, GMACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

}
