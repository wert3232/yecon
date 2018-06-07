package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.TpmsWarn;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReZotyeT600Protocol
 * 
 * @function:众泰T600协议
 * @author Kim
 * @Date: 2016-7-23上午11:17:34
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReZotyeT600Protocol extends ReProtocol {
	/**
	 * 协议数据类型定义众泰T600 ver1.03
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x21; // 基本信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x24; // 基本信息
	public static final byte DATA_TYPE_TPMS_INFO = (byte) 0x38; // 胎压信息
	public static final byte DATA_TYPE_TPMS_WARN = (byte) 0x39; // 胎压信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x20; // 方向盘按键信息

	// Host -> Slave

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_AIR_INFO & 0xFF;
	}

	public int GetTpmsInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_TPMS_INFO & 0xFF;
	}

	public int GetTpmsWarnCmdId() {
		return DATA_TYPE_TPMS_WARN & 0xFF;
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
		airInfo.mAiron = (byte) DataConvert.GetBit(airData0, 7);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 5) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = packet[cursor];
		airInfo.mUpwardWind = (byte) DataConvert.GetBit(airData1, 7);
		airInfo.mParallelWind = (byte) DataConvert.GetBit(airData1, 6);
		airInfo.mDowmWind = (byte) DataConvert.GetBit(airData1, 5);
		airInfo.mWindRate = (byte) (airData1 & 0x07);
		airInfo.mDisplay = 1;
		airInfo.mMaxWindlv = 8;

		// Data 2
		cursor += 1;
		int airData2 = packet[cursor] & 0xFF;

		if (airData2 != 0xFF) {
			if (DataConvert.GetBit(airData2, 7) == 0) {
				airInfo.mbshowLTempLv = true;
				airInfo.mbyLeftTemplv = (byte) airData2;
			} else {
				airInfo.mbshowLTempLv = false;

				airInfo.mMaxTemp = 0x00;
				airInfo.mMinTemp = 0xff;

				if (airData2 == 0x00 || airData2 == 0x1f) {
					if (airData2 == 0x1f) {
						airInfo.mLeftTemp = 0xff;
					} else {
						airInfo.mLeftTemp = airData2;
					}
				} else {
					airInfo.mLeftTemp = (float) (17 + airData2);
				}
			}
		}

		cursor += 1;
		byte airData3 = (byte) packet[cursor];

		if (airData3 != 0xFF) {
			if (DataConvert.GetBit(airData3, 7) == 0) {
				airInfo.mbshowRTempLv = true;
				airInfo.mbyRightTemplv = (byte) airData3;
			} else {
				airInfo.mbshowRTempLv = false;

				airInfo.mMaxTemp = 0x00;
				airInfo.mMinTemp = 0xff;

				if (airData3 == 0x00 || airData3 == 0x1f) {
					if (airData3 == 0x1f) {
						airInfo.mRightTemp = 0xff;
					} else {
						airInfo.mRightTemp = airData3;
					}
				} else {
					airInfo.mRightTemp = (float) (17 + airData3);
				}
			}
		}

		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mFWDefogger = (byte) DataConvert.GetBit(airData4, 7);
		airInfo.mRearLight = (byte) DataConvert.GetBit(airData4, 6);

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

		baseInfo.mFrontBoxDoor = (byte) DataConvert.GetBit(baseData0, 2);
		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
	}

	public TPMSInfo parseTpmsInfo(byte[] packet, TPMSInfo info) {
		int icursor = 3;

		info.mFLTireTemp = packet[icursor] - 40;
		info.mFRTireTemp = packet[icursor + 1] - 40;
		info.mBLTireTemp = packet[icursor + 2] - 40;
		info.mBRTireTemp = packet[icursor + 3] - 40;

		double dFLTirePressure = ((packet[icursor + 4] & 0xFF) == 0xFF) ? 0
				: (packet[icursor + 4] * 1.373);
		info.mfFLTirePressure = String.format("%.2f", dFLTirePressure);

		double dFRTirePressure = ((packet[icursor + 5] & 0xFF) == 0xFF) ? 0
				: (packet[icursor + 5] * 1.373);
		info.mfFRTirePressure = String.format("%.2f", dFRTirePressure);

		double dBLTirePressure = ((packet[icursor + 6] & 0xFF) == 0xFF) ? 0
				: (packet[icursor + 6] * 1.373);
		info.mfBLTirePressure = String.format("%.2f", dBLTirePressure);

		double dBRTirePressure = ((packet[icursor + 7] & 0xFF) == 0xFF) ? 0
				: (packet[icursor + 7] * 1.373);
		info.mfBRTirePressure = String.format("%.2f", dBRTirePressure);

		return info;
	}

	public TpmsWarn parseTpmsWarn(byte[] packet, TpmsWarn info) {
		int cursor = 3;

		//胎压警告信息

		//前左传感器失效
		info.bFlSensorValid = (DataConvert.GetBit(packet[cursor], 7) == 1) ? true : false;
		//前左低压警报
		info.bFlLpressureW = (DataConvert.GetBit(packet[cursor], 6) == 1) ? true : false;
		//前左高压警报
		info.bFlHpressureW = (DataConvert.GetBit(packet[cursor], 5) == 1) ? true : false;
		//前右传感器失效
		info.bFrSensorValid = (DataConvert.GetBit(packet[cursor], 4) == 1) ? true : false;
		//前右低压警报
		info.bFrLpressureW = (DataConvert.GetBit(packet[cursor], 3) == 1) ? true : false;
		//前右高压警报
		info.bFrHpressureW = (DataConvert.GetBit(packet[cursor], 2) == 1) ? true : false;

		//后左传感器失效
		info.bRlSensorValid = (DataConvert.GetBit(packet[cursor+1], 7) == 1) ? true : false;
		//后左低压警报
		info.bRlLpressureW = (DataConvert.GetBit(packet[cursor+1], 6) == 1) ? true : false;
		//后左高压警报
		info.bRlHpressureW = (DataConvert.GetBit(packet[cursor+1], 5) == 1) ? true : false;
		//后右传感器失效
		info.bRrSensorValid = (DataConvert.GetBit(packet[cursor+1], 4) == 1) ? true : false;
		//后右低压警报
		info.bRrLpressureW = (DataConvert.GetBit(packet[cursor+1], 3) == 1) ? true : false;
		//后右高压警报
		info.bRrHpressureW = (DataConvert.GetBit(packet[cursor+1], 2) == 1) ? true : false;

		//系统失效
		info.bSysValid = (DataConvert.GetBit(packet[cursor+1], 0) == 1) ? true : false;

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
		case 0x05:
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
		} else {
			mstrkeycode = info.mstrKeyCode;
		}

		return info;
	}
}
