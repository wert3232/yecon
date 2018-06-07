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
 * ClassName:ReHimaM5Protocol
 * 
 * @function:TODO
 * @author Kim
 * @Date:  2016-8-4下午4:38:51
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReHimaM5Protocol extends ReProtocol {
	/**
	 * 协议数据类型定义 海马M5 ver1.42
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x23; // 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x28; // 基本信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x7F; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x30; // 方向盘
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; // 方向盘按键信息
	public static final byte DATA_TYPE_PANEL_KEY_INFO = (byte) 0x22; // 方向盘按键信息

	// Host -> Slave
	public static final byte DATA_TYPE_CAR_SET = (byte) 0x82; // 设置

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
		airInfo.mAutoLight1 = (byte) DataConvert.GetBit(airData0, 3);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mDisplay = (byte) DataConvert.GetBit(airData1, 4);
		airInfo.mWindRate = (byte) (airData1 & 0x0F);
		airInfo.mMaxWindlv = 8;
		airInfo.mUpwardWind = (byte) DataConvert.GetBit(airData1, 7);
		airInfo.mParallelWind = (byte) DataConvert.GetBit(airData1, 6);
		airInfo.mDowmWind = (byte) DataConvert.GetBit(airData1, 5);

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		byte airData4 = (byte) packet[cursor + 2];
		airInfo.mTempUnit = (byte) DataConvert.GetBit(airData4, 0);

		if (airData2 == 0x00 || airData2 == 0x1F) {
			airInfo.mLeftTemp = airData2;
		} else {
			if (airInfo.mTempUnit == 0) {
				if (airData2 >= 0x01 && airData2 <= 0x1C) {
					airInfo.mLeftTemp = (float) (16 + (airData2 - 1) * 0.5);
				} else if (airData2 >= 0x20 && airData2 <= 0x24) {
					airInfo.mLeftTemp = (float) (0x20 + (airData2 - 0x20) * 0.5);
				}
			} else {
				airInfo.mLeftTemp = airData2 + 59;
			}
		}

		airInfo.mMaxTemp = 0x1F;
		airInfo.mMinTemp = 0x00;

		cursor += 1;
		byte airData3 = (byte) packet[cursor];

		if (airData3 == 0x00 || airData3 == 0x1F) {
			airInfo.mRightTemp = airData3;
		} else {
			if (airInfo.mTempUnit == 0) {
				if (airData3 >= 0x01 && airData3 <= 0x1C) {
					airInfo.mRightTemp = (float) (16 + (airData3 - 1) * 0.5);
				} else if (airData3 >= 0x20 && airData3 <= 0x24) {
					airInfo.mRightTemp = (float) (0x20 + (airData3 - 0x20) * 0.5);
				}
			} else {
				airInfo.mRightTemp = airData3 + 59;
			}
		}

		airInfo.mFWDefogger = (byte) ((airData4 >> 7) & 0x1);
		airInfo.mRearLight = (byte) ((airData4 >> 6) & 0x1);

		if (mbyAirInfo != null) {

			airInfo.bAirUIShow = (!mbyAirInfo.equals(packet)
					&& (airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true
					: false;
		}

		mbyAirInfo = packet;

		return airInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		if (DataConvert.GetBit(baseData0, 0) == 1) {
			baseInfo.mFrontBoxDoor = (byte) DataConvert.GetBit(baseData0, 2);
			baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
			baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
			baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
			baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
			baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);
		}

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

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段
		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor);
		if (DataConvert.GetBit(packet[cursor+1], 7) == 1) {
			wheelInfo.mEps = (((-wheelInfo.mEps) - 0x8000) * 45 /5400);
		}else {
			wheelInfo.mEps = (wheelInfo.mEps * 45 / 5400);
		}

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
		case 0x05:
			info.mstrKeyCode = DDef.Src_bt;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x04:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x08:
			info.mstrKeyCode = DDef.Volume_mute;
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
		case 0x0d:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case 0x0e:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case 0x0f:
			info.mstrKeyCode = DDef.Enter;
			break;
		case 0x17:
			info.mstrKeyCode = DDef.Src_home;
			break;
		case 0x18:
			info.mstrKeyCode = DDef.Blacklight;
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
		case 0x04:
			info.mstrKeyCode = DDef.Src_set;
			break;
		case 0x05:
			info.mstrKeyCode = DDef.Src_eq;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Back;
			break;
		case 0x07:
			info.mstrKeyCode = DDef.Src_radio;
			break;
		case 0x08:
			info.mstrKeyCode = DDef.Src_dvd;
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
		case 0x10:
			info.mstrKeyCode = DDef.Num7;
			break;
		case 0x11:
			info.mstrKeyCode = DDef.Num8;
			break;
		case 0x12:
			info.mstrKeyCode = DDef.Num9;
			break;
		case 0x13:
			info.mstrKeyCode = DDef.Num0;
			break;
		case 0x14:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		case 0x15:
			info.mstrKeyCode = DDef.Eject;
			break;
		case 0x16:
			info.mstrKeyCode = DDef.Src_info;
			break;
		case 0x17:
			info.mstrKeyCode = DDef.Src_set;
			break;
		case 0x18:
			info.mstrKeyCode = DDef.Src_navi;
			break;
		case 0x19:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		case 0x20:
			info.mstrKeyCode = DDef.Enter;
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
		case 0x23:
			info.mstrKeyCode = DDef.Media_next;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x24:
			info.mstrKeyCode = DDef.Media_pre;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x25:
			info.mstrKeyCode = DDef.Media_pause;
			break;
		case 0x26:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x27:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x28:
			info.mstrKeyCode = DDef.Src_aux;
			break;
		case 0x29:
			info.mstrKeyCode = DDef.Media_next;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x30:
			info.mstrKeyCode = DDef.Media_pre;
			info.mKnobSteps = info.mKeyStatus;
			info.mKeyStatus = CanKey.KEY_KNOB;
			break;
		case 0x31:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case 0x32:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case 0x33:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case 0x34:
			info.mstrKeyCode = DDef.Src_home;
			break;
		case 0x35:
			info.mstrKeyCode = DDef.Radio_as;
			break;
		case 0x36:
			info.mstrKeyCode = DDef.Src_home;
			break;
		case 0x37:
			info.mstrKeyCode = DDef.Src_bt;
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
