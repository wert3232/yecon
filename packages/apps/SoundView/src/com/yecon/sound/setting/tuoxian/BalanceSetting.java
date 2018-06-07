package com.yecon.sound.setting.tuoxian;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.mcu.McuManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuoxianui.tools.AtTimerHelpr;
import com.yecon.sound.setting.MyApplication;
import com.yecon.sound.setting.R;
import com.yecon.sound.setting.unitl.L;
import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;
import com.yecon.sound.setting.view.BalanceView;
import com.yecon.sound.setting.view.BalanceView.onBalanceListener;


public class BalanceSetting extends Activity implements OnClickListener, OnTouchListener,
		onBalanceListener{
	private ImageView imageView[] = new ImageView[4];
	private BalanceView mBalanceView;
	private TextView textView[] = new TextView[12];

	private Timer timer;
	private mtksetting mmtksetting;
	private PolicyHandler mHandler;
	private AtTimerHelpr mAtTimerHelpr;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_tuoxian_balance_setting);
		MyApplication.getInstance().addActivity(this);
		mmtksetting = new mtksetting();
//		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		mmtksetting.setSharedPreferences(getSharedPreferences("sound_settings", Context.MODE_PRIVATE));
		mHandler = new PolicyHandler();
		initView();
		
		//一汽要求,不自动流转 mAtTimerHelpr = new AtTimerHelpr();
		AtTimerHelpr.AtTimerHelprDoItCallBack atTimerHelprDoItCallBack = new AtTimerHelpr.AtTimerHelprDoItCallBack() {
			@Override
			public void doIt() {
				backHandler.sendEmptyMessage(0);
			}
		};
		//一汽要求,不自动流转 mAtTimerHelpr.setCallBack(atTimerHelprDoItCallBack);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void initView() {
		mBalanceView = (BalanceView) findViewById(R.id.sound_view_id);
		mBalanceView.setBalanceListener(this);
		imageView[0] = (ImageView) findViewById(R.id.add_front_image);
		imageView[1] = (ImageView) findViewById(R.id.add_rear_image);
		imageView[2] = (ImageView) findViewById(R.id.add_left_image);
		imageView[3] = (ImageView) findViewById(R.id.add_right_image);

		for (int i = 0; i < imageView.length; i++) {
			imageView[i].setOnClickListener(this);
			imageView[i].setOnTouchListener(this);
		}

		textView[0] = (TextView) findViewById(R.id.mode_front);
		textView[1] = (TextView) findViewById(R.id.mode_rear);
		textView[2] = (TextView) findViewById(R.id.mode_left);
		textView[3] = (TextView) findViewById(R.id.mode_right);
		textView[4] = (TextView) findViewById(R.id.mode_fl);
		textView[5] = (TextView) findViewById(R.id.mode_fr);
		textView[6] = (TextView) findViewById(R.id.mode_rl);
		textView[7] = (TextView) findViewById(R.id.mode_rr);
		textView[8] = (TextView) findViewById(R.id.btn_reset);
		textView[9] = (TextView) findViewById(R.id.btn_analogmode);
		textView[10] = (TextView) findViewById(R.id.bal_front_rear_value);
		textView[11] = (TextView) findViewById(R.id.bal_left_right_value);

		for (TextView tv : textView) {
			tv.setOnClickListener(this);
		}

		int fr = mmtksetting.uiState.getInt(Tag.valueY_tag, 0);
		int lr = mmtksetting.uiState.getInt(Tag.valueX_tag, 0);
		
		fr = BalanceView.getViewValueY(fr);
		lr = BalanceView.getViewValueX(lr);
		
		freshBalanceUI(fr, lr);

	}

	@Override
	public void onChangeValue(int back_front, int left_right) {

		freshBalanceUI(back_front, left_right);
	}

	private void freshBalanceUI(int fr, int lr) {
		L.e("fr: " + fr + " lr:" + lr);
		StringBuffer strBuf = new StringBuffer();
		getStringBuffer(fr, strBuf);
		textView[10].setText(getResources().getString(R.string.bal_front_rear_value, strBuf));
		getStringBuffer(lr, strBuf);
		textView[11].setText(getResources().getString(R.string.bal_left_right_value, strBuf));
	}

	private void getStringBuffer(int value, StringBuffer strbuffer) {
		strbuffer.setLength(0);
		if (value >= 0) {
//			strbuffer.append("+").append(value);
			strbuffer.append(value);
		} else {
			strbuffer.append(value); 
		}
	}

	private void sendMessage(final int value) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = value;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task, 500, 100);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			timer = new Timer();
			sendMessage(view.getId());
			break;
		case MotionEvent.ACTION_UP:
			timer.cancel();
			mHandler.removeMessages(view.getId());
			break;

		default:
			break;
		}
		return false;
	}

	private class PolicyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.add_front_image:
				mBalanceView.cutPointY();
				break;
			case R.id.add_left_image:
				mBalanceView.cutPointX();
				break;
			case R.id.add_rear_image:
				mBalanceView.addPointY();
				break;
			case R.id.add_right_image:
				mBalanceView.addPointX();
				break;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.mode_front:
			mBalanceView.changeBalanceMode(1);
			break;
		case R.id.mode_rear:
			mBalanceView.changeBalanceMode(2);
			break;
		case R.id.mode_left:
			mBalanceView.changeBalanceMode(3);
			break;
		case R.id.mode_right:
			mBalanceView.changeBalanceMode(4);
			break;
		case R.id.mode_fl:
			mBalanceView.changeBalanceMode(5);
			break;
		case R.id.mode_fr:
			mBalanceView.changeBalanceMode(6);
			break;
		case R.id.mode_rl:
			mBalanceView.changeBalanceMode(7);
			break;
		case R.id.mode_rr:
			mBalanceView.changeBalanceMode(8);
			break;

		case R.id.add_front_image:
			mBalanceView.cutPointY();
			break;
		case R.id.add_left_image:
			mBalanceView.cutPointX();
			break;
		case R.id.add_rear_image:
			mBalanceView.addPointY();
			break;
		case R.id.add_right_image:
			mBalanceView.addPointX();
			break;
		case R.id.btn_reset:
			mBalanceView.changeBalanceMode(0);
			freshBalanceUI(0, 0);
			break;
		case R.id.btn_analogmode:
			finish();
			break;
		}
	}
	
	
	Handler backHandler = new Handler() {
		public void handleMessage(Message msg) {
			onBackPressed();
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//一汽要求,不自动流转 mAtTimerHelpr.start(10);
	}
	@Override
	protected void onPause() {
		super.onPause();
		//一汽要求,不自动流转 mAtTimerHelpr.stop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//一汽要求,不自动流转 mAtTimerHelpr.reset();
		return super.dispatchTouchEvent(ev);
	}
}
