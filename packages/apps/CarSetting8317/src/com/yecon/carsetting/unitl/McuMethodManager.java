package com.yecon.carsetting.unitl;

import static android.mcu.McuExternalConstant.MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX;
import static android.mcu.McuExternalConstant.*;

import java.util.ArrayList;

import android.content.Context;
import android.mcu.McuManager;
import android.os.RemoteException;
import android.os.SystemProperties;

public class McuMethodManager {

	private McuManager mMcuManager;
	public static McuMethodManager mInstance;

	public McuMethodManager(Context context) {
		if (mMcuManager == null) {
			mMcuManager = (McuManager) context.getSystemService(Context.MCU_SERVICE);
		}
	}

	public static McuMethodManager getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new McuMethodManager(context);
		}
		return mInstance;
	}

	static public class SystemParam {
		public int brightness;
		public int contrast;
		public int hue;
		public int saturation;
		public int backlight;
		public int backcarMirror;
	}

	public void setMcuSystemParam(SystemParam arg0) {

		byte[] videoParma = new byte[7];
		videoParma[0] = (byte) MCU_SYSTEM_PARAM_VIDEOSTATE_INDEX;
		videoParma[1] = (byte) arg0.brightness;
		videoParma[2] = (byte) arg0.contrast;
		videoParma[3] = (byte) arg0.hue;
		videoParma[4] = (byte) arg0.saturation;
		videoParma[5] = (byte) arg0.backlight;
		videoParma[6] = (byte) arg0.backcarMirror;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < videoParma.length; i++) {
			String str = String.format("%d ", videoParma[i] & 0xff);
			sb.append(str);
		}
		L.e("setMcuSystemParam - videoParma: " + sb.toString());

		try {
			if (mMcuManager != null) {
				mMcuManager.RPC_SetSysParams(videoParma, 7);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setMcuSystemParam_sleep(int preSleepTime, int prePowerOffTime) {
        byte[] parma = new byte[9];
        parma[0] = (byte) MCU_SYSTEM_PARAM_SLEEP_INDEX;
        parma[1] = (byte) ((preSleepTime >> 24) & 0xFF);
        parma[2] = (byte) ((preSleepTime >> 16) & 0xFF);
        parma[3] = (byte) ((preSleepTime >> 8) & 0xFF);
        parma[4] = (byte) (preSleepTime & 0xFF);
        parma[5] = (byte) ((prePowerOffTime >> 24) & 0xFF);
        parma[6] = (byte) ((prePowerOffTime >> 16) & 0xFF);
        parma[7] = (byte) ((prePowerOffTime >> 8) & 0xFF);
        parma[8] = (byte) (prePowerOffTime & 0xFF);

        if (mMcuManager != null) {
            try {
                mMcuManager.RPC_SetSysParams(parma, 9);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void setMcuParam_radiotype(int value) {
		byte[] parma = new byte[7];
		parma[0] = (byte) value;
		parma[1] = (byte) XmlParse.DX_LOC[0];
		parma[2] = (byte) XmlParse.DX_LOC[1];
		parma[3] = (byte) XmlParse.DX_LOC[2];
		parma[4] = (byte) XmlParse.DX_LOC[3];
		parma[5] = (byte) XmlParse.DX_LOC[4];
		parma[6] = (byte) XmlParse.DX_LOC[5];
		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SendFMRadioType(parma, 7);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMcuParam_cantype(int value) {
		byte[] parma = new byte[4];
		parma[0] = (byte) value;
		parma[1] = (byte) 0;
		parma[2] = (byte) 0;
		parma[3] = (byte) 0;

		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SendCANType(parma, 4);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setCantype() {
		int index_can_type = 0;
//		ArrayList<String> list_can_type = XmlParse.getStringArray(XmlParse.support_car_type);
//		for (int i = 0; i < list_can_type.size(); i++) {
//			if (XmlParse.support_car_type.equalsIgnoreCase(list_can_type.get(i))) {
//				index_can_type = i;
//				break;
//			}
//		}
		setMcuParam_radiotype(index_can_type);
	}

	public void setMcuParam_RadioCountry(byte radio_index) {
		byte[] radio_byte = { 0x05, radio_index, 0x00, 0x00, 0x00, 0x00, 0x00 };
		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SetSysParams(radio_byte, 7);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendBlackoutKeyCMD(int value) {
		byte[] param = new byte[4];
		param[0] = (byte) value;
		param[1] = (byte) 0x00;
		param[2] = (byte) 0x00;
		param[3] = (byte) 0x00;

		if (mMcuManager != null) {
			try {

				mMcuManager.RPC_KeyCommand(T_BLACKOUT, param);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void sendKeyPowerOffCMD(int flag,int time) {
		byte[] param = new byte[4];
		param[0] = (byte) flag;
		param[1] = (byte) time;
		param[2] = (byte) 0x00;
		param[3] = (byte) 0x00;

		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_KeyCommand(T_QB_POWER_OFF, param);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMcuParam_Headlight_detection(boolean value) {
		byte[] Parma = new byte[3];
		Parma[0] = 3;
		if (value)
			Parma[1] |= 0x40;
		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SetSysParams(Parma, 3);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMcuParam_backcar() {
		byte[] Parma = new byte[3];

		boolean brake_enable = SystemProperties.getBoolean(Tag.PERSYS_BRAKE_ENABLE, false);
		boolean backcar_enable = SystemProperties.getBoolean(Tag.PERSYS_BACKCAR_ENABLE, true);
		boolean backcar_mute = SystemProperties.getBoolean(Tag.PERSYS_BACKCAR_MUTE, false);
		boolean backcar_guidelines = SystemProperties.getBoolean(Tag.PERSYS_BACKCAR_TRACE, false);
		boolean backcar_radar = SystemProperties.getBoolean(Tag.PERSYS_BACKCAR_RADAR, false);
		boolean antenna_ctrl_enable = SystemProperties.getBoolean(Tag.PERSYS_ANTENNA_CTRL, false);
		boolean power_amplifier_ctrl_enable = SystemProperties.getBoolean(
				Tag.PERSYS_POWER_AMPLIFIER_CTRL, false);
		int videoport_front_camera = SystemProperties.getInt(Tag.PERSYS_VIDEO_PORT_FRONT_CAMERA,
				XmlParse.videoport_front_camera);

		if (brake_enable)
			Parma[0] |= 0x01;
		if (backcar_enable)
			Parma[0] |= 0x02;
		if (backcar_guidelines)
			Parma[0] |= 0x04;
		if (backcar_radar)
			Parma[0] |= 0x08;
		if (backcar_mute)
			Parma[0] |= 0x10;
		if (antenna_ctrl_enable)
			Parma[0] |= 0x20;
		if (power_amplifier_ctrl_enable)
			Parma[0] |= 0x40;
		if (videoport_front_camera == 1)
			Parma[0] |= 0x80;

		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SetFactoryInfo(Parma, 3);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMcuParam_sleepTime() {
		if (mMcuManager != null) {
			try {
				int value = SystemProperties.getInt(Tag.PERSYS_SLEEP_TIME, XmlParse.sleep_time);
				mMcuManager.RPC_SendSetSleepType(value);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setMcuParam_qicaideng() {
		byte[] data = new byte[4];
		int len = 4;
		data[0] = (byte) XmlParse.light_mode;
		data[1] = (byte) XmlParse.light_r;
		data[2] = (byte) XmlParse.light_g;
		data[3] = (byte) XmlParse.light_b;

		if (mMcuManager != null) {
			try {
				mMcuManager.RPC_SendSetQicaideng(data, len);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void setRadioCountry() {
		int index_radio_area = 0;
		ArrayList<String> list_radio_area = XmlParse.getStringArray(XmlParse.support_radio_area);
		String default_radio_area = SystemProperties.get(Tag.PERSYS_RADIO_AREA, XmlParse.default_radio_area);
		for (int i = 0; i < list_radio_area.size(); i++) {
			if (default_radio_area.equalsIgnoreCase(list_radio_area.get(i))) {
				index_radio_area = i;
				break;
			}
		}
		setMcuParam_RadioCountry((byte) (index_radio_area));
	}

	public void setRadioType() {
		int index_radio_type = 0;
		ArrayList<String> list_radio_type = XmlParse.getStringArray(XmlParse.support_radio_type);
		for (int i = 0; i < list_radio_type.size(); i++) {
			if (XmlParse.default_radio_type.equalsIgnoreCase(list_radio_type.get(i))) {
				index_radio_type = i;
				break;
			}
		}
		setMcuParam_radiotype(index_radio_type + 1);
	}

	private void resetRadio() {
		/*
		 * try { mMcuManager.RPC_KeyCommand(T_RADIO_RESET, null); } catch
		 * (RemoteException e) { e.printStackTrace(); }
		 */
	}

	public void sendSysRestartKeyCMD() {
		try {
			// mMcuManager.RPC_SetVolumeMute(true);
			byte[] param = new byte[4];
			param[0] = (byte) 0x00;
			param[1] = (byte) 0x00;
			param[2] = (byte) 0x00;
			param[3] = (byte) 0x00;

			mMcuManager.RPC_KeyCommand(T_SYS_RESTART, param);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void send_RearMute(){
		try {
			// mMcuManager.RPC_SetVolumeMute(true);
			byte[] param = new byte[4];
			param[0] = (byte) 0x00;
			param[1] = (byte) 0x00;
			param[2] = (byte) 0x00;
			param[3] = (byte) 0x00;

			mMcuManager.RPC_KeyCommand(K_REAR_MUTE, param);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
