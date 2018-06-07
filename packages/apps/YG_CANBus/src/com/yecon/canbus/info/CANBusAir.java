
package com.yecon.canbus.info;

public class CANBusAir {
    private int mACSwitch; // 空调开关指示 - 0:关 1:开
    private int mAC; // A/C指示 - 0:关 1:开
    private int mInnerLoop; // 内外循环指示 - 0:外循环 1:内循环
    private int mAutoWindHigh; // AUTO 大风灯指示 - 0:关 1:开
    private int mAutoWindLow; // AUTO 小风灯指示 - 0:关 1:开
    private int mDual; // DUAL 灯指示 双区控制 - 0:关 1:开
    private int mMaxFrontLight; // MAX FRONT 灯指示 - 0:关 1:开
    private int mRearLight; // REAR 灯指示 - 0:关 1:开

    private int mBlowLeftHead; // 左边向上送风开关指示 - 0:关 1:开
    private int mBlowLeftHands; // 左边平行送风开关指示 - 0:关 1:开
    private int mBlowLeftFeet; // 左边向下送风开关指示 - 0:关 1:开
    private int mBlowRightHead; // 右边向上送风开关指示 - 0:关 1:开
    private int mBlowRightHands; // 右边平行送风开关指示 - 0:关 1:开
    private int mBlowRightFeet; // 右边向下送风开关指示 - 0:关 1:开

    private int mShowRequest; // 空调显示请求 - 0:不显示 1:请求显示空调信息

    private int mAirVolume; // 风量大小： 0~7级（最大风量，有些车型6级，有些7级）

    private int mLeftTemp; // 左边设定温度 - 摄氏度 ℃
    private int mRightTemp; // 右边设定温度 - 摄氏度 ℃
    private int mTempUnit; // 温度单位 - 0:摄氏度 ℃ 1:华氏度 ℉

    private int mHeatLeftSeat; // 左边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）
    private int mHeatRightSeat; // 右边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）

    private int mBlowLeftSeat; // 左边座椅吹风 - 0:不显示 - 1~4: 1~4级
    private int mBlowRightSeat; // 右边座椅吹风 - 0:不显示 - 1~4: 1~4级

    private int mAQS; // AQS 自动内外循环状态 - 0:非AQS内循环 1:AQS内循环
    private int mFrontWindowDemisting; // 前窗除雾状态 - 0:关 1:开
    private int mRearWindowDemisting; // 后窗除雾状态 - 0:关 1:开
    private int mWindowedHeated; // 加窗加热状态 - 0:关 1:开
    private int mECO; // ECO 绿色节能状态 - 0:关 1:开
    private int mACMax; // AC MAX 最大制冷状态 - 0:关 1:开
    private int mRearCtrl; // REAR CTRL 后座空调控制状态 - 0:关 1:开
    private int mManuallyMoveState; // 手动动空调状态标识 - 0:自动空调 1:手动空调

    /**
     * 获得空调开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getACSwitch() {
        return mACSwitch;
    }

    /**
     * 设置空调开关指示 - 0:关 1:开
     * 
     * @param acSwitch
     */
    public void setACSwitch(int acSwitch) {
        this.mACSwitch = acSwitch;
    }

    /**
     * 获得A/C指示 - 0:关 1:开
     * 
     * @return
     */
    public int getAC() {
        return mAC;
    }

    /**
     * 设置A/C指示 - 0:关 1:开
     * 
     * @param ac
     */
    public void setAC(int ac) {
        this.mAC = ac;
    }

    /**
     * 获得内外循环指示 - 0:外循环 1:内循环
     * 
     * @return
     */
    public int getInnerLoop() {
        return mInnerLoop;
    }

    /**
     * 设置内外循环指示 - 0:外循环 1:内循环
     * 
     * @param innerLoop
     */
    public void setInnerLoop(int innerLoop) {
        this.mInnerLoop = innerLoop;
    }

    /**
     * 获得AUTO 大风灯指示 - 0:关 1:开
     * 
     * @return
     */
    public int getAutoWindHigh() {
        return mAutoWindHigh;
    }

    /**
     * 设置AUTO 大风灯指示 - 0:关 1:开
     * 
     * @param autoWindHigh
     */
    public void setAutoWindHigh(int autoWindHigh) {
        this.mAutoWindHigh = autoWindHigh;
    }

    /**
     * 获得AUTO 小风灯指示 - 0:关 1:开
     * 
     * @return
     */
    public int getAutoWindLow() {
        return mAutoWindLow;
    }

    /**
     * 设置AUTO 小风灯指示 - 0:关 1:开
     * 
     * @param autoWindLow
     */
    public void setAutoWindLow(int autoWindLow) {
        this.mAutoWindLow = autoWindLow;
    }

    /**
     * 获得DUAL 灯指示 双区控制 - 0:关 1:开
     * 
     * @return
     */
    public int getDual() {
        return mDual;
    }

    /**
     * 设置DUAL 灯指示 双区控制 - 0:关 1:开
     * 
     * @param dual
     */
    public void setDual(int dual) {
        this.mDual = dual;
    }

    /**
     * 获得 MAX FRONT 灯指示 - 0:关 1:开
     * 
     * @return
     */
    public int getMaxFrontLight() {
        return mMaxFrontLight;
    }

    /**
     * 设置 MAX FRONT 灯指示 - 0:关 1:开
     * 
     * @param maxFrontLight
     */
    public void setMaxFrontLight(int maxFrontLight) {
        this.mMaxFrontLight = maxFrontLight;
    }

    /**
     * 获得REAR 灯指示 - 0:关 1:开
     * 
     * @return
     */
    public int getRearLight() {
        return mRearLight;
    }

    /**
     * 设置REAR 灯指示 - 0:关 1:开
     * 
     * @param rearLight
     */
    public void setRearLight(int rearLight) {
        this.mRearLight = rearLight;
    }

    /**
     * 获得左边向上送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowLeftHead() {
        return mBlowLeftHead;
    }

    /**
     * 设置左边向上送风开关指示 - 0:关 1:开
     * 
     * @param blowLeftHead
     */
    public void setBlowLeftHead(int blowLeftHead) {
        this.mBlowLeftHead = blowLeftHead;
    }

    /**
     * 获得左边平行送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowLeftHands() {
        return mBlowLeftHands;
    }

    /**
     * 设置左边平行送风开关指示 - 0:关 1:开
     * 
     * @param blowLeftHands
     */
    public void setBlowLeftHands(int blowLeftHands) {
        this.mBlowLeftHands = blowLeftHands;
    }

    /**
     * 获得左边向下送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowLeftFeet() {
        return mBlowLeftFeet;
    }

    /**
     * 设置左边向下送风开关指示 - 0:关 1:开
     * 
     * @param blowLeftFeet
     */
    public void setBlowLeftFeet(int blowLeftFeet) {
        this.mBlowLeftFeet = blowLeftFeet;
    }

    /**
     * 获得右边向上送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowRightHead() {
        return mBlowRightHead;
    }

    /**
     * 设置右边向上送风开关指示 - 0:关 1:开
     * 
     * @param blowRightHead
     */
    public void setBlowRightHead(int blowRightHead) {
        this.mBlowRightHead = blowRightHead;
    }

    /**
     * 获得右边平行送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowRightHands() {
        return mBlowRightHands;
    }

    /**
     * 设置右边平行送风开关指示 - 0:关 1:开
     * 
     * @param blowRightHands
     */
    public void setBlowRightHands(int blowRightHands) {
        this.mBlowRightHands = blowRightHands;
    }

    /**
     * 获得右边向下送风开关指示 - 0:关 1:开
     * 
     * @return
     */
    public int getBlowRightFeet() {
        return mBlowRightFeet;
    }

    /**
     * 设置右边向下送风开关指示 - 0:关 1:开
     * 
     * @param blowRightFeet
     */
    public void setBlowRightFeet(int blowRightFeet) {
        this.mBlowRightFeet = blowRightFeet;
    }

    /**
     * 获得空调显示请求 - 0:不显示 1:请求显示空调信息
     * 
     * @return
     */
    public int getShowRequest() {
        return mShowRequest;
    }

    /**
     * 设置空调显示请求 - 0:不显示 1:请求显示空调信息
     * 
     * @param showRequest
     */
    public void setShowRequest(int showRequest) {
        this.mShowRequest = showRequest;
    }

    /**
     * 获得风量大小： 0~7级（最大风量，有些车型6级，有些7级）
     * 
     * @return
     */
    public int getAirVolume() {
        return mAirVolume;
    }

    /**
     * 设置风量大小： 0~7级（最大风量，有些车型6级，有些7级）
     * 
     * @param airVolume
     */
    public void setAirVolume(int airVolume) {
        this.mAirVolume = airVolume;
    }

    /**
     * 获得左边设定温度 - 摄氏度 ℃
     * 
     * @return
     */
    public int getLeftTemp() {
        return mLeftTemp;
    }

    /**
     * 设置左边设定温度 - 摄氏度 ℃
     * 
     * @param leftTemp
     */
    public void setLeftTemp(int leftTemp) {
        this.mLeftTemp = leftTemp;
    }

    /**
     * 获得右边设定温度 - 摄氏度 ℃
     * 
     * @return
     */
    public int getRightTemp() {
        return mRightTemp;
    }

    /**
     * 设置右边设定温度 - 摄氏度 ℃
     * 
     * @param rightTemp
     */
    public void setRightTemp(int rightTemp) {
        this.mRightTemp = rightTemp;
    }

    /**
     * 获得温度单位 - 0:摄氏度 ℃ 1:华氏度 ℉
     * 
     * @return
     */
    public int getTempUnit() {
        return mTempUnit;
    }

    /**
     * 设置温度单位 - 0:摄氏度 ℃ 1:华氏度 ℉
     * 
     * @param tempUnit
     */
    public void setTempUnit(int tempUnit) {
        this.mTempUnit = tempUnit;
    }

    /**
     * 获得左边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）
     * 
     * @return
     */
    public int getHeatLeftSeat() {
        return mHeatLeftSeat;
    }

    /**
     * 设置左边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）
     * 
     * @param heatLeftSeat
     */
    public void setHeatLeftSeat(int heatLeftSeat) {
        this.mHeatLeftSeat = heatLeftSeat;
    }

    /**
     * 获得右边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）
     * 
     * @return
     */
    public int getHeatRightSeat() {
        return mHeatRightSeat;
    }

    /**
     * 设置右边座椅加热 - 0:不显示 - 1~4: 1~4级（有些车型只有3级）
     * 
     * @param heatRightSeat
     */
    public void setHeatRightSeat(int heatRightSeat) {
        this.mHeatRightSeat = heatRightSeat;
    }

    /**
     * 获得左边座椅吹风 - 0:不显示 - 1~4: 1~4级
     * 
     * @return
     */
    public int getBlowLeftSeat() {
        return mBlowLeftSeat;
    }

    /**
     * 设置左边座椅吹风 - 0:不显示 - 1~4: 1~4级
     * 
     * @param blowLeftSeat
     */
    public void setBlowLeftSeat(int blowLeftSeat) {
        this.mBlowLeftSeat = blowLeftSeat;
    }

    /**
     * 获得右边座椅吹风 - 0:不显示 - 1~4: 1~4级
     * 
     * @return
     */
    public int getBlowRightSeat() {
        return mBlowRightSeat;
    }

    /**
     * 设置右边座椅吹风 - 0:不显示 - 1~4: 1~4级
     * 
     * @param blowRightSeat
     */
    public void setBlowRightSeat(int blowRightSeat) {
        this.mBlowRightSeat = blowRightSeat;
    }

    /**
     * 获得AQS 自动内外循环状态 - 0:非AQS内循环 1:AQS内循环
     * 
     * @return
     */
    public int getAQS() {
        return mAQS;
    }

    /**
     * 设置AQS 自动内外循环状态 - 0:非AQS内循环 1:AQS内循环
     * 
     * @param aqs
     */
    public void setAQS(int aqs) {
        this.mAQS = aqs;
    }

    /**
     * 获得前窗除雾状态 - 0:关 1:开
     * 
     * @return
     */
    public int getFrontWindowDemisting() {
        return mFrontWindowDemisting;
    }

    /**
     * 设置前窗除雾状态 - 0:关 1:开
     * 
     * @param frontWindowDemisting
     */
    public void setFrontWindowDemisting(int frontWindowDemisting) {
        this.mFrontWindowDemisting = frontWindowDemisting;
    }

    /**
	 * getter 后窗除雾状态 - 0:关 1:开
	 * @return mRearWindowDemisting
	 */
	public int getRearWindowDemisting() {
		return mRearWindowDemisting;
	}

	/**
	 * setter 后窗除雾状态 - 0:关 1:开
	 * @param mRearWindowDemisting
	 */
	public void setRearWindowDemisting(int mRearWindowDemisting) {
		this.mRearWindowDemisting = mRearWindowDemisting;
	}

	/**
     * 获得加窗加热状态 - 0:关 1:开
     * 
     * @return
     */
    public int getWindowedHeated() {
        return mWindowedHeated;
    }

    /**
     * 设置加窗加热状态 - 0:关 1:开
     * 
     * @param windowedHeated
     */
    public void setWindowedHeated(int windowedHeated) {
        this.mWindowedHeated = windowedHeated;
    }

    /**
     * 获得ECO 绿色节能状态 - 0:关 1:开
     * 
     * @return
     */
    public int getECO() {
        return mECO;
    }

    /**
     * 设置ECO 绿色节能状态 - 0:关 1:开
     * 
     * @param eco
     */
    public void setECO(int eco) {
        this.mECO = eco;
    }

    /**
     * 获得AC MAX 最大制冷状态 - 0:关 1:开
     * 
     * @return
     */
    public int getACMax() {
        return mACMax;
    }

    /**
     * 设置AC MAX 最大制冷状态 - 0:关 1:开
     * 
     * @param acMax
     */
    public void setACMax(int acMax) {
        this.mACMax = acMax;
    }

    /**
     * 获得REAR CTRL 后座空调控制状态 - 0:关 1:开
     * 
     * @return
     */
    public int getRearCtrl() {
        return mRearCtrl;
    }

    /**
     * 设置REAR CTRL 后座空调控制状态 - 0:关 1:开
     * 
     * @param rearCtrl
     */
    public void setRearCtrl(int rearCtrl) {
        this.mRearCtrl = rearCtrl;
    }

    /**
     * 获得手动动空调状态标识 - 0:自动空调 1:手动空调
     * 
     * @return
     */
    public int getManuallyMoveState() {
        return mManuallyMoveState;
    }

    /**
     * 设置手动动空调状态标识 - 0:自动空调 1:手动空调
     * 
     * @param manuallyMoveState
     */
    public void setManuallyMoveState(int manuallyMoveState) {
        this.mManuallyMoveState = manuallyMoveState;
    }

}
