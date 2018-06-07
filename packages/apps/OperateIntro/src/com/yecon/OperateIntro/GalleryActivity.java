package com.yecon.OperateIntro;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryActivity extends Activity implements OnClickListener, OnTouchListener {

	ImageAdapter adapter;
	static ImageView imageView[] = new ImageView[3];
	static Gallery gallery;
	static boolean isPlay = false;
	static Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);

		imageView[0] = (ImageView) findViewById(R.id.btn_pre);
		imageView[1] = (ImageView) findViewById(R.id.btn_next);
		imageView[2] = (ImageView) findViewById(R.id.btn_play);
		for (int i = 0; i < imageView.length; i++) {
			imageView[i].setOnClickListener(this);
		}

		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Util.imgWidth = dm.widthPixels;
		Util.imgHeight = dm.heightPixels;

		gallery = (Gallery) findViewById(R.id.Gallery01);
		gallery.setOnTouchListener(this);
		adapter = new ImageAdapter(this);
		gallery.setAdapter(adapter);

		Intent intent = getIntent();
		Util.index = intent.getIntExtra(Util.indexPage, Util.index);
		gallery.setSelection(Util.index);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// Toast.makeText(Activity01.this, "you selected " + (position +
				// 1) + " picture",
				// Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopSlide();
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_pre:
			stopSlide();
			SetSelection(--Util.index);
			break;
		case R.id.btn_next:
			stopSlide();
			SetSelection(++Util.index);
			break;
		case R.id.btn_play:
			isPlay = !isPlay;
			if (isPlay) {
				startSlide();
			} else {
				stopSlide();
			}
			break;

		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg1.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (arg0.getId() == R.id.Gallery01)
				stopSlide();
			break;
		case MotionEvent.ACTION_UP:
			break;

		default:
			break;
		}
		return false;
	}

	private static void startSlide() {
		if (timer == null) {
			timer = new Timer();
			sendMessage(Util.ID_MSG_SLIDE);
			imageView[2].setBackgroundResource(R.xml.btn_pause);
		}
	}

	private static void stopSlide() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			isPlay = false;
			mHandler.removeMessages(Util.ID_MSG_SLIDE);
			imageView[2].setBackgroundResource(R.xml.btn_play);
		}
	}

	private static void sendMessage(final int value) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = value;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task, Util.TIMER_DELAY, Util.TIMER_PERIOD);
	}

	static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Util.ID_MSG_SLIDE) {
				SetSelection(++Util.index);
				if (Util.index >= Util.mImageIds.length - 1)
					stopSlide();
			}
		}
	};

	private static void SetSelection(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > Util.mImageIds.length - 1) {
			value = Util.mImageIds.length - 1;
		}
		gallery.setSelection(value);
	}

	private void onHomePressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}

}
