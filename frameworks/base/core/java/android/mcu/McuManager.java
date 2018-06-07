
package android.mcu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import java.util.HashMap;
import android.util.Log;
import android.os.SystemProperties;
public class McuManager {
    public static final String FRONT_SOURCE = "persist.sys.front_source";
    public static final String REAR_SOURCE = "persist.sys.rear_source";
    private final Context mContext;
    private final IMcuManager mService;

    private HashMap<McuListener, ListenerTransport> mListeners = new HashMap<McuListener, ListenerTransport>();

    private class ListenerTransport extends IMcuListener.Stub {
        private static final int TYPE_MCU_INFO_CHANGED = 1;

        private McuListener mListener;
        private final Handler mListenerHandler;

        public ListenerTransport(McuListener listener) {
            mListener = listener;

            mListenerHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    _handleMessage(msg);
                }

            };
        }

        @Override
        public void onMcuInfoChanged(McuBaseInfo mcuBaseInfo, int infoType) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = TYPE_MCU_INFO_CHANGED;
            msg.obj = mcuBaseInfo;
            msg.arg1 = infoType;
            mListenerHandler.sendMessage(msg);
        }

        private void _handleMessage(Message msg) {
            switch (msg.what) {
                case TYPE_MCU_INFO_CHANGED: {
                    McuBaseInfo mcuBaseInfo = (McuBaseInfo) msg.obj;
                    int infoType = msg.arg1;
                    if (mcuBaseInfo != null) {
                        mListener.onMcuInfoChanged(mcuBaseInfo, infoType);
                    }
                    break;
                }
            }

            try {
                mService.RPC_McuCallbackFinished(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public McuManager(Context context, IMcuManager service) {
        mContext = context;
        mService = service;
    }

    private ListenerTransport wrapListener(McuListener listener) {
        if (listener == null) {
            return null;
        }

        synchronized (mListeners) {
            ListenerTransport transport = mListeners.get(listener);
            if (transport == null) {
                transport = new ListenerTransport(listener);
            }

            mListeners.put(listener, transport);

            return transport;
        }
    }

    /**
     * MCU信息改变监听接口
     * 
     * @param listener
     * @throws RemoteException
     */
    public void RPC_RequestMcuInfoChangedListener(McuListener listener) throws RemoteException {
        if (listener == null) {
            return;
        }

        String packageName = mContext.getPackageName();

        ListenerTransport transport = wrapListener(listener);

        mService.RPC_RequestMcuInfoChangedListener(transport, packageName);
    }

    /**
     * 移去MCU信息改变监听接口
     * 
     * @param listener
     * @throws RemoteException
     */
    public void RPC_RemoveMcuInfoChangedListener(McuListener listener) throws RemoteException {
        if (listener == null) {
            return;
        }

        ListenerTransport transport;
        synchronized (mListeners) {
            transport = mListeners.remove(listener);
        }
        if (transport == null) {
            return;
        }

        String packageName = mContext.getPackageName();

        mService.RPC_RemoveMcuInfoChangedListener(transport, packageName);
    }

    /**
     * ARM请求握手命令
     * 
     * @throws RemoteException
     */
    public void RPC_ShakeHand() throws RemoteException {
        mService.RPC_ShakeHand();
    }

    /**
     * ARM初始化请求命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetInitTable() throws RemoteException {
        mService.RPC_GetInitTable();
    }

    /**
     * ARM查询MCU设备状态命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetStatus() throws RemoteException {
        mService.RPC_GetStatus();
    }

    /**
     * ARM请求源切换命令
     *
     * @param frontSource
     * @param rearSource
     * @throws RemoteException
     */
    public void RPC_SetSource(int frontSource, int rearSource) throws RemoteException {
        mService.RPC_SetSource(frontSource, rearSource);
        Log.e("WindowManager","frontSource:" +frontSource + "rearSource:" + rearSource );
        SystemProperties.set(FRONT_SOURCE,"" + frontSource);
        SystemProperties.set(REAR_SOURCE,"" + rearSource);
    }
   /**
    * 获取前台音源
    * */
    public int getFontSource(){
        return SystemProperties.getInt(FRONT_SOURCE,0);
    }

    /**
     * 获取后台音源
     * */
    public int getRearSource(){
        return SystemProperties.getInt(REAR_SOURCE,0);
    }
    /**
     * ARM获取音量信息命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetVolume() throws RemoteException {
        mService.RPC_GetVolume();
    }

	public void Init_RPC_Volume(){
		try{
			int volume = Integer.parseInt(SystemProperties.get("persist.sys.current_volume", Context.DEFUALT_VOLUME + ""));
			int lr = SystemProperties.getInt("persist.sys.volume_balance_lr",0);
			int fr = SystemProperties.getInt("persist.sys.volume_balance_fr",0);
			int[] volumeData = RPC_GetVolumeData();
			byte[] data = new byte[8];
			for(int i = 0; i < data.length;i++){
				data[i] = (byte)volumeData[i];
			}
			data[0] = (byte)volume;		
			data[2] = (byte)lr;
			data[3] = (byte)fr;
			
			if(volume == 0){
				data[4] = 1; //静音
			}else{
				data[4] = 0;//有声
			}
			RPC_SetVolume(data);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(),e.toString());
		}
    }
	/**
     * ARM设置时间信息命令
     * 
     * @param data
     * @throws RemoteException
     */
	 public synchronized void  RPC_SetTime(int year,int month,int day,int hour,int minute,int second){
		try{
			byte[] data = new byte[9];
			data[0] = 0x02;
			data[1] = (byte)(((int)year / 1000) * 16 + ((int)year % 1000 / 100));
			data[2] = (byte)(((int)year  % 100 / 10) * 16 + ((int)(year % 100) % 10));
			data[3] = (byte)(((int)month / 10) * 16 + month % 10);
			data[4] = (byte)(((int)day / 10) * 16 + day % 10);
			data[5] = (byte)(((int)hour / 10) * 16 + hour % 10);
			data[6] = (byte)(((int)minute / 10) * 16 + minute % 10);
			data[7] = (byte)(((int)second / 10) * 16 + second % 10);
			data[8] = 0x00;
			String str = "";
			for(byte b : data){
				str = str + b + " ";
			}
			//Log.w("MCU_SERVER","data:  " + str);
			RPC_SetSysParams(data,9);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(),e.toString());
		}
    }
    /**
     * ARM设置音量信息命令
     * 
     * @param data
     * @throws RemoteException
     */
    public void RPC_SetVolume(byte[] data) throws RemoteException {
        mService.RPC_SetVolume(data);
    }
	public synchronized void  RPC_SetVolume(int streamType,int volume){
		Log.e("MCU_SERVER","package:  RPC_SetVolume " + mContext.getPackageName() +"   "+ volume );
		try{
			int lr = SystemProperties.getInt("persist.sys.volume_balance_lr",0);
			int fr = SystemProperties.getInt("persist.sys.volume_balance_fr",0);
			int[] volumeData = RPC_GetVolumeData();
			byte[] data = new byte[8];
			for(int i = 0; i < data.length;i++){
				data[i] = (byte)volumeData[i];
			}
			if(streamType == 3){
				data[0] = (byte)volume;
				
				data[2] = (byte)lr;
				data[3] = (byte)fr;
				
				if(volume == 0){
					data[4] = 1; //静音
					//mService.RPC_SetVolumeMute(true); 
				}else{
					data[4] = 0;//有声
					//mService.RPC_SetVolumeMute(false); 
				}
				String str = "";
				for(byte b : data){
					str = str + b + " ";
				}
				Log.w("MCU_SERVER","data:  " + str);
				RPC_SetVolume(data);
			}
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(),e.toString());
		}
    }
	
	public void RPC_SetVolumeBalance(int lr,int fr){
		int currentVolume = Integer.parseInt(SystemProperties.get("persist.sys.current_volume", "-1"));
		Log.e("Android",mContext.getApplicationInfo().packageName  + " sysCurrentVolume:" + currentVolume);
		try{
			int[] volumeData = RPC_GetVolumeData();
			SystemProperties.set("persist.sys.volume_balance_lr", lr + "");
			SystemProperties.set("persist.sys.volume_balance_fr", fr + "");
			byte[] data = new byte[8];
			for(int i = 0; i < data.length;i++){
				data[i] = (byte)volumeData[i];
			}
			data[0] = (byte)currentVolume;
			data[2] = (byte)lr;
			data[3] = (byte)fr;
			
			if(currentVolume == 0){
				data[4] = 1; //静音
			}else{
				data[4] = 0;//有声
			}
			
			String str = "";
			for(byte b : data){
				str = str + b + " ";
			}
			Log.w("MCU_SERVER","data:  " + str);
			RPC_SetVolume(data);
		} catch (RemoteException e) {
			Log.e(this.getClass().getName(),e.toString());
		}
    }
    /**
     * 执行ARM获取音量信息命令后返回的音量信息数据
     * 
     * @return
     * @throws RemoteException
     */
    public int[] RPC_GetVolumeData() throws RemoteException {
        return mService.RPC_GetVolumeData();
    }

    /**
     * ARM设置是否静音
     * 
     * @param isMute
     * @throws RemoteException
     */
    public void RPC_SetVolumeMute(boolean isMute) throws RemoteException {
        mService.RPC_SetVolumeMute(isMute); 
    }

    /**
     * ARM获取音效EQ命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetEQ() throws RemoteException {
        mService.RPC_GetEQ();
    }

    /**
     * ARM设置音效EQ命令
     * 
     * @param data
     * @throws RemoteException
     */
    public void RPC_SetEQ(byte[] data) throws RemoteException {
        mService.RPC_SetEQ(data);
    }

    /**
     * 执行ARM获取音效EQ命令后，获取音效EQ数据
     * 
     * @return
     * @throws RemoteException
     */
    public int[] RPC_GetEQData() throws RemoteException {
        return mService.RPC_GetEQData();
    }

    /**
     * ARM向MCU发送按键或控制命令
     * 
     * @param keyValue
     * @param data
     * @throws RemoteException
     */
    public void RPC_KeyCommand(int keyValue, byte[] data) throws RemoteException {
        mService.RPC_KeyCommand(keyValue, data);
    }

    /**
     * ARM向MCU发送触摸屏坐标命令
     * 
     * @param touchStatus
     * @param XCoord
     * @param YCoord
     * @throws RemoteException
     */
    public void RPC_TouchCommand(int touchStatus, byte[] XCoord, byte[] YCoord)
            throws RemoteException {
        mService.RPC_TouchCommand(touchStatus, XCoord, YCoord);
    }

    /**
     * ARM获取所有的Radio信息命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetAllRadioInfo() throws RemoteException {
        mService.RPC_GetAllRadioInfo();
    }

    /**
     * 根据信息类型，ARM获取Radio信息命令
     * 
     * @param infoType
     * @throws RemoteException
     */
    public void RPC_GetRadioInfo(int infoType) throws RemoteException {
        mService.RPC_GetRadioInfo(infoType);
    }

    /**
     * ARM获取系统参数信息命令
     * 
     * @param index
     * @throws RemoteException
     */
    public void RPC_GetSysParams(int index) throws RemoteException {
        mService.RPC_GetSysParams(index);
    }

    /**
     * ARM设置系统参数命令
     * 
     * @param index
     * @param data
     * @param len
     * @throws RemoteException
     */
    public void RPC_SetSysParams(byte[] data, int len) throws RemoteException {
        mService.RPC_SetSysParams(data, len);
    }

    /**
     * ARM获取SWC表命令
     * 
     * @throws RemoteException
     */
    public void RPC_SWCGetTable() throws RemoteException {
        mService.RPC_SWCGetTable();
    }

    /**
     * ARM方向盘学习请求命令
     * 
     * @param cmd
     * @throws RemoteException
     */
    public void RPC_SWCCommand(int cmd) throws RemoteException {
        mService.RPC_SWCCommand(cmd);
    }

    /**
     * ARM获取MCU版本号命令
     * 
     * @throws RemoteException
     */
    public void RPC_GetMcuVer() throws RemoteException {
        mService.RPC_GetMcuVer();
    }

    /**
     * ARM通知MCU升级开始或结束命令
     * 
     * @param update
     * @throws RemoteException
     */
    public void RPC_UpdateMCU(int update) throws RemoteException {
        mService.RPC_UpdateMCU(update);
    }

    /**
     * ARM向MCU发送升级数据命令
     * 
     * @param data
     * @param len
     * @throws RemoteException
     */
    public void RPC_SendMCUData(byte[] data, int len) throws RemoteException {
        mService.RPC_SendMCUData(data, len);
    }

    /**
     * ARM向MCU发送升级数据命令
     * 
     * @param param
     * @param len
     * @throws RemoteException
     */
    public void RPC_SendCANType(byte[] data, int len) throws RemoteException {
        mService.RPC_SendCANType(data, len);
    }

    /**
     * ARM向MCU发送USB升级命令
     * 
     * @param value
     * @throws RemoteException
     */
    public void RPC_UsbUpgrade(int value) throws RemoteException {
        mService.RPC_UsbUpgrade(value);
    }

    /**
     * ARM向MCU发送工厂设置信息
     * 
     * @param data
     * @param len
     * @throws RemoteException
     */
    public void RPC_SetFactoryInfo(byte[] data, int len) throws RemoteException {
        mService.RPC_SetFactoryInfo(data, len);
    }

    /**
     * ARM通过MCU向CAN传递数据
     * 
     * @param param
     * @param len
     * @throws RemoteException
     */
    public void RPC_SendCANInfo(byte[] data, int len) throws RemoteException {
        mService.RPC_SendCANInfo(data, len);
    }

    /**
     * ARM发送CAN数据包信息给 MCU
     * 
     * @param param
     * @param len
     * @throws RemoteException
     */
    public void RPC_SendCANDataPacket(byte[] data, int len) throws RemoteException {
        mService.RPC_SendCANDataPacket(data, len);
    }

    /**
     * ARM发送收音机类型给 MCU
     * 
     * @param param
     * @param len
     * @throws RemoteException
     */
    public void RPC_SendFMRadioType(byte[] data, int len) throws RemoteException {
        mService.RPC_SendFMRadioType(data, len);
    }

    /**
     * ARM获取 MCU系列号
     * 
     * @throws RemoteException
     */
    public void RPC_GetMcuID() throws RemoteException {
        mService.RPC_GetMcuID();
    }

    /**
     * ARM设置MCU待机等待时间
     * 
     * @throws RemoteException
     */
    public void RPC_SendSetSleepType(int sleepType) throws RemoteException {
        mService.RPC_SendSetSleepType(sleepType);
    }

    /**
     * ARM设置七彩灯
     * 
     * @throws RemoteException
     */
    public void RPC_SendSetQicaideng(byte[] data, int len) throws RemoteException {
        mService.RPC_SendSetQicaideng(data, len);
    }
    
    /**
     * Send raw data by extend cmd
     * @param cmd 0xC7 , 0xC8
     * @param data 
     * @param len
     */
    public void RPC_SendExtendCmd(int cmd, byte[] data, int len) throws RemoteException {
    	mService.RPC_SendExtendCmd(cmd, data, len);
    }
    
}
