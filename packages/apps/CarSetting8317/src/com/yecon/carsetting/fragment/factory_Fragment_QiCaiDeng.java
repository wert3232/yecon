package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.R.array;
import com.yecon.carsetting.R.id;
import com.yecon.carsetting.R.layout;
import com.yecon.carsetting.unitl.XmlParse;

public class factory_Fragment_QiCaiDeng extends DialogFragment implements OnSeekBarChangeListener, OnClickListener {

	private Context mContext;
	private SeekBar mRSeekarBar, mGSeekBar, mBSeekBar;
	private final int mMax = 100;
	private int mRDef = 28, mGDef = 28, mBDef = 28;
	private final int mMessageID = 5008;
	private final int mDelayTime = 300;

	private TextView mRTextVal, mGTextVal, mBTextVal;
	private TextView mLightType;
	private Button mLeftButton, mRightButton;
	private int mCurMode = 0;
	private int mModeMax = 0;
	private String[] mLightTypeStrings;

	private void initData() {
		mContext = getActivity();
		mCurMode = XmlParse.light_mode;
		mRDef = XmlParse.light_r;
		mGDef = XmlParse.light_g;
		mBDef = XmlParse.light_b;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.factory_fragment_qicaideng, container, false);
		initView(mRootView);
		return mRootView;
	}

	private void initView(View rootView) {
		mLightType = (TextView) rootView.findViewById(R.id.light_type);

		mLeftButton = (Button) rootView.findViewById(R.id.btn_left);
		mLeftButton.setOnClickListener(this);

		mRightButton = (Button) rootView.findViewById(R.id.btn_right);
		mRightButton.setOnClickListener(this);

		mRSeekarBar = (SeekBar) rootView.findViewById(R.id.r_progress);
		mRSeekarBar.setMax(mMax);
		mRSeekarBar.setProgress(mRDef);
		mRSeekarBar.setOnSeekBarChangeListener(this);
		mRTextVal = (TextView) rootView.findViewById(R.id.text_r_val);
		mRTextVal.setText(mRDef + "");

		mGSeekBar = (SeekBar) rootView.findViewById(R.id.g_progress);
		mGSeekBar.setMax(mMax);
		mGSeekBar.setProgress(mGDef);
		mGSeekBar.setOnSeekBarChangeListener(this);
		mGTextVal = (TextView) rootView.findViewById(R.id.text_g_val);
		mGTextVal.setText(mGDef + "");

		mBSeekBar = (SeekBar) rootView.findViewById(R.id.b_progress);
		mBSeekBar.setMax(mMax);
		mBSeekBar.setProgress(mBDef);
		mBSeekBar.setOnSeekBarChangeListener(this);
		mBTextVal = (TextView) rootView.findViewById(R.id.text_b_val);
		mBTextVal.setText(mGDef + "");

		mLightTypeStrings = getResources().getStringArray(R.array.light_type);
		mModeMax = mLightTypeStrings.length - 1;
		mLightType.setText(mLightTypeStrings[mCurMode]);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == mMessageID) {
				setMcuParam_qicaideng();
			}
		}
	};

	private void setMcuParam_qicaideng() {

		XmlParse.light_mode = mCurMode;
		XmlParse.light_r = mRSeekarBar.getProgress();
		XmlParse.light_g = mGSeekBar.getProgress();
		XmlParse.light_b = mBSeekBar.getProgress();

		// McuMethodManager.getInstance(this).setMcuParam_qicaideng();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		int id = seekBar.getId();
		switch (id) {
		case R.id.r_progress:
			mRTextVal.setText(progress + "");
			sendMsg();
			break;
		case R.id.g_progress:
			mGTextVal.setText(progress + "");
			sendMsg();
			break;
		case R.id.b_progress:
			mBTextVal.setText(progress + "");
			sendMsg();
			break;
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	private void sendMsg() {
		mHandler.removeMessages(mMessageID);
		mHandler.sendEmptyMessageDelayed(mMessageID, mDelayTime);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_left:
			if (mCurMode > 0) {
				--mCurMode;
				mLightType.setText(mLightTypeStrings[mCurMode]);
				sendMsg();
			}
			break;
		case R.id.btn_right:
			if (mCurMode < mModeMax) {
				++mCurMode;
				mLightType.setText(mLightTypeStrings[mCurMode]);
				mHandler.sendEmptyMessageDelayed(mMessageID, mDelayTime);
				sendMsg();
			}
			break;

		default:
			break;
		}

	}

}
