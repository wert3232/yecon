package com.yecon.avin;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.exdevport.SerialPort;
import android.mcu.McuManager;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

public class DvrProtocol {
	private static final String TAG = "DvrProtocol";
	private final int UART_TYPE = 0;//0: mcu ; 1: serial port.
	private final int DATA_START_BYTE=0xFE;
	private final int MCU_EXTEND_CMD_DVR_DATA = 0xC8;
	
	private final int DST_X_MAX = 720;
	private final int DST_Y_MAX = 480;
	
	private final int CMD_POINT_DOWN = 0xA1;
	private final int CMD_POINT_UP = 0xA2;
	private final int CMD_TIME = 0xA4;
	private final int CMD_DATE = 0xA5;
	private byte[]tmpBuffer = new byte[128];
	private byte[]sendBuffer = new byte[128];
	
	private McuManager mcuManager;
	private SerialPort mSerialPort;
	private Context context;
	private int screen_width, screen_height;
	public DvrProtocol(Activity context){
		this.context = context;
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screen_width = dm.widthPixels;
		screen_height = dm.heightPixels;
		Log.i(TAG, String.format("screen_width screen_height[%d, %d]", screen_width, screen_height));
	}
	
	public void init(){
		if(UART_TYPE == 0){
			mcuManager = (McuManager) context.getSystemService(Context.MCU_SERVICE);
		}
		else{
			try 
			{ 
				mSerialPort = new SerialPort(new File("/dev/ttyMT5"),19200);
			}
	       catch (Exception e) 
	       { 
	    	   e.printStackTrace(); 
	       }
		}		
	}
	
	public void sendDate(){
		if(UART_TYPE == 0){
			Calendar cTime = Calendar.getInstance(); 
			tmpBuffer[0] = (byte) (cTime.get(Calendar.YEAR)-2000);
			tmpBuffer[1] = (byte) (cTime.get(Calendar.MONTH)+1);
			tmpBuffer[2] = (byte) cTime.get(Calendar.DAY_OF_MONTH);
			tmpBuffer[3] = (byte) (cTime.get(Calendar.DAY_OF_WEEK)-1);
			Log.i(TAG, String.format("sendDate %02d %02d %02d %02d ", tmpBuffer[0], tmpBuffer[1], tmpBuffer[2],tmpBuffer[3]));
			int ret = pack2databytes(CMD_DATE, tmpBuffer, 4);
			send2mcu(sendBuffer, ret);
		}
		else{
			mSerialPort.sendYear();
		}
	}
	
	public void sendTime(){
		if(UART_TYPE == 0){
			Calendar cTime = Calendar.getInstance();
			tmpBuffer[0] = (byte) cTime.get(Calendar.HOUR_OF_DAY);
			tmpBuffer[1] = (byte) cTime.get(Calendar.MINUTE);
			tmpBuffer[2] = (byte) cTime.get(Calendar.SECOND);
			Log.i(TAG, String.format("sendTime %02d %02d %02d ", tmpBuffer[0], tmpBuffer[1], tmpBuffer[2]));
			int ret = pack2databytes(CMD_TIME, tmpBuffer, 3);
			send2mcu(sendBuffer, ret);
		}
		else{
			mSerialPort.sendTime(DateFormat.is24HourFormat(context.getApplicationContext()));			
		}
	}
	
	public void sendTouchEvent(int code, int x, int y){
		if(UART_TYPE == 0){
			if(mcuManager!=null){
				if(MotionEvent.ACTION_DOWN == code){
					if(x>screen_width)x = screen_width;
					if(y>screen_height)y = screen_height;
					float xx = DST_X_MAX*x/screen_width;
					float yy = DST_Y_MAX*y/screen_height;
					Log.i(TAG, String.format("touch: [%d, %d]", (int)xx,(int)yy));
					tmpBuffer[0] = (byte)(((int)xx)&0xff);
					tmpBuffer[1] = (byte) ((((int)xx>>4)&0xf0) | (((int)yy>>8)&0x0f));
					tmpBuffer[2] = (byte)(((int)yy)&0xff);
					int ret = pack2databytes(CMD_POINT_DOWN, tmpBuffer, 3);
					send2mcu(sendBuffer, ret);
				}
				else if(MotionEvent.ACTION_UP == code){
					int ret = pack2databytes(CMD_POINT_UP, tmpBuffer, 0);
					send2mcu(sendBuffer, ret);
				}
			
			}
		}
		else{
			if (mSerialPort != null) {
				
				try {
					mSerialPort.sendDownPos((int)x, (int)y);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
	}
	
	public void release(){
		if(UART_TYPE == 1){
			if (mSerialPort != null) {
				mSerialPort.close();
			}
		}
	}
	
	private int pack2databytes(int cmd, byte[]data, int len){
		int sum = 0; int i=0;
		sendBuffer[0]=(byte) DATA_START_BYTE;
		sendBuffer[1]=(byte) (len+1);
		sendBuffer[2]=(byte) cmd;
		sum = (sendBuffer[0]+sendBuffer[1]+sendBuffer[2]);
		for(i=0;i<len;i++){
			sendBuffer[i+3]=data[i];
			sum+=sendBuffer[i+3];
		}
		sum = -sum;
		sendBuffer[i+3]= (byte) (sum&0xff);
		return (4+len);
	}
	
	private void send2mcu(byte[]data, int len){
		if(data!=null && len >0 && data.length>=len){
			try {
//				Log.i(TAG, "send2mcu: len " + len);
//				
//				for(int i=0;i<len;i++){
//					Log.i(TAG, String.format("%02x ", data[i]));
//				}
				mcuManager.RPC_SendExtendCmd(MCU_EXTEND_CMD_DVR_DATA, data, len);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	
}
