 package com.yecon.mediaservice;
 
 import org.json.JSONObject;

import com.yecon.mediaprovider.YeconMediaStore;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
 
 public class MediaTrackInfo
   implements Parcelable
 {
   private String mstrName;
   private String mstrTitle;
   private String mstrAlbum;
   private String mstrArtist;
   private int mFileID;
   private int mDuration;
   private int mTrackID;
   private int mTrackTotal;
   private Bitmap mBmpApic;
   private String path;
   public static final Parcelable.Creator<MediaTrackInfo> CREATOR = new Parcelable.Creator()
   {
     public MediaTrackInfo[] newArray(int size)
     {
       return new MediaTrackInfo[size];
     }
 
     public MediaTrackInfo createFromParcel(Parcel source)
     {
       MediaTrackInfo obj = new MediaTrackInfo();
       obj.setName(source.readString());
       obj.setTitle(source.readString());
       obj.setAlbum(source.readString());
       obj.setArtist(source.readString());
       obj.setFileID(source.readInt());
       obj.setDuration(source.readInt());
       obj.setTrackID(source.readInt());
       obj.setTrackTotal(source.readInt());
       obj.setApicBmp((Bitmap)source.readValue(Bitmap.class.getClassLoader()));
       obj.setPath(source.readString());
       return obj;
     }
   };
 
   public int describeContents()
   {
     return 0;
   }
 
   public void writeToParcel(Parcel dest, int flags)
   {
     dest.writeString(this.mstrName);
     dest.writeString(this.mstrTitle);
     dest.writeString(this.mstrAlbum);
     dest.writeString(this.mstrArtist);
     dest.writeInt(this.mFileID);
     dest.writeInt(this.mDuration);
     dest.writeInt(this.mTrackID);
     dest.writeInt(this.mTrackTotal);
     dest.writeValue(this.mBmpApic);
     dest.writeString(this.path);
   }
 
   public String getName() {
     return this.mstrName;
   }
 
   public void setName(String strName) {
     this.mstrName = strName;
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
 
   public int getFilePosInList() {
     return this.mFileID;
   }
 
   public void setFileID(int fileID) {
     this.mFileID = fileID;
   }
 
   public int getDuration() {
     return this.mDuration;
   }
 
   public void setDuration(int duration) {
     this.mDuration = duration;
   }
 
   public int getTrackID() {
     return this.mTrackID;
   }
 
   public void setTrackID(int TrackID)
   {
     this.mTrackID = TrackID;
   }
 
   public int getTrackTotal() {
     return this.mTrackTotal;
   }
 
   public void setTrackTotal(int TrackTotal) {
     this.mTrackTotal = TrackTotal;
   }
 
   public Bitmap getApicBmp() {
     return this.mBmpApic;
   }
 
   public void setApicBmp(Bitmap mBmpApic) {
     this.mBmpApic = mBmpApic;
   }
   
   
   
   public String getPath() {
	   return path;
   }
	
   public void setPath(String path) {
	   this.path = path;
   }
   public JSONObject getJoson(){
	   JSONObject object = null;
	   try {
		   object = new JSONObject();
		   object.put("name", TextUtils.isEmpty(mstrName) ? "" : mstrName);
		   object.put("duration", mDuration);
		   object.put("path", TextUtils.isEmpty(path) ? "" : path);
		   object.put("storage", TextUtils.isEmpty(path) ? "" : YeconMediaStore.getStoragePath(path));
		} catch (Exception e) {
			Log.e("MediaTrackInfo", e.toString());
		}
	   return object;
   }
   public void recycle(){}
  }


