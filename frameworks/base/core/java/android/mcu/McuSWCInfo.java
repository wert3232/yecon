
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public class McuSWCInfo implements Parcelable {
    private int[] mSWCData;
    private int[] mSWCSamplingResults;
    private int mMatchingResistance;

    public McuSWCInfo() {
        mSWCData = new int[MCU_SWC_DATA_LENGTH];

        mSWCSamplingResults = new int[MCU_SWC_SAMPLING_RESULTS_LENGTH];

        mMatchingResistance = MCU_DATA_SWITCH_OFF;
    }

    public McuSWCInfo(McuSWCInfo swcInfo) {
        mSWCData = swcInfo.mSWCData;

        mSWCSamplingResults = swcInfo.mSWCSamplingResults;

        mMatchingResistance = swcInfo.mMatchingResistance;
    }

    public int[] getSWCData() {
        return mSWCData;
    }

    public void setSWCData(int[] swcData) {
        this.mSWCData = swcData;
    }

    public int[] getSWCSamplingResults() {
        return mSWCSamplingResults;
    }

    public void setSWCSamplingResults(int[] swcSamplingResults) {
        this.mSWCSamplingResults = swcSamplingResults;
    }

    public int getMatchingResistance() {
        return mMatchingResistance;
    }

    public void setMatchingResistance(int matchingResistance) {
        this.mMatchingResistance = matchingResistance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeIntArray(mSWCData);
        parcel.writeIntArray(mSWCSamplingResults);
        parcel.writeInt(mMatchingResistance);
    }

    public static final Parcelable.Creator<McuSWCInfo> CREATOR = new Parcelable.Creator<McuSWCInfo>() {

        @Override
        public McuSWCInfo createFromParcel(Parcel parcel) {
            McuSWCInfo info = new McuSWCInfo();
            info.mSWCData = parcel.createIntArray();
            info.mSWCSamplingResults = parcel.createIntArray();
            info.mMatchingResistance = parcel.readInt();

            return info;
        }

        @Override
        public McuSWCInfo[] newArray(int size) {
            return new McuSWCInfo[size];
        }

    };
}
