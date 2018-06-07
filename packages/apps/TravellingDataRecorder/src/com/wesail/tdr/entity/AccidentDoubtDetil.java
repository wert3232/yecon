package com.wesail.tdr.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AccidentDoubtDetil implements Parcelable{
	private float parkingBeforeSeconds;
	private float speed;
	private int brake;
	private int turnLeft;
	private int turnRight;
	private int farLight;
	private int dippedLight;
	private int alarm;
	private int openTheDoor;
	public  AccidentDoubtDetil() {		
	}
	public AccidentDoubtDetil( float parkingBeforeSeconds,float speed,int brake,
			int turnLeft, int turnRight,
			int farLight, int dippedLight,
			int alarm, int openTheDoor) {
		this.parkingBeforeSeconds = parkingBeforeSeconds;
		this.speed = speed;
		this.brake = brake;
		this.turnLeft = turnLeft;
		this.turnRight = turnRight;
		this.farLight = farLight;
		this.dippedLight = dippedLight;
		this.alarm = alarm;
		this.openTheDoor = openTheDoor;
	}
	
	public float getParkingBeforeSeconds() {
		return parkingBeforeSeconds;
	}


	public void setParkingBeforeSeconds(float parkingBeforeSeconds) {
		this.parkingBeforeSeconds = parkingBeforeSeconds;
	}


	public float getSpeed() {
		return speed;
	}


	public void setSpeed(float speed) {
		this.speed = speed;
	}


	public int getBrake() {
		return brake;
	}


	public void setBrake(int brake) {
		this.brake = brake;
	}


	public int getTurnLeft() {
		return turnLeft;
	}


	public void setTurnLeft(int turnLeft) {
		this.turnLeft = turnLeft;
	}


	public int getTurnRight() {
		return turnRight;
	}


	public void setTurnRight(int turnRight) {
		this.turnRight = turnRight;
	}


	public int getFarLight() {
		return farLight;
	}


	public void setFarLight(int farLight) {
		this.farLight = farLight;
	}


	public int getDippedLight() {
		return dippedLight;
	}


	public void setDippedLight(int dippedLight) {
		this.dippedLight = dippedLight;
	}


	public int getAlarm() {
		return alarm;
	}


	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}


	public int getOpenTheDoor() {
		return openTheDoor;
	}


	public void setOpenTheDoor(int openTheDoor) {
		this.openTheDoor = openTheDoor;
	}


	public static Parcelable.Creator<AccidentDoubtDetil> getCreator() {
		return CREATOR;
	}


	public static final Parcelable.Creator<AccidentDoubtDetil> CREATOR = new Creator<AccidentDoubtDetil>() {  
        public AccidentDoubtDetil createFromParcel(Parcel source) {  
        	AccidentDoubtDetil detil = new AccidentDoubtDetil();  
        	detil.parkingBeforeSeconds = source.readFloat();  
        	detil.speed = source.readFloat();
        	detil.brake = source.readInt();
        	detil.turnLeft = source.readInt();
        	detil.turnRight = source.readInt();
        	detil.farLight = source.readInt();
        	detil.dippedLight = source.readInt();
        	detil.alarm = source.readInt();
        	detil.openTheDoor = source.readInt(); 	
            return detil;  
        }  
        public AccidentDoubtDetil[] newArray(int size) {  
            return new AccidentDoubtDetil[size];  
        }  
    };  
    
	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(parkingBeforeSeconds);
		dest.writeFloat(speed); 
		dest.writeInt(brake); 
		dest.writeInt(turnLeft); 
		dest.writeInt(turnRight); 
		dest.writeInt(farLight); 
		dest.writeInt(dippedLight); 
		dest.writeInt(alarm); 
		dest.writeInt(openTheDoor); 
	}

}
