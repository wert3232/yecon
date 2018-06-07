package com.yecon.carsetting;

import static com.yecon.carsetting.unitl.ArraySetting.*;
import android.app.Activity;
import android.backlight.BacklightControl;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.View;
import android.widget.SeekBar;

import com.autochips.settings.AtcSettings;
import com.yecon.carsetting.unitl.ArraySetting;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onProgressChanged;
import com.yecon.carsetting.view.HeaderLayout.onTwoButtonListener;
import com.yecon.metazone.YeconMetazone;

public class VideosetActivity extends Activity implements onOneButtonListener,
		onOneCheckBoxListener, onTwoButtonListener, onProgressChanged {

	private Context mContext;
	HeaderLayout mLayout_Button[] = new HeaderLayout[3];
	HeaderLayout mLayout_TwoButton[] = new HeaderLayout[2];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[1];
	HeaderLayout mLayout_SeekBar[] = new HeaderLayout[4];
	int ID_Button[] = { R.id.set_screen_colorful, R.id.set_screen_soft, R.id.set_screen_reset };
	int ID_TwoButton[] = { R.id.set_backcar_mirror, R.id.set_backlight };
	int ID_CheckBox[] = { R.id.set_parking };
	int ID_SeekBar[] = { R.id.set_screen_bright, R.id.set_screen_contast, R.id.set_screen_hue,
			R.id.set_screen_saturation };

	Integer mVideoPara[] = new Integer[4];
	String[] mirror_title;
	int mirrorIndex;
	int mBacklightLevel;
	boolean mParkingEnable;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Tag.ACTION_BRIGHTNESS_CHANGED)) {
				mBacklightLevel = BacklightControl.getBacklightLevel(BacklightControl
						.getBrightness());
				mLayout_TwoButton[1].setMiddleTitle(mBacklightLevel + "");
			}
		}
	};

	private void initData() {
		mContext = this;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Tag.ACTION_BRIGHTNESS_CHANGED);
		mContext.registerReceiver(mBroadcastReceiver, filter);

		mirror_title = getResources().getStringArray(R.array.mirror_flip_names);
		mirrorIndex = SystemProperties.getInt(Tag.PERSYS_BACKCAR_MIRROR, mirrorIndex);
		mBacklightLevel = BacklightControl.getBacklightLevel(BacklightControl.getBrightness());
		mParkingEnable = SystemProperties.getBoolean(Tag.PERSYS_BRAKE_ENABLE, false);

		// mVideoPara[0] = AtcSettings.Display.GetBrightnessLevel();
		// mVideoPara[1] = AtcSettings.Display.GetContrastLevel();
		// mVideoPara[2] = AtcSettings.Display.GetHueLevel();
		// mVideoPara[3] = AtcSettings.Display.GetSaturationLevel();
		

		for (int i = 0; i < ArraySetting.DEFAULT_VALUES[0].length; i++) {
			mVideoPara[i] = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[0][i],
					ArraySetting.DEFAULT_VALUES[0][i]);
		}

	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_video);
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initView() {
		int i = 0;
		for (HeaderLayout layout : mLayout_Button) {
			layout = (HeaderLayout) findViewById(ID_Button[i]);
			layout.setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_TwoButton) {
			mLayout_TwoButton[i] = (HeaderLayout) findViewById(ID_TwoButton[i]);
			mLayout_TwoButton[i].setTwoButtonListener(this);
			i++;
		}

		mLayout_TwoButton[0].setMiddleTitle(mirror_title[mirrorIndex]);
		mLayout_TwoButton[1].setMiddleTitle(mBacklightLevel + "");

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

		mLayout_CheckBox[0].getOneCheckBox().setChecked(mParkingEnable);

		i = 0;
		for (HeaderLayout layout : mLayout_SeekBar) {
			mLayout_SeekBar[i] = (HeaderLayout) findViewById(ID_SeekBar[i]);
			mLayout_SeekBar[i].setSeekbarPos(mVideoPara[i]);
			mLayout_SeekBar[i].setSeekbarText(mVideoPara[i]);
			mLayout_SeekBar[i].setOnProgressChanged(this);
			i++;
		}
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_Button[0]) {
			setVideoTytle(1);
		} else if (view.getId() == ID_Button[1]) {
			setVideoTytle(2);
		} else if (view.getId() == ID_Button[2]) {
			setVideoTytle(3);
		}
	}

	/**
	 * 
	 */
	private void setVideoTytle(int type) {
		int i = 0;
		for (int m : mVideoPara) {
			mVideoPara[i] = ArraySetting.DEFAULT_VALUES[type][i];
			mLayout_SeekBar[i].setSeekbarPos(mVideoPara[i]);
			mLayout_SeekBar[i].setSeekbarText(mVideoPara[i]);
			i++;
		}
		
		//改到断ACC再写mzone,防止频繁写 del by xuhh
		//YeconMetazone
		//		.MetaSetUIVideoPara(mVideoPara[0], mVideoPara[1], mVideoPara[2], mVideoPara[3]);
		setMcuSystemPara();
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_CheckBox[0]) {
			SystemProperties.set(Tag.PERSYS_BRAKE_ENABLE, value ? "true" : "false");
		}
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_TwoButton[0]) {
			setBackcarMirror(--mirrorIndex);
		} else if (view.getId() == ID_TwoButton[1]) {
			setBacklightMode(false);
			mLayout_TwoButton[1].setMiddleTitle(mBacklightLevel + "");
		}
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == ID_TwoButton[0]) {
			setBackcarMirror(++mirrorIndex);
		} else if (view.getId() == ID_TwoButton[1]) {
			setBacklightMode(true);
			mLayout_TwoButton[1].setMiddleTitle(mBacklightLevel + "");
		}
	}

	@Override
	public void onProgressChanged(View view, SeekBar mSeekbar, int progress) {
		// TODO Auto-generated method stub
		sendMessage(view.getId(), progress);
	}

	public void sendMessage(int what, int index) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = index;
		myHandler.sendMessage(message);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			int i = 0;
			for (Integer nValue : ID_SeekBar) {
				if (nValue == msg.what) {
					mVideoPara[i] = (Integer) msg.obj;
					//改到断ACC再写mzone,防止频繁写 del by xuhh
					//YeconMetazone.MetaSetUIVideoPara(mVideoPara[0], mVideoPara[1], mVideoPara[2],
					//		mVideoPara[3]);
					setMcuSystemPara();
					int value = mVideoPara[i];
					mLayout_SeekBar[i].setSeekbarText(value);
					setDisplayValue(i, value);
					return;
				}
				i++;
			}
		}
	};

	private void setBackcarMirror(int value) {
		mirrorIndex = value;
		if (mirrorIndex < 0)
			mirrorIndex = 0;
		else if (mirrorIndex > 3)
			mirrorIndex = 3;
		SystemProperties.set(Tag.PERSYS_BACKCAR_MIRROR, mirrorIndex + "");
		mLayout_TwoButton[0].setMiddleTitle(mirror_title[mirrorIndex]);
		//改到断ACC再写mzone,防止频繁写 del by xuhh
		//YeconMetazone.SetBackCarMirror(mirrorIndex|0xE0);
		setMcuSystemPara();
	}

	public int setBacklightMode(boolean bRight) {
		int backlight = BacklightControl.getBrightness();
		boolean bIsBlkEnable = BacklightControl.GetBklEnable();

		if (mBacklightLevel == BKL_HIGH) {
			if (bRight)
				return mBacklightLevel;
			mBacklightLevel = BKL_MID;
			backlight = BRIGHT_TAB[mBacklightLevel];
		} else if (mBacklightLevel == BKL_MID) {
			if (bRight)
				mBacklightLevel = BKL_HIGH;
			else
				mBacklightLevel = BKL_LOW;
			backlight = BRIGHT_TAB[mBacklightLevel];
		} else if (mBacklightLevel == BKL_LOW) {
			if (bRight)
				mBacklightLevel = BKL_MID;
			// else
			// mBacklightLevel = BKL_OFF;
			backlight = BRIGHT_TAB[mBacklightLevel];
		}

		int mcuBacklight = 0;
		if (mBacklightLevel <= BKL_OFF) {
			mcuBacklight = BRIGHTNESS_LOW;
		} else {
			mcuBacklight = backlight;
		}
		L.e("SetBacklightMode - backlight: " + backlight + " - mcuBacklight: " + mcuBacklight);

		SystemProperties.set(Tag.PERSYS_BKLIGHT[0], mcuBacklight + "");

		setMcuSystemPara();

		McuMethodManager.getInstance(mContext).sendBlackoutKeyCMD(mBacklightLevel);

		if (mBacklightLevel <= BKL_OFF) {
			mBacklightLevel = BKL_LOW;
		}

		if (backlight < 0 && bIsBlkEnable) {
			Settings.System.putInt(mContext.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, BRIGHTNESS_LOW);
			BacklightControl.SetBklEnable(0, 1, 0);
		} else
			Settings.System.putInt(mContext.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS, backlight);
		return mBacklightLevel;
	}

	private void setMcuSystemPara() {
		McuMethodManager.SystemParam para = new McuMethodManager.SystemParam();
		para.brightness = mVideoPara[0];
		para.contrast = mVideoPara[1];
		para.hue = mVideoPara[2];
		para.saturation = mVideoPara[3];
		para.backlight = BRIGHT_TAB[mBacklightLevel];
		para.backcarMirror = mirrorIndex;
		McuMethodManager.getInstance(mContext).setMcuSystemParam(para);
	}

	public void setDisplayValue(int type, int value) {
		if (value >= 100)
			value = 100;
		if (value <= 0)
			value = 0;
		switch (type) {
		case 0:
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][0], value + "");
			AtcSettings.Display.SetBrightnessLevel(value);
			break;
		case 1:
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][1], value + "");
			AtcSettings.Display.SetContrastLevel(value);
			break;
		case 2:
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][2], value + "");
			AtcSettings.Display.SetHueLevel(value);
			break;
		case 3:
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[0][3], value + "");
			AtcSettings.Display.SetSaturationLevel(value);
			break;
		}
	}

}
