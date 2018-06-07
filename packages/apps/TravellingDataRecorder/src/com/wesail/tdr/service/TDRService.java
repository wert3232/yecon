package com.wesail.tdr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import com.wesail.tdr.L;
import com.wesail.tdr.constant.ActionConstants;
import com.wesail.tdr.constant.ActionConstants.KeyNo;
import com.wesail.tdr.db.DBer;
import com.wesail.tdr.entity.AccidentDoubt;
import com.wesail.tdr.entity.AccidentDoubtDetil;
import com.wesail.tdr.entity.DravelSpeed;
import com.wesail.tdr.entity.DravelSpeedDetil;
import com.wesail.tdr.util.ByteUtils;
import com.wesail.tdr.util.DateUtils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.exdevport.SerialPort;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class TDRService extends Service{
	private int counut = 0;
	private DBer dBer;
	private static final String TAG = "tdr";
	
	private SharedPreferences.Editor mEditor ;
	private SharedPreferences mPref;
	
	private SerialPort serialPort;
	private OutputStream mOutputStream;  
    private InputStream mInputStream;
    private ExecutorService threadPool;
    private boolean isStop = false;
    
    private byte[] receiveData;
	private int receiveSize;
	private byte checksum;
	private byte[] timeBytes;
	private boolean isConnect = false;
	private String m_first_time_to_install = "";
	@Override
	public IBinder onBind(Intent intent) {
		IBinder result = new MyServiceBinder();
		return result;
	}
	@Override
	public void onCreate() {
		/*L.e("onCreate  Thread id: " + Thread.currentThread().getId());*/
		super.onCreate();
		mPref = this.getSharedPreferences("tdr", MODE_PRIVATE);
		mEditor = mPref.edit();
		
		mEditor.putInt("isConnect", 0).commit();
		/*dBer = new DBer(this.getApplicationContext());
		dBer.select("driver_speed");*/
		/*ContentValues contentValues = new ContentValues();
		
		contentValues.put(dBer.DRIVER_SPEED_DATE, "2001-10-10");
		long l = dBer.insert("driver_speed", contentValues);
		L.e("TDRService","dBer.insert" + l + "");*/
		
		threadPool = Executors.newFixedThreadPool(5);
		try {
			serialPort = new SerialPort(new File("/dev/ttyMT6"),115200);
		} catch (Exception e) {
			L.e(e.toString());
			return;
		}
		
		mOutputStream = serialPort.getOutputStream();  
        mInputStream = serialPort.getInputStream();
        
        ReadThread readThread = new ReadThread();   
        threadPool.execute(readThread);
        
        IntentFilter intent = new IntentFilter();
        intent.setPriority(100);
        intent.addAction(ActionConstants.SEND_CMD);
        intent.addAction("com.yecon.action.ACC_OFF");
        registerReceiver(mReceiver, intent);
        
        
        Intent cmdIntent = new Intent(ActionConstants.SEND_CMD);
        cmdIntent.putExtra("cmd", new byte[]{(byte)0x02});
		sendOrderedBroadcast(cmdIntent,null);
        
        
        /*L.w(DateUtils.getCurrentTime("yy MM dd HH mm ss"));
        L.w(DateUtils.getTime(DateUtils.firstDay(new Date()),"yy MM dd HH mm ss"));
        L.w(DateUtils.getTime(DateUtils.lastDay(new Date()),"yy MM dd HH mm ss"));*/
        
        
        
       /* byte[] order = new byte[]{(byte)0x08};
        byte[] startTime = getDateTimeBytes(DateUtils.firstDay(new Date()));
        byte[] endTime = getDateTimeBytes(DateUtils.lastDay(new Date()));
        order = ByteUtils.combineByte(order, startTime);
        order = ByteUtils.combineByte(order, endTime);
        order = ByteUtils.combineByte(order, new byte[]{(byte)0xff,(byte)0xff});
        sendCmd(order);*/
        
        
        /*Message message = handler.obtainMessage(1);       
        handler.sendMessageDelayed(message, 500); //发送message  
*/        
	}
	@SuppressLint("HandlerLeak")
	private final Handler handler = new Handler(){   
		/*Date date = new Date();*/
        public void handleMessage(Message msg){         //-------------------------线程main 
            switch (msg.what) {   
            case 1:
            	 byte[] order = new byte[]{(byte)0x010};
                 byte[] startTime = getDateTimeBytes(DateUtils.firstDay(new Date()));
                 byte[] endTime = getDateTimeBytes(DateUtils.lastDay(new Date()));
                 startTime =  new byte[]{(byte)0x15,(byte)0x10,(byte)0x18,(byte)0x15,(byte)0x31,(byte)0x38};
                 endTime   =  new byte[]{(byte)0x16,(byte)0x10,(byte)0x18,(byte)0x15,(byte)0x31,(byte)0x38};
                 order = ByteUtils.combineByte(order, startTime);
                 order = ByteUtils.combineByte(order, endTime);
                 order = ByteUtils.combineByte(order, new byte[]{(byte)0x00,(byte)0x2f});
                 sendCmd(order);
            	
                /*Message message = handler.obtainMessage(1);   
                handler.sendMessageDelayed(message, 2000);     //发送message , 这样消息就能循环发送  
*/            }   
            super.handleMessage(msg);   
        }   
    };
	private Handler connectStateHandler = new Handler();
	TimerTask task = new TimerTask(){    
		     public void run(){
		    	 if(mPref.getInt("isConnect", 0) == 0){
		    		 putTdrConnectState(ActionConstants.TDR_DISCONNECT);    
		    	 }
		     }    
		 };    
	Timer timer = new Timer();  	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		L.e("onDestroy Thread id: " + Thread.currentThread().getId());
		/*if(threadPool != null && !threadPool.isShutdown()){
			threadPool.shutdown();
		}*/
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	public class MyServiceBinder extends Binder{
        public TDRService getService(){
            return TDRService.this;
        }
    }
	
	private void sendCmd(byte []data){
		if(serialPort==null || serialPort.getOutputStream()==null){
			if(serialPort==null)
				L.w(TAG, "serialPort not opened");				
			else
				L.w(TAG, "serialPort can not write");
			return;
		}
		if (data==null || data.length<1)
			return;
		//L.i(TAG, "sendCmd:"+Integer.toHexString(keyNo.getValue() +plus));
		final byte[] temp = new byte[data.length+6];
		temp[0]=(byte) 0xAA;
		temp[1]=(byte) 0x75;
		temp[2]=data[0];
		temp[3]= (byte) ((data.length-1)%256);
		temp[4]= (byte) ((data.length-1)/256);
		temp[5]=0;
		for(int i = 1; i < data.length; i++)
			temp[5+i] = data[i];
		StringBuffer str = new StringBuffer();
		for(int j = 0; j < temp.length -1; j++) {
			temp[temp.length-1] ^= temp[j];
			str.append(String.format("%02X ", temp[j]));
		}
		str.append(String.format("%02X ", temp[temp.length-1]));
		L.e(TAG,"send "+str);
		
		try {
			serialPort.getOutputStream().write(temp);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private byte[]pkgBuffer=new byte[4];
	private void sendCmd(KeyNo keyNo, int plus){
		if(serialPort==null || serialPort.getOutputStream()==null){
			if(serialPort==null)
				L.w( "serialPort not opened");				
			else
				L.w( "serialPort can not write");
			return;
		}
		L.i( "sendCmd:"+Integer.toHexString(keyNo.getValue() +plus));
		pkgBuffer[0]=(byte) 0xA5;
		pkgBuffer[1]=(byte) ((byte)(keyNo.getValue()+plus)&0xff);
		pkgBuffer[2]=(byte)0x2A;
		
		L.e("pkgBuffer : " + ByteUtils.getHex(pkgBuffer));
		try {
			serialPort.getOutputStream().write(pkgBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sendCmd(KeyNo keyNo){
		sendCmd(keyNo, 0);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ActionConstants.SEND_CMD.equalsIgnoreCase(action)){
				abortBroadcast();
				byte[] cmd = intent.getByteArrayExtra("cmd");
				if(cmd != null){
					L.e("Broadcast cmd : " + cmd[0] + " " + (timeBytes == null));
					try {
						if(cmd[0] == 0x11){
							if(timeBytes == null){
								return;
							}
							cmd = ByteUtils.combineByte(cmd, timeBytes);
							cmd = ByteUtils.combineByte(cmd, new byte[]{0,(byte)0x14});
							sendCmd(cmd);
						}else if(cmd[0] == 0x08){
							if(timeBytes == null){
								return;
							}
							cmd = ByteUtils.combineByte(cmd, timeBytes);
							cmd = ByteUtils.combineByte(cmd, new byte[]{0,(byte)0x2f});
							sendCmd(cmd);
						}		
						else if(cmd[0] == 0x10){
							if(timeBytes == null){
								return;
							}
							cmd = ByteUtils.combineByte(cmd, timeBytes);
							cmd = ByteUtils.combineByte(cmd, new byte[]{0,(byte)0x80});
							sendCmd(cmd);
						}else if(cmd[0] == 0x13){
							if(timeBytes == null){
								return;
							}
							cmd = ByteUtils.combineByte(cmd, timeBytes);
							cmd = ByteUtils.combineByte(cmd, new byte[]{0,(byte)0x80});
							sendCmd(cmd);
						}else if(cmd[0] == 0x02){
							sendCmd(cmd);
							L.e("sendCmd 0x02:");
						    timer.schedule(task, 600); 
						}else if((cmd[0] & 0xff) == 0x87){
							L.e("sendCmd exit(0x87)");
							sendCmd(KeyNo.Exit);
						}
						else{
							sendCmd(cmd);
						}
					} catch (Exception e) {
						L.e(e.toString());
					}
				}
			}else if("com.yecon.action.ACC_OFF".equals(action)){
				try {	
					stopSelf();
				} catch (Exception e) {
					L.e(e.toString());
				}
			}
		}
	};
	
	public class ReadThread implements Runnable{
		@Override
		public void run() {
            while (!isStop) {
                int size;  
                try {  
                    if (mInputStream == null)  
                        return;  
                    byte[] buffer = new byte[4 * 1024];  
                    size = mInputStream.read(buffer);  
                    if (size > 0) {
//                    	L.d(TAG,"ReadThread size:" + size + "  buffer:" + ByteUtils.getHex2(buffer, " "));
						if(buffer[0]==0x55 && buffer[1] == 0x7a) {
							receiveData = new byte[(buffer[3]&0xff) * 256 + (int)(buffer[4] & 0xff) + 7];
							receiveSize = 0;
							checksum = 0;
							L.d(TAG,"NEW DATA " + receiveData.length);	
						}
						
						
						L.w(TAG,"foolr 1");
                    	StringBuffer receive = new StringBuffer();
						try {
							receive = new StringBuffer();
							for(int i = 0; i < size; i++) {
                        		checksum ^= buffer[i];
    							receiveData[i + receiveSize] = buffer[i];
                        		receive.append(String.format("%02X ", buffer[i]));
    						}
							receiveSize += size;
    						L.w(TAG,"receiveSize: " + receiveSize + "  checksum: "+ checksum);
                        	L.w(TAG, "receive: " + receive);
							
							
							L.i(TAG,"foolr 02" + "  " + ByteUtils.getHex2(receiveData, " "));
							
							L.i(TAG, "bl:" + (receiveData[0] == 0x55) + 
                    				" " + (receiveData[1] == 0x7a) +
                    				" " + ( (receiveSize == ((receiveData[3]&0xff) * 256 + (int)(receiveData[4] & 0xff) + 7))) +
                    				" " + (checksum == 0)
                			);
							
							if(receiveData[0] == 0x55 && receiveData[1] == 0x7a && (receiveSize == ((receiveData[3]&0xff) * 256 + (int)(receiveData[4] & 0xff) + 7)) && checksum == 0) {
								receive = new StringBuffer();
								switch(receiveData[2]) {
									case 0:
									break;
									case 1: {
										L.w(TAG,"recvice 0x01");
										String driver_s_license_number = byte2String(buffer,1,18);
										L.d(TAG,"机动车驾驶证号码:" + driver_s_license_number);
										putDriverInfromationMsg(driver_s_license_number);
									}
									break;
									case 2:
										L.w(TAG,"recvice 0x02");
										isConnect = true;
										mEditor.putInt("isConnect", 1).commit();
										timeBytes = new byte[]{BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
												buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11]};
										putTdrConnectState(ActionConstants.TDR_CONNECT);
									break;
									case 3: {
										L.w(TAG,"recvice 0x03");
										StringBuffer s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[6]));
										s.append(String.format("%02X-",receiveData[7]));
										s.append(String.format("%02X ",receiveData[8]));
										s.append(String.format("%02X:",receiveData[9]));
										s.append(String.format("%02X:",receiveData[10]));
										s.append(String.format("%02X",receiveData[11]));	
										L.d(TAG,"记录仪时间:"+s);
										
										s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[12]));
										s.append(String.format("%02X-",receiveData[13]));
										s.append(String.format("%02X ",receiveData[14]));
										s.append(String.format("%02X:",receiveData[15]));
										s.append(String.format("%02X:",receiveData[16]));
										s.append(String.format("%02X",receiveData[17]));
										
										String first_time_to_install = s.toString();
										L.d(TAG,"记录仪初次安装时间:" + s);
														
										float initial_mileage = (BCD2Dec(receiveData[18]) * 100000 + 
												BCD2Dec(receiveData[19]) * 1000 + 
												BCD2Dec(receiveData[20]) * 10 + 
												((float)BCD2Dec(receiveData[21])) / 10);
										L.d(TAG,"初始里程:" + initial_mileage);
										
										float accumulated_miles = (BCD2Dec(receiveData[22]) * 100000 + 
												BCD2Dec(receiveData[23]) * 1000 + 
												BCD2Dec(receiveData[24]) * 10 + 
												((float)BCD2Dec(receiveData[25])) / 10);
										
										L.d(TAG,"累计行驶里程:" + accumulated_miles);
										
										
										if(TextUtils.isEmpty(first_time_to_install) || TextUtils.isEmpty(first_time_to_install.trim())){
											putRecorderInformationMsg(null, null, m_first_time_to_install);
										}else{
											m_first_time_to_install = first_time_to_install;
											putRecorderInformationMsg(null, null, first_time_to_install);
										}
										putMileageMsg(initial_mileage, accumulated_miles);
									}
									break;
									case 4:
										L.w(TAG,"recvice 0x04");
									break;
									case 5:{
										L.w(TAG,"recvice 0x05");
										String vehicle_identification_number = byte2String(buffer,1,17);
										String vehicle_number = byte2String(buffer,18,12);
										String number_plate_classification = byte2String(buffer,30,12);
										L.d(TAG,"车辆识别代号:" + vehicle_identification_number);
										L.d(TAG,"机动车号牌号码:" + vehicle_number);
										L.d(TAG,"机动车号牌分类:" + number_plate_classification);
										putVehicleFileMsg(vehicle_identification_number, vehicle_number, number_plate_classification);
									}
									break;
									case 6:
										L.w(TAG,"recvice 0x06");
										L.d(TAG,""+byte2String(buffer,8,10));
										L.d(TAG,""+byte2String(buffer,18,10));
										L.d(TAG,""+byte2String(buffer,28,10));
										L.d(TAG,""+byte2String(buffer,38,10));
										L.d(TAG,""+byte2String(buffer,48,10));
										L.d(TAG,""+byte2String(buffer,58,10));
										L.d(TAG,""+byte2String(buffer,68,10));
										L.d(TAG,""+byte2String(buffer,78,10));
									break;
									case 7:
										L.w(TAG,"recvice 0x07");
										String authentication_code = byte2String(buffer,1,7);
										L.d(TAG,"生产厂CCC认证代码:" + authentication_code);
										
										String certified_product_model = byte2String(buffer,8,16);
										L.d(TAG,"认证产品型号:" + certified_product_model);
										
										if(!TextUtils.isEmpty(m_first_time_to_install)){
											putRecorderInformationMsg(authentication_code, certified_product_model, m_first_time_to_install);
										}else{
											putRecorderInformationMsg(authentication_code, certified_product_model, null);
										}
										
										L.d(TAG,String.format("年:%02X",buffer[29]));
										L.d(TAG,String.format("月:%02X",buffer[30]));
										L.d(TAG,String.format("日:%02X",buffer[31]));
										L.d(TAG,String.format("产品生产流水号:%02X%02X%02X%02X",buffer[32],buffer[33],buffer[34],buffer[35]));
									break;
									case 8:{
											L.w(TAG,"recvice 0x08");
											ArrayList<DravelSpeed> dravelSpeeds = new ArrayList<DravelSpeed>();
											for(int i = 0 ; i < receiveSize / 126 ; i++) {
												StringBuffer s = new StringBuffer();
												s.append(String.format("20%02X/",receiveData[6+126*i]));
												s.append(String.format("%02X/",receiveData[7+126*i]));
												s.append(String.format("%02X ",receiveData[8+126*i]));
												s.append(String.format("%02X:",receiveData[9+126*i]));
												s.append(String.format("%02X",receiveData[10+126*i]));
												L.d(TAG,s.toString());
												
												DravelSpeed dravelSpeed = new DravelSpeed();
												dravelSpeed.setTime(s.toString());
												ArrayList<DravelSpeedDetil> speedDetils = dravelSpeed.getList(); 							
												for(int j = 0; j < 60; j++) {
													if(receiveData[11+126*i+j*2] != 0||receiveData[12+126*i+j*2] != 0){
														L.d(TAG,s.toString() + String.format(":%02d %02X [speed: %02X]",j,receiveData[11+126*i+j*2],receiveData[12+126*i+j*2]));
														DravelSpeedDetil item = new DravelSpeedDetil();
														item.setTime(s.toString().substring(10) + ":" + String.format("%02d",j));
														int speed = receiveData[12+126*i+j*2]&0xff;
														item.setSpeed(speed);
														speedDetils.add(item);
													}
												}
												dravelSpeeds.add(dravelSpeed);
											}
											putDravelSpeedList(dravelSpeeds);
										}
										
										break;
									case 9:
										L.w(TAG,"recvice 0x09");
									break;
									case 0x10:{
											L.w(TAG,"recvice 0x10");
											ArrayList<AccidentDoubt> doubts = new ArrayList<AccidentDoubt>();
											for(int i = 0; i<receiveSize/234; i++) {
												StringBuffer s = new StringBuffer();
												s.append(String.format("20%02X/",receiveData[6+i*234]));
												s.append(String.format("%02X/",receiveData[7+i*234]));
												s.append(String.format("%02X ",receiveData[8+i*234]));
												s.append(String.format("%02X:",receiveData[9+i*234]));
												s.append(String.format("%02X:",receiveData[10+i*234]));
												s.append(String.format("%02X ",receiveData[11+i*234]));
												L.d(TAG,"行驶结束时间:"+s);
												L.d(TAG,"驾驶证号码:"+byte2String(receiveData,7+i*234,18));
												
												AccidentDoubt accidentDoubt = new AccidentDoubt();
												accidentDoubt.setTime(s.toString());
												accidentDoubt.setDriver_s_license_number(byte2String(receiveData,7+i*234,18));
												ArrayList<AccidentDoubtDetil> doubtDetils = accidentDoubt.getList(); 
												
												for(int j = 0; j<100; j++) {
												//停车前/秒 速度    制动 左转 右转 远光 近光 报警 开门
													L.d(TAG,String.format("%d %d %d %d %d %d %d %d %d",receiveData[12+i*234+j*2],
													(receiveData[13+i*234+j*2]>>7)&0x1,(receiveData[13+i*234+j*2]>>6)&0x1,
													(receiveData[13+i*234+j*2]>>5)&0x1,(receiveData[13+i*234+j*2]>>4)&0x1,
													(receiveData[13+i*234+j*2]>>3)&0x1,(receiveData[13+i*234+j*2]>>2)&0x1,
													(receiveData[13+i*234+j*2]>>1)&0x1,(receiveData[13+i*234+j*2])&0x1));
													
													AccidentDoubtDetil item = new AccidentDoubtDetil();

													item.setSpeed(receiveData[12+i*234+j*2] & 0xff);
													item.setBrake((receiveData[13+i*234+j*2]>>7)&0x1);
													item.setTurnLeft((receiveData[13+i*234+j*2]>>6)&0x1);
													item.setTurnRight((receiveData[13+i*234+j*2]>>5)&0x1);
													item.setFarLight((receiveData[13+i*234+j*2]>>4)&0x1);
													item.setDippedLight((receiveData[13+i*234+j*2]>>3)&0x1);
													item.setAlarm((receiveData[13+i*234+j*2]>>2)&0x1);
													item.setOpenTheDoor((receiveData[13+i*234+j*2]>>1)&0x1);
													item.setParkingBeforeSeconds((receiveData[13+i*234+j*2])&0x1);
													doubtDetils.add(item);
												}
												doubts.add(accidentDoubt);
											}
											putAccidentDoubtList(doubts);
										}
										break;
									case 0x11: {
										L.w(TAG,"recvice 0x11");
										List<String> list = new ArrayList<String>();
										int length = 50;
										for(int i = 0; i < receiveSize / length; i++) {
											JSONObject jsonObject = new JSONObject();
											jsonObject.put("driver_s_license_number", byte2String(receiveData,1+i*length,18));
											Log.d(TAG,"驾驶证号码:" + byte2String(receiveData,1+i*length,18));
											
											StringBuffer s = new StringBuffer();
											s.append(String.format("20%02X/",receiveData[24+i*length]));
											s.append(String.format("%02X/",receiveData[25+i*length]));
											s.append(String.format("%02X ",receiveData[26+i*length]));
											s.append(String.format("%02X:",receiveData[27+i*length]));
											s.append(String.format("%02X:",receiveData[28+i*length]));
											s.append(String.format("%02X ",receiveData[29+i*length]));
											jsonObject.put("start_time", s.toString());
											Log.d(TAG,"驾驶开始时间:" + s);
											s = new StringBuffer();
											s.append(String.format("20%02X-",receiveData[30+i*length]));
											s.append(String.format("%02X-",receiveData[31+i*length]));
											s.append(String.format("%02X ",receiveData[32+i*length]));
											s.append(String.format("%02X:",receiveData[33+i*length]));
											s.append(String.format("%02X:",receiveData[34+i*length]));
											s.append(String.format("%02X ",receiveData[35+i*length]));
											jsonObject.put("end_time", s.toString());
											Log.d(TAG,"驾驶结束时间:"+s);
											Log.d(TAG,"joson:" + jsonObject.toString());
											list.add(jsonObject.toString());
										}
										putOverTimeList(list);
									}
									break;
									case 0x12:
										L.w(TAG,"recvice 0x12");
									break;
									case 0x13:{
										L.w(TAG,"recvice 0x13");
										List<String> list = new ArrayList<String>();
										for(int i = 0; i<(receiveSize/7-1); i++) {
											JSONObject jsonObject = new JSONObject();
											StringBuffer s = new StringBuffer();
											s.append(String.format("20%02X/",receiveData[6+i*7]));
											s.append(String.format("%02X/",receiveData[7+i*7]));
											s.append(String.format("%02X ",receiveData[8+i*7]));
											s.append(String.format("%02X:",receiveData[9+i*7]));
											s.append(String.format("%02X:",receiveData[10+i*7]));
											s.append(String.format("%02X ",receiveData[11+i*7]));
											L.d(TAG,s.toString()+((receiveData[12+i*7]==1)?"通电":"断电"));
											
											jsonObject.put("time_type", receiveData[12+i*7]);
											jsonObject.put("time_of_occurrence", s.toString());
											list.add(jsonObject.toString());
										}
										putPowerList(list);
									}
									break;
									case 0x14:
										 L.w(TAG,"recvice 0x14");
									break;
									case 0x15:
										 L.w(TAG,"recvice 0x15");
									break;
									case (byte)0xb5:{
										 L.w(TAG,"recvice 0xb5");
										 List<String> list = new ArrayList<String>();
										int length = 12;
										for(int i = 0; i< receiveSize / length; i++) {
											JSONObject jsonObject = new JSONObject();
											StringBuffer s = new StringBuffer();
											L.d(TAG,String.format("驾驶员代码 %02X%02X%02X",receiveData[6+i*length],receiveData[7+i*length],receiveData[8+i*length]));
											s.append(String.format("20%02X/",receiveData[9+i*length]));
											s.append(String.format("%02X/",receiveData[10+i*length]));
											s.append(String.format("%02X ",receiveData[11+i*length]));
											s.append(String.format("%02X:",receiveData[12+i*length]));
											s.append(String.format("%02X:",receiveData[13+i*length]));
											s.append(String.format("%02X ",receiveData[14+i*length]));
											L.d(TAG,"超速时间:"+s);
											L.d(TAG,"超速持续时间:"+((receiveData[15+i*length]&0xff)*256+(int)(receiveData[16+i*length]&0xff)));
											L.d(TAG,"最高时速:"+(receiveData[17+i*length]&0xff));
											jsonObject.put("time", s);
											jsonObject.put("overtime", (receiveData[15+i*length]&0xff)*256+(int)(receiveData[16+i*length]&0xff));
											jsonObject.put("overspeed_average_speed", (receiveData[17+i*length]&0xff));
											list.add(jsonObject.toString());
										}
										putOverSpeedList(list);
									}
									break;
									default:
										L.d(TAG,"reserve");
									break;
								}
								 L.i(TAG,"foolr 98");
							}
						} catch (Exception e) {
							 L.e(TAG,"foolr 99");
							L.e(e.toString());
						}
                    }
                    Thread.sleep(10);
                    L.w(TAG,"foolr 100");
                } catch (Exception e) {  
                    e.printStackTrace();
                    L.e(TAG,e.toString());
                    return;  
                }  
            }
            L.i(TAG,"ReadThread end");
    }};
   
    private byte BCDSub(byte b, int sub) {
		int temp = b & 0xff;
		temp = temp / 16 * 10 + temp % 16 - sub;
		temp = temp / 10 * 16 + temp % 10;
		return (byte)temp;
	}
    
	private int BCD2Dec(byte b) {
		return (((b>>4)&0x0f) *10 + (b&0x0f));
	}
	
	private String byte2String(byte[] buffer, int index, int length) {
		index +=5;
		StringBuffer s = new StringBuffer();
		for(int i = 0;i<length; i++) {
			if(buffer[index+i]==0) {
				length = i;
				break;
			}
		}
		byte[] b = new byte[length];
		for(int i = 0;i<length; i++) {
			b[i] = buffer[index+i];
		}
		try {
			return new String(b,"GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private Map<String, Integer> getDateTimeMap(Date date){
		Map<String, Integer> map = new HashMap<String, Integer>();
		String time = DateUtils.getTime(date,"yy MM dd HH mm ss");
		String result[] = time.split(" ");
		map.put("year", Integer.parseInt(result[0]));
		map.put("month", Integer.parseInt(result[1]));
		map.put("day", Integer.parseInt(result[2]));
		map.put("hour", Integer.parseInt(result[3]));
		map.put("minute", Integer.parseInt(result[4]));
		map.put("second", Integer.parseInt(result[5]));
		return map;
	}
	
	private byte[] getDateTimeBytes(Date date){
		String time = DateUtils.getTime(date,"yy MM dd HH mm ss");
		String result[] = time.split(" ");
		int year = Integer.parseInt(result[0]);
		int month = Integer.parseInt(result[1]);
		int day = Integer.parseInt(result[2]);
		int hour = Integer.parseInt(result[3]);
		int minute = Integer.parseInt(result[4]);
		int second = Integer.parseInt(result[5]);
		
		L.w("10:" + " " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5]);
		L.w("16:" + " " + ByteUtils.getHex2(new byte[]{(byte)year,(byte)month,(byte)day,(byte)hour,(byte)minute,(byte)second}, " "));
		
		
		return new byte[]{(byte)year,(byte)month,(byte)day,(byte)hour,(byte)minute,(byte)second};
	}
	
	public boolean getConnectState(){
		return this.isConnect;
	}
	
	public void  putTdrConnectState(String state){
		/*L.e("putTdrConnectState  Thread id: " + Thread.currentThread().getId());
		L.e("putTdrConnectState:" + " isConnect: " + getConnectState()  + " state:" + state + " ::" + mPref.getInt("isConnect", -1));*/
		if(ActionConstants.TDR_CONNECT.equalsIgnoreCase(state)){	
			Intent intent = new Intent(ActionConstants.TDR_CONNECT);
			sendOrderedBroadcast(intent, null);
		}else if(ActionConstants.TDR_DISCONNECT.equalsIgnoreCase(state)){
			Intent intent = new Intent(ActionConstants.TDR_DISCONNECT);
			sendOrderedBroadcast(intent, null);
		}
	}
	
	//返回车辆档案
	public void putVehicleFileMsg(String vehicle_identification_number,
								String vehicle_number,
								String number_plate_classification){
		Intent intent = new Intent(ActionConstants.INFO_VEHICLE_FILE);
		intent.putExtra("vehicle_identification_number", vehicle_identification_number);
		intent.putExtra("vehicle_number", vehicle_number);
		intent.putExtra("number_plate_classification", number_plate_classification);
		sendOrderedBroadcast(intent, null);
	}
	
	public void putOverSpeedList(List<String> list){
		try {
			String[] items = new String[list.size()];
			Intent intent = new Intent(ActionConstants.LIST_DATA_OVER_SPEED);
			list.toArray(items);
			intent.putExtra("items", items);
			sendOrderedBroadcast(intent, null);
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	public void putOverTimeList(List<String> list){
		try {
			String[] items = new String[list.size()];
			Intent intent = new Intent(ActionConstants.LIST_DATA_OVER_TIME);
			list.toArray(items);
			intent.putExtra("items", items);
			/*for(String str : items){
				L.e("put item :" + str );
			}*/
			sendOrderedBroadcast(intent, null);
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	public void putPowerList(List<String> list){
		try {
			String[] items = new String[list.size()];
			Intent intent = new Intent(ActionConstants.LIST_DATA_POWER);
			list.toArray(items);
			intent.putExtra("items", items);
				sendOrderedBroadcast(intent, null);
			} catch (Exception e) {
				L.e(e.toString());
			}
	}
	
	public void putAccidentDoubtList(ArrayList<AccidentDoubt> list){
		try {
			Intent intent = new Intent(ActionConstants.LIST_DATA_ACCIDENT_DOUBT);
			intent.putParcelableArrayListExtra("list", list);
			sendOrderedBroadcast(intent, null);
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	public void putDravelSpeedList(ArrayList<DravelSpeed> list){
		try {
			Intent intent = new Intent(ActionConstants.LIST_DATA_DRAVEL_SPEED);
			intent.putParcelableArrayListExtra("list", list);
			sendOrderedBroadcast(intent, null);
		} catch (Exception e) {
			L.e(e.toString());
		}
	}
	
	//返回记录仪信息
	public void putRecorderInformationMsg(String authentication_code,
										String certified_product_model,
										String first_time_to_install){
		Intent intent = new Intent(ActionConstants.INFO_RECORDER);
		intent.putExtra("authentication_code", authentication_code);
		intent.putExtra("certified_product_model", certified_product_model);
		intent.putExtra("first_time_to_install", first_time_to_install);
		sendOrderedBroadcast(intent, null);
	}
	
	//返回驾驶人信息
	public void putDriverInfromationMsg(String driver_s_license_number){
		Intent intent = new Intent(ActionConstants.INFO_DRIVER);
		intent.putExtra("driver_s_license_number", driver_s_license_number);
		sendOrderedBroadcast(intent, null);
	}
	
	//返回行驶里程
	public void putMileageMsg(float initial_mileage,float accumulated_miles){
		Intent intent = new Intent(ActionConstants.INFO_MILEAGE);
		intent.putExtra("initial_mileage", initial_mileage);
		intent.putExtra("accumulated_miles", accumulated_miles);
		sendOrderedBroadcast(intent, null);
	}
}



/*private class ReadThread2 extends Thread {  
	  
    @Override  
    public void run() {  
        super.run();  
        while (!isStop && !isInterrupted()) {  
            int size;  
            try {  
                if (mInputStream == null)  
                    return;  
                byte[] buffer = new byte[512];  
                size = mInputStream.read(buffer);  
                if (size > 0) {  
                    //if(Log.isDyeLevel()){  
                        //Log.d(TAG, "length is:"+size+",data is:"+new String(buffer, 0, size));  
                    //}  
//                    if (null != onDataReceiveListener) {  
//                        onDataReceiveListener.onDataReceive(buffer, size);  
//                    }  
					if(buffer[0]==0x55 && buffer[1] == 0x7a) {
						receiveData = new byte[(buffer[3]&0xff) * 256 + (int)(buffer[4] & 0xff) + 7];
						receiveSize = 0;
						checksum = 0;
						Log.d(TAG,"NEW DATA "+receiveData.length);
					}
                	StringBuffer receive = new StringBuffer();
                	for(int i = 0; i < size; i++) {
						checksum ^= buffer[i];
						receiveData[i+receiveSize]=buffer[i];
                		receive.append(String.format("%02X ", buffer[i]));
					}
					receiveSize += size;
					Log.d(TAG,"receiveSize"+receiveSize + " "+ checksum);
                	Log.d(TAG, "receive:"+receive);
					receive = new StringBuffer();
					//for(int i = 0; i < receiveSize; i++) {
            		//	receive.append(String.format("%02X ", receiveData[i]));
					//}
            		//Log.d(TAG, "receiveData:"+receive);
					if(receiveData[0]==0x55 && receiveData[1] == 0x7a && (receiveSize == ((receiveData[3]&0xff) * 256 + (int)(receiveData[4] & 0xff) + 7)) && checksum == 0) {
						receive = new StringBuffer();
						for(int i = 0; i < receiveSize; i++) {
                			receive.append(String.format("%02X ", receiveData[i]));
						}
                		Log.d(TAG, "receiveData:"+receive);
						switch(receiveData[2]) {
							case 0:
							break;
							case 1: {
								StringBuffer s = new StringBuffer();
								for(int i = 0 ; i < buffer[4]; i++) {
									if(buffer[i+6]==0)
										break;
									s.append((char)buffer[i+6]);
								}
								Log.d(TAG,"机动车驾驶证号码:"+byte2String(buffer,1,18));
							}
							break;
							case 2:
								//sendCmd(new byte[]{0x13,buffer[6],buffer[7],BCDSub(buffer[8],2),buffer[9],buffer[10],buffer[11],
								//buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0xff});
								sendCmd(new byte[]{0x8,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
								buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0x14});
//								sendCmd(new byte[]{0x10,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
//								buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0x14});
								sendCmd(new byte[]{0x11,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
								buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0xff});
								isConnect = true;
								timeBytes = new byte[]{BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
										buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11]};
							break;
							case 3: {
								StringBuffer s = new StringBuffer();
								s.append(String.format("20%02X-",receiveData[6]));
								s.append(String.format("%02X-",receiveData[7]));
								s.append(String.format("%02X ",receiveData[8]));
								s.append(String.format("%02X:",receiveData[9]));
								s.append(String.format("%02X:",receiveData[10]));
								s.append(String.format("%02X",receiveData[11]));
								Log.d(TAG,"记录仪时间:"+s);
								s = new StringBuffer();
								s.append(String.format("20%02X-",receiveData[12]));
								s.append(String.format("%02X-",receiveData[13]));
								s.append(String.format("%02X ",receiveData[14]));
								s.append(String.format("%02X:",receiveData[15]));
								s.append(String.format("%02X:",receiveData[16]));
								s.append(String.format("%02X",receiveData[17]));
								Log.d(TAG,"记录仪初次安装时间:"+s);
								Log.d(TAG,"初始里程:"+(BCD2Dec(receiveData[18])*100000+BCD2Dec(receiveData[19])*1000+BCD2Dec(receiveData[20])*10+((float)BCD2Dec(receiveData[21]))/10));
								Log.d(TAG,"累计行驶里程:"+(BCD2Dec(receiveData[22])*100000+BCD2Dec(receiveData[23])*1000+BCD2Dec(receiveData[24])*10+((float)BCD2Dec(receiveData[25]))/10));
							}
							break;
							case 4:
							break;
							case 5:{
								StringBuffer s = new StringBuffer();
								for(int i = 0 ; i < 17; i++) {
									s.append((char)buffer[i+6]);
								}
								Log.d(TAG,"车辆识别代号:"+s);
								s = new StringBuffer();
								for(int i = 18 ; i < 29; i++) {
									if(buffer[i+6]==0)
										break;
									s.append((char)buffer[i+6]);
								}
								Log.d(TAG,"机动车号牌号码:"+s);
								s = new StringBuffer();
								for(int i = 30 ; i < 41; i++) {
									if(buffer[i+6]==0)
										break;
									s.append((char)buffer[i+6]);
								}
								Log.d(TAG,"机动车号牌分类:"+s);
								Log.d(TAG,"车辆识别代号:"+byte2String(buffer,1,17));
								Log.d(TAG,"机动车号牌号码:"+byte2String(buffer,18,12));
								Log.d(TAG,"机动车号牌分类:"+byte2String(buffer,30,12));
							}
							break;
							case 6:
								Log.d(TAG,""+byte2String(buffer,8,10));
								Log.d(TAG,""+byte2String(buffer,18,10));
								Log.d(TAG,""+byte2String(buffer,28,10));
								Log.d(TAG,""+byte2String(buffer,38,10));
								Log.d(TAG,""+byte2String(buffer,48,10));
								Log.d(TAG,""+byte2String(buffer,58,10));
								Log.d(TAG,""+byte2String(buffer,68,10));
								Log.d(TAG,""+byte2String(buffer,78,10));
							break;
							case 7:
								Log.d(TAG,"生产厂CCC认证代码:"+byte2String(buffer,1,7));
								Log.d(TAG,"认证产品型号:"+byte2String(buffer,8,16));
								Log.d(TAG,String.format("年:%02X",buffer[29]));
								Log.d(TAG,String.format("月:%02X",buffer[30]));
								Log.d(TAG,String.format("日:%02X",buffer[31]));
								Log.d(TAG,String.format("产品生产流水号:%02X%02X%02X%02X",buffer[32],buffer[33],buffer[34],buffer[35]));
							break;
							case 8:
								for(int i = 0 ; i < receiveSize / 126 ; i++) {
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[6+126*i]));
									s.append(String.format("%02X-",receiveData[7+126*i]));
									s.append(String.format("%02X ",receiveData[8+126*i]));
									s.append(String.format("%02X:",receiveData[9+126*i]));
									s.append(String.format("%02X",receiveData[10+126*i]));
									//Log.d(TAG,s.toString());
									for(int j = 0; j < 60; j++) {
										if(receiveData[11+126*i+j*2]!=0||receiveData[12+126*i+j*2]!=0)
											Log.d(TAG,s.toString()+String.format(":%02d %02X %02X",j,receiveData[11+126*i+j*2],receiveData[12+126*i+j*2]));
									}
								}
							
							break;
							case 9:
							break;
							case 0x10:
								for(int i = 0; i<receiveSize/234; i++) {
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[6+i*234]));
									s.append(String.format("%02X-",receiveData[7+i*234]));
									s.append(String.format("%02X ",receiveData[8+i*234]));
									s.append(String.format("%02X:",receiveData[9+i*234]));
									s.append(String.format("%02X:",receiveData[10+i*234]));
									s.append(String.format("%02X ",receiveData[11+i*234]));
									Log.d(TAG,"行驶结束时间:"+s);
									Log.d(TAG,"驾驶证号码:"+byte2String(receiveData,7+i*234,18));
									for(int j = 0; j<100; j++) {
									//停车前/秒 速度    制动 左转 右转 远光 近光 报警 开门
										Log.d(TAG,String.format("%d %d %d %d %d %d %d %d %d",receiveData[12+i*234+j*2],
										(receiveData[13+i*234+j*2]>>7)&0x1,(receiveData[13+i*234+j*2]>>6)&0x1,
										(receiveData[13+i*234+j*2]>>5)&0x1,(receiveData[13+i*234+j*2]>>4)&0x1,
										(receiveData[13+i*234+j*2]>>3)&0x1,(receiveData[13+i*234+j*2]>>2)&0x1,
										(receiveData[13+i*234+j*2]>>1)&0x1,(receiveData[13+i*234+j*2])&0x1));
									}
									//Log.d(TAG,String.format("经度:%d",receiveData[i*234+230]<<24|receiveData[i*234+231]<<16|receiveData[i*234+232]<<8|receiveData[i*234+233]));
									//Log.d(TAG,String.format("纬度:%d",receiveData[i*234+234]<<24|receiveData[i*234+235]<<16|receiveData[i*234+236]<<8|receiveData[i*234+237]));
									//Log.d(TAG,String.format("海拔:%d",receiveData[i*234+238]<<8|receiveData[i*234+239]));
								}
							break;
							case 0x11: {
								List<String> list = new ArrayList<String>();
								int length = 50;
								for(int i = 0; i < receiveSize / length; i++) {
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("driver_s_license_number", byte2String(receiveData,1+i*length,18));
									Log.d(TAG,"驾驶证号码:" + byte2String(receiveData,1+i*length,18));
									
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[24+i*length]));
									s.append(String.format("%02X-",receiveData[25+i*length]));
									s.append(String.format("%02X ",receiveData[26+i*length]));
									s.append(String.format("%02X:",receiveData[27+i*length]));
									s.append(String.format("%02X:",receiveData[28+i*length]));
									s.append(String.format("%02X ",receiveData[29+i*length]));
									jsonObject.put("start_time", s.toString());
									Log.d(TAG,"驾驶开始时间:" + s);
									s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[30+i*length]));
									s.append(String.format("%02X-",receiveData[31+i*length]));
									s.append(String.format("%02X ",receiveData[32+i*length]));
									s.append(String.format("%02X:",receiveData[33+i*length]));
									s.append(String.format("%02X:",receiveData[34+i*length]));
									s.append(String.format("%02X ",receiveData[35+i*length]));
									jsonObject.put("end_time", s.toString());
									Log.d(TAG,"驾驶结束时间:"+s);
									Log.d(TAG,"joson:" + jsonObject.toString());
									list.add(jsonObject.toString());
								}
								putOverTimeList(list);
							}
							break;
							case 0x12:
							break;
							case 0x13:
								for(int i = 0; i<(receiveSize/7-1); i++) {
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X/",receiveData[6+i*7]));
									s.append(String.format("%02X/",receiveData[7+i*7]));
									s.append(String.format("%02X ",receiveData[8+i*7]));
									s.append(String.format("%02X:",receiveData[9+i*7]));
									s.append(String.format("%02X:",receiveData[10+i*7]));
									s.append(String.format("%02X ",receiveData[11+i*7]));
									Log.d(TAG,s.toString()+((receiveData[12+i*7]==1)?"通电":"断电"));
								}
							break;
							case 0x14:
							break;
							case 0x15:
							break;
							case (byte)0xb5:{
								int length = 12;
								for(int i = 0; i<receiveSize/length; i++) {
									Log.d(TAG,String.format("驾驶员代码 %02X%02X%02X",receiveData[6+i*length],receiveData[7+i*length],receiveData[8+i*length]));
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[9+i*length]));
									s.append(String.format("%02X-",receiveData[10+i*length]));
									s.append(String.format("%02X ",receiveData[11+i*length]));
									s.append(String.format("%02X:",receiveData[12+i*length]));
									s.append(String.format("%02X:",receiveData[13+i*length]));
									s.append(String.format("%02X ",receiveData[14+i*length]));
									Log.d(TAG,"超速时间:"+s);
									Log.d(TAG,"超速持续时间:"+((receiveData[15+i*length]&0xff)*256+(int)(receiveData[16+i*length]&0xff)));
									Log.d(TAG,"最高时速:"+(receiveData[17+i*length]&0xff));
								}
							}
							break;
							default:
								Log.d(TAG,"reserve");
							break;
						}
					}
                }  
                Thread.sleep(10);  
            } catch (Exception e) {  
                e.printStackTrace();  
                return;  
            }  
        }  
    }  
};*/
