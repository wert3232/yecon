package com.yecon.carsetting.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onProgressChanged;

public class factory_fragment_RGBSet extends DialogFragment implements OnCheckedChangeListener,
		onProgressChanged {

	private Context mContext;

	HeaderLayout mLayout_SeekBar[] = new HeaderLayout[4];
	int ID_SeekBar[] = { R.id.seekBar_bright, R.id.seekBar_contrast, R.id.seekBar_hue,
			R.id.seekBar_saturation };
	Integer[] mHandlerIDs = { 100, 200, 300, 400 };

	RadioGroup radioGroup;
	RadioButton radioButton[] = new RadioButton[5];
	int mIndex = Tag.VIDEOTYPE.SCREEN.ordinal();

	public factory_fragment_RGBSet() {
		// TODO Auto-generated constructor stub

	}

	private void initData() {
		mContext = getActivity();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Window window = getDialog().getWindow();
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

		getDialog().setTitle(getResources().getString(R.string.factory_rgb_set));
		// getDialog().getActionBar().setBackgroundDrawable(arg0);
		View rootView = inflater.inflate(R.layout.factory_fragment_rgb_set, container);
		initView(rootView);

		return rootView;
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	void initView(View rootView) {

		radioGroup = (RadioGroup) rootView.findViewById(R.id.RadioGroup_menu);
		radioGroup.setOnCheckedChangeListener(this);
		radioButton[0] = (RadioButton) rootView.findViewById(R.id.Radio_style_0);
		radioButton[1] = (RadioButton) rootView.findViewById(R.id.Radio_style_1);
		radioButton[2] = (RadioButton) rootView.findViewById(R.id.Radio_style_2);
		radioButton[3] = (RadioButton) rootView.findViewById(R.id.Radio_style_3);
		radioButton[4] = (RadioButton) rootView.findViewById(R.id.Radio_style_4);

		int i = 0;
		for (HeaderLayout layout : mLayout_SeekBar) {
			mLayout_SeekBar[i] = (HeaderLayout) rootView.findViewById(ID_SeekBar[i]);
			mLayout_SeekBar[i].setSeekbarPos(XmlParse.rgb_video[0][i]);
			mLayout_SeekBar[i].setSeekbarText(XmlParse.rgb_video[0][i]);
			mLayout_SeekBar[i].setOnProgressChanged(this);
			i++;
		}
		
		//从可见的空间开始，把它checked
		for (int j=0; j<radioButton.length; j++) {
			if (radioButton[j] != null) {
				
			}
			if (radioButton[j].getVisibility() == View.VISIBLE) {
				radioGroup.check(radioButton[j].getId());
				break;
			}
		}
		
	}

	void setSeekBarMax(int index) {
		if (index == Tag.VIDEOTYPE.SCREEN.ordinal() || index == Tag.VIDEOTYPE.BACKCAR.ordinal()) {
			for (int i = 0; i < mLayout_SeekBar.length; i++) {
				mLayout_SeekBar[i].setSeekbarMax(100);
			}
		} else {
			mLayout_SeekBar[0].setSeekbarMax(255);
			mLayout_SeekBar[1].setSeekbarMax(255);
			mLayout_SeekBar[2].setSeekbarMax(63);
			mLayout_SeekBar[3].setSeekbarMax(255);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		switch (arg1) {
		case R.id.Radio_style_0:
			mIndex = Tag.VIDEOTYPE.SCREEN.ordinal();
			setSeekBarMax(mIndex);
			setVideoRGB(mIndex);
			break;
		case R.id.Radio_style_1:
			mIndex = Tag.VIDEOTYPE.AVIN_1.ordinal();
			setSeekBarMax(mIndex);
			setVideoRGB(mIndex);
			break;
		case R.id.Radio_style_2:
			mIndex = Tag.VIDEOTYPE.BACKCAR.ordinal();
			setSeekBarMax(mIndex);
			setVideoRGB(mIndex);
			break;
		case R.id.Radio_style_3:
			mIndex = Tag.VIDEOTYPE.USB.ordinal();
			setSeekBarMax(mIndex);
			setVideoRGB(mIndex);
			break;
		case R.id.Radio_style_4:
			mIndex = Tag.VIDEOTYPE.DVD.ordinal();
			setSeekBarMax(mIndex);
			setVideoRGB(mIndex);
			break;
		default:
			break;
		}
	}

	private void setValue(int index, int i, int value) {
		if (value < 0)
			value = 0;
		else if (value > mLayout_SeekBar[i].getSeekbarMax())
			value = mLayout_SeekBar[i].getSeekbarMax();
		mLayout_SeekBar[i].setSeekbarPos(value);
		mLayout_SeekBar[i].setSeekbarText(value);
		XmlParse.rgb_video[index][i] = value;
	}

	private void setVideoRGB(int index) {
		for (int i = 0; i < XmlParse.rgb_video[index].length; i++) {
			setValue(index, i, XmlParse.rgb_video[index][i]);
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
					// setValue(mIndex, i, pos);
					mLayout_SeekBar[i].setSeekbarText(pos);
					XmlParse.rgb_video[mIndex][i] = pos;
					return;
				}
				i++;
			}
		}
	};
}
