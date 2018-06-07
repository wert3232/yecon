package com.wesail.tdr.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DravelSpeedDetil implements Parcelable{
	private String time;
	private float speed;

	public  DravelSpeedDetil() {		
	}
	
	public DravelSpeedDetil( String time,float speed) {
		this.time = time;
		this.speed = speed;
	}
	
	public float getSpeed() {
		return speed;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public static Parcelable.Creator<DravelSpeedDetil> getCreator() {
		return CREATOR;
	}


	public static final Parcelable.Creator<DravelSpeedDetil> CREATOR = new Creator<DravelSpeedDetil>() {  
        public DravelSpeedDetil createFromParcel(Parcel source) {  
        	DravelSpeedDetil detil = new DravelSpeedDetil();  
        	detil.time = source.readString();  
        	detil.speed = source.readFloat();
            return detil;  
        }  
        public DravelSpeedDetil[] newArray(int size) {  
            return new DravelSpeedDetil[size];  
        }  
    };  
    
	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(time);
		dest.writeFloat(speed); 
	}

}
