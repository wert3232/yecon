package com.can.parser.raise.dz;

import java.util.ArrayList;
import android.os.Message;
import com.can.assist.GdSharedData.MediaInfo;
import com.can.parser.DDef;
import com.can.parser.DDef.AirInfo;
import com.can.parser.DDef.BackLightInfo;
import com.can.parser.DDef.BaseInfo;
import com.can.parser.DDef.CarInfo;
import com.can.parser.DDef.ParkAssistInfo;
import com.can.parser.DDef.RadarInfo;
import com.can.parser.DDef.VerInfo;
import com.can.parser.DDef.WheelInfo;
import com.can.parser.DDef.WheelKeyInfo;
import com.can.parser.raise.ReProtocol;
import com.can.services.CanTxRxStub;
import com.can.tool.DataConvert;

/**
 * ClassName:ReMQB6Protocol
 * 
 * @function:睿志城
 * @author Kim
 * @Date:  2016-5-28 下午12:29:30
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class ReMQB6Protocol extends ReProtocol{

	/**
	 * 协议数据类型定义 大众6代平台 ver1.4
	 */
	//Slave -> Host
	public static final byte DATA_TYPE_AIR_INFO = (byte)0x21;				//空调信息
	public static final byte DATA_TYPE_CAR_INFO = (byte)0x41;				//车辆信息
	public static final byte DATA_TYPE_BACK_RADAR_INFO = (byte)0x22;		//后雷达信息
	public static final byte DATA_TYPE_FRONT_RADAR_INFO = (byte)0x23;		//前雷达信息
	public static final byte DATA_TYPE_BASE_INFO = (byte)0x24;				//基本信息
	public static final byte DATA_TYPE_PARKASSIST_INFO = (byte)0x25;		//辅助系统信息
	public static final byte DATA_TYPE_VERSION_INFO = (byte)0x30;			//版本信息
	public static final byte DATA_TYPE_WHEEL_INFO = (byte)0x26;             //方向盘转角
	public static final byte DATA_TYPE_WHEEL_KEY_INFO = (byte)0x20;         //方向盘按键信息
	public static final byte DATA_TYPE_BACK_LIGHT_INFO = (byte)0x14;        //背光调节
	
	//Host -> Slave
	public static final byte DATA_TYPE_CAR_INFO_REQUEST = (byte)0x90;		//请求控制信息
	public static final byte DATA_TYPE_SOURCE_REQUEST = (byte)0xC0;			//Source
	public static final byte DATA_TYPE_ICON_REQUEST = (byte)0xC1;			//ICON
	public static final byte DATA_TYPE_RADIO_REQUEST = (byte)0xC2;			//收音信息
	public static final byte DATA_TYPE_MEDIA_PLAYER_REQUEST = (byte)0xC3;	//媒体播放信息
	public static final byte DATA_TYPE_VOLUME_REQUEST = (byte)0xC4;			//音量显示控制
	public static final byte DATA_TYPE_TELEPHONE_REQUEST = (byte)0xC5;		//电话状态
	public static final byte DATA_TYPE_RADAR_SOUND_REQUEST = (byte)0xC6;	//Radar声音控制
	public static final byte DATA_TYPE_PA_REQUEST = (byte)0xA0;				//功放控制指令
	public static final byte DATA_TYPE_MEDIA_BOX_REQUEST = (byte)0xA1;		//媒体盒控制指令
	public static final byte DATA_TYPE_NAVI_REQUEST = (byte)0xA2;			//行车电脑导航信息显示
	public static final byte DATA_TYPE_BT_REQUEST = (byte)0xA3;				//蓝牙控制指令
	
	
	//源定义
	public class Source {
		public static final byte TUNER = (byte)0x01; 			// Tuner
		public static final byte DISC_CD_DVD = (byte)0x02; 		// CD、DVD
		public static final byte TV = (byte)0x03;				// TV
		public static final byte NAVI = (byte)0x04;				// NAVI
		public static final byte PHONE = (byte)0x05;			// Phone
		public static final byte IPOD = (byte)0x06;				// IPOD
		public static final byte AUX = (byte)0x07;				// Aux
		public static final byte USB = (byte)0x08;				// USB
		public static final byte SD = (byte)0x09;				// SD
		public static final byte DVB_T = (byte)0x0A;			// DVB-T
		public static final byte PHONE_A2DP = (byte)0x0b;		// Phone A2DP
		public static final byte OTHER = (byte)0x0c;			// 0x0c
		public static final byte CDC= (byte)0x0D;				// CDC		
	}
	
	//媒体类型
	public class MediaType {
		public static final byte TUNER = (byte)0x01;			//Tuner
		public static final byte SAM = (byte)0x10;				//Simple Audio Media
		public static final byte EAM = (byte)0x11;				//Enhanced Audio Media
		public static final byte IPOD = (byte)0x12;				//IPod
		public static final byte FBV = (byte)0x20;				//File Based Video
		public static final byte DVDV = (byte)0x21;				//DVD Video
		public static final byte OV = (byte)0x22;				//Other Video
		public static final byte AUX = (byte)0x30;				//Aux other
		public static final byte PHONE = (byte)0x40;			//Phone	
	}
	
	//收音机波段
	public class TunerBound {
	    public static final byte TUNER_FM = (byte)0x00;
	    public static final byte TUNER_FM1= (byte)0x01;
	    public static final byte TUNER_FM2= (byte)0x02;
	    public static final byte TUNER_FM3= (byte)0x03;
	    public static final byte TUNER_AM = (byte)0x10;
	    public static final byte TUNER_AM1= (byte)0x11;
	    public static final byte TUNER_AM2= (byte)0x12;
	    public static final byte TUNER_AM3= (byte)0x13;
	}
	
	//电话状态
	public class PhoneState{
	    public static final byte POHNE_NOT_ACTIVE = (byte)0x00;
	    public static final byte PHONE_RINGING = (byte)0x01;
	    public static final byte PHONE_DIALING = (byte)0x02;
	    public static final byte PHONE_CONNECTED =(byte)0x03;
	}
	
	//按键类型
	public class KeyCode{	   
	    public static final byte  KEY_CODE_VOL_UP = (byte)0x01;
	    public static final byte  KEY_CODE_VOL_DOWN = (byte)0x02;
	    public static final byte  KEY_CODE_MENU_UP = (byte)0x03;
	    public static final byte  KEY_CODE_MENU_DOWN = (byte)0x04;
	    public static final byte  KEY_CODE_TEL = (byte)0x05;
	    public static final byte  KEY_CODE_MUTE = (byte)0x06;
	    public static final byte  KEY_CODE_SRC = (byte)0x07;
	    public static final byte  KEY_CODE_MIC = (byte)0x08;
	}
	
	//按键状态
	public class KeyStatus{ 	    
	    public static final byte KEY_STATUS_DOWN = (byte)0x01;
	    public static final byte KEY_STATUS_UP = (byte)0x00;
	    public static final byte KEY_STATUS_LONGPRESS =(byte)0x02;  
	}
	
	@Override
	public byte[] connect() {
		// TODO Auto-generated method stub
		return super.connect();
	}
	
	@Override
	public int GetAirInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_AIR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_CAR_INFO & 0xFF);
	}

	@Override
	public int GetCarInfoRequestCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_CAR_INFO_REQUEST & 0xFF);
	}

	@Override
	public int GetBackRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BACK_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetFrontRadarInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_FRONT_RADAR_INFO & 0xFF);
	}

	@Override
	public int GetBaseInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BASE_INFO & 0xFF);
	}

	@Override
	public int GetParkAssistCmdId() {
		// TODO Auto-generated method stub
		return  (int) (DATA_TYPE_PARKASSIST_INFO & 0xFF);
	}

	@Override
	public int GetVersionInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_VERSION_INFO & 0xFF);
	}

	@Override
	public int GetWheelInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_WHEEL_INFO & 0xFF);
	}

	@Override
	public int GetWheelKeyInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_WHEEL_KEY_INFO & 0xFF);
	}

	@Override
	public int GetBackLightInfoCmdId() {
		// TODO Auto-generated method stub
		return (int) (DATA_TYPE_BACK_LIGHT_INFO & 0xFF);
	}

	@Override
	public CarInfo parseCarInfo(byte[] packet, CarInfo carInfo) {
		// TODO Auto-generated method stub
		int cursor=3; //跳过 2E、Data Type、Length字段
		
		byte carData0 = (byte)packet[cursor];
		byte carData1 = (byte)packet[cursor+1];
		
		if(carData0 == 0x01){
			//Data 1
			carInfo.mSafetyBelt = (byte) (( carData1 >> 7) & 0x1);  	
			carInfo.mWashLiquid = (byte)(( carData1 >> 6) & 0x1);       
			carInfo.mHandbrake = (byte)(( carData1 >> 5) & 0x1);      
			carInfo.mTailBoxDoor = (byte)(( carData1 >> 4) & 0x1);       
			carInfo.mRightBackDoor = (byte)(( carData1 >> 3) & 0x1);       
			carInfo.mLeftBackDoor = (byte)(( carData1 >> 2) & 0x1);       
			carInfo.mRightFrontDoor = (byte)(( carData1 >> 1) & 0x1);       
			carInfo.mLeftFrontDoor = (byte)(( carData1 >> 0) & 0x1);
			
		}else if(carData0 == 0x02){
			
			byte carData2 =(byte)packet[cursor+2];
			byte carData3 =(byte)packet[cursor+3];
			byte carData4 =(byte)packet[cursor+4];
			byte carData5 =(byte)packet[cursor+5];
			byte carData6 =(byte)packet[cursor+6];
			byte carData7 =(byte)packet[cursor+7];
			byte carData8 =(byte)packet[cursor+8];
			byte carData9 =(byte)packet[cursor+9];
			byte carData10 =(byte)packet[cursor+10];
			byte carData11 =(byte)packet[cursor+11];
			byte carData12 =(byte)packet[cursor+12];

			
			carInfo.mRotationlSpeed = DataConvert.byteToInt(carData1)*256 + DataConvert.byteToInt(carData2);
			carInfo.mCurrentSpeed = (float)((DataConvert.byteToInt(carData3)*256 + DataConvert.byteToInt(carData4))*0.01);
			
			carInfo.mBatteryVoltage =(float)((DataConvert.byteToInt(carData5)*256 + DataConvert.byteToInt(carData6))*0.01);
			
			if (DataConvert.GetBit(carData7, 7) == 1) {
				carInfo.mOutCarTemp = -(float)((0xFFFF - (DataConvert.byteToInt(carData7)*256 + DataConvert.byteToInt(carData8))+1)*0.1);
			}else {
				carInfo.mOutCarTemp = (float)((DataConvert.byteToInt(carData7)*256 + DataConvert.byteToInt(carData8))*0.1);				
			}

			carInfo.mTravelMileage = DataConvert.byteToInt(carData9)*65536 + DataConvert.byteToInt(carData10)*256+DataConvert.byteToInt(carData11);
			carInfo.mSurplusmOil = DataConvert.byteToInt(carData12);
		}else if(carData0 == 0x03){ 
		    
		    carInfo.mLowOilWarn = (byte) (( carData1 >> 7) & 0x1);    
            carInfo.mLowBatVolWarn = (byte)(( carData1 >> 6) & 0x1);
		}	
		
		return carInfo;
	}
	
	private byte[] mbyAirInfo = null;

	@Override
	public AirInfo parseAirInfo(byte[] packet, AirInfo airInfo) {
		// TODO Auto-generated method stub
	
	 	int cursor = 3; //跳过2E、Data Type、Length字段
        
        //Data 0 
        byte airData0 = (byte)packet[cursor]; 
        airInfo.mAiron = (byte) (( airData0 >> 7) & 0x1);   
        airInfo.mAcState = (byte)(( airData0 >> 6) & 0x1);       
        airInfo.mCircleState = (byte)(( airData0 >> 5) & 0x1);      
        airInfo.mAutoLight1 = (byte)(( airData0 >> 4) & 0x1);       
        airInfo.mAutoLight2 = (byte)(( airData0 >> 3) & 0x1);       
        airInfo.mDaulLight = (byte)(( airData0 >> 2) & 0x1);       
        airInfo.mMaxForntLight = (byte)(( airData0 >> 1) & 0x1);       
        airInfo.mRearLight = (byte)(( airData0 >> 0) & 0x1);
        
        //Data 1
        cursor += 1;
        byte airData1 = (byte)packet[cursor]; 
        airInfo.mUpwardWind = (byte) (( airData1 >> 7) & 0x1);
        airInfo.mParallelWind = (byte)((airData1 >> 6) & 0x1);
        airInfo.mDowmWind = (byte)((airData1) >> 5 & 0x1);
        airInfo.mDisplay = (byte)((airData1) >> 4 & 0x1);
        airInfo.mWindRate = (byte)((airData1) & 0x07);
        
        //Data 2
        cursor += 1;
        byte airData2 = (byte)packet[cursor];
        airInfo.mLeftTemp = (float)(18+(airData2-1)*0.5);
        airInfo.mMinTemp = (float) 17.5;
        airInfo.mMaxTemp = (float) 26.5;
        
        //Data 3 
        cursor += 1;
        byte airData3 = (byte)packet[cursor];
        airInfo.mRightTemp = (float) (18+(airData3-1)*0.5);
        
        //Data 4
        cursor += 1;
        byte airData4 = (byte)packet[cursor];
        airInfo.mAqsInCircle = (byte) (( airData4 >> 7) & 0x1);
        airInfo.mLeftHotSeatTemp = (byte) (( airData4 >> 4) & 0x3);
        airInfo.mRearLock = (byte) (( airData4 >> 3) & 0x1);
        airInfo.mAcMax = (byte)(( airData4 >> 2) & 0x1);
        airInfo.mRightHotSeatTemp = (byte)(( airData4 >> 0) & 0x3);
        
        if (mbyAirInfo != null) {
			
        	airInfo.bAirUIShow = (!mbyAirInfo.equals(packet) && 
        			(airInfo.mAiron == 1) && (airInfo.mDisplay == 1)) ? true : false;
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
		
		radarInfo.mbyRightShowType = 1;
	
		return radarInfo;
	}

	@Override
	public BaseInfo parseBaseInfo(byte[] packet, BaseInfo baseInfo) {
		// TODO Auto-generated method stub
		int cursor=3; //跳过 2E、Data Type、Length字段
		
		//Data0
		byte baseData0 = (byte)packet[cursor]; 
		baseInfo.mLightState= (byte)(( baseData0 >> 2) & 0x1);       
        baseInfo.mPStopBlockState = (byte)(( baseData0 >> 1) & 0x1);       
        baseInfo.mBackCarState  = (byte)(( baseData0 >> 0) & 0x1);
        
        //Data1
      	byte baseData1 = (byte)packet[cursor+1]; 
      	baseInfo.mFrontBoxDoor = (byte)(( baseData1 >> 5) & 0x1);  
        baseInfo.mTailBoxDoor = (byte)(( baseData1 >> 4) & 0x1);       
        baseInfo.mRightBackDoor = (byte)(( baseData1 >> 3) & 0x1);       
        baseInfo.mLeftBackDoor = (byte)(( baseData1 >> 2) & 0x1);       
        baseInfo.mRightFrontDoor = (byte)(( baseData1 >> 1) & 0x1);       
        baseInfo.mLeftFrontDoor = (byte)(( baseData1 >> 0) & 0x1);
		
		return baseInfo;
	}

	@Override
	public ParkAssistInfo parseParkAssistantInfo(byte[] packet) {
		// TODO Auto-generated method stub
		int cursor=3; //跳过 2E、Data Type、Length字段
		ParkAssistInfo parkAssistInfo= new ParkAssistInfo();
		
		//Data0
		byte baseData0 = (byte)packet[cursor]; 
		parkAssistInfo.mParkSystemState = (byte)(( baseData0 >> 1) & 0x1);       
		parkAssistInfo.mRadarSoundState = (byte)(( baseData0 >> 0) & 0x1); 
		
		return parkAssistInfo;
	}

	@Override
	public VerInfo parseVersionInfo(byte[] packet) {
		// TODO Auto-generated method stub
		int cursor=3; //跳过 2E、Data Type、Length字段
		VerInfo version = new VerInfo();		
		byte[] ver = new byte[16];
		System.arraycopy(packet, cursor, ver, 0, 16);
		version.mVersion = DataConvert.BytesToStr(ver);	
		return version;
	}

	@Override
	public WheelInfo parseWheelInfo(byte[] packet, WheelInfo wheelInfo) {
		// TODO Auto-generated method stub
	     int cursor=3; //跳过 2E、Data Type、Length字段
	     
	     wheelInfo.mEps = DataConvert.byte2Short(packet,cursor); 
	     wheelInfo.mEps = -(wheelInfo.mEps*45/539);
	     
        return wheelInfo;
	}

	@Override
	public WheelKeyInfo parseWheelKeyInfo(byte[] packet, WheelKeyInfo wheelInfo) {
		// TODO Auto-generated method stub
        int cursor=3; //跳过 2E、Data Type、Length字段

        wheelInfo.mKeyCode = packet[cursor];       
        wheelInfo.mKeyStatus = (byte)packet[cursor+1]; 
        
        return TranslateKey(wheelInfo);
	}

	@Override
	public BackLightInfo parseBackLightInfo(byte[] packet,
			BackLightInfo backLightInfo) {
		// TODO Auto-generated method stub
        int cursor=3; //跳过 2E、Data Type、Length字段

        backLightInfo.mLight = DataConvert.byteToInt((byte)packet[cursor]);       
        return backLightInfo;
	}

	//媒体信息接口
    
	private String mstrKeyCode = null;
	public WheelKeyInfo TranslateKey(WheelKeyInfo info) {
		// TODO Auto-generated method stub
	
		switch (info.mKeyCode) {
		case KeyCode.KEY_CODE_VOL_DOWN:
			info.mstrKeyCode = DDef.Volume_del;
			break;
		case KeyCode.KEY_CODE_VOL_UP:
			info.mstrKeyCode = DDef.Volume_add;
			break;
		case KeyCode.KEY_CODE_MIC:
			info.mstrKeyCode = DDef.Speech;
			break;
		case KeyCode.KEY_CODE_MENU_UP:
			info.mstrKeyCode = DDef.Media_pre;
			break;
		case KeyCode.KEY_CODE_MENU_DOWN:
			info.mstrKeyCode = DDef.Media_next;
			break;
		case KeyCode.KEY_CODE_SRC:
			info.mstrKeyCode = DDef.Src_mode;
			break;
		case KeyCode.KEY_CODE_MUTE:
			info.mstrKeyCode = DDef.Volume_mute;
			break;
		case KeyCode.KEY_CODE_TEL:
			info.mstrKeyCode = DDef.Phone_dial;
			break;
		default:
			break;
		}
		
		if (info.mKeyCode == 0) {
			info.mstrKeyCode = mstrKeyCode;
			mstrKeyCode = null;
		}else {
			mstrKeyCode = info.mstrKeyCode;
		}

		return info;
	}
	
	/**
	 * 
	* <p>Title: getMediaType</p>
	* <p>Description: 小屏媒体类型</p>
	* @param bySource
	* @return
	 */
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
	
	public byte[] getPhoneInfo(int iState){
		byte[] bydata = null;
		
		switch (iState) {
		case PhoneState.POHNE_NOT_ACTIVE:
			bydata = new byte[7];
			bydata[0] = PhoneState.POHNE_NOT_ACTIVE;
			break;
		case PhoneState.PHONE_CONNECTED:
			bydata = new byte[7];
			bydata[0] = PhoneState.PHONE_CONNECTED;
			break;
		case PhoneState.PHONE_DIALING:
			bydata = new byte[1];
			bydata[0] = PhoneState.PHONE_DIALING;
			break;
		case PhoneState.PHONE_RINGING:
			bydata = new byte[1];
			bydata[0] = PhoneState.PHONE_RINGING;
			break;
		default:
			bydata = new byte[6];
			break;
		}
		
		return bydata;
	}
	/**
	 * 
	* <p>Title: SourceInfo</p>
	* <p>Description: 源信息</p>
	* @param mediaInfo
	* @return
	 */
	public byte[] SourceInfo(MediaInfo mediaInfo){
		
		byte[] bydata = new byte[2];
		
		bydata[0] = TranslateSource(mediaInfo.getSource());
		bydata[1] = getMediaType(bydata[0]);
		
		return bydata;
	}
	
	public byte TranslateBand(byte byBand){
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
	
	/**
	 * 
	* <p>Title: RadioInfo</p>
	* <p>Description:收音机信息 </p>
	* @param mediainfo
	* @return
	 */
	public byte[] RadioInfo(MediaInfo mediainfo){
        
        byte[] bydata = new byte[4]; 
        
        bydata[0] = TranslateBand(mediainfo.getBand());             
        int iFreq = mediainfo.getMainFreq();
        byte[] itmpFreq = DataConvert.Hi2Lo2Byte(iFreq);
        bydata[1] = itmpFreq[0];                                
        bydata[2] = itmpFreq[1];                                
        bydata[3] = mediainfo.getFreqIndex();
        
        return bydata;
    }
    
    /**
     * 
    * <p>Title: MediaPlayInfo</p>
    * <p>Description: 媒体播放信息</p>
    * @param mediaInfo
    * @return
     */
    public byte[] MediaPlayInfo(MediaInfo mediaInfo){
    	
    	byte[] bydata = null;
    	
    	switch (getMediaType(TranslateSource(mediaInfo.getSource()))) {
		case MediaType.SAM:
			break;
		case MediaType.EAM:
			bydata = new byte[6];
            byte[] byCurTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getCurTrack());
            bydata[0] = byCurTrack[0];
            bydata[1] = byCurTrack[1];
            byte[] iTotalTrack = DataConvert.Hi2Lo2Byte(mediaInfo.getTotalTrack());
            bydata[2] = iTotalTrack[0];
            bydata[3] = iTotalTrack[1];
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
			bydata = getPhoneInfo(mediaInfo.getPhonestate());
			break;
		default:
			bydata = new byte[6];
			break;
		}
    	
    	return bydata;
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
	
	private byte[] getVol(int iVol){
		byte[] byData = {0};
		
		byte byVol = (byte)iVol;
		
		byData[0] = (byte) ((byVol > 0) ? (byData[0] & 0x00) : (byData[0] | 0x80));
		byData[0] = (byte) (byData[0] | ((0x7F & byData[0]) | byVol));
		
		return byData;
	}
	
	@Override
	public Message getVolInfo(int iVol) {
		// TODO Auto-generated method stub
		return CanTxRxStub.getTxMessage(ReMQB6Protocol.DATA_TYPE_VOLUME_REQUEST, ReMQB6Protocol.DATA_TYPE_VOLUME_REQUEST, getVol(iVol), this);
	}

	private Message getMediaData(MediaInfo mediaInfo){
		
		byte   byCmdId;
    	byte[] byData = null;
		int iSource = mediaInfo.getSource();
		
		if (iSource == DDef.SOURCE_DEF.Src_radio) {
    		
    		byCmdId = ReMQB6Protocol.DATA_TYPE_RADIO_REQUEST;
			byData  = RadioInfo(mediaInfo);
			
		}else if (iSource == DDef.SOURCE_DEF.Src_bt_phone) {
			
			byCmdId = ReMQB6Protocol.DATA_TYPE_TELEPHONE_REQUEST;
			byData  = getPhoneInfo(mediaInfo.getPhonestate());
			
		}else {
			
			byCmdId = ReMQB6Protocol.DATA_TYPE_MEDIA_PLAYER_REQUEST;
			byData  = MediaPlayInfo(mediaInfo);
		}
    	
		return CanTxRxStub.getTxMessage(byCmdId, byCmdId, byData, this);
	}

	@Override
	public boolean IsSuportMeInfo() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public ArrayList<Message> getMediaInfo(MediaInfo mediaInfo) {
		// TODO Auto-generated method stub
		ArrayList<Message> aListMsg = new ArrayList<Message>();
		
		aListMsg.add(CanTxRxStub.getTxMessage(ReMQB6Protocol.DATA_TYPE_SOURCE_REQUEST, ReMQB6Protocol.DATA_TYPE_SOURCE_REQUEST, SourceInfo(mediaInfo), this));
		aListMsg.add(getMediaData(mediaInfo));

		return aListMsg;
	}

	@Override
	public Message getMuteRadarData(boolean isMute) {
		// TODO Auto-generated method stub
        byte[] muteData = new byte[2];
        muteData[0] = 0x00;   //协议固定
        
        if(isMute){
            muteData[1] = 0x00;  //关闭倒车雷达声
        }else{
            muteData[1] = 0x01;  //打开倒车雷达声
        }
       
        return CanTxRxStub.getTxMessage(DATA_TYPE_RADAR_SOUND_REQUEST, DATA_TYPE_RADAR_SOUND_REQUEST, muteData, this);
	}

	@Override
	public Message getInquiryTrackData() {
		// TODO Auto-generated method stub
		byte[] byData = { 0x26};
		
		return CanTxRxStub.getTxMessage(DATA_TYPE_CAR_INFO_REQUEST, DATA_TYPE_CAR_INFO_REQUEST, byData, this);
	}

	@Override
	public Message getParkData(boolean bPark) {
		// TODO Auto-generated method stub
        byte[] muteData = new byte[2];
        muteData[0] = 0x02;   
        
        if(bPark){
            muteData[1] = 0x01;  
        }else{
            muteData[1] = 0x00; 
        }
       
        return CanTxRxStub.getTxMessage(DATA_TYPE_RADAR_SOUND_REQUEST, DATA_TYPE_RADAR_SOUND_REQUEST, muteData, this);
	}
}
