
package com.yecon.music;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicFileInfo implements Parcelable {
    private long mId;
    private int mParent;
    private int mCount;
    private String mDirName;
    private String mDirPath;
    private String mFilename;
    private String mArtistName;
    private boolean mIsPlaying;

    public MusicFileInfo() {
        mId = 0;
        mParent = 0;
        mCount = 0;
        mDirName = "";
        mDirPath = "";
        mFilename = "";
        mArtistName = "";
        mIsPlaying = false;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public int getParent() {
        return mParent;
    }

    public void setParent(int parent) {
        this.mParent = parent;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public String getDirName() {
        return mDirName;
    }

    public void setDirName(String dirName) {
        this.mDirName = dirName;
    }

    public String getDirPath() {
        return mDirPath;
    }

    public void setDirPath(String dirPath) {
        this.mDirPath = dirPath;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        this.mFilename = filename;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public void setArtistName(String artistName) {
        this.mArtistName = artistName;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.mIsPlaying = isPlaying;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(mId);
        parcel.writeInt(mParent);
        parcel.writeInt(mCount);
        parcel.writeString(mDirName);
        parcel.writeString(mDirPath);
        parcel.writeString(mFilename);
        parcel.writeString(mArtistName);
        parcel.writeBooleanArray(new boolean[] {
                mIsPlaying
        });
    }

    public static final Parcelable.Creator<MusicFileInfo> CREATOR = new Parcelable.Creator<MusicFileInfo>() {

        @Override
        public MusicFileInfo createFromParcel(Parcel parcel) {
            MusicFileInfo info = new MusicFileInfo();
            info.setId(parcel.readLong());
            info.setParent(parcel.readInt());
            info.setCount(parcel.readInt());
            info.setDirName(parcel.readString());
            info.setDirPath(parcel.readString());
            info.setFilename(parcel.readString());
            info.setArtistName(parcel.readString());
            boolean[] isPlaying = parcel.createBooleanArray();
            info.setIsPlaying(isPlaying[0]);
            return null;
        }

        @Override
        public MusicFileInfo[] newArray(int size) {
            return new MusicFileInfo[size];
        }

    };
}
