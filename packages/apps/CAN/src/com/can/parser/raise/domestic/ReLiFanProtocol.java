package com.can.parser.raise.domestic;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReLiFanProtocol
 * 
 * @function:力帆协议
 * @author Kim
 * @Date:  2016-7-23下午12:01:14
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReLiFanProtocol extends ReProtocol{
	/**
	 * 协议数据类型定义力帆 ver1.01
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x03; // 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x28; // 基本信息
	public static final byte DATA_TYPE_RADAR_INFO = (byte) 0x22; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x30; // 方向盘
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; // 方向盘按键信息
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x02; // 方向盘按键信息

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
	public int GetWheelInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_INFO & 0xFF;
	}
	
	public int GetRadarCmdId(){
		return DATA_TYPE_RADAR_INFO & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	public int GetPanelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_PANEL_KEY_INFO & 0xFF;
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
		airInfo.mRearLight = (byte) ((airData0 >> 4) & 0x1);
		airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData0, 3);
		airInfo.mWindRate = (byte) (airData0 & 0x07);
		// Data 1
		cursor += 1;
		byte airData1 = packet[cursor];
		byte byWindMode = (byte) (airData1 & 0x7F);
		airInfo.mDisplay = 1;

		airInfo.mUpwardWind = 0;
		airInfo.mParallelWind = 0;
		airInfo.mDowmWind = 0;
		airInfo.mFWDefogger = 0;
		
		if (byWindMode == 1) {
			airInfo.mAutoWind = 1;
		}else if (byWindMode == 2) {
			airInfo.mFWDefogger = 1;
		}else if (byWindMode == 3) {
			airInfo.mDowmWind = 1;
		}else if (byWindMode == 4) {
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		}else if (byWindMode == 5) {
			airInfo.mUpwardWind = 1;
		}else if (byWindMode == 6) {
			airInfo.mFWDefogger = 1;
			airInfo.mUpwardWind = 1;
		}else if (byWindMode == 7) {
			airInfo.mFWDefogger = 1;
		}else if (byWindMode == 8) {
			airInfo.mDowmWind = 1;
			airInfo.mFWDefogger = 1;
		}else if (byWindMode == 9) {
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
			airInfo.mFWDefogger = 1;
		}
		
		airInfo.mMaxWindlv = 7;

		// Data 2
		cursor += 1;
		int airData2 = packet[cursor] & 0xFF;
		
		if (airData2 != 0xFF) {
			if (DataConvert.GetBit(airData2, 7) == 0) {
				airInfo.mbshowLTempLv = true;
				airInfo.mbyLeftTemplv = (byte) airData2;
			}else {
				airInfo.mbshowLTempLv = false;
				
				airInfo.mMaxTemp = (float) 32.5; 
				airInfo.mMinTemp = 18;
				airInfo.mLeftTemp = (float) (18 + (airData2-0x80)*0.5);
			}
		}

		cursor += 1;
		byte airData3 = (byte) packet[cursor];

		if (airData3 != 0xFF) {
			if (DataConvert.GetBit(airData3, 7) == 0) {
				airInfo.mbshowRTempLv = true;
				airInfo.mbyRightTemplv = (byte) airData3;
			}else {
				airInfo.mbshowRTempLv = false;
				
				airInfo.mMaxTemp = (float) 32.5;
				airInfo.mMinTemp = 18;
				airInfo.mRightTemp = (float) (18 + (airData3-0x80)*0.5);
			}
		}

		cursor += 1;
		byte airData4 = (byte) packet[cursor];
	    airInfo.mLeftHotSeatTemp = (byte) (( airData4 >> 4) & 0x0F);
	    airInfo.mRightHotSeatTemp = (byte) (airData4 & 0x0F);
	    
	    cursor += 1;
	    int airdata5 = (byte) packet[cursor];
	    if (airdata5 != 0xFF) {
	    	airInfo.mOutTempEnable = true;
	    	
	    	if (DataConvert.GetBit(airdata5, 7) == 1) {
				airInfo.mOutTemp = -(airdata5 & 0x7F);
			}else {
				airInfo.mOutTemp = (airdata5 & 0x7F);
			}
		}else {
			airInfo.mOutTempEnable = false;
		}

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!equals(mbyAirInfo, packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}
	
	
	private boolean equals(byte[] bylastInfo, byte[] byNewInfo) {

		int iIndex = 0;
		boolean bequals = true;

		for (byte b : byNewInfo) {
			if (iIndex == 8) {
			} else {

				if ((b != bylastInfo[iIndex]) && bequals) {
					bequals = false;
				}
			}
			iIndex++;
		}

		return bequals;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		baseInfo.mFrontBoxDoor = (byte) DataConvert.GetBit(baseData0, 2);
		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
	}
	
	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		// TODO Auto-generated method stub
		int cursor=0;	
		cursor += 3; //跳过 2E、Data Type、Length字段
		
		radarInfo.mBackLeftDis =((packet[cursor] & 0xFF) == 0xFF) ? 0 : (packet[cursor]);
		radarInfo.mBackLeftCenterDis = ((packet[cursor] & 0xFF) == 0xFF) ? 0 : (packet[cursor]);
		radarInfo.mBackRightCenterDis = ((packet[cursor+1] & 0xFF) == 0xFF) ? 0 : (packet[cursor+1]);
		radarInfo.mBackRightDis = ((packet[cursor+1] & 0xFF) == 0xFF) ? 0 : (packet[cursor+1]);
		
		radarInfo.mbyRightShowType = 0;
	
		return radarInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor+1];
		bydata[1] = packet[cursor];
		
		wheelInfo.mEps = DataConvert.byte2Short(bydata, 0);
		wheelInfo.mEps = (wheelInfo.mEps * 45 / 32736);

		return wheelInfo;
	}

	public WheelKeyInfo parsePanelKeyInfo(byte[] packet, WheelKeyInfo KeyInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		KeyInfo.mKeyCode = packet[cursor];
		KeyInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslatePanelKey(KeyInfo);
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
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x09:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x0a:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case 0x0b:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x0c:
			info.mstrKeyCode = DDef.Media_next;
			break;
		default:
			break;
		}

		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrkeycode;
			mstrkeycode = null;
		} else {
			mstrkeycode = info.mstrKeyCode;
		}

		return info;
	}

	public String mstrPanelkeycode = null;

	public WheelKeyInfo TranslatePanelKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Power;
			break;
		case 0x02:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x05:
			info.mstrKeyCode = DDef.Src_eq;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_radio;
			break;
		case 0x09:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x0a:
			info.mstrKeyCode = DDef.Num1;
			break;
		case 0x0b:
			info.mstrKeyCode = DDef.Num2;
			break;
		case 0x0c:
			info.mstrKeyCode = DDef.Num3;
			break;
		case 0x0d:
			info.mstrKeyCode = DDef.Num4;
			break;
		case 0x0e:
			info.mstrKeyCode = DDef.Num5;
			break;
		case 0x0f:
			info.mstrKeyCode = DDef.Num6;
			break;
		case 0x11:
			info.mstrKeyCode = DDef.Eject;
			break;
		case 0x16:
			info.mstrKeyCode = DDef.Enter;
			break;
		case 0x17:
			info.mstrKeyCode = DDef.Volume_add;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x18:
			info.mstrKeyCode = DDef.Volume_del;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x20:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		case 0x21:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x22:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		default:
			break;
		}

		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrPanelkeycode;
			mstrPanelkeycode = null;
		} else {
			mstrPanelkeycode = info.mstrKeyCode;
		}

		return info;
	}
}
