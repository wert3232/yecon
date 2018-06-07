/**
 * @Title: RaiseVWGolf7Parse.java
 * @Package com.yecon.canbus.raise.vw
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月18日 下午7:36:16
 * @version V1.0
 */
package com.yecon.canbus.raise.vw;

/**
 * @ClassName: RaiseVWGolf7Parse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月18日 下午7:36:16
 *
 */
public class RaiseVWGolf7Parse extends RaiseVWParse {

	public static final int RAISE_VW_GOLF7_DATACMD_CARSTATUS = 0x41;
	public static final int RAISE_VW_GOLF7_DATACMD_SPEED = 0x16;
	public static final int RAISE_VW_GOLF7_DATACMD_OUTSIDETEMP = 0x27;

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
				getAir().setFrontWindowDemisting(GetBit(iDataBuffer[4], 7));
				getAir().setRearWindowDemisting(GetBit(iDataBuffer[4], 6));
				getAir().setAQS(GetBit(iDataBuffer[4], 5));
				getAir().setECO(GetBit(iDataBuffer[4], 4));
				getAir().setACMax(GetBit(iDataBuffer[4], 3));
				getAir().setTempUnit(GetBit(iDataBuffer[4], 0));
				// data 5
				getAir().setHeatLeftSeat(iDataBuffer[5] & 0x30);
				getAir().setHeatRightSeat(iDataBuffer[5] & 0x03);
				break;
				
			case RAISE_VW_DATACMD_CAR:
				getCar().setRightFrontDoor(GetBit(iDataBuffer[0], 7));
				getCar().setLeftFrontDoor(GetBit(iDataBuffer[0], 6));
				getCar().setRightBackDoor(GetBit(iDataBuffer[0], 5));
				getCar().setLeftBackDoor(GetBit(iDataBuffer[0], 4));
				getCar().setRearDoor(GetBit(iDataBuffer[0], 3));
				getCar().setBonnet(GetBit(iDataBuffer[0], 2));
				break;
				
//			case RAISE_VW_GOLF7_DATACMD_OUTSIDETEMP:
//				
//				break;
				
			case RAISE_VW_GOLF7_DATACMD_SPEED:
				getCar().setTravelingSpeed((double)(iDataBuffer[1] << 8 | iDataBuffer[0])/16);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bRet;
	}
}
