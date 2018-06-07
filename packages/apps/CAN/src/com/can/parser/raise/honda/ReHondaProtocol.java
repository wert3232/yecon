package com.can.parser.raise.honda;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import android.os.Message;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.CarSetting;
import com.can.parser.DDef.CompassInfo;
import com.can.parser.DDef.FuelMilInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.TimeInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

public class ReHondaProtocol extends ReProtocol{

    // Slave -> Host
    public static final byte DATA_TYPE_AIR_INFO = 0x21; 
    public static final byte DATA_TYPE_CAR_INFO = 0x32;  
    public static final byte DATA_TYPE_BACK_RADAR_INFO = 0x22;
    public static final byte DATA_TYPE_FRONT_RADAR_INFO = 0x23;
    public static final byte DATA_TYPE_BASE_INFO = 0x24; 
    public static final byte DATA_TYPE_PARKASSIST_INFO = 0x25; 
    public static final byte DATA_TYPE_VERSION_INFO = 0x30;
    public static final byte DATA_TYPE_WHEEL_INFO = 0x29;
    public static final byte DATA_TYPE_WHEEL_KEY_INFO = 0x20;
    public static final byte DATA_TYPE_BACK_LIGHT_INFO = 0x14;

    public static final byte DATA_TYPE_FUEL_MIL_INFO = 0x33;
    public static final byte DATA_TYPE_TIMESETUP_INFO = (byte) 0xD1;
    public static final int DATA_TYPE_COMPASS_INFO =  0xD2 & 0xFF;
    public static final byte DATA_TYPE_BLUETOOTHCMD_INFO = (byte) 0xD3;
    // Host -> Slave
    public static final byte DATA_TYPE_CAN_START_END_REQUEST    = (byte) 0x81;
    public static final byte DATA_TYPE_CAR_SETTING_REQUEST      = (byte) 0xC6;
    public static final byte DATA_TYPE_CAR_INFO_REQUEST = (byte) 0x90;
    public static final byte DATA_TYPE_SOURCE_REQUEST = (byte) 0xC0;
    public static final byte DATA_TYPE_ICON_REQUEST = (byte) 0xC1;
    public static final byte DATA_TYPE_RADIO_REQUEST = (byte) 0xC2;
    public static final byte DATA_TYPE_MEDIA_PLAYER_REQUEST = (byte) 0xC3; 
    public static final byte DATA_TYPE_VOLUME_REQUEST = (byte) 0xC4;
    public static final byte DATA_TYPE_USBIPOD_PLAY_REQUEST = (byte) 0xC5;
    public static final byte DATA_TYPE_COMPASS_REQUEST = (byte) 0xC5;
    public static final byte DATA_TYPE_ID3_INFO = (byte) 0xCB;
    // 源定义
    public class Source {
        public static final byte TUNER = 0x01; 				// Tuner
        public static final byte DISC_CD_DVD = 0x02; 		// CD、DVD
        public static final byte TV = 0x03; 				// TV
        public static final byte NAVI = 0x04; 				// NAVI
        public static final byte PHONE = 0x05; 				// Phone
        public static final byte IPOD = 0x06; 				// IPOD
        public static final byte AUX = 0x07; 				// Aux
        public static final byte USB = 0x08; 				// USB
        public static final byte SD = 0x09; 				// SD
        public static final byte DVB_T = 0x0A; 				// DVB-T
        public static final byte PHONE_A2DP = 0x0b; 		// Phone A2DP
        public static final byte OTHER = 0x0c; 				// 0x0c
        public static final byte CDC = 0x0D; 				// CDC
    }

    // 媒体类型
    public class MediaType {
        public static final byte TUNER = 0x01; 		// Tuner
        public static final byte SAM = 0x10; 		// Simple Audio Media
        public static final byte EAM = 0x11; 		// Enhanced Audio Media
        public static final byte IPOD = 0x12; 		// IPod
        public static final byte FBV = (byte) 0x20; 		// File Based Video
        public static final byte DVDV = (byte) 0x21; 		// DVD Video
        public static final byte OV = (byte) 0x22; 			// Other Video
        public static final byte AUX = (byte) 0x30; 		// Aux other
        public static final byte PHONE = (byte) 0x40; 		// Phone
    }

    // 收音机波段
    public class TunerBound {
        public static final byte TUNER_FM 	= 0x00;
        public static final byte TUNER_FM1 	= 0x01;
        public static final byte TUNER_FM2 	= 0x02;
        public static final byte TUNER_FM3	= 0x03;
        public static final byte TUNER_AM 	= 0x10;
    }

    // 通话状态
    public class PhoneState {
        public static final byte POHNE_NOT_ACTIVE 	=  0x00;
        public static final byte PHONE_RINGING 		=  0x01;
        public static final byte PHONE_DIALING 		=  0x02;
        public static final byte PHONE_CONNECTED 	=  0x03;
    }

    // 按键类型
    public class KeyCode {
        public static final byte KEY_CODE_NO_KEY = 0x00; 	// 无按键
        public static final byte KEY_CODE_VOL_UP = 0x01; 	// vol+
        public static final byte KEY_CODE_VOL_DOWN = 0x02;	// vol-
        public static final byte KEY_CODE_MENU_UP = 0x03;	
        public static final byte KEY_CODE_MENU_DOWN = 0x04; 
//      public static final byte KEY_CODE_TEL = 0x05;
//      public static final byte KEY_CODE_MUTE = 0x06;
        public static final byte KEY_CODE_SRC = 0x07; 		// 
        public static final byte KEY_CODE_SPEECH = 0x08; 		// SPEECH
        public static final byte KEY_CODE_PICKUP = 0x09; 	
        public static final byte KEY_CODE_HUNGUP = 0x0A;
//        public static final byte KEY_CODE_MIC_HOLD = 0x0B; 
//        public static final byte KEY_CODE_PICKUP_HOLD = 0x0C; 
//        public static final byte KEY_CODE_HANGUP_HOLD = 0x0D;
        public static final byte KEY_CODE＿UP = 0x13;		// 16款思域
        public static final byte KEY_CODE_DOWN = 0x14;		// 16款思域
        public static final byte KEY_CODE_ENTER = 0x16;		// 16款思域
        public static final byte KEY_CODE_MENU = 0x17; 		// 15款锋范 16款凌派为静音功能	
        public static final byte KEY_CODE_DISP = 0x18; 
        public static final byte KEY_CODE_CAMERA = 0x29; 	// 歌诗图有,切换右视
        public static final byte KEY_CODE_MUTE = 0x30;
        public static final byte KEY_CODE_VOL_UP_SY = (byte) 0x81; // 16款思域
        public static final byte KEY_CODE_VOL_DOWN_SY = (byte) 0x82;// 16款思域
        public static final byte KEY_CODE_MUTE_SY = (byte) 0x86;	// 16款思域
    }

	@Override
	public byte[] connect() {
		return super.connect();
	}
	
	@Override
	public int GetAirInfoCmdId() {
		return (int) (DATA_TYPE_AIR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoCmdId() {
		return (int) (DATA_TYPE_CAR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoRequestCmdId() {
		return (int) (DATA_TYPE_CAR_INFO_REQUEST & 0xFF);
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		return (int) (DATA_TYPE_BACK_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		return (int) (DATA_TYPE_FRONT_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetBaseInfoCmdId() {
		return (int) (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetParkAssistCmdId() {
		return  (int) (DATA_TYPE_PARKASSIST_INFO & 0xFF);
	}

	@Override
	public int GetVersionInfoCmdId() {
		return (int) (DATA_TYPE_VERSION_INFO & 0xFF);
	}

	@Override
	public int GetWheelInfoCmdId() {
		return (int) (DATA_TYPE_WHEEL_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		return (int) (DATA_TYPE_WHEEL_KEY_INFO & 0xFF);
	}

	@Override
	public int GetBackLightInfoCmdId() {
		return (int) (DATA_TYPE_BACK_LIGHT_INFO & 0xFF);
	}

    private String mstrKeyCode = null;

	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {

		switch (info.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_DOWN:
		case KeyCode.KEY_CODE_VOL_DOWN_SY:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_VOL_UP:
		case KeyCode.KEY_CODE_VOL_UP_SY:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_SPEECH:
			info.mstrKeyCode = DDef.Speech;
			break;
		case KeyCode.KEY_CODE_MENU_UP:
		case KeyCode.KEY_CODE＿UP:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_MENU_DOWN:
		case KeyCode.KEY_CODE_DOWN:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_SRC:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_MUTE:
		case KeyCode.KEY_CODE_MUTE_SY:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_PICKUP:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		case KeyCode.KEY_CODE_HUNGUP:
			info.mstrKeyCode = DDef.Phone_hang;
			break;
		case KeyCode.KEY_CODE_CAMERA:
			// 右视
			break;
		case KeyCode.KEY_CODE_ENTER:
			info.mstrKeyCode = DDef.Enter;
			break;
		case KeyCode.KEY_CODE_DISP:
			info.mstrKeyCode = DDef.Blacklight;
			break;
		case KeyCode.KEY_CODE_MENU:
			// 15款锋范 16款凌派为静音功能	
			info.mstrKeyCode = DDef.Src_home;
			break;
		default:
			break;
		}
		if (info.mKeyCode != 0) {
			mstrKeyCode = info.mstrKeyCode;
		}
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}
		return info;
	}

	@Override
	public byte TranslateSource(int iSource) {
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

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
        return null;
	}

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		int cursor = 3; //跳过2E、Data Type、Length字段
        
        //Data 0 
        byte airData0 = (byte)packet[cursor]; 
        airInfo.mAiron = (byte) (( airData0 >> 7) & 0x1);   
        airInfo.mAcState = (byte)(( airData0 >> 6) & 0x1);       
        airInfo.mCircleState = (byte)(( airData0 >> 5) & 0x1);      
        airInfo.mSync = (byte)(( airData0 >> 4) & 0x1);       
        airInfo.mAutoLight2 = (byte)(( airData0 >> 3) & 0x1);       
        airInfo.mDaulLight = (byte)(( airData0 >> 2) & 0x1);       
        airInfo.mRearLight = (byte)(( airData0 >> 0) & 0x1);
        
        //Data 1
        cursor += 1;
        byte airData1 = (byte)packet[cursor]; 
        airInfo.mUpwardWind = (byte) (( airData1 >> 7) & 0x1);
        airInfo.mParallelWind = (byte)((airData1 >> 6) & 0x1);
        airInfo.mDowmWind = (byte)((airData1) >> 5 & 0x1);
        airInfo.mDisplay = (byte)((airData1) >> 4 & 0x1);
        airInfo.mWindRate = (byte)((airData1) & 0x07);
        
        //Data 4  
        byte airData4 = (byte)packet[cursor+3];
        airInfo.mFWDefogger = (byte) (( airData4 >> 7) & 0x1);
        airInfo.mClimate = (byte) (airData4 >> 6 & 0x01);
        airInfo.mRearLock = (byte) (( airData4 >> 2) & 0x1);
        airInfo.mManual = (byte)(( airData4 >> 1) & 0x1);
        airInfo.mTempUnit = (byte)(airData4 & 0x1);
        
        //Data 2
        cursor += 1;
        byte airData2 = (byte)packet[cursor];
        if (airInfo.mTempUnit == 0x00) {
        	airInfo.mLeftTemp = (float)(airData2 * 0.5);
		} else {
			airInfo.mLeftTemp = airData2;
		}
        
        
        //Data 3 
        cursor += 1;
        byte airData3 = (byte)packet[cursor];
        if (airInfo.mTempUnit == 0x00) {
			airInfo.mRightTemp = (float)(airData3 * 0.5);
		} else {
			airInfo.mRightTemp = airData3;
		}
        
        airInfo.mMinTemp = 0x00;
        airInfo.mMaxTemp = 0xFF;
        return airInfo;
	}

	@Override
	public RadarInfo parseRadarInfo(byte[] packet, RadarInfo radarInfo) {
		int cursor = 0;
        byte DataType = (byte) packet[cursor + 1];
        cursor += 3; // 跳过 2E、Data Type、Length字段

        if (DataType == DATA_TYPE_BACK_RADAR_INFO) {
            radarInfo.mBackLeftDis = packet[cursor];
            radarInfo.mBackLeftCenterDis = packet[cursor + 1];
            radarInfo.mBackRightCenterDis = packet[cursor + 2];
            radarInfo.mBackRightDis = packet[cursor + 3];
        } else if (DataType == DATA_TYPE_FRONT_RADAR_INFO) {
            radarInfo.mFrontLeftDis = packet[cursor];
            radarInfo.mFrontLeftCenterDis = packet[cursor + 1];
            radarInfo.mFrontRightCenterDis = packet[cursor + 2];
            radarInfo.mFrontRightDis = packet[cursor + 3];
        }

        return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		int cursor = 3; // 跳过 2E、Data Type、Length字段

        // Data0
        byte baseData0 = (byte) packet[cursor];
        baseInfo.mRightFrontDoor = (byte) ((baseData0 >> 7) & 0x1);
        baseInfo.mLeftFrontDoor = (byte) ((baseData0 >> 6) & 0x1);
        baseInfo.mRightBackDoor = (byte) ((baseData0 >> 5) & 0x1);
        baseInfo.mLeftBackDoor = (byte) ((baseData0 >> 4) & 0x1);
        baseInfo.mTailBoxDoor = (byte) ((baseData0 >> 3) & 0x1);
        baseInfo.mFrontBoxDoor = (byte) ((baseData0 >> 2) & 0x1);

        // Data1
        byte baseData1 = (byte) packet[cursor + 1];
        baseInfo.mTurnLight = (byte) ((baseData1 >> 6) & 0x03);
        baseInfo.mBackCarState = (byte) ((baseData1 >> 0) & 0x1);
        baseInfo.mPStopBlockState = (byte) ((baseData1 >> 1) & 0x1);
        baseInfo.mLightState = (byte) ((baseData1 >> 2) & 0x1);
        return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        ParkAssistInfo parkAssistInfo = new ParkAssistInfo();

        // Data0
        byte baseData0 = (byte) packet[cursor];
        parkAssistInfo.mParkSystemState = (byte) ((baseData0 >> 1) & 0x1);
        parkAssistInfo.mRadarSoundState = (byte) ((baseData0 >> 0) & 0x1);

        return parkAssistInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        int length = packet[2];
        VerInfo version = new VerInfo();
        byte[] ver = new byte[length];
        System.arraycopy(packet, cursor, ver, 0, length);
        version.mVersion = DataConvert.BytesToStr(ver);
        return version;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段

        wheelInfo.mEps = DataConvert.byte2Short(packet, cursor)*45/0x1200;
        return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段

        wheelInfo.mKeyCode = packet[cursor];
        wheelInfo.mKeyStatus = (byte) packet[cursor + 1];
        return TranslateKey(wheelInfo);
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
        int cursor = 3; // 跳过 2E、Data Type、Length字段
        backLightInfo.mLight = packet[cursor] & 0xFF;
		return backLightInfo;
	}

	@Override
	public boolean IsSuportMeInfo() {
		return true;
	}

	@Override
	public Message getVolInfo(int iVol) {
		return CanTxRxStub.getTxMessage(DATA_TYPE_VOLUME_REQUEST,
				DATA_TYPE_VOLUME_REQUEST, getVol(iVol), this);
	}
	
	private Message getTimeData(MediaInfo mediaInfo) {
		byte byCmdId = ReHondaProtocol.DATA_TYPE_CAR_SETTING_REQUEST;
		byte[] byData = new byte[4];

		TimeInfo timeInfo = mediaInfo.getTimeInfo();
		byte byMode = (byte) ((timeInfo.by24Mode == 0x00 ? 0x01 : 0x00) << 6);
		byData[0] = 0x50;
		byData[1] = (byte) (timeInfo.byHour + byMode);
		byData[2] = timeInfo.byMinute;
		byData[3] = timeInfo.bySecond;

		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}
	
	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		ArrayList<Message> aListMsg = new ArrayList<Message>();

		aListMsg.add(CanTxRxStub.getTxMessage(
				DATA_TYPE_SOURCE_REQUEST,
				DATA_TYPE_SOURCE_REQUEST,
				SourceInfo(mediaInfo), this));
		
		aListMsg.add(getTimeData(mediaInfo));
		aListMsg.add(getMediaData(mediaInfo));
		aListMsg.add(getID3Info(mediaInfo, DDef.ID3_TITLE));
		aListMsg.add(getID3Info(mediaInfo, DDef.ID3_ALUM));
		aListMsg.add(getID3Info(mediaInfo, DDef.ID3_AUTHOR));
		return aListMsg;
	}
	
	@Override
	public Message getBtInfo(int iType, int iValue, MediaInfo mediaInfo) {
		byte byCmdId = DATA_TYPE_ID3_INFO;
		byte[] bydata = null;
		if (iType == 0x01) {
			switch (mediaInfo.getPhonestate()) {
			case DDef.PHONE_STATE_INCOMING:
			case DDef.PHONE_STATE_OUTGOING:
			case DDef.PHONE_STATE_SPEAKING:
				String strPhoneNum = mediaInfo.getPhoneNumber();
				if (strPhoneNum != null) {
					if (strPhoneNum.isEmpty()) {
						bydata = new byte[1];
						bydata[0] = 0x01;
					}else {
						bydata = new byte[strPhoneNum.length()+1];
						
						bydata[0] = 0x01;
						
		                for (int i = 1; i < bydata.length; i++) {
		                    bydata[i] = (byte) strPhoneNum.charAt(i-1);
		                }
					}
				}
				break;
			case DDef.PHONE_NONE:
			case DDef.PHONE_STATE_IDLE:
				bydata = new byte[1];
				bydata[0] = 0x01;
				break;
			default:
				break;
			}
		}
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, bydata, this);
	}

	private Message getID3Info(MediaInfo mediaInfo, int type) {
		byte byCmdId = DATA_TYPE_ID3_INFO;
		byte[] byData = null;
		byte byId = 0;
		String str = "";
		switch (type) {
		case DDef.ID3_TITLE:
			str = mediaInfo.getID3Title();
			byId = 0x02;
			break;
		case DDef.ID3_ALUM:
			str = mediaInfo.getID3Album();
			byId = 0x03;
			break;
		case DDef.ID3_AUTHOR:
			str = mediaInfo.getID3Author();
			byId = 0x04;
			break;
		default:
			break;
		}
		
		if (str != null) {
			if (str.isEmpty()) {
				byData = new byte[1];
				byData[0] = byId;
			}else {
				byte[] datas = null;
				try {
					datas = str.getBytes("unicode");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				if (datas != null && datas.length > 0) {
					byData = new byte[datas.length+1];
					byData[0] = byId;
					System.arraycopy(datas, 2, byData, 1, datas.length - 2);
				} 
			}
		} else {
			byData = new byte[1];
			byData[0] = byId;
		}
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}
	
	private Message getMediaData(MediaInfo mediaInfo) {

		byte byCmdId = 0;
		byte[] byData = null;
		int iSource = mediaInfo.getSource();
		if (iSource == DDef.SOURCE_DEF.Src_radio) {
    		
    		byCmdId = DATA_TYPE_RADIO_REQUEST;
			byData  = RadioInfo(mediaInfo);
			
		} else {
			
			byCmdId = DATA_TYPE_MEDIA_PLAYER_REQUEST;
			byData  = MediaPlayInfo(mediaInfo);
		}
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	private byte[] getVol(int iVol) {
		byte[] byData = { 0 };

		byte byVol = (byte) iVol;

		byData[0] = (byte) ((byVol > 0) ? (byData[0] & 0x00)
				: (byData[0] | 0x80));
		byData[0] = (byte) (byData[0] | ((0x7F & byData[0]) | byVol));
		
		return byData;
	}

	public byte[] SourceInfo(MediaInfo mediaInfo) {

		byte[] bydata = new byte[2];

		byte bySource = TranslateSource(mediaInfo.getSource());
		bydata[0] = bySource;
		bydata[1] = getMediaType(bySource);
		return bydata;
	}

	public byte TranslateBand(byte byBand) {
		byte bybandEx = TunerBound.TUNER_FM1;

		switch (byBand) {
		case 0x00:
		case 0x01:
		case 0x02:
			bybandEx = TunerBound.TUNER_FM;
			break;
		case 0x03:
		case 0x04:
			bybandEx = TunerBound.TUNER_AM;
			break;
		default:
			break;
		}

		return bybandEx;
	}

	private byte[] RadioInfo(MediaInfo mediainfo) {

		byte[] bydata = new byte[4];
		bydata[0] = TranslateBand(mediainfo.getBand());
		int iFreq = mediainfo.getMainFreq();
        byte[] itmpFreq = DataConvert.Hi2Lo2Byte(iFreq);
        bydata[1] = itmpFreq[0];                                
        bydata[2] = itmpFreq[1];   
		bydata[3] = mediainfo.getFreqIndex();
		return bydata;
	}

	private byte getMediaType(byte bySource){
		
		byte byMeType = 0x00;
		
		switch (bySource) {
		case Source.TUNER:
			byMeType = MediaType.TUNER;
			break;
		case Source.AUX:
			byMeType = MediaType.AUX;
			break;
		case Source.DISC_CD_DVD:
			byMeType = MediaType.EAM;
			break;
		case Source.IPOD:
			byMeType = MediaType.IPOD;
			break;
		case Source.OTHER:
			byMeType = MediaType.AUX;
			break;
		case Source.PHONE:
			byMeType = MediaType.PHONE;
			break;
		case Source.PHONE_A2DP:
			byMeType = MediaType.EAM;
			break;
		case Source.TV:
			byMeType = MediaType.AUX;
			break;
		case Source.USB:
			byMeType = MediaType.EAM;
			break;
		default:
			byMeType = MediaType.AUX;
			break;
		}
		
		return byMeType;
	}

	public byte[] MediaPlayInfo(MediaInfo mediaInfo) {

		byte[] bydata = null;
    	
    	switch (getMediaType(TranslateSource(mediaInfo.getSource()))) {
		case MediaType.SAM:
			break;
		case MediaType.EAM:
			bydata = new byte[6];
            byte[] iTotalTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getTotalTrack());
            bydata[0] = iTotalTrack[0];
            bydata[1] = iTotalTrack[1];
            byte[] byCurTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getCurTrack());
            bydata[2] = byCurTrack[0];
            bydata[3] = byCurTrack[1];
            int icurTime = mediaInfo.getPlayTime();
            bydata[4] = (byte) ((icurTime/60)%60);
            bydata[5] = (byte) (icurTime%60);
			break;
		case MediaType.IPOD:
			break;
		case MediaType.FBV:
			break;
		case MediaType.DVDV:
			break;
		case MediaType.OV:
			break;
		case MediaType.AUX:
			bydata = new byte[6];
			break;
		case MediaType.PHONE:
			break;
		default:
			bydata = new byte[6];
			break;
		}
    	
    	return bydata;
	}
	
	@Override
	public FuelMilInfo parseFuelMilInfo(byte[] packet, FuelMilInfo fuelMilInfo) {
		int cursor = 3; 

        fuelMilInfo.mIndex = (byte) packet[cursor]; 
        if (fuelMilInfo.mIndex == 1) {
            fuelMilInfo.mImmediateFuel = (byte) packet[cursor + 1]; 
            fuelMilInfo.mCurrentAverFuel = DataConvert.byteToInt(packet[cursor + 2]) * 256
                    + DataConvert.byteToInt(packet[cursor + 3]);
            fuelMilInfo.mHistoryAverFuel = DataConvert.byteToInt(packet[cursor + 4]) * 256
                    + DataConvert.byteToInt(packet[cursor + 5]);
            fuelMilInfo.mAverFuel = DataConvert.byteToInt(packet[cursor + 6]) * 256
                    + DataConvert.byteToInt(packet[cursor + 7]);
            fuelMilInfo.mTripaIndex1 = DataConvert.byteToInt(packet[cursor + 8]) * 256 * 256
                    + DataConvert.byteToInt(packet[cursor + 9]) * 256 + DataConvert.byteToInt(packet[cursor + 10]);// TRIPA
            fuelMilInfo.mCanDriveMil = DataConvert.byteToInt(packet[cursor + 11]) * 256
                    + DataConvert.byteToInt(packet[cursor + 12]);
            byte dataUnit = packet[cursor + 13];
            fuelMilInfo.mCanDriveMilUnit = (byte) ((dataUnit >> 7) & 0x1);
            fuelMilInfo.mTripaUnit = (byte) ((dataUnit >> 6) & 0x1);
            fuelMilInfo.mAverFuelUnit = (byte) ((dataUnit >> 4) & 0x3);
            fuelMilInfo.mCHAverFuelUnit = (byte) ((dataUnit >> 2) & 0x3);
            fuelMilInfo.mImmediateFuelUnit = (byte) ((dataUnit >> 0) & 0x3);
            fuelMilInfo.mFuelRange = (byte) packet[cursor + 14];
        } else if (fuelMilInfo.mIndex == 2) {
            fuelMilInfo.mFirstTripaRecord = DataConvert.byteToInt(packet[cursor + 1]) * 256 * 256
                    + DataConvert.byteToInt(packet[cursor + 2]) * 256 + DataConvert.byteToInt(packet[cursor + 3]);
            fuelMilInfo.mFirstAverFuelRecord = DataConvert.byteToInt(packet[cursor + 4]) * 256
                    + DataConvert.byteToInt(packet[cursor + 5]);
            fuelMilInfo.mSecondTripaRecord = DataConvert.byteToInt(packet[cursor + 6]) * 256 * 256
                    + DataConvert.byteToInt(packet[cursor + 7]) * 256 + DataConvert.byteToInt(packet[cursor + 8]);// �ڶ���TRIPA��¼
            fuelMilInfo.mSecondAverFuelRecord = DataConvert.byteToInt(packet[cursor + 9]) * 256
                    + DataConvert.byteToInt(packet[cursor + 10]);
            fuelMilInfo.mThirdTripaRecord = DataConvert.byteToInt(packet[cursor + 11]) * 256 * 256
                    + DataConvert.byteToInt(packet[cursor + 12]) * 256 + DataConvert.byteToInt(packet[cursor + 13]);// ������TRIPA��¼
            fuelMilInfo.mThirdAverFuelRecord = DataConvert.byteToInt(packet[cursor + 14]) * 256
                    + DataConvert.byteToInt(packet[cursor + 15]);
            byte dataUnit = packet[cursor + 16];
            fuelMilInfo.mCanDriveMilUnit = (byte) ((dataUnit >> 7) & 0x1);
            fuelMilInfo.mTripaUnit = (byte) ((dataUnit >> 6) & 0x1);
            fuelMilInfo.mAverFuelUnit = (byte) ((dataUnit >> 4) & 0x3);
            fuelMilInfo.mCHAverFuelUnit = (byte) ((dataUnit >> 2) & 0x3);
            fuelMilInfo.mImmediateFuelUnit = (byte) ((dataUnit >> 0) & 0x3);
            fuelMilInfo.mFuelRange = (byte) packet[cursor + 17];
        }
        
        return fuelMilInfo;
	}

	@Override
	public CompassInfo parseCompassInfo(byte[] packet, CompassInfo compassInfo) {
        int cursor = 3; 
        byte ComPassStatus = (byte) packet[cursor];;
        if(DataConvert.GetBit(ComPassStatus, 7) == 1) {
        	compassInfo.CompassAdjust = true;
        }else {
              compassInfo.CompassAdjust = false;
        }
        compassInfo.Compassarea = (byte)(ComPassStatus & 0x0f);
        compassInfo.compassAngle = (packet[cursor + 1] & 0xff)*3/2;
        return compassInfo;
	}

	@Override
	public CarSetting parseCarSettingInfo(byte[] packet, CarSetting carSetting) {
        int cursor = 3; 
        byte data0 = packet[cursor];
        carSetting.mOutTemp = data0 & 0x0F;
        carSetting.mTripAReset = (data0 >> 4) & 0x03;
        carSetting.mTripBReset = (data0 >> 6) & 0x03;
        
        byte data1 = packet[cursor + 1];
        carSetting.mInLightDimTime = data1 & 0x03;
        carSetting.mHeadLightTime = data1 >> 2 & 0x03;
        carSetting.mAutoLightSend = data1 >> 4 & 0x07;
        
        byte data2 = packet[cursor + 2];
        carSetting.mAutoLockDoor = data2 & 0x03;
        carSetting.mAutoUnLockDoor = data2 >> 2 & 0x03;
        carSetting.mRelockTime = data2 >> 4 & 0x03;
        carSetting.mDoorUnLock = data2 >> 6 & 0x01;
        carSetting.mKeyLockAnswer = (data2 >> 7 & 0x01) == 0x01 ? true : false;
        
        byte data3 = packet[cursor + 3];
        carSetting.mAutoInSend = data3 & 0x07;
        carSetting.mKeylessLight = (data3 >> 3 & 0x01) == 0x01 ? true : false;
        carSetting.mDoorLockMode = data3 >> 4 & 0x01;
        carSetting.mRemoteSys = (data3 >> 5 & 0x01) == 0x01 ? true : false;
        carSetting.mKeylessBeep = (data3 >> 6 & 0x01) == 0x01 ? true : false;
        
		byte data4 = packet[cursor + 4];
		carSetting.mAutoHeadLight = (data4 & 0x01) == 0x01 ? true : false;
		carSetting.mWalkAwayLock = (data4 >> 1 & 0x01) == 0x01 ? true : false;
		carSetting.mTachometer = (data4 >> 2 & 0x01) == 0x01 ? true : false;
		carSetting.mSpeedUnit = data4 >> 3 & 0x01;
		carSetting.mMsgNotify = (data4 >> 4 & 0x01) == 0x01 ? true : false;
		carSetting.mFuelBackLight = (data4 >> 5 & 0x01) == 0x01 ? true : false;
		carSetting.mAdjustAlarm = data4 >> 6 & 0x03;
		
		byte data5 = packet[cursor + 5];
		carSetting.mLaneDeparture = data5 & 0x03;
		carSetting.mWarnDisctance = data5 >> 2 & 0x03;
		carSetting.mPauseLKAS = (data5 >> 4 & 0x01) == 0x01 ? true : false;
		carSetting.mAccPromptTone = (data5 >> 5 & 0x01) == 0x01 ? true : false;
		carSetting.mEngineAutoMatic = (data5 >> 6 & 0x01) == 0x01 ? true : false;
		carSetting.mAlarmSysVolume = data5 >> 7 & 0x01;
		
		byte data6 = packet[cursor + 6];
		carSetting.mTachmeterSet = (data6 >> 7 & 0x01) == 0x01 ? true : false;
		
        return carSetting;
	}
}
