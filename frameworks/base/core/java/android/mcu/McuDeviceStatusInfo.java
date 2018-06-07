
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public class McuDeviceStatusInfo implements Parcelable {
    private int mCDCStatus;
    private int mDISCStatus;
    private int mDISCAutoSuctionStatus;
    private int mBackcarStatus;
    private int mCarHeadlightsStatus;
    private int mBrakeStatus;
	private int mSleepStauts;
    private int mTAStatus;
    private int mSourceStatus;
    private int mMPEGStatus;
    private int mTVStatus;
    private int mCAMERAStatus;
    private int mDVDCStatus;
    private int mAUX1Status;

    public McuDeviceStatusInfo() {
        mCDCStatus = MCU_DATA_STATUS_NOT_EXIST;
        mDISCStatus = MCU_DATA_STATUS_NOT_EXIST;
        mDISCAutoSuctionStatus = MCU_DATA_NATURAL_DISH;
        mBackcarStatus = MCU_DATA_SWITCH_OFF;
        mCarHeadlightsStatus = MCU_DATA_SWITCH_OFF;
        mBrakeStatus = MCU_DATA_BRAKE_STARTUP;
		mSleepStauts = MCU_DATA_SLEEP_OFF;
        mTAStatus = MCU_DATA_TA_NOT_RECEIVED;
        mSourceStatus = MCU_SOURCE_OFF;
        mMPEGStatus = MCU_DATA_VIDEO_INVALID;
        mTVStatus = MCU_DATA_VIDEO_INVALID;
        mCAMERAStatus = MCU_DATA_VIDEO_INVALID;
        mDVDCStatus = MCU_DATA_VIDEO_INVALID;
        mAUX1Status = MCU_DATA_VIDEO_INVALID;
    }

    public McuDeviceStatusInfo(McuDeviceStatusInfo deviceStatusInfo) {
        mCDCStatus = deviceStatusInfo.mCDCStatus;
        mDISCStatus = deviceStatusInfo.mDISCStatus;
        mDISCAutoSuctionStatus = deviceStatusInfo.mDISCAutoSuctionStatus;
        mBackcarStatus = deviceStatusInfo.mBackcarStatus;
        mCarHeadlightsStatus = deviceStatusInfo.mCarHeadlightsStatus;
        mBrakeStatus = deviceStatusInfo.mBrakeStatus;
		mSleepStauts = deviceStatusInfo.mSleepStauts;
        mTAStatus = deviceStatusInfo.mTAStatus;
        mSourceStatus = deviceStatusInfo.mSourceStatus;
        mMPEGStatus = deviceStatusInfo.mMPEGStatus;
        mTVStatus = deviceStatusInfo.mTVStatus;
        mCAMERAStatus = deviceStatusInfo.mCAMERAStatus;
        mDVDCStatus = deviceStatusInfo.mDVDCStatus;
        mAUX1Status = deviceStatusInfo.mAUX1Status;
    }

    public int getCDCStatus() {
        return mCDCStatus;
    }

    public void setCDCStatus(int cDCStatus) {
        this.mCDCStatus = cDCStatus;
    }

    public int getDISCStatus() {
        return mDISCStatus;
    }

    public void setDISCStatus(int dISCStatus) {
        this.mDISCStatus = dISCStatus;
    }

    public int getDISCAutoSuctionStatus() {
        return mDISCAutoSuctionStatus;
    }

    public void setDISCAutoSuctionStatus(int dISCAutoSuctionStatus) {
        this.mDISCAutoSuctionStatus = dISCAutoSuctionStatus;
    }

    public int getBackcarStatus() {
        return mBackcarStatus;
    }

    public void setBackcarStatus(int backcarStatus) {
        this.mBackcarStatus = backcarStatus;
    }

    public int getCarHeadlightsStatus() {
        return mCarHeadlightsStatus;
    }

    public void setCarHeadlightsStatus(int carHeadlightsStatus) {
        this.mCarHeadlightsStatus = carHeadlightsStatus;
    }

    public int getBrakeStatus() {
        return mBrakeStatus;
    }

    public void setBrakeStatus(int brakeStatus) {
        this.mBrakeStatus = brakeStatus;
    }
	
	public int getSleepStatus() {
        return mSleepStauts;
    }
	
	public void setSleepStatus(int sleepStauts){
		this.mSleepStauts = sleepStauts;
	}
	
    public int getTAStatus() {
        return mTAStatus;
    }

    public void setTAStatus(int tAStatus) {
        this.mTAStatus = tAStatus;
    }

    public int getSourceStatus() {
        return mSourceStatus;
    }

    public void setSourceStatus(int sourceStatus) {
        this.mSourceStatus = sourceStatus;
    }

    public int getMPEGStatus() {
        return mMPEGStatus;
    }

    public void setMPEGStatus(int mPEGStatus) {
        this.mMPEGStatus = mPEGStatus;
    }

    public int getTVStatus() {
        return mTVStatus;
    }

    public void setTVStatus(int tVStatus) {
        this.mTVStatus = tVStatus;
    }

    public int getCAMERAStatus() {
        return mCAMERAStatus;
    }

    public void setCAMERAStatus(int cAMERAStatus) {
        this.mCAMERAStatus = cAMERAStatus;
    }

    public int getDVDCStatus() {
        return mDVDCStatus;
    }

    public void setDVDCStatus(int dVDCStatus) {
        this.mDVDCStatus = dVDCStatus;
    }

    public int getAUX1Status() {
        return mAUX1Status;
    }

    public void setAUX1Status(int aUX1Status) {
        this.mAUX1Status = aUX1Status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mCDCStatus);
        parcel.writeInt(mDISCStatus);
        parcel.writeInt(mDISCAutoSuctionStatus);
        parcel.writeInt(mBackcarStatus);
        parcel.writeInt(mCarHeadlightsStatus);
        parcel.writeInt(mBrakeStatus);
		parcel.writeInt(mSleepStauts);
        parcel.writeInt(mTAStatus);
        parcel.writeInt(mSourceStatus);
        parcel.writeInt(mMPEGStatus);
        parcel.writeInt(mTVStatus);
        parcel.writeInt(mCAMERAStatus);
        parcel.writeInt(mDVDCStatus);
        parcel.writeInt(mAUX1Status);
    }

    public static final Parcelable.Creator<McuDeviceStatusInfo> CREATOR = new Parcelable.Creator<McuDeviceStatusInfo>() {

        @Override
        public McuDeviceStatusInfo createFromParcel(Parcel parcel) {
            McuDeviceStatusInfo deviceStatusInfo = new McuDeviceStatusInfo();
            deviceStatusInfo.setCDCStatus(parcel.readInt());
            deviceStatusInfo.setDISCStatus(parcel.readInt());
            deviceStatusInfo.setDISCAutoSuctionStatus(parcel.readInt());
            deviceStatusInfo.setBackcarStatus(parcel.readInt());
            deviceStatusInfo.setCarHeadlightsStatus(parcel.readInt());
            deviceStatusInfo.setBrakeStatus(parcel.readInt());
			deviceStatusInfo.setSleepStatus(parcel.readInt());
            deviceStatusInfo.setTAStatus(parcel.readInt());
            deviceStatusInfo.setSourceStatus(parcel.readInt());
            deviceStatusInfo.setMPEGStatus(parcel.readInt());
            deviceStatusInfo.setTVStatus(parcel.readInt());
            deviceStatusInfo.setCAMERAStatus(parcel.readInt());
            deviceStatusInfo.setDVDCStatus(parcel.readInt());
            deviceStatusInfo.setAUX1Status(parcel.readInt());

            return deviceStatusInfo;
        }

        @Override
        public McuDeviceStatusInfo[] newArray(int size) {
            return new McuDeviceStatusInfo[size];
        }
    };
}
