package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReChanaCs75Protocol
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-7-23下午4:33:08
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReChanaCs75Protocol extends ReProtocol{
	/**
	 * 协议数据类型定义 长安Cs75 ver1.42
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x21; 			// 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x3A; 		// 基本信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte)0x22;	// 后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = (byte)0x23;	// 前雷达信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0xFF; 		// 版本信息
	public static final byte DATA_TYPE_CAR_VIDEO = (byte) 0x30; 		// 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; 	// 方向盘按键信息

	// Host -> Slave
	public static final byte DATA_TYPE_CAR_SET = (byte) 0x85; 			// 设置

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BACK_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_FRONT_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
	
		airInfo.mAcState = (byte) ((airData0 >> 3) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 2) & 0x1);
		airInfo.mFWDefogger = (byte) ((airData0 >> 1) & 0x1);
		airInfo.mRearLight = (byte) ((airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mDisplay = 1;
		airInfo.mWindRate = airData1;
		airInfo.mMaxWindlv = 8;
		airInfo.mAiron = (byte) ((airInfo.mWindRate == 0) ? 0 : 1);

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		airInfo.mUpwardWind = 0;
		airInfo.mParallelWind = 0;
		airInfo.mDowmWind = 0;
		
		if (airData2 == 0) {
			airInfo.mUpwardWind = 1;
		}else if (airData2 == 1) {
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		}else if (airData2 == 2) {
			airInfo.mDowmWind = 1;
		}else if (airData2 == 3) {
			airInfo.mFWDefogger = 1;
		}else if (airData2 == 4) {
			airInfo.mFWDefogger = 1;
			airInfo.mDowmWind = 1;
		}
		
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		
		airInfo.mbshowLTempLv = true;
		airInfo.mbshowRTempLv = true;
		airInfo.mbyLeftTemplv = airData3;
		airInfo.mbyRightTemplv = airData3;
		
		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!mbyAirInfo.equals(packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor=0;
		byte DataType = (byte)packet[cursor+1];		
		cursor += 3; //跳过 2E、Data Type、Length字段
		
		if(DataType == DATA_TYPE_BACK_RADAR_INFO){
			
			radarInfo.mBackLeftDis =packet[cursor];
			radarInfo.mBackLeftCenterDis = packet[cursor+1];
			radarInfo.mBackRightCenterDis = packet[cursor+2];
			radarInfo.mBackRightDis = packet[cursor+3];
		}else if(DataType == DATA_TYPE_FRONT_RADAR_INFO){
			radarInfo.mFrontLeftDis =packet[cursor];
			radarInfo.mFrontLeftCenterDis = packet[cursor+1];
			radarInfo.mFrontRightCenterDis = packet[cursor+2];
			radarInfo.mFrontRightDis = packet[cursor+3];
		}
		
		radarInfo.mbyRightShowType = 0;
	
		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		baseInfo.mFrontBoxDoor = (byte) DataConvert.GetBit(baseData0, 0);
		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 1);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 2);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 5);

		return baseInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}
	
	public String mstrkeycode = null;
	
	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x02:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case 0x05:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x04:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x08:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		default:
			break;
		}
		
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrkeycode;
			mstrkeycode = null;
		}else {
			mstrkeycode = info.mstrKeyCode;
		}

		return info;
	}
}
