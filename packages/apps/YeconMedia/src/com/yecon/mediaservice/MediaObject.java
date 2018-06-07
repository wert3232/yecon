 package com.yecon.mediaservice;
 
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.os.Parcelable.Creator;
 
 public class MediaObject
   implements Parcelable
 {
   private static final String DEFAULT_VALUE = "";
   private int miObjectType = 0;
 
   private int miID = -1;
 
   private String mstrName = "";
 
   private String mstrPath = "";
 
   private String mstrTitle = "";
 
   private String mstrAlbum = "";
 
   private String mstrArtist = "";
 
   private int miAudio = 0;
 
   private int miImage = 0;
 
   private int miVideo = 0;
 
   private int miFavor = 1;

   public static final Parcelable.Creator<MediaObject> CREATOR = new Parcelable.Creator()
   {
     public MediaObject createFromParcel(Parcel source)
     {
       MediaObject obj = new MediaObject();
       obj.miObjectType = source.readInt();
       obj.miID = source.readInt();
       obj.mstrName = source.readString();
       obj.mstrPath = source.readString();
       obj.mstrTitle = source.readString();
       obj.mstrAlbum = source.readString();
       obj.mstrArtist = source.readString();
       obj.miAudio = source.readInt();
       obj.miVideo = source.readInt();
       obj.miImage = source.readInt();
       obj.miFavor = source.readInt();
       return obj;
     }
 
     public MediaObject[] newArray(int size)
     {
       return new MediaObject[size];
     }
   };
 
   public int describeContents()
   {
     return 0;
   }
 
   public void writeToParcel(Parcel dest, int flags)
   {
     dest.writeInt(this.miObjectType);
     dest.writeInt(this.miID);
     dest.writeString(this.mstrName);
     dest.writeString(this.mstrPath);
     dest.writeString(this.mstrTitle);
     dest.writeString(this.mstrAlbum);
     dest.writeString(this.mstrArtist);
     dest.writeInt(this.miAudio);
     dest.writeInt(this.miImage);
     dest.writeInt(this.miVideo);
     dest.writeInt(this.miFavor);
   }
 
   public int getObjectType() {
     return this.miObjectType;
   }
 
   public void setObjectType(int iMediaType) {
     this.miObjectType = iMediaType;
   }
 
   public int getID() {
     return this.miID;
   }
 
   public void setID(int iID) {
     this.miID = iID;
   }
 
   public String getName() {
     return this.mstrName;
   }
 
   public void setName(String strName) {
     this.mstrName = strName;
   }
 
   public String getPath() {
     return this.mstrPath;
   }
 
   public void setPath(String strPath) {
     this.mstrPath = strPath;
   }
 
   public String getTitle() {
     return this.mstrTitle;
   }
 
   public void setTitle(String strTitle) {
     this.mstrTitle = strTitle;
   }
 
   public String getAlbum() {
     return this.mstrAlbum;
   }
 
   public void setAlbum(String strAlbum) {
     this.mstrAlbum = strAlbum;
   }
 
   public String getArtist() {
     return this.mstrArtist;
   }
 
   public void setArtist(String strArtist) {
     this.mstrArtist = strArtist;
   }
 
   public int getAudioCount() {
     return this.miAudio;
   }
 
   public void setAudioCount(int iAudio) {
     this.miAudio = iAudio;
   }
 
   public int getImageCount() {
     return this.miImage;
   }
 
   public void setImageCount(int iImage) {
     this.miImage = iImage;
   }
 
   public int getVideoCount() {
     return this.miVideo;
   }
 
   public void setVideoCount(int iVideo) {
     this.miVideo = iVideo;
   }
 
   public void setFavor(boolean bExist) {
     this.miFavor = (bExist ? 1 : 0);
   }
 
   public void setFavor(int iFavor) {
     this.miFavor = iFavor;
   }
 
   public boolean getFavor() {
     return this.miFavor == 1;
   }
 }

