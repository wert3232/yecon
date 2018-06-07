package android.mcu;

import android.mcu.IMcuListener;

/**
 * {@hide}
 */
interface IMcuManager {
	void RPC_McuCallbackFinished(in IMcuListener listener);
	
	/**
	 * Mcu updates notification interface
	 */
	void RPC_RequestMcuInfoChangedListener(in IMcuListener listener, String packageName);
	
	void RPC_RemoveMcuInfoChangedListener(in IMcuListener listener, String packageName);
	
	/**
	 * System initialization and configuration interface
	 */
	void RPC_ShakeHand();
	
	void RPC_GetInitTable();
	
	/**
	 * Device status query and reporting interface
	 */
	void RPC_GetStatus();
	
	/**
	 * Source switching interface
	 **/
	void RPC_SetSource(int frontSource, int rearSource);
	
	/**
	 * Audio processing interface
	 */
	void RPC_GetVolume();
	
	void RPC_SetVolume(in byte[] data);
	
	int[] RPC_GetVolumeData();
	
	void RPC_SetVolumeMute(boolean isMute);
	
	void RPC_GetEQ();
	
	void RPC_SetEQ(in byte[] data);
	
	int[] RPC_GetEQData();
	
	/**
	 * Key operations and control interface
	 **/
	void RPC_KeyCommand(int keyValue, in byte[] data);
	
	void RPC_TouchCommand(int touchStatus, in byte[] XCoord, in byte[] YCoord);
	
	/**
	 * Source-related interface 
	 */
	void RPC_GetAllRadioInfo();
	
	void RPC_GetRadioInfo(int infoType);
	
	/**
	 * Parameter settings interface
	 **/
	void RPC_GetSysParams(int index);
	
	void RPC_SetSysParams(in byte[] data, int len);
	
	/**
	 * SWC interface
	 */
	void RPC_SWCGetTable();
	
	void RPC_SWCCommand(int cmd);
	
	/**
	 * Additional interface
	 */
	void RPC_GetMcuVer();
	
	void RPC_UpdateMCU(int update);
	
	void RPC_SendMCUData(in byte[] data, int len);
	
	void RPC_SendCANType(in byte[] data, int len);
	
	void RPC_UsbUpgrade(int value);
	
	void RPC_SetFactoryInfo(in byte[] data, int len);
	
	void RPC_SendCANInfo(in byte[] data, int len);
	
	void RPC_SendCANDataPacket(in byte[] data, int len);
	
	void RPC_SendFMRadioType(in byte[] data, int len);
	
	void RPC_GetMcuID();

	void RPC_SendSetSleepType(int sleepType);

	void RPC_SendSetQicaideng(in byte[] data, int len);
	
	void RPC_SendExtendCmd(int cmd, in byte[] data, int len);
}