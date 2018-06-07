package com.yecon.sound.setting;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
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

import com.yecon.sound.setting.unitl.Tag;
import com.yecon.sound.setting.unitl.mtksetting;
import com.yecon.sound.setting.view.BalanceView;
import com.yecon.sound.setting.view.BalanceView.onBalanceListener;

public class BalanceActivity extends Activity implements OnClickListener, OnTouchListener,
		onBalanceListener {
	private	ImageView imageView[] = new ImageView[4];
	private BalanceView mBalanceView;
	private TextView textView[] = new TextView[12];

	private Timer timer;
	private mtksetting mmtksetting;
	private PolicyHandler mHandler;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_balance);
		MyApplication.getInstance().addActivity(this);
		mmtksetting = new mtksetting();
		mmtksetting.setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		mHandler = new PolicyHandler();
		initView();
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
		freshBalanceUI(fr, lr);

	}

	@Override
	public void onChangeValue(int back_front, int left_right) {

		freshBalanceUI(back_front, left_right);
	}

	private void freshBalanceUI(int fr, int lr) {
		StringBuffer strBuf = new StringBuffer();
		getStringBuffer(fr, strBuf);
		textView[10].setText(getResources().getString(R.string.bal_front_rear_value, strBuf));
		getStringBuffer(lr, strBuf);
		textView[11].setText(getResources().getString(R.string.bal_left_right_value, strBuf));
	}

	private void getStringBuffer(int value, StringBuffer strbuffer) {
		strbuffer.setLength(0);
		if (value >= 0) {
			strbuffer.append("+").append(value);
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
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(this, AudioSetting.class);
			startActivity(intent);
			break;
		}
	}
}
