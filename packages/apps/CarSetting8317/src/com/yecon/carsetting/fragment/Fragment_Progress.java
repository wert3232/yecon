package com.yecon.carsetting.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yecon.carsetting.R;

public class Fragment_Progress extends DialogFragment implements OnClickListener,
		OnSeekBarChangeListener, OnTouchListener {

	private Context mContext;
	Timer timer;
	TextView text_title, text_value;
	SeekBar seekBar;
	ImageView btn_left, btn_right;
	private static String titleName;
	private static int mID, mMax, mPos;
	private static int mWidth, mHeight;

	OnProgressDlgListener mListener;

	public void setOnProgressDlgListener(OnProgressDlgListener Listener) {
		// TODO Auto-generated method stub
		mListener = Listener;
	}

	public interface OnProgressDlgListener {
		void onProgessDlgSetValue(int id, int value);

	}

	public Fragment_Progress() {
		// TODO Auto-generated constructor stub

	}

	public Fragment_Progress(int width, int height) {
		// TODO Auto-generated constructor stub
		mWidth = width;
		mHeight = height;
	}

	public void initData(int id, String title, int progress, int pos) {
		mID = id;
		titleName = title;
		mMax = progress;
		mPos = pos;
	}

	private void initData() {
		mContext = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomizeActivityTheme);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(titleName);
		View rootView = inflater.inflate(R.layout.fragment_progress, container);
		initView(rootView);
		return rootView;
	}

	void initView(View rootView) {
		text_title = (TextView) rootView.findViewById(R.id.textview_title);
		text_title.setText(titleName);
		text_value = (TextView) rootView.findViewById(R.id.text_value_id);
		text_value.setText(mPos + "");
		btn_left = (ImageView) rootView.findViewById(R.id.btn_left_id);
		btn_left.setOnClickListener(this);
		btn_left.setOnTouchListener(this);
		btn_right = (ImageView) rootView.findViewById(R.id.btn_right_id);
		btn_right.setOnClickListener(this);
		btn_right.setOnTouchListener(this);
		seekBar = (SeekBar) rootView.findViewById(R.id.SeekBar_id);
		seekBar.setMax(mMax);
		seekBar.setProgress(mPos);
		seekBar.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// getDialog().getWindow().setLayout(mWidth, mHeight);
		Bitmap bk = BitmapFactory.decodeResource(getResources(), R.drawable.dialog_prompt_bk);
		getDialog().getWindow().setLayout(bk.getWidth(), bk.getHeight());
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	private void minusValue() {
		mPos -= 1;
		if (mPos < 0)
			mPos = 0;
		text_value.setText(mPos + "");
		mListener.onProgessDlgSetValue(mID, mPos);
		seekBar.setProgress(mPos);
	}

	private void addValue() {
		mPos += 1;
		if (mPos > mMax)
			mPos = mMax;
		text_value.setText(mPos + "");
		mListener.onProgessDlgSetValue(mID, mPos);
		seekBar.setProgress(mPos);
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
		case R.id.SeekBar_id:
			mPos = arg1;
			text_value.setText(mPos + "");
			mListener.onProgessDlgSetValue(mID, arg1);
			break;

		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

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
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			timer = new Timer();
			sendMessage(arg0.getId());
			break;
		case MotionEvent.ACTION_UP:
			timer.cancel();
			mHandler.removeMessages(arg0.getId());
			break;
		default:
			break;
		}
		return false;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ChangeValue(msg.what);
		}
	};

	private void ChangeValue(int id) {
		switch (id) {
		case R.id.btn_left_id:
			minusValue();
			break;
		case R.id.btn_right_id:
			addValue();
			break;
		}
	}

}
