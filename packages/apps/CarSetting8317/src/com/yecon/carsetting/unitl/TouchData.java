package com.yecon.carsetting.unitl;

public class TouchData {

	public int x0;//X起始坐标
	public int x1;//X终止坐标
	public int y0;//Y起始坐标
	public int y1;//Y终止坐标
	public int shortKeyCode;//短按功能键
	public int longKeyCode;//长按功能键
	public int btnID;
	
	public TouchData(){
		x0 = 0; x1 = 0;
		y0 = 0; y1 = 0;
		longKeyCode = 0;
		shortKeyCode = 0;
		btnID = 0;
	}	
}