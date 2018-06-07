/**
 * @Title: RaiseGC7Parse.java
 * @Package com.yecon.canbus.raise.mazda
 * @Description: TODO
 * Copyright: Copyright (c) 2011 
 * Company:深圳市益光实业有限公司
 * 
 * @author hzGuo
 * @date 2016年4月19日 下午1:50:39
 * @version V1.0
 */
package com.yecon.canbus.raise.mazda;

import android.content.Context;
import android.content.Intent;

/**
 * @ClassName: RaiseGC7Parse
 * @Description: TODO
 * @author hzGuo
 * @date 2016年4月19日 下午1:50:39
 *
 */
public class RaiseGC7Parse extends RaiseMazdaParse {
	public static final int RAISE_MAZDA_DATACMD_GC7AIR = 0x13;
	
	@Override
	public boolean ParseData(int iDataCmd, int[] iDataBuffer) {
		// TODO Auto-generated method stub
		boolean bRet = false;
		if (iDataCmd == RAISE_MAZDA_DATACMD_GC7AIR) {
			bRet = true;
			// data 0
			int iTemp = iDataBuffer[0] * 2 + (iDataBuffer[3]>0 ? 1 : 0) ;
			getAir().setLeftTemp(iTemp);
			getAir().setRightTemp(iTemp);
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
		}
		return bRet;
	}

	@Override
	public void LaunchCanbusUI(Context content, int iDataCmd) {
		// TODO Auto-generated method stub
		if (iDataCmd == RAISE_MAZDA_DATACMD_GC7AIR) {
			if (!isForeground(content, MazdaACActivity.class.getName())) {
				Intent intent = new Intent(content, MazdaACActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				content.startActivity(intent);
			}
		}
	}

	
	
}
