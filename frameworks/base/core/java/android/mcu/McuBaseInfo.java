
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

public class McuBaseInfo implements Parcelable {
    // private static final String TAG = "mcu_services";

    private McuRadioBandInfo mRadioBandInfo;
    private McuRadioPresetListInfo mRadioPresetListInfo;
    private McuDeviceStatusInfo mDeviceStatusInfo;
    private McuSWCInfo mSWCInfo;
    private McuSystemParamInfo mSystemParamInfo;
    private McuUpgradeInfo mUpgradeInfo;
    private McuVersionInfo mVersionInfo;
    private McuOriginalInfo mOriginalInfo;

    public McuBaseInfo() {
        // Log.e(TAG, "McuBaseInfo - create McuBaseInfo object");

        mRadioBandInfo = new McuRadioBandInfo();
        mRadioPresetListInfo = new McuRadioPresetListInfo();
        mDeviceStatusInfo = new McuDeviceStatusInfo();
        mSWCInfo = new McuSWCInfo();
        mSystemParamInfo = new McuSystemParamInfo();
        mUpgradeInfo = new McuUpgradeInfo();
        mVersionInfo = new McuVersionInfo();
        mOriginalInfo = new McuOriginalInfo();
    }

    public McuBaseInfo(McuBaseInfo mcuBaseInfo) {
        mRadioBandInfo = mcuBaseInfo.mRadioBandInfo;
        mRadioPresetListInfo = mcuBaseInfo.mRadioPresetListInfo;
        mDeviceStatusInfo = mcuBaseInfo.mDeviceStatusInfo;
        mSWCInfo = mcuBaseInfo.mSWCInfo;
        mSystemParamInfo = mcuBaseInfo.mSystemParamInfo;
        mUpgradeInfo = mcuBaseInfo.mUpgradeInfo;
        mVersionInfo = mcuBaseInfo.mVersionInfo;
        mOriginalInfo = mcuBaseInfo.mOriginalInfo;
    }

    public McuRadioBandInfo getRadioBandInfo() {
        return mRadioBandInfo;
    }

    public void setRadioBandInfo(McuRadioBandInfo radioBandInfo) {
        this.mRadioBandInfo = radioBandInfo;
    }

    public McuRadioPresetListInfo getRadioPresetListInfo() {
        return mRadioPresetListInfo;
    }

    public void setRadioPresetListInfo(McuRadioPresetListInfo radioPresetListInfo) {
        this.mRadioPresetListInfo = radioPresetListInfo;
    }

    public McuDeviceStatusInfo getDeviceStatusInfo() {
        return mDeviceStatusInfo;
    }

    public void setDeviceStatusInfo(McuDeviceStatusInfo deviceStatusInfo) {
        this.mDeviceStatusInfo = deviceStatusInfo;
    }

    public McuSWCInfo getSWCInfo() {
        return mSWCInfo;
    }

    public void setSWCInfo(McuSWCInfo swcInfo) {
        this.mSWCInfo = swcInfo;
    }

    public McuSystemParamInfo getSystemParamInfo() {
        return mSystemParamInfo;
    }

    public void setSystemParamInfo(McuSystemParamInfo systemParamInfo) {
        this.mSystemParamInfo = systemParamInfo;
    }

    public McuUpgradeInfo getUpgradeInfo() {
        return mUpgradeInfo;
    }

    public void setUpgradeInfo(McuUpgradeInfo upgradeInfo) {
        this.mUpgradeInfo = upgradeInfo;
    }

    public McuVersionInfo getVersionInfo() {
        return mVersionInfo;
    }

    public void setVersionInfo(McuVersionInfo versionInfo) {
        this.mVersionInfo = versionInfo;
    }

    public McuOriginalInfo getOriginalInfo() {
        return mOriginalInfo;
    }

    public void setOriginalInfo(McuOriginalInfo originalInfo) {
        this.mOriginalInfo = originalInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        // Log.e(TAG, "McuBaseInfo - writeToParcel - flags: " + flags);

        parcel.writeParcelable(mRadioBandInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mRadioPresetListInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mDeviceStatusInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mSWCInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mSystemParamInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mUpgradeInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mVersionInfo, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(mOriginalInfo, PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Parcelable.Creator<McuBaseInfo> CREATOR = new
            Parcelable.Creator<McuBaseInfo>() {

                @Override
                public McuBaseInfo createFromParcel(Parcel parcel) {
                    // Log.e(TAG, "McuBaseInfo - createFromParcel");

                    McuBaseInfo info = new McuBaseInfo();
                    info.mRadioBandInfo = parcel.readParcelable(McuRadioBandInfo.class
                            .getClassLoader());
                    info.mRadioPresetListInfo = parcel.readParcelable(McuRadioPresetListInfo.class
                            .getClassLoader());
                    info.mDeviceStatusInfo = parcel.readParcelable(McuDeviceStatusInfo.class
                            .getClassLoader());
                    info.mSWCInfo = parcel.readParcelable(McuSWCInfo.class.getClassLoader());
                    info.mSystemParamInfo = parcel.readParcelable(McuSystemParamInfo.class
                            .getClassLoader());
                    info.mUpgradeInfo = parcel
                            .readParcelable(McuUpgradeInfo.class.getClassLoader());
                    info.mVersionInfo = parcel
                            .readParcelable(McuVersionInfo.class.getClassLoader());
                    info.mOriginalInfo = parcel.readParcelable(McuOriginalInfo.class
                            .getClassLoader());

                    return info;
                }

                @Override
                public McuBaseInfo[] newArray(int size) {
                    // Log.e(TAG, "McuBaseInfo - newArray");

                    return new McuBaseInfo[size];
                }
            };

}
