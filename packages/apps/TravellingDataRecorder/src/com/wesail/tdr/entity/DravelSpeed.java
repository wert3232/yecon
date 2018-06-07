package com.wesail.tdr.entity;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DravelSpeed implements Parcelable{
	private String time;
	private ArrayList<DravelSpeedDetil> list;
	public DravelSpeed(){
		list = new ArrayList<DravelSpeedDetil>();
	}
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public ArrayList<DravelSpeedDetil> getList() {
		return list;
	}
	public void setList(ArrayList<DravelSpeedDetil> list) {
		this.list = list;
	}
	public static final Parcelable.Creator<DravelSpeed> CREATOR = new Creator<DravelSpeed>() {  
        public DravelSpeed createFromParcel(Parcel source) {  
        	DravelSpeed item = new DravelSpeed();  
        	item.time = source.readString();  
        	source.readTypedList(item.list, DravelSpeedDetil.CREATOR);
            return item;  
        }  
        public DravelSpeed[] newArray(int size) {  
            return new DravelSpeed[size];  
        }  
    };  
    
	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(time);  
		dest.writeTypedList(list);
	}

}
