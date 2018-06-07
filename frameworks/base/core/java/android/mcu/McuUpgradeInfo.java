
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

import static android.mcu.McuExternalConstant.*;

public class McuUpgradeInfo implements Parcelable {
    private int mUpgradeState;

    public McuUpgradeInfo() {
        mUpgradeState = MCU_UPGRADE_UNKNOWN;
    }

    public int getUpgradeState() {
        return mUpgradeState;
    }

    public void setUpgradeState(int upgradeState) {
        this.mUpgradeState = upgradeState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mUpgradeState);
    }

    public static final Parcelable.Creator<McuUpgradeInfo> CREATOR = new Parcelable.Creator<McuUpgradeInfo>() {

        @Override
        public McuUpgradeInfo createFromParcel(Parcel parcel) {
            McuUpgradeInfo info = new McuUpgradeInfo();
            info.mUpgradeState = parcel.readInt();
            return info;
        }

        @Override
        public McuUpgradeInfo[] newArray(int size) {
            return new McuUpgradeInfo[size];
        }

    };
}
