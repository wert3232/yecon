
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public final class McuRadioPresetListInfo implements Parcelable {
    private int mCurrBand;
    private int mCurrPreset;
    private int mCurrBandFreq;
    private int mPresetList[];

    public McuRadioPresetListInfo() {
        mCurrBand = 0;
        mCurrPreset = 0;
        mCurrBandFreq = 0;
        mPresetList = new int[MCU_RADIO_PRESETLIST_LENGTH];
    }

    public McuRadioPresetListInfo(McuRadioPresetListInfo presetListInfo) {
        mCurrBand = presetListInfo.mCurrBand;
        mCurrPreset = presetListInfo.mCurrPreset;
        mCurrBandFreq = presetListInfo.mCurrBandFreq;
        mPresetList = presetListInfo.mPresetList;
    }

    public int getCurrBand() {
        return mCurrBand;
    }

    public void setCurrBand(int currBand) {
        this.mCurrBand = currBand;
    }

    public int getCurrPreset() {
        return mCurrPreset;
    }

    public void setCurrPreset(int currPreset) {
        this.mCurrPreset = currPreset;
    }

    public int getCurrBandFreq() {
        return mCurrBandFreq;
    }

    public void setCurrBandFreq(int currBandFreq) {
        this.mCurrBandFreq = currBandFreq;
    }

    public int[] getPresetList() {
        return mPresetList;
    }

    public void setPresetList(int[] presetList) {
        this.mPresetList = presetList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mCurrBand);
        parcel.writeInt(mCurrPreset);
        parcel.writeInt(mCurrBandFreq);
        parcel.writeIntArray(mPresetList);
    }

    public static final Parcelable.Creator<McuRadioPresetListInfo> CREATOR = new Parcelable.Creator<McuRadioPresetListInfo>() {

        @Override
        public McuRadioPresetListInfo createFromParcel(Parcel parcel) {
            McuRadioPresetListInfo info = new McuRadioPresetListInfo();
            info.mCurrBand = parcel.readInt();
            info.mCurrPreset = parcel.readInt();
            info.mCurrBandFreq = parcel.readInt();
            info.mPresetList = parcel.createIntArray();

            return info;
        }

        @Override
        public McuRadioPresetListInfo[] newArray(int size) {
            return new McuRadioPresetListInfo[size];
        }

    };

}
