package com.yecon.carsetting.unitl;
public class TouchPos {

	private int xPos;
	private int yPos;
	
	public TouchPos(){
		
	}
	
	public TouchPos(int xpos,int ypos){
		xPos = xpos;
		yPos = ypos;
	}
	
	public void setPos(int xpos,int ypos){
		xPos = xpos;
		yPos = ypos;
	}
	
	public int getXPos(){
		return xPos;
	}
	
	public int getYPos(){
		return yPos;
	}
	
}