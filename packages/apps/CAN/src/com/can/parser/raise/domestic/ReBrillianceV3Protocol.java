package com.can.parser.raise.domestic;

import java.util.ArrayList;
import android.os.Message;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.parser.raise.dz.ReMQB6Protocol.Source;
import com.can.parser.raise.dz.ReMQB6Protocol.TunerBound;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReBrillianceV3Protocol
 * 
 * @function:中华V3协议
 * @author Kim
 * @Date: 2016-7-23上午11:21:23
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReBrillianceV3Protocol extends ReProtocol {

	/**
	 * 协议数据类型定义 中华 ver1.0
	 */
	// Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte) 0x03; // 空调信息
	public static final byte DATA_TYPE_BASE_INFO = (byte) 0x06; // 基本信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte) 0x7F; // 版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte) 0x0A; // 方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte) 0x02; // 方向盘按键信息

	// Host -> Slave
	public static final byte DATA_TYPE_REQUEST = (byte) 0xFF; // 请求控制信息
	public static final byte DATA_TYPE_SOURCE_REQUEST = (byte) 0x82; // Source
	public static final byte DATA_TYPE_TIME_SET = (byte) 0x85; // 时间设置
	public static final byte DATA_TYPE_BT_REQUEST = (byte) 0x88; // 蓝牙控制指令

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

	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过2E、Data Type、Length字段

		// Data 0
		byte airData0 = (byte) packet[cursor];
		airInfo.mAiron = (byte) ((airData0 >> 7) & 0x1);
		airInfo.mAcState = (byte) ((airData0 >> 6) & 0x1);
		airInfo.mCircleState = (byte) ((airData0 >> 5) & 0x1);
		airInfo.mFWDefogger = (byte) ((airData0 >> 1) & 0x1);
		airInfo.mRearLight = (byte) ((airData0 >> 0) & 0x1);

		// Data 1
		cursor += 1;
		byte airData1 = (byte) packet[cursor];
		airInfo.mUpwardWind = (byte) ((airData1 >> 7) & 0x1);
		airInfo.mParallelWind = (byte) ((airData1 >> 6) & 0x1);
		airInfo.mDowmWind = (byte) ((airData1) >> 5 & 0x1);
		airInfo.mDisplay = 1;
		airInfo.mWindRate = (byte) ((airData1) & 0x0F);

		// Data 2
		cursor += 1;
		byte airData2 = (byte) packet[cursor];
		if (airData2 == 0x00 || airData2 == 0x0E) {
			if (airData2 == 0x0E) {
				airInfo.mLeftTemp = 0xFF;
			}else {
				airInfo.mLeftTemp = airData2;
			}
		} else if ((airData2 & 0xFF) == 0x7F) {
			airInfo.mbshowLTemp = false;
		} else {
			airInfo.mLeftTemp = (float) (16 + airData2);
		}

		airInfo.mMinTemp = (float) 0;
		airInfo.mMaxTemp = (float) 0xFF;

		// Data 3
		cursor += 1;
		byte airData3 = (byte) packet[cursor];
		if (airData3 == 0x00 || airData3 == 0x0E) {
			if (airData3 == 0x0E) {
				airInfo.mRightTemp = 0xFF;			
			}else {
				airInfo.mRightTemp = airData3;		
			}
		} else if ((airData3 & 0xFF) == 0x7F) {
			airInfo.mbshowRTemp = false;
		} else {
			airInfo.mRightTemp = (float) (16 + airData3);
		}

		// Data 4
		cursor += 1;
		byte airData4 = (byte) packet[cursor];
		airInfo.mTempUnit = (byte) DataConvert.GetBit(airData4, 0);

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

		baseInfo.mTailBoxDoor = (byte) DataConvert.GetBit(baseData0, 3);
		baseInfo.mRightBackDoor = (byte) DataConvert.GetBit(baseData0, 5);
		baseInfo.mLeftBackDoor = (byte) DataConvert.GetBit(baseData0, 4);
		baseInfo.mRightFrontDoor = (byte) DataConvert.GetBit(baseData0, 7);
		baseInfo.mLeftFrontDoor = (byte) DataConvert.GetBit(baseData0, 6);

		return baseInfo;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
		int cursor = 3; // 跳过 2E、Data Type、Length字段

		wheelInfo.mEps = DataConvert.byte2Short(packet, cursor + 1);

		if (DataConvert.GetBit(packet[cursor], 0) == 1) {
			wheelInfo.mEps = -(wheelInfo.mEps * 45 / 0x1520);
		} else {
			wheelInfo.mEps = (wheelInfo.mEps * 45 / 0x1520);
		}

		return wheelInfo;
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
		case 0x06:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case 0x04:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case 0x03:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case 0x05:
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

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}

	public byte TranslateBand(byte byBand) {
		byte bybandEx = TunerBound.TUNER_FM;

		switch (byBand) {
		case 0x00:
			bybandEx = TunerBound.TUNER_FM1;
			break;
		case 0x01:
			bybandEx = TunerBound.TUNER_FM2;
			break;
		case 0x02:
			bybandEx = TunerBound.TUNER_FM3;
			break;
		case 0x03:
			bybandEx = TunerBound.TUNER_AM1;
			break;
		case 0x04:
			bybandEx = TunerBound.TUNER_AM2;
			break;
		default:
			break;
		}

		return bybandEx;
	}

	@Override
	public byte TranslateSource(int iSource) {
		// TODO Auto-generated method stub
		byte bySource = 0x00;

		switch (iSource) {
		case DDef.SOURCE_DEF.Src_radio:
			bySource = Source.TUNER;
			break;
		case DDef.SOURCE_DEF.Src_music:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_video:
			bySource = Source.USB;
			break;
		case DDef.SOURCE_DEF.Src_bluetooth:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_dvd:
			bySource = Source.DISC_CD_DVD;
			break;
		case DDef.SOURCE_DEF.Src_dvr:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_dtv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_atv:
			bySource = Source.TV;
			break;
		case DDef.SOURCE_DEF.Src_avin:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_ipod:
			bySource = Source.IPOD;
			break;
		case DDef.SOURCE_DEF.Src_phonelink:
			bySource = Source.OTHER;
			break;
		case DDef.SOURCE_DEF.Src_backcar:
			bySource = Source.AUX;
			break;
		case DDef.SOURCE_DEF.Src_bt_phone:
			bySource = Source.PHONE;
			break;
		case DDef.SOURCE_DEF.Src_photo:
			bySource = Source.USB;
			break;
		default:
			bySource = Source.OTHER;
			break;
		}

		return bySource;
	}

	private Message getMediaData(MediaInfo mediaInfo) {

		byte[] bydata = null;
		int iSource = mediaInfo.getSource();

		bydata = new byte[8];
		if (iSource == DDef.SOURCE_DEF.Src_radio) {

			bydata[0] = TranslateSource(iSource);
			bydata[1] = 0x01;
			bydata[2] = TranslateBand(mediaInfo.getBand());
			int iFreq = mediaInfo.getMainFreq();
			byte[] itmpFreq = DataConvert.Hi2Lo2Byte(iFreq);
			bydata[3] = itmpFreq[0];
			bydata[4] = itmpFreq[1];
			bydata[5] = mediaInfo.getFreqIndex();
		}

		return CanTxRxStub.getTxMessage(DATA_TYPE_SOURCE_REQUEST,
				DATA_TYPE_SOURCE_REQUEST, bydata, this);
	}

	private byte[] getTime(MediaInfo mediaInfo) {
		byte[] byData = new byte[7];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();

		byData[0] = (byte) (timeInfo.iYear - 2000);
		byData[1] = timeInfo.byMonth;
		byData[2] = timeInfo.byDay;
		byData[3] = timeInfo.byMinute;
		byData[4] = timeInfo.byHour;

		return byData;
	}

	@Override
	public Message getBtInfo(int iType, int iValue, MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		byte[] bydata = null;
		bydata = new byte[2];
		if (iType == 0x01) {
			switch (mediaInfo.getPhonestate()) {
			case DDef.PHONE_STATE_INCOMING:
				bydata[1] = 0x01;
				break;
			case DDef.PHONE_STATE_OUTGOING:
				bydata[1] = 0x02;
				break;
			case DDef.PHONE_STATE_SPEAKING:
				bydata[1] = 0x04;
				break;
			case DDef.PHONE_NONE:
			case DDef.PHONE_STATE_IDLE:
				bydata[1] = 0x05;
				break;
			default:
				break;
			}
		}

		return CanTxRxStub.getTxMessage(DATA_TYPE_BT_REQUEST,
				DATA_TYPE_BT_REQUEST, bydata, this);
	}

	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();

		aListMsg.add(getMediaData(mediaInfo));
		aListMsg.add(CanTxRxStub.getTxMessage(DATA_TYPE_TIME_SET,
				DATA_TYPE_TIME_SET, getTime(mediaInfo), this));

		return aListMsg;
	}
}
