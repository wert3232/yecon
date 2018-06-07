
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

public class McuOriginalInfo implements Parcelable {
    private int mDataLength;
    private byte[] mMcuData;

    public McuOriginalInfo() {
        if (mDataLength == 0) {
            mDataLength = 1;
        }
        mMcuData = new byte[mDataLength];
    }

    public McuOriginalInfo(McuOriginalInfo info) {
        mDataLength = info.mDataLength;
        mMcuData = info.mMcuData;
    }

    public McuOriginalInfo(int dataLength) {
        if (dataLength <= 0) {
            dataLength = 1;
        }
        mDataLength = dataLength;
        mMcuData = new byte[mDataLength];
    }

    public int getDataLength() {
        return mDataLength;
    }

    public void setDataLength(int dataLength) {
        this.mDataLength = dataLength;
    }

    public byte[] getMcuData() {
        return mMcuData;
    }

    public void setMcuData(byte[] mcuData) {
        this.mMcuData = mcuData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mDataLength);
        parcel.writeByteArray(mMcuData);
    }

    public static final Parcelable.Creator<McuOriginalInfo> CREATOR = new Parcelable.Creator<McuOriginalInfo>() {

        @Override
        public McuOriginalInfo createFromParcel(Parcel parcel) {
            McuOriginalInfo info = new McuOriginalInfo();
            info.mDataLength = parcel.readInt();
            info.mMcuData = parcel.createByteArray();
            return info;
        }

        @Override
        public McuOriginalInfo[] newArray(int size) {
            return new McuOriginalInfo[size];
        }

    };
}
