package com.can.parser.raise.domestic;

import com.can.parser.DDef;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.TPMSInfo;
import com.can.parser.DDef.TpmsWarn;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.tool.DataConvert;

/**
 * ClassName:ReJacRefineS3Protocol
 * 
 * @function:江淮瑞风S3
 * @author Kim
 * @Date:  2016-7-23下午2:13:31
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReJacRefineS3Protocol extends ReProtocol{
	/**
	 * 协议数据类型定义 江淮瑞风S3 ver1.21
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x28; 		// 基本信息
	public static final byte DATA_TYPE_TPMS_INFO = (byte)0x38;			// 胎压信息
	public static final byte DATA_TYPE_TPMS_WARN = (byte)0x39;			// 胎压信息
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x21; 	// 方向盘按键信息

	// Host -> Slave
	public static final byte DATA_TYPE_TIME_SET = (byte) 0x82; 			// 时间设置
	

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_BASE_INFO & 0xFF;
	}

	public int GetTpmsInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_TPMS_INFO & 0xFF;
	}
	
	public int GetTpmsWarnCmdId(){
		return DATA_TYPE_TPMS_WARN & 0xFF;
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return DATA_TYPE_WHEEL_KEY_INFO & 0xFF;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		// Data0
		byte baseData0 = (byte) packet[cursor];

		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
	}
	
	public TPMSInfo parseTpmsInfo(byte[] packet, TPMSInfo info){
		int icursor = 3;
		
		info.mFLTireTemp = packet[icursor] - 40;
		info.mFRTireTemp = packet[icursor+1] - 40;
		info.mBLTireTemp = packet[icursor+2] - 40;
		info.mBRTireTemp = packet[icursor+3] - 40;
		
		double dFLTirePressure = (packet[icursor+4]*0.02745);
		info.mfFLTirePressure = String.format("%.2f", dFLTirePressure);
		
		double dFRTirePressure = (packet[icursor+5]*0.02745);
		info.mfFRTirePressure = String.format("%.2f", dFRTirePressure);
		
		double dBLTirePressure = (packet[icursor+6]*0.02745);
		info.mfBLTirePressure = String.format("%.2f", dBLTirePressure);
		
		double dBRTirePressure = (packet[icursor+7]*0.02745);
		info.mfBRTirePressure = String.format("%.2f", dBRTirePressure);
		
		return info;
	}
	
	public TpmsWarn parseTpmsWarn(byte[] packet, TpmsWarn info){
		int cursor = 3;
		
		info.mbyFLTireWarn = packet[cursor];
		info.mbyFRTireWarn = packet[cursor+1];
		info.mbyBLTireWarn = packet[cursor+2];
		info.mbyBRTireWarn = packet[cursor+3];
		
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
		case 0x0c:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x0b:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
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
