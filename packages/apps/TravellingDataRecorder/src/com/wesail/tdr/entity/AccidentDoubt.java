package com.wesail.tdr.entity;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class AccidentDoubt implements Parcelable{
	private String time;
	private String driver_s_license_number;
	private ArrayList<AccidentDoubtDetil> list;
	public AccidentDoubt(){
		list = new ArrayList<AccidentDoubtDetil>();
	}
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getDriver_s_license_number() {
		return driver_s_license_number;
	}
	
	public ArrayList<AccidentDoubtDetil> getList() {
		return list;
	}
	public void setList(ArrayList<AccidentDoubtDetil> list) {
		this.list = list;
	}
	public void setDriver_s_license_number(String driver_s_license_number) {
		this.driver_s_license_number = driver_s_license_number;
	}
	public static final Parcelable.Creator<AccidentDoubt> CREATOR = new Creator<AccidentDoubt>() {  
        public AccidentDoubt createFromParcel(Parcel source) {  
        	AccidentDoubt item = new AccidentDoubt();  
        	item.time = source.readString();  
        	item.driver_s_license_number = source.readString();
        	source.readTypedList(item.list, AccidentDoubtDetil.CREATOR);
            return item;  
        }  
        public AccidentDoubt[] newArray(int size) {  
            return new AccidentDoubt[size];  
        }  
    };  
    
	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(time);  
		dest.writeString(driver_s_license_number);
		dest.writeTypedList(list);
	}

}
