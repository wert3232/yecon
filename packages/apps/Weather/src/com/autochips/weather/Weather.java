package com.autochips.weather;

import java.text.DecimalFormat;

import android.graphics.drawable.Drawable;

public class Weather {

	public String highTemp;    

	public void setHighTempF(String highTemp) {
		DecimalFormat decimalFormat=new DecimalFormat(".0");
		this.highTemp = decimalFormat.format(5*(Float.parseFloat(highTemp)-32)/9);
	}
	public void setHighTempC(String highTemp) {
		this.highTemp = highTemp;
	}
	

	public void setLowTempF(String lowTemp) {
		DecimalFormat decimalFormat=new DecimalFormat(".0");
		this.lowTemp = decimalFormat.format(5*(Float.parseFloat(lowTemp)-32)/9);
	}
	public void setLowTempC(String lowTemp) {
		this.lowTemp = lowTemp;
	}


	public void setCurrentTempC(String currentTemp) {
		this.currentTemp = currentTemp;
	}
	public void setCurrentTempF(String currentTemp) {
		//5¡Á(¨H- 32)/9
		DecimalFormat decimalFormat=new DecimalFormat(".0");
		this.currentTemp = decimalFormat.format(5*(Float.parseFloat(currentTemp)-32)/9);
	}
	
	public String lowTemp;

	public String currentTemp;
    
    public String cityname;

    public String condition;

    public String unit;

    public int code = -1;

    public Drawable imageDrawable;

    public Weather() {

    }

    public Weather(String arg0) {
        highTemp = arg0;
        lowTemp = arg0;
        currentTemp = arg0;
        condition = arg0;
        cityname = arg0;
        code = -1;
    }

    public void wClone(Weather w) {
        this.code = w.code;
        this.condition = w.condition;
        this.currentTemp = w.currentTemp;
        this.highTemp = w.highTemp;
        this.lowTemp = w.lowTemp;
        this.imageDrawable = w.imageDrawable;
        this.unit = w.unit;
        this.cityname = w.cityname;
    }
    
    
}
