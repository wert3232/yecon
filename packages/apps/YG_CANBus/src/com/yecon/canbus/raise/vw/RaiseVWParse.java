/**
 * @Title: RaiseVWParse.java
 * @Package com.yecon.canbus.raise.vw
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午5:04:34
 * @version V1.0
 */
package com.yecon.canbus.raise.vw;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaiseVWParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午5:04:34
 *
 */
public class RaiseVWParse extends CANBusParse {

	public static final int RAISE_VW_DATACMD_AIR = 0x21;
	public static final int RAISE_VW_DATACMD_CAR = 0x24;
	public static final int RAISE_VW_DATACMD_CARSTATUS = 0x41;

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
			Log.d(this.getClass().getName(), String.format("CheckSum 0x%02x",
					((iCheckSum & 0xFF) ^ 0xFF)));
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
			case RAISE_VW_DATACMD_AIR:
				// data 0
				getAir().setACSwitch(GetBit(iDataBuffer[0], 7)); // 空调开关
				getAir().setAC(GetBit(iDataBuffer[0], 6)); // 制冷开关
				getAir().setInnerLoop(GetBit(iDataBuffer[0], 5)); // 内外循环
				getAir().setAutoWindHigh(GetBit(iDataBuffer[0], 4)); // AUTO
				getAir().setAutoWindLow(GetBit(iDataBuffer[0], 3)); // AUTO
				getAir().setDual(GetBit(iDataBuffer[0], 2)); // 温度双驱控制
				getAir().setMaxFrontLight(GetBit(iDataBuffer[0], 1)); // MAX FRONT
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[0], 0));
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
				getAir().setAQS(GetBit(iDataBuffer[4], 7));
				getAir().setHeatLeftSeat(iDataBuffer[4] & 0x30);
				getAir().setHeatRightSeat(iDataBuffer[4] & 0x03);
				break;

			case RAISE_VW_DATACMD_CAR:
				break;

			case RAISE_VW_DATACMD_CARSTATUS:
				switch (iDataBuffer[0]) {
				case 0x01:
					getCar().setDriverBelts(GetBit(iDataBuffer[1], 7));
					getCar().setCleaningFluid(GetBit(iDataBuffer[1], 6));
					getCar().setHandbrake(GetBit(iDataBuffer[1], 5));
					getCar().setRearDoor(GetBit(iDataBuffer[1], 4));
					getCar().setRightBackDoor(GetBit(iDataBuffer[1], 3));
					getCar().setLeftBackDoor(GetBit(iDataBuffer[1], 2));
					getCar().setRightFrontDoor(GetBit(iDataBuffer[1], 1));
					getCar().setLeftFrontDoor(GetBit(iDataBuffer[1], 0));
					break;

				case 0x02:
					getCar().setEnginSpeed(iDataBuffer[1] << 8 | iDataBuffer[2]);
					getCar().setTravelingSpeed((double)(iDataBuffer[3] << 8 | iDataBuffer[4])/100);
					getCar().setBatteryVoltage((double)(iDataBuffer[5] << 8 | iDataBuffer[6])/100);
					getCar().setOutdoorTemp((double)(iDataBuffer[7] << 8 | iDataBuffer[8])/10);
					getCar().setDrivenDistance((iDataBuffer[9] << 32 | iDataBuffer[10] << 8 | iDataBuffer[11]));
					getCar().setOilLeft(iDataBuffer[12]);
					break;

				case 0x03:
					getCar().setOilLow(GetBit(iDataBuffer[1], 7));
					getCar().setBatteryLow(GetBit(iDataBuffer[1], 6));
					break;
				default:
					break;
				}
				break;

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
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
		if (iDataCmd == RAISE_VW_DATACMD_AIR) {
			if (!isForeground(content, VWACActivity.class.getName())) {
				Intent intent = new Intent(content, VWACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}
}
