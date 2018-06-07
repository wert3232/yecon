package com.wesail.tdr;

//import static android.mcu.McuExternalConstant.*;
//import static com.yecon.avin.AVINConstant.*;
//import static android.constant.YeconConstants.*;

import java.io.File;
import java.io.IOException;
//import com.autochips.inputsource.AVIN;
//import com.autochips.inputsource.InputSource;
//import com.yecon.avin.R;
//import com.yecon.avin.unitl.L;
//import com.yecon.common.SourceManager;
//import com.yecon.settings.YeconSettings;

import java.io.InputStream;
import java.io.OutputStream;

import com.wesail.tdr.util.ByteUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.exdevport.SerialPort;
import android.graphics.ImageFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
//import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class TDRActivity extends Activity  implements OnClickListener {
	protected static final String TAG = "tdr";
	private SerialPort serialPort;
//	private static AVINSurfaceView mAvinsurfaceview4Front = null;
	private AudioManager mAudioManager;
//	public AVIN  avinV, avinA;
	private int videoport = 5;
//	private YeconSettings.RGBTYPE rgbType = YeconSettings.RGBTYPE.AVIN_5;
	private int audioport = 4;
	private boolean avinV_signal_on = false;
	private static boolean rdsTest = false;
	private static int sScreenWidth = 0;
	private static int sScreenHeight = 0;
//	private int mCurrentStatus = InputSource.STATUS_NONE;
	
	private View  toolsSetting, toolsOrientation, toolsChannel, toolbar, bottombar;
	private TextView av_signal;
	private Object sourceTocken;
//	private RearManager rearManager;  
    private OutputStream mOutputStream;  
    private InputStream mInputStream;  
    private ReadThread mReadThread;  
    //private OnDataReceiveListener onDataReceiveListener = null;  
    private boolean isStop = false;  
	
	private byte[] receiveData;
	private int receiveSize;
	private byte checksum;
	
	enum KeyNo{
		Poweron(0x8A),Mute(0x8C),Num0(0x90),Num9(0x99),
	    ChUp(0x80),ChDown(0x81),VolDownLeft(0x83),VolUpRight(0x82),
	    Menu(0x85),Exit(0x87),Info(0x89),Epg(0x9B),
	    Subtitle(0xC4),FastScan(0x9A),OK(0x9F);
		private final int value; 
        public int getValue() { 
            return value; 
        }
        KeyNo(int value) { 
            this.value = value; 
        } 
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		sourceTocken = SourceManager.registerSourceListener(this, SourceManager.SRC_NO.dtv);
//		 SourceManager.setAudioFocusNotify(sourceTocken, new SourceManager.AudioFocusNotify() {
//				
//				@Override
//				public void onAudioFocusChange(int arg0) {
//					L.i( "onAudioFocusChange:" +arg0 );
//					switch (arg0) {
//	                case AudioManager.AUDIOFOCUS_LOSS:
//	                	finish();
//	                	break;
//	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//	                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//	                	pauseAudio();
//	                    break;
//	                case AudioManager.AUDIOFOCUS_GAIN:
//	                	resumeAudio();
//	                    break;
//					}
//					
//				}
//			});
//		sendBroadcast(new Intent(AVINConstant.ACTION_AVIN_REQUEST_NOTIFY));
//		SystemClock.sleep(50);
		
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		
//		this.setContentView(R.layout.activity_tv);
//		toolbar = findViewById(R.id.toolbar);
//		toolsSetting = (View) findViewById(R.id.toolbar_setting);
//		toolsSetting.setVisibility(View.GONE);
//		toolsOrientation = (View) findViewById(R.id.toolbar_orientation);
//		toolsOrientation.setVisibility(View.GONE);
//		toolsChannel = (View) findViewById(R.id.toolbar_channel);
//		toolsChannel.setVisibility(View.GONE);
//		bottombar = findViewById(R.id.bottombar);
//		av_signal = (TextView) findViewById(R.id.av_signal); 
//		mAvinsurfaceview4Front = (AVINSurfaceView) findViewById(R.id.avin_surfaceview);
		
		initData();
//		avinV = new AVIN();
//		avinA = new AVIN();
//		mAvinsurfaceview4Front.mAvinV = avinV;
//		
//		rearManager = new RearManager(getApplicationContext());
//		rearManager.init(avinV, avinA);
//		
//		YeconSettings.initVideoRgb(rgbType);
		
		startPlay(videoport, audioport);
		
		//try 
		{ 
			/*for(int i = 0; i < 64; i++) {
				L.d( ""+i);
				try{
					serialPort = new SerialPort(new File("/dev/ttyMT"+i),115200); 
					sendCmd(new byte[]{0});
				} catch (Exception e) {
					continue;
				}
				if(serialPort!=null)
					break;
			}*/
			try {
				serialPort = new SerialPort(new File("/dev/ttyMT6"),115200);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			//sendCmd(new byte[]{2});
            mOutputStream = serialPort.getOutputStream();  
            mInputStream = serialPort.getInputStream();  
              
            mReadThread = new ReadThread();  
            isStop = false;  
            mReadThread.start();  
			/*new Thread(new Runnable(){
				
				@Override
				public void run() {
					for(byte i = 0;i <= 0x15; i++) {
						sendCmd(new byte[]{i});
						try{
							Thread.sleep(3000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();*/
			sendCmd(new byte[]{(byte)0x02});
		}
		//catch (SecurityException e)
		{ 
		//	e.printStackTrace(); 
		}
//       catch (IOException e) 
//       { 
//    	   e.printStackTrace(); 
//       }
		
	}
	private void sendCmd(byte []data){
		if(serialPort==null || serialPort.getOutputStream()==null){
			if(serialPort==null)
				L.w( "serialPort not opened");				
			else
				L.w( "serialPort can not write");
			return;
		}
		if (data==null || data.length<1)
			return;
		//L.i( "sendCmd:"+Integer.toHexString(keyNo.getValue() +plus));
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
		//temp[temp.length-1] = (byte) - temp[temp.length-1];
		str.append(String.format("%02X ", temp[temp.length-1]));
		L.d("send "+str);
		/*new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						//serialPort.getOutputStream().write(new byte[]{0x15,(byte) 0xc5,(byte) 0x7f,(byte) 0xfe,(byte) 0xfe,(byte) 0x8a});
						serialPort.getOutputStream().write(temp);
						serialPort.writeTouchUp();
						Thread.sleep(3000);//serialPort.getOutputStream().write(temp);
						L.d( "SEND END");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}}).start();*/
		try {
			serialPort.getOutputStream().write(temp);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void initData() {
	}
	
	public void startPlay(int videoid, int audioid) {
	}
	
	protected void showSurfaceView(int visible) {
	}
	
	@Override
    protected void onStop() {
        super.onStop();
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		sendCmd(KeyNo.Exit);
		serialPort.close();
//		unregisterReceiver(mQuickBootListener);
//		rearManager.release();
//		DestroyAvin();
		super.onDestroy();
//		SourceManager.unregisterSourceListener(sourceTocken);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		SourceManager.acquireSource(sourceTocken);
//		resumeAudio();
//		uiHandler.removeMessages(AVINConstant.SHOW_NO_SIGNAL_STATE);
//		uiHandler.sendEmptyMessageDelayed(AVINConstant.CHECK_SIGNAL,  500);
//		YeconSettings.initVideoRgb(rgbType);
		super.onResume();
	}

	private View getContentShow(){
		if(toolsChannel.getVisibility()==View.VISIBLE)
			return toolsChannel;
		if(toolsOrientation.getVisibility()==View.VISIBLE)
			return toolsOrientation;
		if(toolsSetting.getVisibility()==View.VISIBLE)
			return toolsSetting;
		return null;
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
	private class ReadThread extends Thread {  
		  
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
                    	L.i(TAG,"ReadThread size:" + size + "  buffer:" + ByteUtils.getHex2(buffer, " "));
                        //if(Log.isDyeLevel()){  
                            //L.d( "length is:"+size+",data is:"+new String(buffer, 0, size));  
                        //}  
//                        if (null != onDataReceiveListener) {  
//                            onDataReceiveListener.onDataReceive(buffer, size);  
//                        }  
						if(buffer[0]==0x55 && buffer[1] == 0x7a) {
							receiveData = new byte[(buffer[3]&0xff) * 256 + (int)(buffer[4] & 0xff) + 7];
							receiveSize = 0;
							checksum = 0;
							L.d("NEW DATA "+receiveData.length);
						}
                    	StringBuffer receive = new StringBuffer();
                    	for(int i = 0; i < size; i++) {
							checksum ^= buffer[i];
							receiveData[i+receiveSize]=buffer[i];
                    		receive.append(String.format("%02X ", buffer[i]));
						}
						receiveSize += size;
						L.d("receiveSize"+receiveSize + " "+ checksum);
                    	L.d( "receive:"+receive);
						receive = new StringBuffer();
						//for(int i = 0; i < receiveSize; i++) {
                		//	receive.append(String.format("%02X ", receiveData[i]));
						//}
                		//L.d( "receiveData:"+receive);
						if(receiveData[0]==0x55 && receiveData[1] == 0x7a && (receiveSize == ((receiveData[3]&0xff) * 256 + (int)(receiveData[4] & 0xff) + 7)) && checksum == 0) {
							receive = new StringBuffer();
							/*for(int i = 0; i < receiveSize; i++) {
                    			receive.append(String.format("%02X ", receiveData[i]));
							}
                    		L.d( "receiveData:"+receive);*/
							switch(receiveData[2]) {
								case 0:
								break;
								case 1: {
									/*StringBuffer s = new StringBuffer();
									for(int i = 0 ; i < buffer[4]; i++) {
										if(buffer[i+6]==0)
											break;
										s.append((char)buffer[i+6]);
									}*/
									L.d("机动车驾驶证号码:"+byte2String(buffer,1,18));
								}
								break;
								case 2:
									//sendCmd(new byte[]{0x13,buffer[6],buffer[7],BCDSub(buffer[8],2),buffer[9],buffer[10],buffer[11],
									//buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0xff});
									//sendCmd(new byte[]{0x8,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
									//buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0x14});
//									sendCmd(new byte[]{0x10,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
//									buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0x14});
									//sendCmd(new byte[]{0x11,BCDSub(buffer[6],1),buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],
									//buffer[6],buffer[7],buffer[8],buffer[9],buffer[10],buffer[11],0,(byte)0xff});
								break;
								case 3: {
									StringBuffer s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[6]));
									s.append(String.format("%02X-",receiveData[7]));
									s.append(String.format("%02X ",receiveData[8]));
									s.append(String.format("%02X:",receiveData[9]));
									s.append(String.format("%02X:",receiveData[10]));
									s.append(String.format("%02X",receiveData[11]));
									L.d("记录仪时间:"+s);
									s = new StringBuffer();
									s.append(String.format("20%02X-",receiveData[12]));
									s.append(String.format("%02X-",receiveData[13]));
									s.append(String.format("%02X ",receiveData[14]));
									s.append(String.format("%02X:",receiveData[15]));
									s.append(String.format("%02X:",receiveData[16]));
									s.append(String.format("%02X",receiveData[17]));
									L.d("记录仪初次安装时间:"+s);
									L.d("初始里程:"+(BCD2Dec(receiveData[18])*100000+BCD2Dec(receiveData[19])*1000+BCD2Dec(receiveData[20])*10+((float)BCD2Dec(receiveData[21]))/10));
									L.d("累计行驶里程:"+(BCD2Dec(receiveData[22])*100000+BCD2Dec(receiveData[23])*1000+BCD2Dec(receiveData[24])*10+((float)BCD2Dec(receiveData[25]))/10));
								}
								break;
								case 4:
								break;
								case 5:{
									/*StringBuffer s = new StringBuffer();
									for(int i = 0 ; i < 17; i++) {
										s.append((char)buffer[i+6]);
									}
									L.d("车辆识别代号:"+s);
									s = new StringBuffer();
									for(int i = 18 ; i < 29; i++) {
										if(buffer[i+6]==0)
											break;
										s.append((char)buffer[i+6]);
									}
									L.d("机动车号牌号码:"+s);
									s = new StringBuffer();
									for(int i = 30 ; i < 41; i++) {
										if(buffer[i+6]==0)
											break;
										s.append((char)buffer[i+6]);
									}
									L.d("机动车号牌分类:"+s);*/
									L.d("车辆识别代号:"+byte2String(buffer,1,17));
									L.d("机动车号牌号码:"+byte2String(buffer,18,12));
									L.d("机动车号牌分类:"+byte2String(buffer,30,12));
								}
								break;
								case 6:
									L.d(""+byte2String(buffer,8,10));
									L.d(""+byte2String(buffer,18,10));
									L.d(""+byte2String(buffer,28,10));
									L.d(""+byte2String(buffer,38,10));
									L.d(""+byte2String(buffer,48,10));
									L.d(""+byte2String(buffer,58,10));
									L.d(""+byte2String(buffer,68,10));
									L.d(""+byte2String(buffer,78,10));
								break;
								case 7:
									L.d("生产厂CCC认证代码:"+byte2String(buffer,1,7));
									L.d("认证产品型号:"+byte2String(buffer,8,16));
									L.d(String.format("年:%02X",buffer[29]));
									L.d(String.format("月:%02X",buffer[30]));
									L.d(String.format("日:%02X",buffer[31]));
									L.d(String.format("产品生产流水号:%02X%02X%02X%02X",buffer[32],buffer[33],buffer[34],buffer[35]));
								break;
								case 8:
									for(int i = 0 ; i < receiveSize / 126 ; i++) {
										StringBuffer s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[6+126*i]));
										s.append(String.format("%02X-",receiveData[7+126*i]));
										s.append(String.format("%02X ",receiveData[8+126*i]));
										s.append(String.format("%02X:",receiveData[9+126*i]));
										s.append(String.format("%02X",receiveData[10+126*i]));
										//L.d(s.toString());
										for(int j = 0; j < 60; j++) {
											if(receiveData[11+126*i+j*2]!=0||receiveData[12+126*i+j*2]!=0)
												L.d(s.toString()+String.format(":%02d %02X %02X",j,receiveData[11+126*i+j*2],receiveData[12+126*i+j*2]));
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
										L.d("行驶结束时间:"+s);
										L.d("驾驶证号码:"+byte2String(receiveData,7+i*234,18));
										for(int j = 0; j<100; j++) {
										//停车前/秒 速度    制动 左转 右转 远光 近光 报警 开门
											L.d(String.format("%d %d %d %d %d %d %d %d %d",receiveData[12+i*234+j*2],
											(receiveData[13+i*234+j*2]>>7)&0x1,(receiveData[13+i*234+j*2]>>6)&0x1,
											(receiveData[13+i*234+j*2]>>5)&0x1,(receiveData[13+i*234+j*2]>>4)&0x1,
											(receiveData[13+i*234+j*2]>>3)&0x1,(receiveData[13+i*234+j*2]>>2)&0x1,
											(receiveData[13+i*234+j*2]>>1)&0x1,(receiveData[13+i*234+j*2])&0x1));
										}
										//L.d(String.format("经度:%d",receiveData[i*234+230]<<24|receiveData[i*234+231]<<16|receiveData[i*234+232]<<8|receiveData[i*234+233]));
										//L.d(String.format("纬度:%d",receiveData[i*234+234]<<24|receiveData[i*234+235]<<16|receiveData[i*234+236]<<8|receiveData[i*234+237]));
										//L.d(String.format("海拔:%d",receiveData[i*234+238]<<8|receiveData[i*234+239]));
									}
								break;
								case 0x11: {
									int length = 50;
									for(int i = 0; i<receiveSize/length; i++) {
										L.d("驾驶证号码:"+byte2String(receiveData,1+i*length,18));
										StringBuffer s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[24+i*length]));
										s.append(String.format("%02X-",receiveData[25+i*length]));
										s.append(String.format("%02X ",receiveData[26+i*length]));
										s.append(String.format("%02X:",receiveData[27+i*length]));
										s.append(String.format("%02X:",receiveData[28+i*length]));
										s.append(String.format("%02X ",receiveData[29+i*length]));
										L.d("驾驶开始时间:"+s);
										s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[30+i*length]));
										s.append(String.format("%02X-",receiveData[31+i*length]));
										s.append(String.format("%02X ",receiveData[32+i*length]));
										s.append(String.format("%02X:",receiveData[33+i*length]));
										s.append(String.format("%02X:",receiveData[34+i*length]));
										s.append(String.format("%02X ",receiveData[35+i*length]));
										L.d("驾驶结束时间:"+s);
									}
								}
								break;
								case 0x12:
								break;
								case 0x13:
									for(int i = 0; i<(receiveSize/7-1); i++) {
										StringBuffer s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[6+i*7]));
										s.append(String.format("%02X-",receiveData[7+i*7]));
										s.append(String.format("%02X ",receiveData[8+i*7]));
										s.append(String.format("%02X:",receiveData[9+i*7]));
										s.append(String.format("%02X:",receiveData[10+i*7]));
										s.append(String.format("%02X ",receiveData[11+i*7]));
										L.d(s.toString()+((receiveData[12+i*7]==1)?"通电":"断电"));
									}
								break;
								case 0x14:
								break;
								case 0x15:
								break;
								case (byte)0xb5:{
									int length = 12;
									for(int i = 0; i<receiveSize/length; i++) {
										L.d(String.format("驾驶员代码 %02X%02X%02X",receiveData[6+i*length],receiveData[7+i*length],receiveData[8+i*length]));
										StringBuffer s = new StringBuffer();
										s.append(String.format("20%02X-",receiveData[9+i*length]));
										s.append(String.format("%02X-",receiveData[10+i*length]));
										s.append(String.format("%02X ",receiveData[11+i*length]));
										s.append(String.format("%02X:",receiveData[12+i*length]));
										s.append(String.format("%02X:",receiveData[13+i*length]));
										s.append(String.format("%02X ",receiveData[14+i*length]));
										L.d("超速时间:"+s);
										L.d("超速持续时间:"+((receiveData[15+i*length]&0xff)*256+(int)(receiveData[16+i*length]&0xff)));
										L.d("最高时速:"+(receiveData[17+i*length]&0xff));
									}
								}
								break;
								default:
									L.d("reserve");
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
    }  
	
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
		//	s.append(String.format("%02X ",buffer[index+i]));
		}
		//L.d("byte2String "+s);
		try {
			return new String(b,"GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		View view = this.getContentShow();
		
//		switch (arg0.getId()) {
//		case R.id.avin_surfaceview:
//			showToolBar(true, true);
//			return;
//		}
//		uiHandler.removeMessages(AVINConstant.HIDE_TOP_BAR);
//		uiHandler.sendEmptyMessageDelayed(AVINConstant.HIDE_TOP_BAR, AVINConstant.HIDE_TOP_BAT_TIME);
//	
	}

}
