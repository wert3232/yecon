package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.OutTemputerInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReHimaM8Protocol
 * 
 * @function:海马M8协议
 * @author Kim
 * @Date:  2016-7-23下午2:43:56
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReHimaM8Protocol extends ReProtocol{
	/**
	 * 协议数据类型定义 M8 ver1.21
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x21; 			// 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; 		// 基本信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte)0x22;	// 后雷达信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0xFF; 		// 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x26; 		// 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; 	// 方向盘按键信息
	public static final byte DATA_TYPE_OUT_TEMP = (byte) 0x27; 			// 室外温度

	// Host -> Slave

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
	public int GetWheelInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}
	
	public int GetOutTempCmdId(){
		return DATA_TYPE_OUT_TEMP & 0xFF;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) DataConvert.GetBit(airData0, 7);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 5) & 0x1);
		airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData0, 4);
		airInfo.mDaulLight = (byte) DataConvert.GetBit(airData0, 2);
		airInfo.mFWDefogger = (byte) ((airData0 >> 1) & 0x1);
		airInfo.mRearLight = (byte) ((airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		
		airInfo.mUpwardWind = 0;
		airInfo.mParallelWind = 0;
		airInfo.mDowmWind = 0;
		
		byte byWindmode = (byte) ((airData1 >> 4) & 0x0F);
		
		if (byWindmode == 0) {

		}else if (byWindmode == 1) {
			airInfo.mParallelWind = 1;
		}else if (byWindmode == 2) {
			airInfo.mParallelWind = 1;
			airInfo.mDowmWind = 1;
		}else if (byWindmode == 3) {
			airInfo.mDowmWind = 1;
		}else if (byWindmode == 4) {
			airInfo.mFWDefogger = 1;
			airInfo.mDowmWind = 1;
		}
		
		airInfo.mDisplay = 1;
		airInfo.mWindRate = (byte) (airData1 & 0x0F);
		airInfo.mMaxWindlv = 7;

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		if (airData2 == 0x00 || airData2 == 0x1F) {
			if (airData2 == 0x1F) {
				airInfo.mLeftTemp = 0xFF;
			}else {
				airInfo.mLeftTemp = airData2;
			}

		}else {
			airInfo.mLeftTemp = (float) (18 + (airData2-1)*0.5);
		}
		
		airInfo.mMinTemp = 0x00;
		airInfo.mMaxTemp = 0xFF;
		
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		
		if (airData3 == 0x00 || airData3 == 0x1F) {
			if (airData3 == 0x1F) {
				airInfo.mRightTemp = 0xFF;
			}else {
				airInfo.mRightTemp = airData3;			
			}
		}else {
			airInfo.mRightTemp = (float) (18 + (airData3-1)*0.5);
		}
		
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
		cursor += 3; //跳过 2E、Data Type、Length字段
		
		radarInfo.mBackLeftDis =packet[cursor];
		radarInfo.mBackLeftCenterDis = packet[cursor+1];
		radarInfo.mBackRightCenterDis = packet[cursor+2];
		radarInfo.mBackRightDis = packet[cursor+3];
		
		radarInfo.mbyRightShowType = 0;
	
		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor+1];

		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 2);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 1);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 0);

		return baseInfo;
	}
	
	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor+1];
		bydata[1] = packet[cursor];
		
		wheelInfo.mEps = DataConvert.byte2Short(bydata, 0);
		if (DataConvert.GetBit(packet[cursor], 7) == 1) {
			wheelInfo.mEps = -(((-wheelInfo.mEps) - 0x8000) * 45 /540);
		}else {
			wheelInfo.mEps = -(wheelInfo.mEps * 45 / 540);
		}

		return wheelInfo;
	}
	
	public OutTemputerInfo parseOutTempterInfo(byte[] packet, OutTemputerInfo info){
		int cursor = 3;
		info.mbEnable = true;
		
		if (DataConvert.GetBit(packet[cursor], 7) == 1) {
			info.mOutCTemp = -(packet[cursor] & 0x7F);
		}else {
			info.mOutCTemp = (packet[cursor] & 0x7F);
		}
		
		return info;
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
			info.mstrKeyCode = DDef.Src_bt;
			break;
		case 0x04:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
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
