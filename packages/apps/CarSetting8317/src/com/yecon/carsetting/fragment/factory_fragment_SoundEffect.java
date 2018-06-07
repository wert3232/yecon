package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onProgressChanged;

public class factory_fragment_SoundEffect extends DialogFragment implements onOneCheckBoxListener,
		onProgressChanged {

	private Context mContext;

	int ID_SeekBar[] = { R.id.seekBar_treble, R.id.seekBar_alto, R.id.seekBar_bass,
			R.id.seekBar_subwoofer, R.id.seekBar_loundness, R.id.seekBar_balance_fr,
			R.id.seekBar_balance_lr };
	HeaderLayout mLayout_SeekBar[] = new HeaderLayout[ID_SeekBar.length];
	int ID_CheckBox[] = {};
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	Integer[] mHandlerIDs = { 100, 200, 300, 400, 500, 600, 700 };

	int offset[] = { 14, 14, 14, 0, 0, 20, 20 };
	int max[] = { 28, 28, 28, 40, 19, 40, 40 };
	int range[][] = { { -14, 14 }, { -14, 14 }, { -14, 14 }, { 0, 40 }, { 0, 19 }, { -20, 20 },
			{ -20, 20 } };

	public factory_fragment_SoundEffect() {
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
		getDialog().setTitle(getResources().getString(R.string.factory_sound_effect));
		View rootView = inflater.inflate(R.layout.factory_fragment_soundeffect_set, container);
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
			mLayout_SeekBar[i].setSeekbarPos(XmlParse.audio[i] + offset[i]);
			mLayout_SeekBar[i].setSeekbarText(XmlParse.audio[i]);
			mLayout_SeekBar[i].setOnProgressChanged(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		default:
			break;
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
					XmlParse.audio[i] = pos - offset[i];
					mLayout_SeekBar[i].setSeekbarText(XmlParse.audio[i]);
					// SystemProperties.set(Tag.PERSYS_AUDIO[i],
					// XmlParse.audio[i] + "");
					return;
				}
				i++;
			}
		}
	};

}
