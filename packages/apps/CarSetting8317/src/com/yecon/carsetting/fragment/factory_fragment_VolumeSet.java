package com.yecon.carsetting.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onProgressChanged;

public class factory_fragment_VolumeSet extends DialogFragment implements onProgressChanged {

	private Context mContext;

	HeaderLayout mLayout_SeekBar[] = new HeaderLayout[5];
	int ID_SeekBar[] = { R.id.seekBar_volume_media_front, R.id.seekBar_volume_media_rear,
			R.id.seekBar_volume_gis, R.id.seekBar_volume_backcar, R.id.seekBar_volume_bt };
	Integer[] mHandlerIDs = { 100, 200, 300, 400, 500 };

	public factory_fragment_VolumeSet() {
		// TODO Auto-generated constructor stub
	}

	private void initData() {
		mContext = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().setTitle(getResources().getString(R.string.factory_volume_set));
		View rootView = inflater.inflate(R.layout.factory_fragment_volume_set, container);
		initView(rootView);
		return rootView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	void initView(View rootView) {
		int i = 0;
		for (HeaderLayout layout : mLayout_SeekBar) {
			mLayout_SeekBar[i] = (HeaderLayout) rootView.findViewById(ID_SeekBar[i]);
			mLayout_SeekBar[i].setSeekbarPos(XmlParse.volume[i]);
			mLayout_SeekBar[i].setSeekbarText(XmlParse.volume[i]);
			mLayout_SeekBar[i].setOnProgressChanged(this);
			i++;
		}
	}

	@Override
	public void onProgressChanged(View view, SeekBar mSeekbar, int progress) {
		// TODO Auto-generated method stub
		int i = 0;
		for (int value : ID_SeekBar) {
			if (value == view.getId()) {
				sendMessage(mHandlerIDs[i], progress);
				return;
			}
			i++;
		}
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
			for (Integer nValue : mHandlerIDs) {
				if (nValue == msg.what) {
					int pos = (Integer) msg.obj;
					XmlParse.volume[i] = pos;
					mLayout_SeekBar[i].setSeekbarText(XmlParse.volume[i]);
					return;
				}
				i++;
			}
		}
	};

}
