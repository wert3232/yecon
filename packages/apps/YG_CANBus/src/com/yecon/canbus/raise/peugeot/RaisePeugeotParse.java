/**
 * @Title: RaisePeugeotParse.java
 * @Package com.yecon.canbus.raise.peugeot
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月15日 下午3:58:38
 * @version V1.0
 */
package com.yecon.canbus.raise.peugeot;

import android.content.Context;
import android.content.Intent;
import com.yecon.canbus.parse.CANBusParse;

/**
 * @ClassName: RaisePeugeotParse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月15日 下午3:58:38
 *
 */
public class RaisePeugeotParse extends CANBusParse {

	public static final int RAISE_PEUGEOT_DATACMD_AIR = 0x21;
	public static final int RAISE_PEUGEOT_DATACMD_CAR = 0x38;
	public static final int RAISE_PEUGEOT_DATACMD_TEMP = 0x36;
	
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
		ps.mPacketHead = 0xfd;
		ps.mPacketMinLength = 4;
		ps.mDataCmdPos = 2;
		ps.mDataLenPos = 1;
		ps.mDataOffset = 3;
		return ps;
	}

	@Override
	public int DataLength(int[] iPacketBuffer) {
		// TODO Auto-generated method stub
		return (super.DataLength(iPacketBuffer) - 3);
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
			case RAISE_PEUGEOT_DATACMD_AIR:
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
				getAir().setAirVolume((iDataBuffer[1] & 0x0f) | 0x0900); // 风量大小及最大值
				// data 2
				getAir().setLeftTemp(iDataBuffer[2]); // 左温度值 (实际温度值 = 温度值
															// *
															// 温度步进)
				// data 3
				getAir().setRightTemp(iDataBuffer[3]); // 右温度值 (实际温度值 = 温度值
															// *
															// 温度步进)
				// data 4
				getAir()
						.setFrontWindowDemisting(GetBit(iDataBuffer[4], 7)); // 前窗除雾
				getAir().setACMax(GetBit(iDataBuffer[4], 3)); // AC MAX
				getAir().setManuallyMoveState(GetBit(iDataBuffer[4], 1));
				getAir().setTempUnit(GetBit(iDataBuffer[4], 0)); // 温度单位
				break;
			case RAISE_PEUGEOT_DATACMD_CAR:
				// data 0
				getCar().setLeftFrontDoor(GetBit(iDataBuffer[0], 7)); // 左前门
				getCar().setRightFrontDoor(GetBit(iDataBuffer[0], 6)); // 右前门
				getCar().setLeftBackDoor(GetBit(iDataBuffer[0], 5)); // 左后门
				getCar().setRightBackDoor(GetBit(iDataBuffer[0], 4)); // 右后门
				getCar().setRearDoor(GetBit(iDataBuffer[0], 3)); // 后备箱
				getCar().setBonnet(GetBit(iDataBuffer[0], 2)); // 引擎盖
				getCar().setDriverBelts(GetBit(iDataBuffer[0], 1)); // 驾驶员安全带状态
				getCar().setSeatBelts(GetBit(iDataBuffer[0], 0)); // 副驾驶安全带状态
				// data 1
				getCar().setRearWiper(GetBit(iDataBuffer[1], 7)); // 后雨刷状态
				getCar().setOilLow(GetBit(iDataBuffer[1], 5)); // 燃油低警告状态
				getCar().setAutoLockDoor(GetBit(iDataBuffer[1], 4)); // 自动落锁功能状态
				getCar().setPackingAssist(GetBit(iDataBuffer[1], 3)); // 驻车辅助功能状态
				getCar().setCtrlDoor(GetBit(iDataBuffer[1], 2)); // 中控门锁状态
				getCar().setCurCarInfoPage(GetBit(iDataBuffer[1], 0)); // 当前的行车信息数据PAGE
				// data 2
				getCar().setDayLightLamp(GetBit(iDataBuffer[2], 7)); // 日间照明灯状态
				getCar()
						.setLampDeleyTime((iDataBuffer[2] >> 5) & 0x03); // 大灯延时
				// data 3
				getCar().setInsideLight((iDataBuffer[3] >> 5) & 0x07); // 车内氛围灯状态
				getCar().setReverseRadar(GetBit(iDataBuffer[3], 3)); // 倒车雷达状态
				getCar().setReverse(GetBit(iDataBuffer[3], 2)); // 倒车状态
				getCar().setParkOrBrake(GetBit(iDataBuffer[3], 1)); // P档或者手刹
																				// 0:P档
																				// 1:手刹
				getCar().setLamplet(GetBit(iDataBuffer[3], 0)); // 小灯状态
				// data 4
				getCar().setLight4Home((iDataBuffer[4] >> 6) & 0x03); // 伴我回家照明
				getCar().setLight4Guest((iDataBuffer[4] >> 4) & 0x03); // 迎宾照明
				getCar().setEQ((iDataBuffer[4] >> 1) & 0x03); // 音效设置
				getCar().setOilUnit(GetBit(iDataBuffer[4], 0)); // 油耗单位
																			// 0：KM/L-KM
																			// 1：L/100KM-KM
				// data 5
				getCar().setProbeBlindArea(GetBit(iDataBuffer[5], 7)); // 盲区设置
				getCar().setEnginStartStop(GetBit(iDataBuffer[5], 6)); // 发动机启停功能
				getCar().setWelcomeGuest(GetBit(iDataBuffer[5], 5)); // 迎宾功能开关
				getCar().setCtrlAllDoor(GetBit(iDataBuffer[5], 4)); // 车门开启选项
				getCar().setSetLanguage(iDataBuffer[5] & 0x0f); // 语言设置
				break;
			case RAISE_PEUGEOT_DATACMD_TEMP:
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
	 * <p>Title: LaunchCanbusUI</p>
	 * <p>Description: </p>
	 * @param content
	 * @param iDataCmd
	 * @see com.yecon.canbus.parse.CANBusParse#LaunchCanbusUI(android.content.Context, int)
	 */
	@Override
	public void LaunchCanbusUI(Context content, int iDataCmd) {
		// TODO Auto-generated method stub
		if (iDataCmd == RAISE_PEUGEOT_DATACMD_AIR) {
			if (!isForeground(content, PeugeotACActivity.class.getName())) {
				Intent intent = new Intent(content, PeugeotACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

}
