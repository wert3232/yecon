package com.can.parser.raise.domestic;

import com.can.assist.CanKey;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReChanaV7Protocol
 * 
 * @function:长安锐翔V7
 * @author Kim
 * @Date: 2016-7-23上午11:42:54
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReChanaV7Protocol extends ReProtocol {
	/**
	 * 协议数据类型定义 长安锐翔V7 ver1.01
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x23; // 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x28; // 基本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x30; // 方向盘转角
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x7F; // 版本信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; // 方向盘按键信息
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x22; // 方向盘按键信息
	// Host -> Slave
	public static final byte DATA_TYPE_CAR_SET = (byte) 0x85; // 设置

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

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}
	
	public int GetPanelKeyCmdId(){
		return DATA_TYPE_PANEL_KEY_INFO & 0xFF;
	}

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		
		airInfo.mUpwardWind = 0;
		airInfo.mParallelWind = 0;
		airInfo.mDowmWind = 0;
		airInfo.mFWDefogger = 0;
		
		if (airData1 == 1) {
			airInfo.mUpwardWind = 1;
		} else if (airData1 == 2) {
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		} else if (airData1 == 3) {
			airInfo.mDowmWind = 1;
		} else if (airData1 == 5) {
			airInfo.mFWDefogger = 1;
		} else if (airData1 == 4) {
			airInfo.mFWDefogger = 1;
			airInfo.mDowmWind = 1;
		} else if (airData1 == 6) {
			airInfo.mFWDefogger = 1;
			airInfo.mUpwardWind = 1;
		} else if (airData1 == 7) {
			airInfo.mFWDefogger = 1;
			airInfo.mUpwardWind = 1;
			airInfo.mDowmWind = 1;
		}

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];

		airInfo.mDisplay = 1;
		airInfo.mMaxWindlv = 8;
		airInfo.mWindRate = airData2;
		airInfo.mAiron = (byte) ((airInfo.mWindRate == 0) ? 0 : 1);

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!mbyAirInfo.equals(packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		byte[] bydata = new byte[2];
		bydata[0] = packet[cursor+1];
		bydata[1] = packet[cursor];
		
		wheelInfo.mEps = DataConvert.byte2Short(bydata, 0);
		wheelInfo.mEps = (wheelInfo.mEps * 45 / 6000);

		return wheelInfo;
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
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mKeyCode = packet[cursor];
		wheelInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslateKey(wheelInfo);
	}
	
	public WheelKeyInfo parsePanelKeyInfo(byte[] packet, WheelKeyInfo KeyInfo){
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		KeyInfo.mKeyCode = packet[cursor];
		KeyInfo.mKeyStatus = (byte) packet[cursor + 1];

		return TranslatePanelKey(KeyInfo);
	}

	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x02:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x0c:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x0b:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		default:
			break;
		}

		return info;
	}
	
	public String mstrkeycode = null;
	
	public WheelKeyInfo TranslatePanelKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub

		switch (info.mKeyCode) {
		case 0x01:
			info.mstrKeyCode = DDef.Power;
			break;
		case 0x05:
			info.mstrKeyCode = DDef.Src_eq;
			break;
		case 0x21:
			info.mstrKeyCode = DDef.Volume_add;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x22:
			info.mstrKeyCode = DDef.Volume_del;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x29:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x30:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x35:
			info.mstrKeyCode = DDef.Src_set;
			break;
		case 0x36:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		case 0x37:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x38:
			info.mstrKeyCode = DDef.Radio_am;
			break;
		case 0x39:
			info.mstrKeyCode = DDef.Radio_fm;
			break;
		case 0x40:
			info.mstrKeyCode = DDef.Blacklight;
			break;
		case 0x41:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		case 0x42:
			info.mstrKeyCode = DDef.Src_home;
			break;
		case 0x43:
			info.mstrKeyCode = DDef.Media_next;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x44:
			info.mstrKeyCode = DDef.Media_pre;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
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
