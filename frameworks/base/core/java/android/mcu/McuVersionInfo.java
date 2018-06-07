
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public class McuVersionInfo implements Parcelable {
    private int mDataLength;
    private int[] mVersionData;
    private int[] mMcuID;

    public McuVersionInfo() {
        if (mDataLength == 0) {
            mDataLength = 1;
        }

        mVersionData = new int[mDataLength];

        mMcuID = new int[MCU_ID_INFO_LENGTH];
    }

    public McuVersionInfo(McuVersionInfo versionInfo) {
        mDataLength = versionInfo.mDataLength;
        mVersionData = versionInfo.mVersionData;
        mMcuID = versionInfo.mMcuID;
    }

    public McuVersionInfo(int dataLength) {
        mDataLength = dataLength;
        mVersionData = new int[dataLength];
        mMcuID = new int[MCU_ID_INFO_LENGTH];
    }

    public int getDataLength() {
        return mDataLength;
    }

    public void setDataLength(int dataLength) {
        this.mDataLength = dataLength;
    }

    public int[] getVersionData() {
        return mVersionData;
    }

    public void setVersionData(int[] versionData) {
        this.mVersionData = versionData;
    }

    public int[] getMcuID() {
        return mMcuID;
    }

    public void setMcuID(int[] mcuID) {
        this.mMcuID = mcuID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mDataLength);
        parcel.writeIntArray(mVersionData);
        parcel.writeIntArray(mMcuID);
    }

    public static final Parcelable.Creator<McuVersionInfo> CREATOR = new Parcelable.Creator<McuVersionInfo>() {

        @Override
        public McuVersionInfo createFromParcel(Parcel parcel) {
            McuVersionInfo info = new McuVersionInfo();
            info.mDataLength = parcel.readInt();
            info.mVersionData = parcel.createIntArray();
            info.mMcuID = parcel.createIntArray();
            return info;
        }

        @Override
        public McuVersionInfo[] newArray(int size) {
            return new McuVersionInfo[size];
        }

    };

}
