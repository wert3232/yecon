
package android.mcu;

import android.os.Parcel;
import android.os.Parcelable;

public final class McuRadioBandInfo implements Parcelable {
    private int mCurrBandMinFreq;
    private int mCurrBandMaxFreq;
    private int mGranularity;
    private int mAutoMemoryScanStatus;
    private boolean mIsAutoScan;
    private boolean mIsStereo;
    private boolean mUseStereo;
    private boolean mIsLoc;
    private int mPreviewScanStatus;
	private int mSeekStatus;
	private int[] mFMPreset;
	private int[] mAMPreset;
    public McuRadioBandInfo() {
        mCurrBandMinFreq = 0;
        mCurrBandMaxFreq = 0;
        mGranularity = 0;
        mAutoMemoryScanStatus = 0;
		mIsAutoScan = false;
        mIsStereo = false;
        mUseStereo = false;
        mIsLoc = false;
        mPreviewScanStatus = 0;
		mSeekStatus = 0;
		mFMPreset = new int[]{};
		mAMPreset = new int[]{};
    }

    public McuRadioBandInfo(McuRadioBandInfo bandInfo) {
        mCurrBandMinFreq = bandInfo.mCurrBandMinFreq;
        mCurrBandMaxFreq = bandInfo.mCurrBandMaxFreq;
        mGranularity = bandInfo.mGranularity;
        mAutoMemoryScanStatus = bandInfo.mAutoMemoryScanStatus;
        mIsAutoScan = bandInfo.mIsAutoScan;
        mIsStereo = bandInfo.mIsStereo;
        mUseStereo = bandInfo.mUseStereo;
        mIsLoc = bandInfo.mIsLoc;
        mPreviewScanStatus = bandInfo.mPreviewScanStatus;
		mSeekStatus = bandInfo.mSeekStatus;
		mFMPreset = bandInfo.mFMPreset;
		mAMPreset = bandInfo.mAMPreset;
    }

    public int getCurrBandMinFreq() {
        return mCurrBandMinFreq;
    }

    public void setCurrBandMinFreq(int currBandMinFreq) {
        this.mCurrBandMinFreq = currBandMinFreq;
    }

    public int getCurrBandMaxFreq() {
        return mCurrBandMaxFreq;
    }

    public void setCurrBandMaxFreq(int currBandMaxFreq) {
        this.mCurrBandMaxFreq = currBandMaxFreq;
    }

    public int getGranularity() {
        return mGranularity;
    }

    public void setGranularity(int granularity) {
        this.mGranularity = granularity;
    }

    public int getAutoMemoryScanStatus() {
        return mAutoMemoryScanStatus;
    }

    public void setAutoMemoryScanStatus(int autoMemoryScanStatus) {
        this.mAutoMemoryScanStatus = autoMemoryScanStatus;
    }

    public boolean isStereo() {
        return mIsStereo;
    }

    public void setStereo(boolean isStereo) {
        this.mIsStereo = isStereo;
    }

    public boolean isAutoScan() {
        return mIsAutoScan;
    }

    public void setAutoScan(boolean isAutoScan) {
        this.mIsAutoScan = isAutoScan;
    }

    public boolean isUseStereo() {
        return mUseStereo;
    }

    public void setUseStereo(boolean useStereo) {
        this.mUseStereo = useStereo;
    }

    public boolean isLoc() {
        return mIsLoc;
    }

    public void setLoc(boolean isLoc) {
        this.mIsLoc = isLoc;
    }

    public int getPreviewScanStatus() {
        return mPreviewScanStatus;
    }

    public void setPreviewScanStatus(int previewScanStatus) {
        this.mPreviewScanStatus = previewScanStatus;
    }
	
	public int getSeekStatus() {
        return mSeekStatus;
    }

    public void setSeekStatus(int mSeekStatus) {
        this.mSeekStatus = mSeekStatus;
    }
	
	public int[] getFMPreset() {
		return mFMPreset;
    }

    public void setFMPreset(int [] mFMPreset) {
        this.mFMPreset = mFMPreset;
    }

    public void setFMPreset(int index,int preset) {
        if(index == (mFMPreset.length)){
			int[] temp = new int[index+1];
			for(int i = 0;i<mFMPreset.length;i++){
				temp[i] = mFMPreset[i];
			}
			temp[mFMPreset.length] = preset;
			mFMPreset = temp;
		}else if(index == 0){
			mFMPreset = new int[]{preset};
		}else if(index <= mFMPreset.length){
			mFMPreset[index] = preset;
		}
    }

    public void cleanFMPreset() {
        this.mFMPreset = new int[]{};
    }
	
	public int[] getAMPreset() {
		return mAMPreset;
    }

    public void setAMPreset(int [] mAMPreset) {
        this.mAMPreset = mAMPreset;
    }

    public void setAMPreset(int index,int preset) {
        if(index == (mAMPreset.length)){
			int[] temp = new int[index+1];
			for(int i = 0;i<mAMPreset.length;i++){
				temp[i] = mAMPreset[i];
			}
			temp[mAMPreset.length] = preset;
			mAMPreset = temp;
		}else if(index ==0){
			mAMPreset = new int[]{preset};
		}else if(index <= mAMPreset.length){
			mAMPreset[index] = preset;
		}
    }

    public void cleanAMPreset() {
        this.mAMPreset = new int[]{};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mCurrBandMinFreq);
        parcel.writeInt(mCurrBandMaxFreq);
        parcel.writeInt(mGranularity);
        parcel.writeInt(mAutoMemoryScanStatus);
        parcel.writeBooleanArray(new boolean[] {
                mIsAutoScan
        });
        parcel.writeBooleanArray(new boolean[] {
                mIsStereo
        });
        parcel.writeBooleanArray(new boolean[] {
                mUseStereo
        });
        parcel.writeBooleanArray(new boolean[] {
                mIsLoc
        });
        parcel.writeInt(mPreviewScanStatus);
		parcel.writeInt(mSeekStatus);
		parcel.writeIntArray(mFMPreset);
		parcel.writeIntArray(mAMPreset);
    }

    public static final Parcelable.Creator<McuRadioBandInfo> CREATOR = new Parcelable.Creator<McuRadioBandInfo>() {

        @Override
        public McuRadioBandInfo createFromParcel(Parcel parcel) {
            McuRadioBandInfo info = new McuRadioBandInfo();
            info.setCurrBandMinFreq(parcel.readInt());
            info.setCurrBandMaxFreq(parcel.readInt());
            info.setGranularity(parcel.readInt());
            info.setAutoMemoryScanStatus(parcel.readInt());
            boolean[] isAutoScan = parcel.createBooleanArray();
            info.setAutoScan(isAutoScan[0]);
            boolean[] isStereo = parcel.createBooleanArray();
            info.setStereo(isStereo[0]);
            boolean[] useStereo = parcel.createBooleanArray();
            info.setUseStereo(useStereo[0]);
            boolean[] isLoc = parcel.createBooleanArray();
            info.setLoc(isLoc[0]);
            info.setPreviewScanStatus(parcel.readInt());
			info.setSeekStatus(parcel.readInt());
			info.setFMPreset(parcel.createIntArray());
			info.setAMPreset(parcel.createIntArray());
			
            return info;
        }

        @Override
        public McuRadioBandInfo[] newArray(int size) {
            return new McuRadioBandInfo[size];
        }

    };

}
