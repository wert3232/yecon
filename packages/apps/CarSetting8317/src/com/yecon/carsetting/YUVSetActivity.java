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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.autochips.settings.AtcSettings;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.Tag;

public class YUVSetActivity extends Activity implements OnClickListener, OnSeekBarChangeListener,
		OnTouchListener {
	private static final String TAG = "YUVSetActivity";
	Timer timer;
	ImageView imageView[][] = new ImageView[3][2];
	SeekBar seekBar[] = new SeekBar[3];
	TextView textView[] = new TextView[3];
	int mType = 0;
	public static int yuv_gain_value[][] = { { 0xfb, 0xd7, 0xD8 }, { 0xfb, 0xd7, 0xD8 },
			{ 0xfb, 0xd7, 0xD8 }, { 0xfb, 0xd7, 0xD8 }, { 0xfb, 0xd7, 0xD8 },
			{ 0x110, 0xaa, 0xc0 }, { 0x96, 0x62, 0x62 }, { 0x96, 0x62, 0x62 },
			{ 0x96, 0x62, 0x62 }, { 0x96, 0x62, 0x62 }, { 0x96, 0x62, 0x62 }, { 0x96, 0x62, 0x62 } };

	public static enum YUVTYPE {
		AVIN_1(AtcSettings.VCP.SrcType.AVIN_1), AVIN_2(AtcSettings.VCP.SrcType.AVIN_2), AVIN_3(
				AtcSettings.VCP.SrcType.AVIN_3), AVIN_4(AtcSettings.VCP.SrcType.AVIN_4), AVIN_5(
				AtcSettings.VCP.SrcType.AVIN_5), BACKCAR(AtcSettings.VCP.SrcType.BACKCAR), DGI(
				AtcSettings.VCP.SrcType.DGI), DVD(AtcSettings.VCP.SrcType.DVD), HDMI(
				AtcSettings.VCP.SrcType.HDMI), USB(AtcSettings.VCP.SrcType.USB), VGA(
				AtcSettings.VCP.SrcType.VGA), YPBPR(AtcSettings.VCP.SrcType.YPBPR);

		private final int value;

		YUVTYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private void setWindowPara() {
		Bitmap yuvBk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_set_bk);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		lp.alpha = 0.8f;
		lp.y -= 30;
		lp.width = yuvBk.getWidth() + 50;
		lp.height = yuvBk.getHeight();
		getWindow().setAttributes(lp);
	}

	private void initData() {
		ApplicationManage.getInstance().addActivity(this);
		for (int i = 0; i < yuv_gain_value.length; i++) {
			for (int j = 0; j < yuv_gain_value[i].length; j++) {
				yuv_gain_value[i][j] = SystemProperties.getInt(Tag.PERSYS_YUV_GAIN[i][j],
						yuv_gain_value[i][j]);
			}
		}
	}

	private int finishTimeOut = 0;

	private void finishLater() {
		if (finishTimeOut > 0) {
			mHandler.removeMessages(88);
			mHandler.sendEmptyMessageDelayed(88, finishTimeOut);
		}
	}

	private void finishCancel() {
		if (finishTimeOut > 0) {
			mHandler.removeMessages(88);
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_yuvset);

		Intent mIntent = getIntent();
		if (mIntent == null) {
			Log.e(TAG, "Input intent is null");
			finish();
			return;
		}

		setWindowPara();
		initData();
		initView();

		int srctype = mIntent.getIntExtra("yuv_type", AtcSettings.VCP.SrcType.USB);
		for (YUVTYPE c : YUVTYPE.values()) {
			if (srctype == c.getValue()) {
				mType = c.ordinal();
				break;
			}
		}

		L.i("lzy.............................mType......." + mType);
		// mType = YUVTYPE.USB.ordinal();
		setYUVGain(mType);

		finishTimeOut = mIntent.getIntExtra("finish_timeout", finishTimeOut);
		finishLater();
	}

	private void initView() {
		imageView[0][0] = (ImageView) findViewById(R.id.ygain_minus);
		imageView[0][1] = (ImageView) findViewById(R.id.ygain_add);
		imageView[1][0] = (ImageView) findViewById(R.id.ugain_minus);
		imageView[1][1] = (ImageView) findViewById(R.id.ugain_add);
		imageView[2][0] = (ImageView) findViewById(R.id.vgain_minus);
		imageView[2][1] = (ImageView) findViewById(R.id.vgain_add);

		for (int i = 0; i < imageView.length; i++) {
			for (int j = 0; j < imageView[0].length; j++) {
				imageView[i][j].setOnClickListener(this);
				imageView[i][j].setOnTouchListener(this);
			}
		}

		seekBar[0] = (SeekBar) findViewById(R.id.seekBar_ygain);
		seekBar[1] = (SeekBar) findViewById(R.id.seekBar_ugain);
		seekBar[2] = (SeekBar) findViewById(R.id.seekBar_vgain);

		for (int i = 0; i < seekBar.length; i++) {
			seekBar[i].setOnSeekBarChangeListener(this);
		}

		textView[0] = (TextView) findViewById(R.id.text_ygain_value);
		textView[1] = (TextView) findViewById(R.id.text_ugain_value);
		textView[2] = (TextView) findViewById(R.id.text_vgain_value);
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		ChangeValue(arg0.getId());
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.seekBar_ygain:
			setValue(mType, 0, arg1);
			break;
		case R.id.seekBar_ugain:
			setValue(mType, 1, arg1);
			break;
		case R.id.seekBar_vgain:
			setValue(mType, 2, arg1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		finishCancel();
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
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
			mHandler.removeMessages(0);
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
				message.what = 0;
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
			if (0 == msg.what) {
				ChangeValue(msg.arg1);
			} else if (88 == msg.what) {
				finish();
			}
		}
	};

	private void ChangeValue(int id) {
		switch (id) {
		case R.id.ygain_minus:
			setValue(mType, 0, --yuv_gain_value[mType][0]);
			break;
		case R.id.ygain_add:
			setValue(mType, 0, ++yuv_gain_value[mType][0]);
			break;
		case R.id.ugain_minus:
			setValue(mType, 1, --yuv_gain_value[mType][1]);
			break;
		case R.id.ugain_add:
			setValue(mType, 1, ++yuv_gain_value[mType][1]);
			break;
		case R.id.vgain_minus:
			setValue(mType, 2, --yuv_gain_value[mType][2]);
			break;
		case R.id.vgain_add:
			setValue(mType, 2, ++yuv_gain_value[mType][2]);
			break;
		}
	}

	private void setValue(int i, int j, int value) {
		if (value < 0)
			value = 0;
		else if (value > 0x1ff)
			value = 0x1ff;
		seekBar[j].setProgress(value);
		textView[j].setText(String.valueOf(value));
		yuv_gain_value[i][j] = value;
		SystemProperties.set(Tag.PERSYS_YUV_GAIN[i][j], value + "");
		// text_ygain_value.setText("0x" +
		// Integer.toHexString(yuv_gain[i][j]).toUpperCase());
		setVcp(i);
	}

	private void setYUVGain(int index) {
		for (int j = 0; j < Tag.PERSYS_YUV_GAIN[0].length; j++)
			setValue(index, j, yuv_gain_value[index][j]);
	}

	private void setVcp(int i) {
		AtcSettings.VCP.YUVGain vcpinfo = new AtcSettings.VCP.YUVGain();
		vcpinfo.i4YGain = yuv_gain_value[i][0];
		vcpinfo.i4UGain = yuv_gain_value[i][1];
		vcpinfo.i4VGain = yuv_gain_value[i][2];
		int srctype = YUVTYPE.values()[i].getValue();
		vcpinfo.srctype = srctype;
		AtcSettings.VCP.SetVcpYUVGainLevel(vcpinfo);
	}

}
