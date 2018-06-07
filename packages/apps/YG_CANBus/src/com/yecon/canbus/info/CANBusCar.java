package com.yecon.canbus.info;

public class CANBusCar {
	private int mSeatBelts; // 安全带状态 - 0:正常 1:未系
	private int mCleaningFluid; // 清洗液状态 - 0:正常 1:过低
	private int mHandbrake; // 手刹状态 - 0:正常 1:未放下
	private int mBonnet; // 引擎盖状态  - 0:关 1:开
	private int mRearDoor; // 尾箱盖状态 - 0:关 1:开
	private int mRightBackDoor; // 右后门状态 - 0:关 1:开
	private int mLeftBackDoor; // 左后门状态 - 0:关 1:开
	private int mRightFrontDoor; // 右前门状态 - 0:关 1:开
	private int mLeftFrontDoor; // 左前门状态 - 0:关 1:开

	private int mEnginSpeed; // 发动机转速 - Rpm
	private double mTravelingSpeed; // 瞬时速度 - Km/h
	private double mBatteryVoltage; // 电池电压 - V
	private double mOutdoorTemp; // 车外温度 - 摄氏度 ℃
	private int mDrivenDistance; // 行车里程 - Km
	private int mOilLeft; // 剩余燃油量 - L

	private int mOilLow; // 油量警告标志 - 0:正常 1:过低
	private int mBatteryLow; // 电池电压警告标志 - 0:正常 1:过低

	private int mDriverBelts; // 驾驶员安全带状态 - 0:正常 1:未系
	private int mRearWiper; // 后雨刷器状态- 0:关 1:开
	private int mAutoLockDoor; // 车门自动锁定状态- 0:关 1:开
	private int mPackingAssist; // 驻车辅助状态- 0:关 1:开
	private int mCtrlDoor; // 中控门锁状态- 0:关 1:开
	private int mCurCarInfoPage; // 当前行车电脑显示页 PAGE0 PAGE1 PAGE2 
	private int mDayLightLamp; // 日间照明灯 0:关闭 1:打开
	private int mLampDeleyTime; // 大灯延时状态 0:关闭 1:15秒 2:30秒 3:60秒
	private int mInsideLight; // 车内照明灯(氛围照明灯)状态   0:关闭  1~7亮度值
	private int mReverse; // 倒车状态 0:非倒车 1:倒车
	private int mReverseRadar; // 倒车雷达状态 0:关闭 1:打开
	private int mParkOrBrake; // P档或者手刹 0:P档 1:手刹
	private int mLamplet; // 小灯状态  0:关闭 1:打开
	private int mLight4Home; // 伴我回家照明状态 0:关闭 1:15秒 2:30秒 3:60秒
	private int mLight4Guest; // 迎宾照明  0:关闭 1:15秒 2:30秒 3:60秒
	private int mEQ; // 音效设置 0:经典 1:水晶韵律 2:都市节奏 3:丛林梦幻
	private int mOilUnit; // 油耗单位 0：KM/L-KM 1：L/100KM-KM
	private int mProbeBlindArea; // 盲区探测 0:关闭 1:打开
	private int mEnginStartStop; // 发动机启停功能 0:关闭 1:打开
	private int mWelcomeGuest; // 迎宾功能 0:关闭 1:打开
	private int mCtrlAllDoor; // 车门开启选项 0:驾驶员车门 1:所有车门
	private int mSetLanguage; // 语言设置 0:英文 1:中文
	
	/**
	 * 获得安全带状态 - 0:正常 1:未系
	 * 
	 * @return
	 */
	public int getSeatBelts() {
		return mSeatBelts;
	}

	/**
	 * 设置安全带状态 - 0:正常 1:未系
	 * 
	 * @param seatBelts
	 */
	public void setSeatBelts(int seatBelts) {
		this.mSeatBelts = seatBelts;
	}

	/**
	 * 获得清洗液状态 - 0:正常 1:过低
	 * 
	 * @return
	 */
	public int getCleaningFluid() {
		return mCleaningFluid;
	}

	/**
	 * 设置清洗液状态 - 0:正常 1:过低
	 * 
	 * @param cleaningFluid
	 */
	public void setCleaningFluid(int cleaningFluid) {
		this.mCleaningFluid = cleaningFluid;
	}

	/**
	 * 获得手刹状态 - 0:正常 1:未放下
	 * 
	 * @return
	 */
	public int getHandbrake() {
		return mHandbrake;
	}

	/**
	 * 设置手刹状态 - 0:正常 1:未放下
	 * 
	 * @param handbrake
	 */
	public void setHandbrake(int handbrake) {
		this.mHandbrake = handbrake;
	}

	/**
	 * getter 引擎盖状态
	 * @return mBonnet
	 */
	public int getBonnet() {
		return mBonnet;
	}

	/**
	 * setter 引擎盖状态
	 * @param mBonnet
	 */
	public void setBonnet(int mBonnet) {
		this.mBonnet = mBonnet;
	}

	/**
	 * 获得尾箱盖状态 - 0:关 1:开
	 * 
	 * @return
	 */
	public int getRearDoor() {
		return mRearDoor;
	}

	/**
	 * 设置尾箱盖状态 - 0:关 1:开
	 * 
	 * @param rearDoor
	 */
	public void setRearDoor(int rearDoor) {
		this.mRearDoor = rearDoor;
	}

	/**
	 * 获得右后门状态 - 0:关 1:开
	 * 
	 * @return
	 */
	public int getRightBackDoor() {
		return mRightBackDoor;
	}

	/**
	 * 设置右后门状态 - 0:关 1:开
	 * 
	 * @param rightBackDoor
	 */
	public void setRightBackDoor(int rightBackDoor) {
		this.mRightBackDoor = rightBackDoor;
	}

	/**
	 * 获得左后门状态 - 0:关 1:开
	 * 
	 * @return
	 */
	public int getLeftBackDoor() {
		return mLeftBackDoor;
	}

	/**
	 * 设置左后门状态 - 0:关 1:开
	 * 
	 * @param rightBackDoor
	 */
	public void setLeftBackDoor(int leftBackDoor) {
		this.mLeftBackDoor = leftBackDoor;
	}

	/**
	 * 获得右前门状态 - 0:关 1:开
	 * 
	 * @return
	 */
	public int getRightFrontDoor() {
		return mRightFrontDoor;
	}

	/**
	 * 设置右前门状态 - 0:关 1:开
	 * 
	 * @param rightFrontDoor
	 */
	public void setRightFrontDoor(int rightFrontDoor) {
		this.mRightFrontDoor = rightFrontDoor;
	}

	/**
	 * 获得左前门状态 - 0:关 1:开
	 * 
	 * @return
	 */
	public int getLeftFrontDoor() {
		return mLeftFrontDoor;
	}

	/**
	 * 设置左前门状态 - 0:关 1:开
	 * 
	 * @param leftFrontDoor
	 */
	public void setLeftFrontDoor(int leftFrontDoor) {
		this.mLeftFrontDoor = leftFrontDoor;
	}

	/**
	 * 获得发动机转速 - Rpm
	 * 
	 * @return
	 */
	public int getEnginSpeed() {
		return mEnginSpeed;
	}

	/**
	 * 设置发动机转速 - Rpm
	 * 
	 * @param enginSpeed
	 */
	public void setEnginSpeed(int enginSpeed) {
		this.mEnginSpeed = enginSpeed;
	}

	/**
	 * 获得瞬时速度 - Km/h
	 * 
	 * @return
	 */
	public double getTravelingSpeed() {
		return mTravelingSpeed;
	}

	/**
	 * 设置瞬时速度 - Km/h
	 * 
	 * @param travelingSpeed
	 */
	public void setTravelingSpeed(double travelingSpeed) {
		this.mTravelingSpeed = travelingSpeed;
	}

	/**
	 * 获得电池电压 - V
	 * 
	 * @return
	 */
	public double getBatteryVoltage() {
		return mBatteryVoltage;
	}

	/**
	 * 设置电池电压 - V
	 * 
	 * @param batteryVoltage
	 */
	public void setBatteryVoltage(double batteryVoltage) {
		this.mBatteryVoltage = batteryVoltage;
	}

	/**
	 * 获得车外温度 - 摄氏度 ℃
	 * 
	 * @return
	 */
	public double getOutdoorTemp() {
		return mOutdoorTemp;
	}

	/**
	 * 设置车外温度 - 摄氏度 ℃
	 * 
	 * @param outdoorTemp
	 */
	public void setOutdoorTemp(double outdoorTemp) {
		this.mOutdoorTemp = outdoorTemp;
	}

	/**
	 * 获得行车里程 - Km
	 * 
	 * @return
	 */
	public int getDrivenDistance() {
		return mDrivenDistance;
	}

	/**
	 * 设置行车里程 - Km
	 * 
	 * @param drivenDistance
	 */
	public void setDrivenDistance(int drivenDistance) {
		this.mDrivenDistance = drivenDistance;
	}

	/**
	 * 获得剩余燃油量 - L
	 * 
	 * @return
	 */
	public int getOilLeft() {
		return mOilLeft;
	}

	/**
	 * 设置剩余燃油量 - L
	 * 
	 * @param oilLeft
	 */
	public void setOilLeft(int oilLeft) {
		this.mOilLeft = oilLeft;
	}

	/**
	 * 获得油量警告标志 - 0:正常 1:过低
	 * 
	 * @return
	 */
	public int getOilLow() {
		return mOilLow;
	}

	/**
	 * 设置油量警告标志 - 0:正常 1:过低
	 * 
	 * @param oilLow
	 */
	public void setOilLow(int oilLow) {
		this.mOilLow = oilLow;
	}

	/**
	 * 获得电池电压警告标志 - 0:正常 1:过低
	 * 
	 * @return
	 */
	public int getBatteryLow() {
		return mBatteryLow;
	}

	/**
	 * 设置电池电压警告标志 - 0:正常 1:过低
	 * 
	 * @param batteryLow
	 */
	public void setBatteryLow(int batteryLow) {
		this.mBatteryLow = batteryLow;
	}

	
	/**
	 * 获得驾驶员安全带状态 - 0:正常 1:未系
	 * 
	 * @return
	 */
	public int getDriverBelts() {
		return mDriverBelts;
	}

	/**
	 * 设置驾驶员安全带状态 - 0:正常 1:未系
	 * 
	 * @param seatBelts
	 */
	public void setDriverBelts(int mDriverBelts) {
		this.mDriverBelts = mDriverBelts;
	}
	
	/**
	 * 获取后雨刷状态 - 0:倒车时停用 1:倒车时使用
	 * 
	 * @return the mRearWiper
	 */
	public int getRearWiper() {
		return mRearWiper;
	}

	/**
	 * 设置后雨刷状态 - 0:倒车时停用 1:倒车时使用
	 * 
	 * @param mRearWiper
	 */
	public void setRearWiper(int mRearWiper) {
		this.mRearWiper = mRearWiper;
	}

	/**
	 * 获取车门自动锁定状态 - 0:停用 1:启用
	 * 
	 * @return mAutoLockDoor
	 */
	public int getAutoLockDoor() {
		return mAutoLockDoor;
	}

	/**
	 * 设置车门自动锁定状态 - 0:停用 1:启用
	 * 
	 * @param mAutoLockDoor
	 */
	public void setAutoLockDoor(int mAutoLockDoor) {
		this.mAutoLockDoor = mAutoLockDoor;
	}

	/**
	 * 获取驻车辅助状态 - 0 :开 1:关
	 * 
	 * @return mPackingAssist
	 */
	public int getPackingAssist() {
		return mPackingAssist;
	}

	/**
	 * 设置驻车辅助状态 - 0 :开 1:关
	 * 
	 * @param mPackingAssist
	 */
	public void setPackingAssist(int mPackingAssist) {
		this.mPackingAssist = mPackingAssist;
	}

	/**
	 * 获取中控门锁状态 - 0 未锁  1已锁
	 * 
	 * @return mCtrlDoor
	 */
	public int getCtrlDoor() {
		return mCtrlDoor;
	}

	/**
	 * 设置中控门锁状态 - 0 未锁  1已锁
	 * 
	 * @param mCtrlDoor
	 */
	public void setCtrlDoor(int mCtrlDoor) {
		this.mCtrlDoor = mCtrlDoor;
	}

	/**
	 * 获取当前行车电脑显示页 PAGE0 PAGE1 PAGE2
	 * 
	 * @return mCurCarInfoPage
	 */
	public int getCurCarInfoPage() {
		return mCurCarInfoPage;
	}

	/**
	 * 设置当前行车电脑显示页 PAGE0 PAGE1 PAGE2
	 * 
	 * @param mCurCarInfoPage
	 */
	public void setCurCarInfoPage(int mCurCarInfoPage) {
		this.mCurCarInfoPage = mCurCarInfoPage;
	}

	/**
	 * getter 日间照明灯 0:关闭 1:打开
	 * @return mDayLightLamp
	 */
	public int getDayLightLamp() {
		return mDayLightLamp;
	}

	/**
	 * setter 日间照明灯 0:关闭 1:打开
	 * @param mDayLightLamp
	 */
	public void setDayLightLamp(int mDayLightLamp) {
		this.mDayLightLamp = mDayLightLamp;
	}

	/**
	 * getter 大灯延时状态 0:关闭 1:15秒 2:30秒 3:60秒
	 * @return mLampDeleyTime
	 */
	public int getLampDeleyTime() {
		return mLampDeleyTime;
	}

	/**
	 * setter 大灯延时状态 0:关闭 1:15秒 2:30秒 3:60秒
	 * @param mLampDeleyTime
	 */
	public void setLampDeleyTime(int mLampDeleyTime) {
		this.mLampDeleyTime = mLampDeleyTime;
	}

	/**
	 * getter 车内照明灯(氛围照明灯)状态   0:关闭  1~7亮度值
	 * @return mInsideLight
	 */
	public int getInsideLight() {
		return mInsideLight;
	}

	/**
	 * setter 车内照明灯(氛围照明灯)状态   0:关闭  1~7亮度值
	 * @param mInsideLight
	 */
	public void setInsideLight(int mInsideLight) {
		this.mInsideLight = mInsideLight;
	}

	/**
	 * getter 倒车状态 0:非倒车 1:倒车
	 * @return mReverse
	 */
	public int getReverse() {
		return mReverse;
	}

	/**
	 * setter 倒车状态 0:非倒车 1:倒车
	 * @param mReverse
	 */
	public void setReverse(int mReverse) {
		this.mReverse = mReverse;
	}

	/**
	 * getter 倒车雷达状态 0:关闭 1:打开
	 * @return mReverseRadar
	 */
	public int getReverseRadar() {
		return mReverseRadar;
	}

	/**
	 * setter 倒车雷达状态 0:关闭 1:打开
	 * @param mReverseRadar
	 */
	public void setReverseRadar(int mReverseRadar) {
		this.mReverseRadar = mReverseRadar;
	}

	/**
	 * getter P档或者手刹 0:P档 1:手刹
	 * @return mParkOrBrake
	 */
	public int getParkOrBrake() {
		return mParkOrBrake;
	}

	/**
	 * setter P档或者手刹 0:P档 1:手刹
	 * @param mParkOrBrake
	 */
	public void setParkOrBrake(int mParkOrBrake) {
		this.mParkOrBrake = mParkOrBrake;
	}

	/**
	 * getter 小灯状态  0:关闭 1:打开
	 * @return mLamplet
	 */
	public int getLamplet() {
		return mLamplet;
	}

	/**
	 * setter 小灯状态  0:关闭 1:打开
	 * @param mLamplet
	 */
	public void setLamplet(int mLamplet) {
		this.mLamplet = mLamplet;
	}

	/**
	 * getter 伴我回家照明状态 0:关闭 1:15秒 2:30秒 3:60秒
	 * @return mLight4Home
	 */
	public int getLight4Home() {
		return mLight4Home;
	}

	/**
	 * setter 伴我回家照明状态 0:关闭 1:15秒 2:30秒 3:60秒
	 * @param mLight4Home
	 */
	public void setLight4Home(int mLight4Home) {
		this.mLight4Home = mLight4Home;
	}

	/**
	 * getter 迎宾照明  0:关闭 1:15秒 2:30秒 3:60秒
	 * @return mLight4Guest
	 */
	public int getLight4Guest() {
		return mLight4Guest;
	}

	/**
	 * setter 迎宾照明  0:关闭 1:15秒 2:30秒 3:60秒
	 * @param mLight4Guest
	 */
	public void setLight4Guest(int mLight4Guest) {
		this.mLight4Guest = mLight4Guest;
	}

	/**
	 * getter 音效设置 0:经典 1:水晶韵律 2:都市节奏 3:丛林梦幻
	 * @return mEQ
	 */
	public int getEQ() {
		return mEQ;
	}

	/**
	 * setter 音效设置 0:经典 1:水晶韵律 2:都市节奏 3:丛林梦幻
	 * @param mEQ
	 */
	public void setEQ(int mEQ) {
		this.mEQ = mEQ;
	}

	/**
	 * getter 油耗单位 0：KM/L-KM 1：L/100KM-KM
	 * @return mOilUnit
	 */
	public int getOilUnit() {
		return mOilUnit;
	}

	/**
	 * setter 油耗单位 0：KM/L-KM 1：L/100KM-KM
	 * @param mOilUnit
	 */
	public void setOilUnit(int mOilUnit) {
		this.mOilUnit = mOilUnit;
	}

	/**
	 * getter 盲区探测 0:关闭 1:打开
	 * @return mProbeBlindArea
	 */
	public int getProbeBlindArea() {
		return mProbeBlindArea;
	}

	/**
	 * setter 盲区探测 0:关闭 1:打开
	 * @param mProbeBlindArea
	 */
	public void setProbeBlindArea(int mProbeBlindArea) {
		this.mProbeBlindArea = mProbeBlindArea;
	}

	/**
	 * getter 发动机启停功能 0:关闭 1:打开
	 * @return mEnginStartStop
	 */
	public int getEnginStartStop() {
		return mEnginStartStop;
	}

	/**
	 * setter 发动机启停功能 0:关闭 1:打开
	 * @param mEnginStartStop
	 */
	public void setEnginStartStop(int mEnginStartStop) {
		this.mEnginStartStop = mEnginStartStop;
	}

	/**
	 * getter 迎宾功能 0:关闭 1:打开
	 * @return mWelcomeGuest
	 */
	public int getWelcomeGuest() {
		return mWelcomeGuest;
	}

	/**
	 * setter 迎宾功能 0:关闭 1:打开
	 * @param mWelcomeGuest
	 */
	public void setWelcomeGuest(int mWelcomeGuest) {
		this.mWelcomeGuest = mWelcomeGuest;
	}

	/**
	 * getter 车门开启选项 0:驾驶员车门 1:所有车门
	 * @return mCtrlAllDoor
	 */
	public int getCtrlAllDoor() {
		return mCtrlAllDoor;
	}

	/**
	 * setter 车门开启选项 0:驾驶员车门 1:所有车门
	 * @param mCtrlAllDoor
	 */
	public void setCtrlAllDoor(int mCtrlAllDoor) {
		this.mCtrlAllDoor = mCtrlAllDoor;
	}

	/**
	 * getter 语言设置 0:英文 1:中文
	 * @return mSetLanguage
	 */
	public int getSetLanguage() {
		return mSetLanguage;
	}

	/**
	 * setter 语言设置 0:英文 1:中文
	 * @param mSetLanguage
	 */
	public void setSetLanguage(int mSetLanguage) {
		this.mSetLanguage = mSetLanguage;
	}

}
