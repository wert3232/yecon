package com.yecon.carsetting.fragment;

import android.R.integer;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onProgressChanged;

public class factory_Fragment_Video extends DialogFragment implements onOneButtonListener,
		onProgressChanged {

	private Context mContext;
	FragmentManager mFragmentManager;
	final int low_seekbar_start=20;
	final int mid_seekbar_start=40;
	final int high_seekbar_start=79;

	final int SEEKBAR_START[] = {0, low_seekbar_start, mid_seekbar_start, high_seekbar_start};
	int ID_OneButton[] = { R.id.factory_rgb_set, };
	int ID_SeekBar[] = { R.id.factory_backlight_def, R.id.factory_backlight_low,
			R.id.factory_backlight_mid, R.id.factory_backlight_high, };
	
	HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_SeekBar[] = new HeaderLayout[ID_SeekBar.length];

	public factory_Fragment_Video() {
		super();
	}

	public void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
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
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.factory_fragment_video, container);
		initView(mRootView);
		return mRootView;
	}

	private void initView(View rootView) {

		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			layout = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			layout.setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_SeekBar) {
			mLayout_SeekBar[i] = (HeaderLayout) rootView.findViewById(ID_SeekBar[i]);
			if (i >0) {
				mLayout_SeekBar[i].setSeekbarPos(XmlParse.backlight[i]-SEEKBAR_START[i]);
			}else {
				mLayout_SeekBar[i].setSeekbarPos(XmlParse.backlight[i]);
			}
			mLayout_SeekBar[i].setSeekbarText(XmlParse.backlight[i]);
			
			mLayout_SeekBar[i].setOnProgressChanged(this);
			i++;
		}

	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_rgb_set:
			Function.onSet_RGBSet(mFragmentManager);
			break;
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
				sendMessage(ID_SeekBar[i], progress);
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
			for (Integer nValue : ID_SeekBar) {
				if (nValue == msg.what) {
					int pos = (Integer) msg.obj;
					if (i > 0) {
						XmlParse.backlight[i] = pos+SEEKBAR_START[i];
						mLayout_SeekBar[i].setSeekbarText(pos+SEEKBAR_START[i]);
					}else {
						XmlParse.backlight[i] = pos;
						mLayout_SeekBar[i].setSeekbarText(pos);
					}
					
					return;
				}
				i++;
			}
		}
	};

}
