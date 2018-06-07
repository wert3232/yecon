package com.yecon.carsetting;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.autochips.settings.AtcSettings;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;

public class RGBSetActivity extends Activity implements OnClickListener, OnSeekBarChangeListener,
		OnTouchListener {
	private static final String TAG = "RGBSetActivity";
	private static final int MSG_VALUE_SET = 100;
	private static final int MSG_DLG_FINISH = 101;
	private Button mRgbResetBtn;
	private static int finishTimeOut = 0;
	private static boolean bStartTracking = false;
	Timer timer;

	SeekBar seekBar[] = new SeekBar[4];
	TextView textView[] = new TextView[4];
	int mType = 0;
	
	// public static int rgb_value[][] = { { 40, 25, 50, 50 }, { 45, 45, 50, 97
	// }, { 45, 45, 50, 97 } };

	public static int rgb_value[][] = { { 115, 115, 32, 247 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 }, { 115, 115, 32, 247 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 }, { 115, 115, 32, 247 }, { 102, 64, 32, 128 },
			{ 115, 115, 32, 247 }, { 102, 64, 32, 128 }, { 115, 115, 32, 247 },
			{ 115, 115, 32, 247 } };

	public static enum RGBTYPE {
		AVIN_1(AtcSettings.VCP.SrcType.AVIN_1), 
		AVIN_2(AtcSettings.VCP.SrcType.AVIN_2), 
		AVIN_3(AtcSettings.VCP.SrcType.AVIN_3), 
		AVIN_4(AtcSettings.VCP.SrcType.AVIN_4), 
		AVIN_5(AtcSettings.VCP.SrcType.AVIN_5), 
		BACKCAR(AtcSettings.VCP.SrcType.BACKCAR), 
		DGI(AtcSettings.VCP.SrcType.DGI), 
		DVD(AtcSettings.VCP.SrcType.DVD), 
		HDMI(AtcSettings.VCP.SrcType.HDMI), 
		USB(AtcSettings.VCP.SrcType.USB), 
		VGA(AtcSettings.VCP.SrcType.VGA), 
		YPBPR(AtcSettings.VCP.SrcType.YPBPR);

		private final int value;

		RGBTYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private void setWindowPara() {
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_set_bk);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		lp.alpha = 0.8f;
		lp.y = -30;
		lp.width = bk.getWidth();
		lp.height = bk.getHeight();
		getWindow().setAttributes(lp);
	}

	/**
	 * read every source default rgb value
	 */
	private void initData() {
		for (int i = 0; i < rgb_value.length; i++) {
			for (int j = 0; j < rgb_value[i].length; j++) {
				rgb_value[i][j] = SystemProperties.getInt(Tag.PERSYS_RGB_VIDEO[i + 1][j],
						rgb_value[i][j]);
			}
		}
	}

	private void finishLater() {
		if (finishTimeOut > 0) {
			mHandler.removeMessages(MSG_DLG_FINISH);
			mHandler.sendEmptyMessageDelayed(MSG_DLG_FINISH, finishTimeOut);
		}
	}

	private void finishCancel() {
		if (finishTimeOut > 0) {
			mHandler.removeMessages(MSG_DLG_FINISH);
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		sendBroadcast(new Intent(Tag.ACTION_SETTING_EXIT));
		setContentView(R.layout.activity_rgbset);

		Intent mIntent = getIntent();
		if (mIntent == null) {
			finish();
			return;
		}

		setWindowPara();
		initData();		

		int srctype = mIntent.getIntExtra("srctype", AtcSettings.VCP.SrcType.USB);
		for (RGBTYPE c : RGBTYPE.values()) {
			if (srctype == c.getValue()) {
				mType = c.ordinal();
				break;
			}
		}
		
		initView();
		// mType = RGBTYPE.USB.ordinal();

		for (int i = 0; i < Tag.PERSYS_RGB_VIDEO[mType].length; i++)
			showRGBEffect(mType, i, rgb_value[mType][i], false);

		finishTimeOut = mIntent.getIntExtra("finish_timeout", finishTimeOut);
		finishLater();
	}

	private void initView() {

		mRgbResetBtn = (Button)findViewById(R.id.rgb_reset_all);
		mRgbResetBtn.setOnClickListener(this);
		
		seekBar[0] = (SeekBar) findViewById(R.id.seekBar_brightness);
		seekBar[1] = (SeekBar) findViewById(R.id.seekBar_contrast);
		seekBar[2] = (SeekBar) findViewById(R.id.seekBar_hue);
		seekBar[3] = (SeekBar) findViewById(R.id.seekBar_saturation);
		if (mType == AtcSettings.VCP.SrcType.BACKCAR) {
			seekBar[0].setMax(100);
			seekBar[1].setMax(100);
			seekBar[2].setMax(100);
			seekBar[3].setMax(100);
		}

		for (int i = 0; i < seekBar.length; i++) {
			seekBar[i].setOnSeekBarChangeListener(this);
		}

		textView[0] = (TextView) findViewById(R.id.text_bright_value);
		textView[1] = (TextView) findViewById(R.id.text_contrast_value);
		textView[2] = (TextView) findViewById(R.id.text_hue_value);
		textView[3] = (TextView) findViewById(R.id.text_saturation_value);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

	void resetRGB() {
		for (int i = 0; i < Tag.PERSYS_RGB_VIDEO[mType].length; i++) {
			rgb_value[mType][i] = XmlParse.rgb_video[mType+1][i];
			Log.d(TAG, "#4# mtype="+mType+",value="+XmlParse.rgb_video[mType+1][i]+", i="+i);
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[mType + 1][i], rgb_value[mType][i] + "");
			
			seekBar[i].setProgress(rgb_value[mType][i]);
			textView[i].setText(String.valueOf(rgb_value[mType][i]));
		}
		
		if (mType == AtcSettings.VCP.SrcType.BACKCAR) {
			AtcSettings.Display.SetBrightnessLevel(rgb_value[mType][0]);
			AtcSettings.Display.SetContrastLevel(rgb_value[mType][1]);
			AtcSettings.Display.SetSaturationLevel(rgb_value[mType][3]);
			AtcSettings.Display.SetHueLevel(rgb_value[mType][2]);
		} else {
			AtcSettings.VCP.ContrBritSatr vcpinfo = new AtcSettings.VCP.ContrBritSatr();
			vcpinfo.i4Brit = rgb_value[mType][0];
			vcpinfo.i4Contr = rgb_value[mType][1];
			vcpinfo.i4Satr = rgb_value[mType][3];
			vcpinfo.srctype = mType;
			AtcSettings.VCP.SetVcpContrBritSatrLevel(vcpinfo);
			
			AtcSettings.VCP.HUE hueinfo = new AtcSettings.VCP.HUE();
			hueinfo.i4hue = rgb_value[mType][2];
			hueinfo.srctype = mType;
			AtcSettings.VCP.SetVcpHUELevel(hueinfo);
		}
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.rgb_reset_all) {
			resetRGB();
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.seekBar_brightness:
			showRGBEffect(mType, 0, arg1, false);
			break;
		case R.id.seekBar_contrast:
			showRGBEffect(mType, 1, arg1, false);
			break;
		case R.id.seekBar_hue:
			showRGBEffect(mType, 2, arg1, false);
			break;
		case R.id.seekBar_saturation:
			showRGBEffect(mType, 3, arg1, false);
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		bStartTracking = true;
		finishCancel();
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		bStartTracking = false;
		finishLater();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finishLater();
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			finishCancel();
			timer = new Timer();
			sendMessage(arg0.getId());
			break;
		case MotionEvent.ACTION_UP:
			timer.cancel();
			mHandler.removeMessages(MSG_VALUE_SET);
			finishLater();
			break;
		default:
			break;
		}
		return false;
	}

	private void sendMessage(final int value) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = MSG_VALUE_SET;
				message.arg1 = value;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task, 500, 100);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (MSG_VALUE_SET == msg.what) {
			} else if (MSG_DLG_FINISH == msg.what) {
				finish();
			}
		}
	};

	
	private void showRGBEffect(int i, int j, int value, boolean forceFlag) {
		seekBar[j].setProgress(value);
		textView[j].setText(String.valueOf(value));
		rgb_value[i][j] = value;
		int srctype = RGBTYPE.values()[i].getValue();

		if (bStartTracking || forceFlag) {
			SystemProperties.set(Tag.PERSYS_RGB_VIDEO[i + 1][j], rgb_value[i][j] + "");
			
			if (srctype == AtcSettings.VCP.SrcType.BACKCAR) {
				AtcSettings.Display.SetBrightnessLevel(rgb_value[i][0]);
				AtcSettings.Display.SetContrastLevel(rgb_value[i][1]);
				AtcSettings.Display.SetSaturationLevel(rgb_value[i][3]);
				AtcSettings.Display.SetHueLevel(rgb_value[i][2]);
			}else {
				switch (j) {
				case 0:
				case 1:
				case 3:
					AtcSettings.VCP.ContrBritSatr vcpinfo = new AtcSettings.VCP.ContrBritSatr();
					vcpinfo.i4Brit = rgb_value[i][0];
					vcpinfo.i4Contr = rgb_value[i][1];
					vcpinfo.i4Satr = rgb_value[i][3];
					vcpinfo.srctype = srctype;
					AtcSettings.VCP.SetVcpContrBritSatrLevel(vcpinfo);
					break;
				case 2:
					AtcSettings.VCP.HUE hueinfo = new AtcSettings.VCP.HUE();
					hueinfo.i4hue = rgb_value[i][2];
					hueinfo.srctype = srctype;
					AtcSettings.VCP.SetVcpHUELevel(hueinfo);
					break;
				default:
					break;
				}
			}
			
		}
	}

}
