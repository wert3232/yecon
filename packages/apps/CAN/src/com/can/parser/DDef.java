package com.can.parser;

import java.util.HashMap;

/**
 * ClassName:DDef
 * 
 * @function:Can数据结构定义
 * @author Kim
 * @Date:  2016-5-26 上午11:52:52
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 */
public class DDef {
	// 本田
	public static class CarHondaType {
		public static int HONDA_GM 	= 0x00;		//凌派/杰德/歌诗图/缤智/思域/飞度/奥德赛/15锋范/16凌派、XRV
		public static int HONDA_CRV = 0x01; 	//12CRV/15CRV地中配
	}
	
	// 通用
	public static class CarGmType {
		public static int GM  = 0x00;			//兼容版
		public static int ENCORE = 0x01;		//昂克拉
		public static int GL8 = 0x02;			//GL8
	}
	
	// 国产
	public static class CarDomestic {
		public static int BAOJUN 		= 0x00;		//宝骏730 宝骏560
		public static int AEOLUS 		= 0x01;		//风神
		public static int BRILLIANCEV3 	= 0x02;		//中华V3
		public static int CHANACS75 	= 0x03;		//长安CS75
		public static int CHANAV7 		= 0x04;		//长安锐翔V7
		public static int GEELYEC7 		= 0x05;		//吉利帝豪14EC7
		public static int GREATWALLC30 	= 0x06;		//长城C30
		public static int GREATWALLH2 	= 0x07;		//长城哈弗H2
		public static int GREATWALLW6 	= 0x08;		//长城风骏6
		public static int BQD60 		= 0x09;		//北汽绅宝D60
		public static int BQX55 		= 0x0A;		//北汽绅宝X55神
	}
	
	// 通话状态
	public static class PhoneState {
		public static int IDLE	   = 0x01;
		public static int INCOMING = 0x02;
		public static int OUTGOING = 0x03;
		public static int SPEAKING = 0x04;
	}
	
	// 类型
	public static enum E_CMD_TYPE{
		eCmd_Type_PopWind,
		eCmd_Type_CarInfo,
		eCmd_Type_BackCar,
		eCmd_Type_CarSet,
		eCmd_Type_AirSet,
		eCmd_Type_Compass,
		eCmd_Type_FuelMil,
		eCmd_Type_HisFuel,
		eCmd_Type_Tpms,
		eCmd_Type_Hybrid,
		eCmd_Type_Dsp,
		eCmd_Type_Other,
	};
	
	// 安卓车机源定义
	public enum SOURCE_DEF{
        ;
        public static final int Src_radio = 0,
        		Src_music     = 1,
        		Src_video     = 2,
        		Src_bluetooth = 3,
        		Src_dvd       = 4,
        		Src_dvr       = 5,
        		Src_dtv       = 6,
        		Src_atv       = 7,
        		Src_avin      = 8,
        		Src_ipod      = 9,
        		Src_phonelink = 10,
        		Src_backcar   = 11,
        		Src_bt_phone  = 12,
        		Src_photo     = 13,
        		Src_dummy     = 14,
        		Src_max       = 15;
    };
    
    //电话状态
    public static final int PHONE_NONE           = 0;
    public static final int PHONE_STATE_IDLE     = 1;
    public static final int PHONE_STATE_INCOMING = 2;
    public static final int PHONE_STATE_OUTGOING = 3;
    public static final int PHONE_STATE_SPEAKING = 4;
    public static final int PHONE_STATE_WAITING  = 5;
    public static final int PHONE_STATE_HELD     = 6;
    
    public static final int ID3_TITLE 	= 0;
    public static final int ID3_ALUM 	= 1;
    public static final int ID3_AUTHOR	= 2;
    
    public static class TimeInfo{
    	public int  iYear;
    	public byte byMonth;
    	public byte byDay;
    	public byte byHour;
    	public byte byMinute;
    	public byte bySecond;
    	public byte by24Mode;
    	public byte byAmPm;
    }
	
	// 按键定义
	public static final String Volume_add = "Volume_add"; 
	public static final String Volume_del = "Volume_del"; 
	public static final String Volume_mute = "Volume_mute"; 
	public static final String Media_pre   = "Media_pre"; 
	public static final String Media_next  = "Media_next"; 
	public static final String Media_pause = "Media_pause"; 
	public static final String Phone_dial  = "Phone_dial"; 
	public static final String Phone_hang  = "Phone_hang"; 
	public static final String Radio_fm    = "Radio_fm"; 
	public static final String Radio_am    = "Radio_am";
	public static final String Radio_as    = "Radio_as"; 
	public static final String Src_home    = "Src_home"; 
	public static final String Src_mode    = "Src_mode"; 
	public static final String Src_radio   = "Src_radio"; 
	public static final String Src_dvd     = "Src_dvd";
	public static final String Src_usb     = "Src_usb";
	public static final String Src_bt      = "Src_bt"; 
	public static final String Src_set     = "Src_set"; 
	public static final String Src_navi    = "Src_navi"; 
	public static final String Src_eq      = "Src_eq"; 
	public static final String Src_aux     = "Src_aux";
	public static final String Src_info    = "Src_info";
	public static final String Power       = "Power"; 
	public static final String Eject       = "Eject"; 
	public static final String Blacklight  = "Blacklight"; 
	public static final String Enter       = "Enter"; 
	public static final String Back        = "Back";
	
	public static final String Num0        = "Num0";
	public static final String Num1        = "Num1";
	public static final String Num2        = "Num2";
	public static final String Num3        = "Num3";
	public static final String Num4        = "Num4";
	public static final String Num5        = "Num5";
	public static final String Num6        = "Num6";
	public static final String Num7        = "Num7";
	public static final String Num8        = "Num8";
	public static final String Num9        = "Num9";
	public static final String Numa        = "Numa";
	public static final String Numb        = "Numb";
	
	public static final String Speech      = "Speech";
	public static final String K1          = "k1";
	public static final String K2          = "k2";
	public static final String K3          = "k3";
	public static final String K4          = "k4";
	public static final String Left        = "Left";
	public static final String Right       = "Right";
	public static final String Up          = "Up";
	public static final String Down        = "Down";
	
	public static final String PAGE_SW 	   = "Page_sw";
	public static final String MEM_SPEED   = "Mem_speed";

	public static final int AIR_CMD_ID = 1;
	public static final int CARINFO_CMD_ID = 2;
	public static final int BASE_CMD_ID = 3;
	public static final int CANKEY_CMD_ID = 4;
	public static final int RADAR_CMD_ID = 5;
	public static final int WHEEL_CMD_ID = 6;
	public static final int PARK_CMD_ID = 7;
	public static final int LIGHT_CMD_ID = 8;
	public static final int SOURCE_CMD_ID = 9;
	public static final int PHONESTS_CMD_ID = 10;
	public static final int INTANTOIL_CMD_ID = 11;
	public static final int TPMS_CMD_ID = 12;
	public static final int CUROIL_CMD_ID = 13;
	public static final int DSP_CMD_ID = 14;
	public static final int SYSINFO_CMD_ID = 15;
	public static final int OILELE_CMD_ID = 16;
	public static final int FUELMIL_CMD_ID = 17;
	public static final int COMPASS_CMD_ID = 18;
	public static final int CAR_SET_CMD_ID = 19;
	public static final int SET_INFO_CMD_ID = 20;
	public static final int TRIP_INFO_MINOIL = 21;
	public static final int TRIP_INFO_HISOIL = 22;
	public static final int CAR_TIME_INFO = 23;
	public static final int OUT_TEMP_ID = 24;
	public static final int CONTROLENABLE_INFO = 25;
	public static final int DRIVING_DATA = 26;
	public static final int CONV_CONSUMERS = 27;
	public static final int CAR_SERVICE = 28;
	public static final int USB_IPOD_INFO = 29;
	public static final int CAN_AUDIO_INFO = 30;
	public static final int SYNC_STATE = 31;
	public static final int SYNC_MENU = 32;
	public static final int SYNC_OPTION = 33;
	public static final int SYNC_MEDTIME = 34;
	public static final int SYNC_TALKTIME = 35;
	public static final int ON_STAR_ID = 36;
	public static final int CD_STATE_ID = 37;
	public static final int CD_TX_INFO_ID = 38;
	public static final int PSA_WARN_INFO = 39;
	public static final int PSA_DIOG_INFO = 40;
	public static final int PSA_FUNC_INFO = 41;
	public static final int PSA_CRU_SPEED = 42;
	public static final int PSA_MEM_SPEED = 43;
	public static final int CAN_FRAGMENT_SW = 44;
	
	public static final int FINISH_BIND = 0x222;
	
	/**
	 * @Function: 空调信息. <br/>
	 */
	public static class AirInfo {

		// 空调状态
		public byte mAiron; // 空调开关
		public byte mAcState; // A/C指示
		public byte mCircleState; // 内外循环
		public byte mAutoLight1; // AUTO 大风灯
		public byte mAutoLight2; // AUTO 小风灯
		public byte mDaulLight; // DAUL灯
		public byte mMaxForntLight; // Max Fornt灯
		public byte mRearLight; // REAR 灯
		public byte mSwitchState;

		public byte mUpwardWind; // 向上送风
		public byte mDowmWind; // 向下送风
		public byte mParallelWind; // 平行送风
		public byte mDisplay; // 空调显示
		public byte mWindRate; // 风量 1-3级
		public byte mMaxWindlv = 7; // 风量 1-3级
		public byte mAutoWind;
		public byte mEco;
		
		// Hi World分为左右风速
		public byte mLeftWindRate;
		public byte mRightWindRate;

		public float mLeftTemp; // 左侧温度
		public float mRightTemp; // 右侧温度
		public byte  mbyLeftTemplv;
		public byte  mbyRightTemplv;
		public float mOutTemp;
		public boolean mOutTempEnable;
		public boolean mbshowLTemp = true;
		public boolean mbshowRTemp = true;
		public boolean mbshowLTempLv = false;
		public boolean mbshowRTempLv = false;

		public byte mAqsInCircle; // AQS内循环
		public byte mLeftHotSeatTemp; // 左热椅 00 不显示 01b~11b 1~3级温度
		public byte mRearLock; // REAR LOCK
		public byte mAcMax; // AC MAX
		public byte mRightHotSeatTemp; // 右热椅 00 不显示 01b~11b 1~3级温度

		// HiWorld大众
		public byte mBackAirAble; // 后区空调使能
		public byte mBWDefogger; // 后窗除雾/后窗加热
		public byte mFWDefogger; // 前窗除雾
		public byte mLeftUpwardWind; // 左向上送风
		public byte mLeftDowmWind; // 左向下送风
		public byte mLeftParallelWind; // 左平行送风
		public byte mRightUpwardWind; // 右向上送风
		public byte mRightDowmWind; // 右向下送风
		public byte mRightParallelWind; // 右平行送风

		public byte mTempUnit; // 温度单位

		public byte mManual; // 手动/自动空调：1 手动， 0 自动

		public boolean bAirUIShow = true;
		public byte mSync; // SYNC状态
		public float mMinTemp; // 最小温度
		public float mMaxTemp; // 最大温度
		public byte mClimate = 0; // 切换空调主机:0不切换，1切换
		public byte mWindMode;	
	}

	/**
	 * @ClassName: CarInfo <br/>
	 * @Function: 车辆信息. <br/>
	 */
	public static class CarInfo {

		// data[1]
		public byte mSafetyBelt; // 安全带 、安全带告警
		public byte mWashLiquid; // 洗涤液 、 洗涤液告警
		public byte mHandbrake; // 手刹
		public byte mHoodBoxDoor; // 引擎盖
		public byte mTailBoxDoor; // 尾箱门
		public byte mRightBackDoor; // 右后门
		public byte mLeftBackDoor; // 左后门
		public byte mRightFrontDoor; // 右前门
		public byte mLeftFrontDoor; // 左前门

		public int mRotationlSpeed; // 转速=Data1*256 + Data2;
		public float mCurrentSpeed; // 瞬时速度 =（Data3*256+Data4）*0.01
		public float mBatteryVoltage; // 电池电压
		public float mOutCarTemp; // 车外温度
		public byte mOutCarTempFlag; // 车外温度单位标签
		public int mTravelMileage; // 行程里程
		public float mSurplusmOil; // 剩余油量
		public byte mAntifreezeFluid; // 防冻液 保留字段

		public byte mDoorFlag; // 门状态有效
		public byte mLowOilWarn; // 油量过低告警
		public byte mLowBatVolWarn; // 电池电压过低告警
		public float mIntantConsumeOil; // 瞬时耗油

		// 睿志诚-本田添加
		public float mAverageSpeedMin; // 每分钟平均车速
		public int mElapsedTimeMin; // 每分钟行驶时间
		public int mCruisingRangeMin; // 每分钟行驶里程
		public byte mUintMin; // 计量单位

		public byte mHistoryOilUnit; // 历史耗油单位
		public float mCurrentHistoryOil; // 当前行程耗油
		public float mTripFuel1; // 行程1油耗
		public float mTripFuel2; // 行程2油耗
		public float mTripFuel3; // 行程3油耗
		public float mTripFuel4; // 行程4油耗
		public float mTripFuel5; // 行程5油耗

		public byte mGears; // 档位
		public byte mNearlyHeadlight; // 近光灯
		public byte mFarHeadlight; // 远光灯
		public byte mWidlelight; // 示宽灯

		public byte mBackCarLight; // 倒车灯
		public byte mBrakeLight; // 刹车灯
		public byte mLeftTurnLight; // 左转向灯
		public byte mRightTurnLight; // 左转向灯
		public byte mWarningLight; // 警示灯
		public byte mBackFogLamp; // 后雾灯
		public byte mFontFogLamp; // 前雾灯
		public byte mBackDoorLockState; // 后排车门锁
		public byte mFrontLeftDoorLockState; // 左前门锁
		public byte mFrontRightDoorLockState; // 右前门锁
		public byte mUpWindowSwitch; // 天窗
		public long mTotalMileage; // 总里程
		public long mMileage; // 续航里程
		public long mMileageA; // 里程A
		public long mMileageB; // 里程B
		public float mAverageSpeed; // 平均速度

		//Golf7
		public byte mbylangIndex; // 语言列表

		// Esc system
		public byte mbyEscSysState;

		// Types
		public byte mbySpeedWarn;
		public byte mbySpeedUnit;
		public int  miSpeedValue;

		// Drive assistance 1
		public byte mbyLaneAssistEnable;
		public byte mbyLaneAssist;
		public byte mbyDriverAlertSys;

		// Drive assistance 2
		public byte mbyLastDistance;
		public byte mbyFrontAssistActive;
		public byte mbyFrontAssistAdvance;
		public byte mbyFrontAssistDisplay;
		public byte mbyAccDriveProgram;
		public byte mbyAccDistance;

		// Parking and manoeuvring
		public byte mbyActivateAutoMatically;
		public byte mbyFrontVolume;
		public byte mbyFrontToneSetting;
		public byte mbyRearVolume;
		public byte mbyRearToneSetting;

		// light
		public byte mbySwicthOnTime;
		public byte mbyAutoHeadlightCtrlInRain;
		public byte mbyLaneChangeFlash;
		public byte mbyDaytimeRunlight;
		public byte mbyInstrumentSwlight;
		public byte mbyComeHomeFunction;
		public byte mbyLeaveHomeFunction;
		public byte mbyTravlMode;
		public byte mbyDoorAmbientlight;
		public byte mbyFootwelllight;
		public byte mbyDynclightAssist;
		public byte mbyDyncBiglightAssist;

		// Mirrors / wipers
		public byte mbySynchronousAdjust;
		public byte mbyLowerWhileReverse;
		public byte mbyAutomaticWiperInRain;
		public byte mbyRearWindWiperIngrear;
		public byte mbyFoldAwayAfterParking;

		// open/close
		public byte mbyConvOpen;
		public byte mbyDoorUnlock;
		public byte mbyAutomaticlock;
		public byte mbyAcousticConfirmation;

		// MFD
		public byte mbyCurrentConsumption;
		public byte mbyAverageConsumption;
		public byte mbyConvenienceConsumers;
		public byte mbyEcoTips;
		public byte mbyTravellingTime;
		public byte mbyDistanceTraveled;
		public byte mbyAverageSpeed;
		public byte mbyDigitalSpeedDisplay;
		public byte mbySpeedWarning;
		public byte mbyOilTemperature;

		// Unit
		public byte mbyDistanceUnit;
		public byte mbySpeedUnint;
		public byte mbyTemperatureUnit;
		public byte mbyVolumeUnit;
		public byte mbyConsumptionUnit;
		public byte mbyPressureUnit;
		
		//Jeep
		public byte  mbyParksenseRadar;			//Parksense雷达泊车
		public byte  mbyFrontParksenseVol;		//前Parksense音量
		public byte  mbyAfterParksenseVol;		//后Parksense音量
		public byte  mbyBackcarTiltMirror;		//倒车时倾斜后视镜
		public byte  mbyParksenseDynTrack;		//Parkview影响泊车动态引导线
		public byte  mbyRainsensingwipers;		//雨量感应式雨刷
		public byte  mbyRampstartassist;			//坡道起步辅助
		public byte  mbyBusywarning;				//忙点警报
		public byte  mbyParksensestaTrack;		//Parkview影响泊车固定引导线
		public byte  mbyRearRadarparking;			//后雷达泊车自动辅助
		public byte  mbyAllowautobrakeser;		//允许自动制动服务
		public byte  mbyAutomaticparkbrake;		//自动泊车制动
		public byte  mbyLanedevcorrection;		//车道偏离校正力度
		public byte  mbyAutobrakfrontcwarn;		//前方碰撞警报自动制动
		public byte  mbyForwardcollisionwarn;		//前方碰撞警告
		public byte  mbyAutoantidazzlelight;		//自动防炫远光灯
		public byte  mbyDooralarm;				//车门报警
		public byte  mbyAutodriverseatvehicle;	//车辆启动时自动启动主驾驶座椅加热
		public byte  mbyAutolightuphdlightclose;	//靠近时大灯自动亮起
		public byte  mbyLaneDepartureWarn;		//车道偏离警告
		public byte  mbyBuzzerswitchonoff;		//蜂鸣器开关

		public byte  mbyHeadlightoffdelay;		//大灯关闭延时
		public byte  mbySautowheadlightwiper;		//启动雨刷时自动启动大灯
		public byte  mbyDaytimeRunninglight;		//日间行车灯
		public byte  mbyLockcarturnlightflash;	//锁车转向灯闪烁

		public byte  mbyAutomaticdoorlock;		//自动车门锁定
		public byte  mbyAutounlockgetoff;			//下车时自动解锁
		public byte  mbyDrivingautolatch;			//行车自动落锁
		public byte  mbyLockcarprompttone;		//锁车时发出提示音
		public byte  mbyForfirstunlockcarkeys;	//首次按车钥匙解锁
		public byte  mbyNokeyentry;				//无钥匙进入
		public byte  mbySmartkeypersonal;			//智能钥匙个性化设置
		public byte  mbyElectrictaildooralarm;	//电动尾门报警

		public byte  mbySeatconvenientinout;		//座椅便利进出
		public byte  mbyEnginepoweroffdelay;		//发动机电源关闭延迟
		public byte  mbyUpdownautosuspension;		//上下车自动调节悬架
		public byte  mbyDispsuspensioninfo;		//显示悬架信息
		public byte  mbyTirejack;					//轮胎千斤顶
		public byte  mbyTransportmode;			//运输模式
		public byte  mbyTirecalibrationmodel;		//轮胎校准模式

		public byte  mbyMirrorlightmirror;		//后视镜调光镜
		public byte  mbyUnitSetting;				//单位设置
		public byte  mbylangSetting;				//语言设置
	}

	/**
	 * 
	 * @ClassName: IntantOilInfo
	 * @Functions: 瞬时油耗
	 */
	public static class IntantOilInfo {
		public byte mIntantConsumeOilUint; // 瞬时耗油单位
		public float mIntantConsumeOil; // 瞬时耗油
	}

	/**
	 * 
	 * @ClassName: CurOilInfo
	 * @Functions:当前油耗
	 */
	public static class CurOilInfo {
		// 过去15分钟，每分钟耗油
		public byte m15minOilFuelUint; // 油耗单位
		public float m15minOilFuel_15; // 第15分钟，油耗
		public float m15minOilFuel_14; // 第14分钟，油耗
		public float m15minOilFuel_13; // 第13分钟，油耗
		public float m15minOilFuel_12; // 第12分钟，油耗
		public float m15minOilFuel_11; // 第11分钟，油耗
		public float m15minOilFuel_10; // 第10分钟，油耗
		public float m15minOilFuel_9; // 第9分钟，油耗
		public float m15minOilFuel_8; // 第8分钟，油耗
		public float m15minOilFuel_7; // 第7分钟，油耗
		public float m15minOilFuel_6; // 第6分钟，油耗
		public float m15minOilFuel_5; // 第5分钟，油耗
		public float m15minOilFuel_4; // 第4分钟，油耗
		public float m15minOilFuel_3; // 第3分钟，油耗
		public float m15minOilFuel_2; // 第2分钟，油耗
		public float m15minOilFuel_1; // 第1分钟，油耗
	}

	/**
	 * @ClassName: RadarInfo <br/>
	 * @Function: 雷达信息. <br/>
	 */
	public static class RadarInfo {

		// 前雷达
		public byte mFrontLeftDis; // 前左
		public byte mFrontLeftCenterDis; // 前左中
		public byte mFrontRightCenterDis; // 右左中
		public byte mFrontRightDis; // 前右

		// 后雷达
		public byte mBackLeftDis; // 后左
		public byte mBackLeftCenterDis; // 后左中
		public byte mBackRightCenterDis; // 后左中
		public byte mBackRightDis; // 后右

		// 左雷达
		public byte mLeftUpDis; // 左上
		public byte mLeftUpCenterDis; // 左上中
		public byte mLeftDnCenterDis; // 左下中
		public byte mLeftDnDis; // 左下

		// 右雷达
		public byte mRightUpDis; // 右上
		public byte mRightUpCenterDis; // 右上中
		public byte mRightDnCenterDis; // 右下中
		public byte mRightDnDis; // 右下

		// 威驰丰田 雷达状态
		public byte mRadarShowSwitch; // 雷达显示开关
		public byte mDistance; // 距离
		public byte mRadarSwtich; // 雷达开关
		public byte mVol; // 音量

		public byte mFrontALLDis;
		public byte mBackAllDis;
		
		public byte mbyRightShowType; // 小雷达右侧显示类型 
									  // 0-不显示 ，
									  // 1-显示静音/泊车/全雷达切换 ，
									  // 2-显示静音和全雷达切换
									  // 3-显示视频角度切换
									  // 4-...
	}

	/**
	 * @ClassName: BaseInfo <br/>
	 * @Function: 基本信息. <br/>
	 */
	public static class BaseInfo {

		// SimpleDz Data0
		public byte mBackCarState; // 倒车状态
		public byte mPStopBlockState; // P档状态
		public byte mLightState; // 灯光信息

		// 欣朴 大众
		public byte mFrontBoxDoor;
		public byte mTailBoxDoor;
		public byte mRightBackDoor;
		public byte mLeftBackDoor;
		public byte mRightFrontDoor;
		public byte mLeftFrontDoor;

		public boolean getDoorValid() {
			boolean bRet = false;

			if (mLeftFrontDoor == 1 || mLeftBackDoor == 1
					|| mRightFrontDoor == 1 || mRightBackDoor == 1
					|| mTailBoxDoor == 1 || mFrontBoxDoor == 1) {

				bRet = true;
			}

			return bRet;
		};

		// 睿志诚 丰田
		public byte mTailElectricDoor;
		public byte mTailElectricDoorDirect;
		public byte mIG;

		// 威驰 大众信息 0x81-Data0
		public byte mBT; 		// 蓝牙是否有效
		public byte mRadar; 	// 雷达信息是否有效
		public byte mKeyIn; 	// 信号是否有效
		public byte mILLLight;  // ILL灯光是否开启
		public byte mACCLight;  // ACC灯是否上电

		// Rasie Honda新增
		public byte mTurnLight; // 转向灯状态（仅歌诗图有）
		
		//Ford
		public byte mbyTractionCtrl;	// 牵引力控制系统
		public byte mbyTrunLightOnce;	// 转向灯是否设定闪烁一次 true:闪烁一次 false:闪烁三次
		public byte mbyMsgToneOn;		// 信息提示音是否打开开
		public byte mbyWarnToneOn;		// 警告提示音是否打开开
		public byte mbyToneType;		// 当前发声模式(由主机端决定) 0==OFF,1==慢速,2==中速,3==急促,4==自动(默认值，由解码盒自行判断需要发出何种声音)
		public byte mbyMileUnit;		// 里程单位 true:MILE  false:KM
		
		public byte mbyPlan;			// 行驶规划 0~5 有效值（0：全空5：全满） 6~7无效值（原车显示值为2，两片叶）
		public byte mbySpeed;			// 车速		0~5 有效值（0：全空5：全满） 6~7无效值（原车显示值为2，两片叶）
		public byte mbyAotuBright;		// 亮度是否自动
		public byte mbyEngineHotPer;	// XX%里程引擎未热百分比 0==0%-----0x64(DEC100)==100%
		public byte mbyShowEngineHot;	// 是否显示引擎未热项
		
		public byte mbyRainSensor;		// 雨水感应器
		public byte mbyInteriorlight;	// 车内照明
		public byte mbyParklockCtrl;	// Park lock Ctrl
	}

	/**
	 * @ClassName: ParkAssistInfo <br/>
	 * @Function: 泊车辅助系统状态. <br/>
	 */
	public static class ParkAssistInfo {

		public byte mRadarSoundState; // 雷达发声状态
		public byte mParkSystemState; // 泊车,手刹是否拉起
		public byte mRoadsidePark; // 路边驻车
		public byte mCarbarnPark; // 车库驻车

	}

	/**
	 * @ClassName: VerInfo<br/>
	 * @Function: 版本信息 <br/>
	 */
	public static class VerInfo {
		public String mVersion; // 版本信息
	}

	/**
	 * 
	 * @ClassName: WheelInfo
	 * @Functions: 方向盘转角信息
	 */
	public static class WheelInfo {
		public int mEps; // 方向盘角度
		public int mDirect; // 方向，向左，向右
	}

	public static class SetInfo {
		public byte mbyRadarDis;
		public byte mbyFuelUnit;
		public byte mbyColorTheme;
	}

	/**
	 * 
	 * @ClassName: PowerAmplifier
	 * @Functions: 功放信息
	 */
	public static class PowerAmplifier {
		public byte mFAD; // FAD指示
		public byte mBAL; // BAL指示
		public byte mBASS; // BASS指示
		public byte mTRE; // TRE指示
		public byte mMID; // MID指示
		public byte mASL; // ASL指示
		public int mCurVol; // 当前音量
		public byte mVolByASL; // 车辆音量跟随ASL
		public byte mMute;
		public byte mDspDev;
	}

	/**
	 * 
	 * @ClassName: SystemInfo
	 * @Functions: 系统信息
	 */
	public static class SystemInfo {
		public byte mVehiclePowerAmplifier; // 原车功放
		public byte mBackseat; // 后座
		public byte mPanoramicCamera; // 全景摄像
		public byte mPanoramicView; // 全景切入
		public byte mVehiclePowerAmplifierSwitch; // 原车功放开关
		public byte mMuteSwitch; // 静音
	}

	/**
	 * 
	 * @ClassName: OilEleInfo
	 * @Functions: 油电混合信息
	 */
	public static class OilVatteryInfo {
		public byte mBatteryVoltage; // 电池电压
		public byte mHybridEleVehicle; // 油电混合车型
		public byte mMotorDriveVattery; // 马达驱动电池
		public byte mMotorDriveWheel; // 马达驱动车轮
		public byte mEngineDriveMotor; // 发动机驱动马达
		public byte mEngineDriveWheel; // 发动机驱动车轮
		public byte mVatteryDriveMotor; // 电池驱动马达
		public byte mWheelDriveMotor; // 车轮驱动马达
	}

	/**
	 * 
	 * @ClassName: BackLightInfo
	 * @Functions:背光信息
	 */
	public static class BackLightInfo {
		public int mLight;
	}

	/**
	 * 
	 * @ClassName: WheelKeyInfo
	 * @Functions: 方向盘按键
	 */
	public static class WheelKeyInfo {
		public String mstrKeyCode; // KeyCode
		public int mKeyCode;
		public int mKeyStatus; // KeyStatus
		public int mKnobSteps; // 步进
		public byte mbyKeyRepeat = 0;
		public boolean bInternal = false; // 按键是否内部处理
	}

	/**
	 * 
	 * @ClassName: BreakInfo
	 * @Functions: 解析刹车状态信息
	 */
	public static class BreakInfo {
		public byte mBreakFlag; // 是否刹车
	}

	/**
	 * 
	 * @ClassName: RearviwInfo
	 * @Functions: 解析倒车状态信息
	 */
	public static class RearviwInfo {
		public byte mRearviwFlag; // 是否倒车
	}

	/**
	 * 
	 * @ClassName: TpmsStatusInfo
	 * @Functions: TPMS 状态信息
	 */
	public static class TPMSInfo {
		public byte mIsExistDevice; // 是否有TPMS
		public byte mIsNormal; // 状态是否正常
		public byte mShowSpareTire; // 显示备胎
		public byte mTpmsUnit; // 计量单位
		public byte mTireShowMode; // 轮胎显示方式
		public int mFLTirePressure; // 前左轮胎气压
		public int mFRTirePressure; // 前右轮胎气压
		public int mBLTirePressure; // 后左轮胎气压
		public int mBRTirePressure; // 后右轮胎气压
		public int mSpareTirePressure; // 备胎气压

		public int mFLTireTemp; 	// 前左轮胎温度
		public int mFRTireTemp; 	// 前右轮胎温度
		public int mBLTireTemp; 	// 后左轮胎温度
		public int mBRTireTemp; 	// 后右轮胎温度
		
		public String mfFLTirePressure; // 前左轮胎气压
		public String mfFRTirePressure; // 前右轮胎气压
		public String mfBLTirePressure; // 后左轮胎气压
		public String mfBRTirePressure; // 后右轮胎气压
	}
	
	public static class TpmsWarn {
		public byte mbyFLTireWarn; 			//前左轮胎警告
		public byte mbyFRTireWarn; 			//前右轮胎警告
		public byte mbyBLTireWarn; 			//后左轮胎警告
		public byte mbyBRTireWarn; 			//后右轮胎警告
		
		public boolean bFlSensorValid;		//前左传感器失效
		public boolean bFlLpressureW;		//前左低压报警
		public boolean bFlHpressureW;		//前左高压报警
		public boolean bFlAbnormal;         //左前异常

		public boolean bFrSensorValid;		//前右传感器失效
		public boolean bFrLpressureW;		//前右低压报警
		public boolean bFrHpressureW;		//前右高压报警
		public boolean bFrAbnormal;         //前右异常

		public boolean bRlSensorValid;		//后左传感器失效
		public boolean bRlLpressureW;		//后左低压报警
		public boolean bRlHpressureW;		//后左高压报警
		public boolean bRlAbnormal;         //后左异常

		public boolean bRrSensorValid;		//后右传感器失效
		public boolean bRrLpressureW;		//后右低压报警
		public boolean bRrHpressureW;		//后右高压报警
		public boolean bRrAbnormal;         //后右异常

		public boolean bSysValid;			//系统失效
	}

	/**
	 * 
	 * @ClassName: VehicleSettings
	 * @Functions: 车辆设置
	 */
	public static class VehicleSettings {

		// 灯光设置
		public byte mDayTimeRunningLights; // 日间行车灯开关
		public byte mHeadlampsOnSensitivity; // 灵敏度
		public byte mLightsOffTimer; // 车内照明关闭时间
		public byte mHeadlampsAutoOFFTimer; // 灯光自动关闭时间

		// 车锁设置
		public byte mAutoLockBySpeed; // AutoLock by speed
		public byte mAutoLockBySHIFTFORMP; // AutoLock by shift from p
		public byte mAutoLockBySHIFTToP; // AutoUnLock by shift to p
		public byte mRemote2PressUnlock; // Remote 2 - press unlock;
		public byte mLockUnLockFeedBackTONE; // Lock/unLock FeedBack TONE

		// 车锁设置2
		public byte mLockHandleKeyTwoTimes; // 操作钥匙两次解锁
		public byte mLockDrivingSeatOpenDoor; // 驾驶席开门联动解锁
		public byte mLockIntelligentDoor; // 智能车门解锁
		public byte mLockIntelligentVehicle; // 智能车锁
		public byte mUnLockFlash; // 解锁闪烁
		public byte mElectricdooradjust; // 电动后面档位调节

		// 空调设置
		public byte mAirSwitchAndAutoLinkage; // 空调和Auto健联动
		public byte mAirCircleAndAutoLinkage; // 内外气切换和Auto健联动
		public byte mAutoRelockTimer; // 自动锁时间

		public byte mRadarTrack = 0x00;
	}

	public static class OutTemputerInfo {
		public float mOutCTemp; // 车外温度
		public float mOutFTemp;
		public boolean mbEnable;
		public boolean mbFEndble;
	}

	/**
	 * 本田油耗
	 */
	public static class FuelMilInfo {
		// 本田油耗和里程
		public byte mIndex; // 索引
		public byte mImmediateFuel; // 即时油耗
		public int mCurrentAverFuel; // 当前平均油耗
		public int mHistoryAverFuel; // 历史平均油耗
		public int mAverFuel; // 平均油耗
		public int mTripaIndex1; // 索引1的TRIPA
		public int mCanDriveMil; // 续行里程
		public byte mImmediateFuelUnit; // 即时油耗单位
		public byte mCHAverFuelUnit; // 当前平均油耗/历史平均油耗单位
		public byte mAverFuelUnit; // 平均油耗单位
		public byte mTripaUnit; // TRIPA单位
		public byte mCanDriveMilUnit; // 续行里程单位
		public byte mFuelRange; // 油耗量程

		public int mFirstTripaRecord; // 第一条TRIPA记录
		public int mFirstAverFuelRecord; // 第一条平均油耗记录
		public int mSecondTripaRecord; // 第二条TRIPA记录
		public int mSecondAverFuelRecord; // 第二条平均油耗记录
		public int mThirdTripaRecord; // 第三条TRIPA记录
		public int mThirdAverFuelRecord; // 第三条平均油耗记录
	}

	/**
	 * 
	 * @ClassName: UsbIpodInfo
	 */
	public static class UsbIpodInfo {
		// 本田 原车USB、原车IPOD
		public byte USBPlayState; // USB播放状态
		public byte USBStatus; // USB状态
		public byte USBPlayMin; // USB播放分钟
		public byte USBPlaySec; // USB播放秒
		public byte USBCurrentTrackH; // USB当前曲目高位
		public byte USBCurrentTrackL; // USB当前曲目低位
		public byte USBTotalTrackH; // USB总曲目高位
		public byte USBTotalTrackL; // USB总曲目高位
		public byte USBFolder; // USB文件夹
		public byte USBPlayRate; // USB播放进度
	}

	/**
	 * 
	 * @ClassName: CompassInfo
	 */

	public static class CompassInfo {
		// 指南针
		public boolean CompassAdjust; // 指南针调整
		public byte Compassarea; // 指南针区域
		public int compassAngle; // 指南针角度
		public byte mbyCompassDir;
		public byte mbyCompassState;
	}

	/**
	 * 
	 * @ClassName:CarSetting
	 */

	public static class CarSetting {
		// 本田原车设置
		// 里程设置
		public int mOutTemp; // 车外温度显示 0-10: -5 - 5
		public int mTripAReset; // 里程A重设条件的切换
		public int mTripBReset; // 里程B重设条件的切换

		// 灯光设置
		public int mInLightDimTime; // 车内灯光减光时间
		public int mHeadLightTime; // 前大灯自动熄灭时间
		public int mAutoLightSend; // 自动点灯灵敏度

		// 车锁设置
		public int mAutoLockDoor; // 自动落锁方式
		public int mAutoUnLockDoor; // 自动解锁方式
		public int mDoorUnLock; // 车门解锁
		public int mRelockTime; // 自动重锁时间
		public boolean mKeyLockAnswer; // 遥控落锁提示

		// 无线锁设置
		public boolean mKeylessBeep; // 遥控门锁蜂鸣
		public boolean mRemoteSys; // 遥控启动系统
		public int mDoorLockMode; // 门锁模式
		public boolean mKeylessLight; // 遥控门锁车边灯提示
		public int mAutoInSend; // 自动车内照明灵敏度

		// 车身设置
		public int mAdjustAlarm; // 调整报警音量
		public boolean mFuelBackLight; // 节能模式的背景照明
		public boolean mMsgNotify; // 新消息提醒
		public int mSpeedUnit; // 速度单位
		public boolean mTachometer; // 转速计显示
		public boolean mWalkAwayLock; // 离开锁止个性化设定
		public boolean mAutoHeadLight; // 雨刷和自动大灯联动个性化设定

		// 驾驶辅助系统设置
		public int mAlarmSysVolume; // 语音报警系统音量
		public boolean mEngineAutoMatic;// 发动机节能自动启停
		public boolean mAccPromptTone; // ACC前车探车警示音
		public boolean mPauseLKAS; // 暂停LKAS提示音
		public int mWarnDisctance; // 设定前方危险警告距离
		public int mLaneDeparture; // 车道偏离辅修系统设定

		public boolean mTachmeterSet; // 转速表设置
	}

	/**
	 * 
	 * ClassName:CarTimeInfo
	 * 
	 * @function:车身时间
	 * @author Kim
	 * @Date: 2016-7-4下午2:21:34
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public static class CarTimeInfo {
		public int miYear;
		public byte mbyMonth;
		public byte mbyDay;

		public byte mby24Mode;
		public byte mbyHour;
		public byte mbyMinute;
		public byte mbySecond;
		public byte mbyFormat;
	}

	/**
	 * 
	 * ClassName:ControlEnable
	 * 
	 * @function:TODO
	 * @author Kim
	 * @Date: 2016-7-4下午7:30:54
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public static class ControlEnable {
		// Esc system
		public byte mbyEscSysStateEnable;

		// Types
		public byte mbySpeedWarnEnable;
		public byte mbySpeedUnitEnable;

		// Drive assistance 1
		public byte mbyLaneAssistEnable;
		public byte mbyDriverAlertSysEnable;

		// Drive assistance 2
		public byte mbyLastDistanceEnable;
		public byte mbyFrontAssistActiveEnable;
		public byte mbyFrontAssistAdvanceEnable;
		public byte mbyFrontAssistDisplayEnable;
		public byte mbyAccDriveProgramEnable;
		public byte mbyAccDistanceEnable;

		// Parking and manoeuvring
		public byte mbyActivateAutoMaticallyEnable;
		public byte mbyFrontVolumeEnable;
		public byte mbyFrontToneSettingEnable;
		public byte mbyRearVolumeEnable;
		public byte mbyRearToneSettingEnable;

		// light
		public byte mbySwicthOnTimeEnable;
		public byte mbyAutoHeadlightCtrlInRainEnable;
		public byte mbyLaneChangeFlashEnable;
		public byte mbyDaytimeRunlightEnable;
		public byte mbyInstrumentSwlightEnable;
		public byte mbyComeHomeFunctionEnable;
		public byte mbyLeaveHomeFunctionEnable;
		public byte mbyTravlModeEnable;
		public byte mbyDoorAmbientlightEnable;
		public byte mbyFootwelllightEnable;
		public byte mbyDynclightAssistEnable;
		public byte mbyDyncBiglightAssistEnable;

		// Mirrors / wipers
		public byte mbySynchronousAdjustEnable;
		public byte mbyLowerWhileReverseEnable;
		public byte mbyAutomaticWiperInRainEnable;
		public byte mbyRearWindWiperIngrearEnable;
		public byte mbyFoldAwayAfterParkingEnable;

		// open/close
		public byte mbyConvOpenEnable;
		public byte mbyDoorUnlockEnable;
		public byte mbyAutomaticlockEnable;
		public byte mbyAcousticConfirmationEnable;

		// MFD
		public byte mbyCurrentConsumptionEnable;
		public byte mbyAverageConsumptionEnable;
		public byte mbyConvenienceConsumersEnable;
		public byte mbyEcoTipsEnable;
		public byte mbyTravellingTimeEnable;
		public byte mbyDistanceTraveledEnable;
		public byte mbyAverageSpeedEnable;
		public byte mbyDigitalSpeedDisplayEnable;
		public byte mbySpeedWarningEnable;
		public byte mbyOilTemperatureEnable;

		// Unit
		public byte mbyDistanceUnitEnable;
		public byte mbySpeedUnintEnable;
		public byte mbyTemperatureUnitEnable;
		public byte mbyVolumeUnitEnable;
		public byte mbyConsumptionUnitEnable;
		public byte mbyPressureUnitEnable;
	}
	/**
	 * 
	 * ClassName:DrivingData
	 * 
	 * @function:TODO
	 * @author Kim
	 * @Date:  2016-7-5上午9:59:48
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public static class DrivingData {
		public byte mbyPageId;
		public int  miRange;
		public byte mbyRangeUnit;
		
		public byte mbyDistanceUnit;
		public int  miDistanceSinceStart;
		public int  miDistanceSinceRefuel;
		public int  miDistanceLongTerm;
		
		public byte mbyConsumptionUnit;
		public float  mfAvgConsSinceStart;
		public float  mfAvgConsSinceRefuel;
		public float  mfAvgConsLongTerm;
		
		public byte mbyAvgSpeedUnit;
		public float  mfAvgSpeedSinceStart;
		public float  mfAvgSpeedSinceRefuel;
		public float  mfAvgSpeedLongTerm;
		
		public int  miTravellTimeSinceStart;
		public int  miTravellTimeSinceRefuel;
		public int  miTravellTimeLongTerm;
		
		public byte mbyConvConsumersUnit;
		public int  miConvConsumers;
		
		public byte mbyInstantFuelUnit;
		public int  mbyInstantFuel;
	}
	/**
	 * 
	 * ClassName:ConvConsumers
	 * 
	 * @function:TODO
	 * @author Kim
	 * @Date:  2016-7-5上午11:30:46
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public static class ConvConsumers {
		
		public byte mbyConvNum;
		public byte[] mbyConvConsumers;
	}
	/**
	 * 
	 * CarService
	 * 
	 * @function:TODO
	 * @author Kim
	 * @Date:  2016-7-5下午1:40:16
	 * @Copyright: Copyright (c) 2016
	 * @version 1.0
	 */
	public static class CarService {
		
		public String mStrVehicleNo;
		public byte   mbyVolksWagenDaysType;
		public int    miVolksWagenDays;
		
		public byte   mbyVolksWagenDisUnit;
		public byte   mbyVolksWagenDisType;
		public int	  miVolksWagenDistance;
	
		public byte   mbyOilChangeSerivesDayType;
		public int    miOilChangeSerivesDays;
		
		public byte   mbyOilChangeServiceDisUnit;
		public byte   mbyOilChangeServiceDisType;
		public int    miOilChangeServiceDistance;
	}
	public static class GmCarSet {
		//空调设置
		public byte mAutoWindSet;		//自动模式风量设置
		public byte mAirQualitySensor;	//空气质量传感器设置
		public byte mPartTemp;			//空调分区温度
		public byte mAutoDefogR;		//后窗自动除雾
		public byte mAutoDefogF;		//前窗自动除雾
		public byte mRemoteSeatHeat;	//遥控启动座椅自动加热
		public byte mStartMode;			//空调启动模式设置
		
		public byte mLookForLights;		//寻车灯
		public byte mHeadlampDelaylock; //落锁大灯延时设置
		public byte mPreventDoorlock;	//防止开门时自动落锁
		public byte mAutolockByStart;	//起步自动落锁
		public byte mAUtoUnlockByPark;	//驻车自动解锁
		public byte mDelayLock;			//延时落锁
		
		public byte mRemoteUnlockLight;	//遥控解锁灯光反馈
		public byte mRemotelockLight;	//遥控锁门灯光
		public byte mRemoteUnlock;		//遥控解锁设置
		public byte mBackWiper;			//倒车后雨刷自动启动
		public byte mRemoteThenLock;	//遥控再锁门
		public byte mRemoteStart;		//遥控启动车辆
		public byte mCarBodyCtrl;		//车身中控操作位
		public byte mNearCarUnlock;		//近车解锁设置
		public byte mForgetKey;			//钥匙遗忘提醒
		public byte mPresonByDriver;	
		public byte mAutoRelockDoor;	
		public byte mFlankWarn;			//侧翼盲区警告
		public byte mAutolockByAway;	//离车自动落锁
		
		public byte mAutoCollision;		//自动防撞
		public byte mCarState;			//汽车状态通知
		public byte mAutoWiper;			//自动雨刮
		public byte mRemoteWind;		//遥控车窗控制
		
		public int mSpeed;				//车速信号
		public byte mLan;				//语言状态
		public byte mWarnVol;			//警示音音量
		public byte mRadar;				//倒车辅助雷达
	}
	public static class GmOnStar{
		public String mStrNum;			//电话号码
	}
	
	public static class CanAudio {
		public byte mbyAudioState;
		public boolean mbshow;
		public byte mbyMode; //
	}
	
	//Sync
	public static class SyncState {
		
		public byte  mbySyncMode;	    // Sync工作模式
		// Sync工作状态
		public byte  mbyPresentDev;		// 是否存在设备
		public byte  mbyConnBt;			// 是否蓝牙连接
		public byte  mbyShowMsg;		// 是否是否显示信息图标
		public byte  mbySpeechOn;		// 语音是否打开
		public byte  mbyTalking;		// 是否在通话
		public byte  mbyEnableKey;		// 媒体信息软键是否可用(美版)

		public byte  mbySignal;			// 信号 0~4有效，大于4无效
		public byte  mbyPower;			// 电量 0~4有效，大于4无效
	}
	
	public static class Sync_listInfo {
		public int ilefticon;
		public int irighticon;
		public String strText;
	}
	
	public static class SyncMenu {
		
		public byte   mbyMenuType;		// 当前菜单类型
		public byte   mbySelOption;		// 当前选中项目
		public byte   mbyMenuPer;		// 菜单项目当前显示百分比 (== 0 或 超过100不显示)
		public byte   mbyMsgType;		// 当前对话框类型
		public byte   mbyMsgSelOption;	// 当前对话框选中项目
		public byte   mbyMenuIcon;		// 当前菜单大图标
		public byte   mbyMenuBar;		// 菜单百分比显示条(此项做不做问题不大)
		public HashMap<Integer, Sync_listInfo> mHashMap;
	}
	
	public static class SyncOption {
		public byte mbyRowNum;			// 1-5 表示普通菜单文本行（从上至下）
		// 6-10 表示对话框的文本行
		// 11-14 表示普通菜单中的4个SOFT KEY的字符串/ICON（从左至右）
		// 15-18 表示对话框中的4个SOFT KEY的字符串/ICON（从左至右） (参数值为DEC v1.14)
		public byte mbyTextType;
		public byte mbyLineText;		// 文本类型
		public byte mbyKeyState; 		// 按键类型
		public byte mbyEnable;			// 是否可被选择
		public byte mbyLeftIcon;		// 文本左边ICON 或 按键ICON
		public byte mbyRightIcon;		// 文本右边ICON 按键时无效
		public String strText;			// 文本
	}
	
	public static class SyncMediaTime {
		public byte mbyHuor;
		public byte mbyMinute;
		public byte mbySecond;
	}
	
	public static class SyncTalkTime {
		public byte mbyHuor;
		public byte mbyMinute;
		public byte mbySecond;
	}
	
	public static int[] tbArIcon =  {
		0x01,0x28,0x27,0x91,0x93,0xd6,0xd5,0x11,0x76,0x5e,0x4c,0x24,0x21,0x4e,0x4f,0x50,0x51,0x53,0x54,0x57,//
		0x58,0x59,0x5a,0x5c,0x03,0x79,0x15,0x81,0x77,0x09,0xcd,0x1f,0x0b,0x1c,0x49,0xe5,0x26,0x25,
	};
	
	//Jeep
	public static class CDState {
		public byte mbyCDPlayMode; 
		public byte mbyCDStatus; 
		public int  miCDCurTrack; 
		public int  miCDTotalTrack; 
		public int  miCDCurTrackTime;
		public int  miCDCurTrackTimeNum; 
		public byte mbyCDPlayHour;
		public byte mbyCDPlayMin; 
		public byte mbyCDPlaySec; 
	}
	
	public static class CDTxInfo {
		public int 	miType;
		public int  miTrackNum;
		public String mstrText; 
	}
	
	public static class PsaCarState {
		public byte mRearWiper;				// 后雨刷器(世嘉两箱、3008、2008、C3-XR) 0b: 倒车时停用  1b: 倒车时启用
		public byte mDriverAssist3008;		// 驾驶辅助 (标致 3008)
		public byte mDoorAutoLock;			// 车门自锁状态 0b: 停用        1b: 启用
		public byte mDriverAssistOther;		// 301、世嘉、DS5LS、C4L
		public byte mCentDoorLockSts;		// 中控门锁状态 0b: 未锁        1b: 上锁
		public byte mTripEcoPages;			// 行车电脑当前显示页 00b:page0 01b:page1 10b:page2
		
		public byte mLightStuats;			// 日间照明灯状态 0b:关闭    1b:开启
		public byte mLightDelaySts;			// 大灯延迟状态 
		public byte mAutoRunILL;			// 随动转向大灯
		
		public byte mLightAtmosphere;		// 氛围照明状态
		public byte mRadarStop;				// 倒车雷达停用
		public byte mBackCarSts;			// 倒车状态
		public byte mIsPGear;				// P档或手刹
		public byte mSmallLight;			// 小灯状态
		
		public byte mLightGoHome;			// 伴我回家照明
		public byte mLightHost;				// 迎宾照明
		public byte mEqSet;					// 音效设置
		public byte mFuelSet;				// 油耗单位设置    
		
		public byte mBlindAreaProbe;		// 盲区探测
		public byte mEnginesstopfcdis;		// 发动机启停功能停用
		public byte mGuestfunction;			// 迎宾功能
		public byte mDoorOpenOptions;		// 车门开启选项
		public byte mLauguageSet;			// 语言设置
	}
	
	public static class PsaTripComputer {
		// page0
		public float	mFuelConsumption;				// 百公里瞬时油耗
		public int		mResidualOilMileage;			// 剩余油量可行驶里程
		public int		mObjectiveTomileage;			// 目的地里程
		public byte     mTimingH;						// 启停功能计时  时
		public byte 	mTimingM;						// 启停功能计时  分
		public byte 	mTimingS;						// 启停功能计时  秒
		
		// page1
		public float	mFuelAverage1;					// 百公里平均油耗
		public int 		mSpeedAverage1;					// 平均速度
		public int 		mAccumulatMileage1;				// 累计里程
		
		// page2
		public float	mFuelAverage2;					// 百公里平均油耗
		public int 		mSpeedAverage2;					// 平均速度
		public int 		mAccumulatMileage2;				// 累计里程
	}
	
	public static class PsaWarnInfo {
		public int mWarnInfoTotal;
		public int[] mWarnInfo;
	}
	
	public static class PsaDiagInfo {
		public int mDiagInfoTotal;
		public int[] mDiagInfo;
	}
	
	public static class PsaFuncInfo {
		public int mFuncInfoTotal;
		public int[] mFuncInfo;
	}
	
	public static class PsaMemSpeed {
		public byte mMemory;
		public byte[] mSpeedsSel = new byte[5];
		public int[] mSpeeds = new int[5];
	}
	
	// 巡航速度
	public static class PsaCruSpeed {
		public int[] mCruSpeed = new int[6];
		public int[] mLimitSpeed = new int[6];
	}
	
	public static class CQCarSet {
		public byte mLan;
		
		//空调设置
		public byte mCompressor;	// Auto时压缩机状态
		public byte mCycleCtrl;		// Auto时内外循环控制方式
		public byte mCozy;			// 空调舒适曲线设置
		public byte mAnionMode;		// 负离子模式
		//座椅设置
		public byte mLeftAutoHeat;	// 驾驶座椅自动加热设置
		public byte mRightAutoHeat;	// 副驾驶座椅自动加热设置
		public byte mLeftHeatLv;	// 驾驶位置座椅加热
		public byte mRightHeatLv;	// 副驾驶位置座椅加热  
		public byte mWelcomeFunc;	// 座椅迎宾功能设置
		public byte mAutoKey;		// 智能钥匙自动识别座椅位置
		//驾驶辅助
		public byte mOverSpeed;		// 超速报警（km/h）
		public byte mAlramVol;		// 组合仪表报警音量
		public byte mPowerTime;		// 远程上电时间(分)
		public byte mStartTime;		// 远程启动时间(分)
		public byte mSteerMode;		// 转向模式
		//车身附件
		public byte mRemoteUnlock;	// 遥控解锁
		public byte mLockBySpeed;	// 车速上锁
		public byte mAutoUnlock;	// 自动解锁
		public byte mRemoteWind;	// 遥控左前窗和天窗
		public byte mFrontWiper;	// 前雨刮维护功能
		public byte mRearWiper;		// 后雨刮倒档自动刮刷功能
		public byte mRearviewMirror;// 外后视镜自动折叠
		//灯光控制
		public byte mFollowToHome;	// 伴我回家
		public byte mFogLights;		// 雾灯转向辅助
		public byte mDayLights;		// 日间行车灯
		public byte mLightSensitivity;	//自动灯光灵敏度
	}
	
	public static class MGCarSet {
		//车锁控制
		public byte mDrivingLatch;	// 行车落锁
		public byte mUnlock;		// 解锁
		public byte mUnlockMode;	// 解锁模式
		public byte mNearUnlock;	// 智能近车解锁
		//灯光控制（伴我回家）
		public byte mReversLights;	// 倒车灯
		public byte mNearLights;	// 近车灯
		public byte mFogLights;		// 后雾灯
		public byte mTime;			// 持续时间
		//灯光控制（寻车指示）
		public byte mCarReversLights;	// 倒车灯
		public byte mCarNearLights;	// 近车灯
		public byte mCarFogLights;	// 后雾灯
		public byte mCarTime;		// 持续时间
	}
};
