
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public class McuSystemParamInfo implements Parcelable {
    private int[] mMPEGSetData;
    private int[] mBTSetData;
    private int[] mTimeSetData;
    private int[] mGeneralSetData;
    private int[] mVideoStateSetData;
    private int[] mFMRadioSetData;
    private int[] mRDSSetData;
    private int[] mAudioVideoSetData;
    private int[] mBackcarSetData;

    public McuSystemParamInfo() {
        mMPEGSetData = new int[MCU_MPEG_SET_DATA_LENGTH];

        mBTSetData = new int[MCU_BT_SET_DATA_LENGTH];

        mTimeSetData = new int[MCU_TIME_SET_DATA_LENGTH];

        mGeneralSetData = new int[MCU_GENERNAL_SET_DATA_LENGTH];

        mVideoStateSetData = new int[MCU_VIDEO_STATE_SET_DATA_LENGTH];

        mBackcarSetData = new int[MCU_BACKCAR_SET_DATA_LENGTH];

        mFMRadioSetData = new int[MCU_FMRADIO_SET_DATA_LENGTH];

        mRDSSetData = new int[MCU_RDS_SET_DATA_LENGTH];

        mAudioVideoSetData = new int[MCU_AUDIO_VIDEO_SET_DATA_LENGTH];
    }

    public int[] getMPEGSetData() {
        return mMPEGSetData;
    }

    public void setMPEGSetData(int[] mPEGSetData) {
        this.mMPEGSetData = mPEGSetData;
    }

    public int[] getBTSetData() {
        return mBTSetData;
    }

    public void setBTSetData(int[] bTSetData) {
        this.mBTSetData = bTSetData;
    }

    public int[] getTimeSetData() {
        return mTimeSetData;
    }

    public void setTimeSetData(int[] timeSetData) {
        this.mTimeSetData = timeSetData;
    }

    public int[] getGeneralSetData() {
        return mGeneralSetData;
    }

    public void setGeneralSetData(int[] generalSetData) {
        this.mGeneralSetData = generalSetData;
    }

    public int[] getVideoStateSetData() {
        return mVideoStateSetData;
    }

    public void setVideoStateSetData(int[] videoStateSetData) {
        this.mVideoStateSetData = videoStateSetData;
    }

    public int[] getFMRadioSetData() {
        return mFMRadioSetData;
    }

    public void setFMRadioSetData(int[] fMRadioSetData) {
        this.mFMRadioSetData = fMRadioSetData;
    }

    public int[] getRDSSetData() {
        return mRDSSetData;
    }

    public void setRDSSetData(int[] rDSSetData) {
        this.mRDSSetData = rDSSetData;
    }

    public int[] getAudioVideoSetData() {
        return mAudioVideoSetData;
    }

    public void setAudioVideoSetData(int[] audioVideoSetData) {
        this.mAudioVideoSetData = audioVideoSetData;
    }

    public int[] getBackcarSetData() {
        return mBackcarSetData;
    }

    public void setBackcarSetData(int[] backcarSetData) {
        this.mBackcarSetData = backcarSetData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeIntArray(mMPEGSetData);
        parcel.writeIntArray(mBTSetData);
        parcel.writeIntArray(mTimeSetData);
        parcel.writeIntArray(mGeneralSetData);
        parcel.writeIntArray(mVideoStateSetData);
        parcel.writeIntArray(mFMRadioSetData);
        parcel.writeIntArray(mRDSSetData);
        parcel.writeIntArray(mAudioVideoSetData);
        parcel.writeIntArray(mBackcarSetData);
    }

    public static final Parcelable.Creator<McuSystemParamInfo> CREATOR = new Parcelable.Creator<McuSystemParamInfo>() {

        @Override
        public McuSystemParamInfo createFromParcel(Parcel parcel) {
            McuSystemParamInfo info = new McuSystemParamInfo();
            info.mMPEGSetData = parcel.createIntArray();
            info.mBTSetData = parcel.createIntArray();
            info.mTimeSetData = parcel.createIntArray();
            info.mGeneralSetData = parcel.createIntArray();
            info.mVideoStateSetData = parcel.createIntArray();
            info.mFMRadioSetData = parcel.createIntArray();
            info.mRDSSetData = parcel.createIntArray();
            info.mAudioVideoSetData = parcel.createIntArray();
            info.mBackcarSetData = parcel.createIntArray();

            return info;
        }

        @Override
        public McuSystemParamInfo[] newArray(int size) {
            return new McuSystemParamInfo[size];
        }

    };
}
