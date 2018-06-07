 package com.yecon.mediaservice;
 
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.os.Parcelable.Creator;
 
 public class MediaStorage
   implements Parcelable
 {
   private String mstrPath;
   private String mstrState;
   private int miAmountAudio;
   private int miAmountVideo;
   private int miAmountImage;
   private int mbAttach = 0;
 
   public static final Parcelable.Creator<MediaStorage> CREATOR = new Parcelable.Creator()
   {
     public MediaStorage[] newArray(int size)
     {
       return new MediaStorage[size];
     }
 
     public MediaStorage createFromParcel(Parcel source)
     {
       MediaStorage storage = new MediaStorage();
       storage.mstrPath = source.readString();
       storage.mstrState = source.readString();
       storage.miAmountAudio = source.readInt();
       storage.miAmountVideo = source.readInt();
       storage.miAmountImage = source.readInt();
       storage.mbAttach = source.readInt();
       return storage;
     }
   };
 
   public int describeContents()
   {
     return 0;
   }
 
   public void writeToParcel(Parcel dest, int flags)
   {
     dest.writeString(this.mstrPath);
     dest.writeString(this.mstrState);
     dest.writeInt(this.miAmountAudio);
     dest.writeInt(this.miAmountVideo);
     dest.writeInt(this.miAmountImage);
     dest.writeInt(this.mbAttach);
   }
 
   public boolean getPlaying() {
     return this.mbAttach != 0;
   }
 
   public void setPlaying(boolean bplaying) {
     this.mbAttach = (bplaying ? 1 : 0);
   }
 
   public String getPath() {
     return this.mstrPath;
   }
 
   public void setPath(String strPath) {
     this.mstrPath = strPath;
   }
 
   public String getState() {
     return this.mstrState;
   }
 
   public void setState(String strState) {
     this.mstrState = strState;
   }
 
   public int getAudioCount() {
     return this.miAmountAudio;
   }
 
   public void setAudioCount(int iAudio) {
     this.miAmountAudio = iAudio;
   }
 
   public int getVideoCount() {
     return this.miAmountVideo;
   }
 
   public void setVideoCount(int iVideo) {
     this.miAmountVideo = iVideo;
   }
 
   public int getImageCount() {
     return this.miAmountImage;
   }
 
   public void setImageCount(int iImage) {
     this.miAmountImage = iImage;
   }
 }

