package android.exdevport;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream; 
import java.util.Calendar;

//import com.yecon.avin.AVINConstant;

import android.R.integer;
import android.util.Log;

public class SerialPort {

	
	private static final String TAG = "tdr";
	/* * Do not remove or rename the field mFd: it is used by native method close(); */ 
	private FileDescriptor mFd; 
	private FileInputStream mFileInputStream; 
	private FileOutputStream mFileOutputStream; 
	public SerialPort(File device, int baudrate) throws SecurityException, IOException {
		mFd = open(device.getAbsolutePath(), baudrate); 
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException(); 
			} 
		
		mFileInputStream = new FileInputStream(mFd); 
		mFileOutputStream = new FileOutputStream(mFd); 
		
		}
	
	// Getters and setters 
	public InputStream getInputStream() 
	{ 
		return mFileInputStream; 
	} 
	
	public OutputStream getOutputStream() 
	{ 
		return mFileOutputStream;
	} 
	
	public void sendTime(boolean timeFormat)
	{
		Calendar cTime = Calendar.getInstance();
		if(timeFormat)
		{
			writeTime(cTime.get(Calendar.HOUR_OF_DAY),cTime.get(Calendar.MINUTE),24);
		}
		else {
			writeTime(cTime.get(Calendar.HOUR_OF_DAY),cTime.get(Calendar.MINUTE),12);
		}
		
		//System.out.print("time = %d,%d,%d",cTime.get(Calendar.HOUR_OF_DAY),cTime.get(Calendar.MINUTE));
		Log.i("****************time", cTime.get(Calendar.HOUR_OF_DAY)+"    " +cTime.get(Calendar.MINUTE));
		
	}
	
	public void sendYear()
	{
		Calendar cTime = Calendar.getInstance(); 
		writeYear(cTime.get(Calendar.YEAR),cTime.get(Calendar.MONTH),cTime.get(Calendar.DAY_OF_MONTH),cTime.get(Calendar.DAY_OF_WEEK));
		
		Log.i("****************year", cTime.get(Calendar.YEAR)+"  "+cTime.get(Calendar.MONTH)+" " + cTime.get(Calendar.DAY_OF_MONTH)+"  "+cTime.get(Calendar.DAY_OF_WEEK));
		
	}
	
	public void sendUPPos()
	{
		writeTouchUp();
	}
	
	public void sendDownPos(int xPos,int yPos) throws IOException{
		
	/*	int [] dataArray = new int[4];
		dataArray[0]=0xfe;
		dataArray[1]=0x02;
		dataArray[2]=0xa3;
		dataArray[3]=cmd;*/
		
//		writeTouchDown(AVINConstant.sScreenWidth, AVINConstant.sScreenHeight, AVINConstant.DVR_SCREEN_WIDTH, AVINConstant.DVR_SCREEN_HEIGHT, xPos, yPos);
		
		//write(cmd);
		
		/*
		byte [] dataArray = new byte[5];
		
		dataArray[0]=(byte)0xfe;
		dataArray[1]=0x02;
		dataArray[2]=(byte)0xa3;
		dataArray[3]=(byte)cmd;
		
		int checkSum = 0;
		for (int i = 0; i < dataArray.length-1; i++) {
			checkSum += dataArray[i];
		}
		
		//checkSum &= 0xff;
		checkSum = ~checkSum + 1;
		dataArray[4] = (byte)(checkSum & 0xff);
		
		if (mFileOutputStream != null) {
			
			try {
				mFileOutputStream.write(dataArray);
				
			} catch (Exception e) {
				// TODO: handle exception
			}
	
			
			
		}*/
		
	}
	
	// JNI 
	private native static FileDescriptor open(String path, int baudrate);
	public native void close(); 
	public native void writeTouchDown(int screenWidth,int screenHeight,int dvrWidth,int dvrHeight,int xPos,int yPos);
	public native void writeTouchUp();
	public native void writeTime(int hour,int minute,int timeFormat);
	public native void writeYear(int year,int month,int day,int weekDay);
	static { 
		System.loadLibrary("ExDevPort"); 
		}
	
}
