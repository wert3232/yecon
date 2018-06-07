package com.yecon.carsetting.fragment;

import java.util.Locale;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.internal.app.LocalePicker;
import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class Fragment_General extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, OnListDlgListener {

	Context mContext;
	FragmentManager mFragmentManager;

	int ID_OneButton[] = { R.id.set_language, R.id.set_reset, R.id.set_about };
	int ID_CheckBox[] = { R.id.set_buzzer, R.id.set_lighting_detect, R.id.set_assistive_touch,
			R.id.set_voice_button, R.id.set_voice_wakeup_button };

	HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	private String[] specialLocaleCodes, specialLocaleNames;
	private int index_language;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		}
	};

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		IntentFilter filter = new IntentFilter(Tag.ACTION_BRIGHTNESS_CHANGED);
		mContext.registerReceiver(mBroadcastReceiver, filter);

		specialLocaleCodes = getResources().getStringArray(R.array.special_locale_codes);
		specialLocaleNames = getResources().getStringArray(R.array.special_locale_names);
		String curCountry = getResources().getConfiguration().locale.getCountry();
		if (curCountry.equals("CN")) {
			index_language = 0;
		} else if (curCountry.equals("TW")) {
			index_language = 1;
		} else if (curCountry.equals("US")) {
			index_language = 2;
		}
	}

	private void initView(View rootView) {
		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			mLayout_OneButton[i] = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			mLayout_OneButton[i].setOneButtonListener(this);
			i++;
		}

		mLayout_OneButton[0].setPromptTitle(specialLocaleNames[index_language]);

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}

		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[0], true))
			((HeaderLayout) rootView.findViewById(R.id.set_lighting_detect))
					.setVisibility(View.GONE);
		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[1], true))
			((HeaderLayout) rootView.findViewById(R.id.set_assistive_touch))
					.setVisibility(View.GONE);
		if (!SystemProperties.getBoolean(Tag.PERSYS_FUNS_OTHER[2], true))
			((HeaderLayout) rootView.findViewById(R.id.set_voice_button)).setVisibility(View.GONE);

		boolean enable = Function.getBuzzerEnable(mContext);
		mLayout_CheckBox[0].getOneCheckBox().setChecked(enable);
		enable = SystemProperties.getBoolean(Tag.PERSYS_HEADLIGHT_ENABLE, false);
		((HeaderLayout) rootView.findViewById(R.id.set_lighting_detect)).getOneCheckBox()
				.setChecked(enable);
		enable = SystemProperties.getBoolean(Tag.PERSYS_ASSISTIVE_TOUCH[0], false);
		((HeaderLayout) rootView.findViewById(R.id.set_assistive_touch)).getOneCheckBox()
				.setChecked(enable);
		enable = SystemProperties.getBoolean(Tag.PERSYS_VOICE_TOUCH, true);
		((HeaderLayout) rootView.findViewById(R.id.set_voice_button)).getOneCheckBox().setChecked(
				enable);
		enable = SystemProperties.getBoolean(Tag.PERSYS_VOICE_WAKEUP, true);
		((HeaderLayout) rootView.findViewById(R.id.set_voice_wakeup_button)).getOneCheckBox().setChecked(
				enable);
	}

	public Fragment_General() {

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
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.fragment_general, container, false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.set_language:
			Fragment_List dialog = Function.buildListDialog(view.getId(), mFragmentManager,
					specialLocaleNames, index_language);
			dialog.setOnItemClickListener(this);
			break;
		case R.id.set_reset:
			Function.onSet_resetfactory(mContext, mFragmentManager,
					getResources().getString(R.string.set_reset));
			break;
		case R.id.set_about:
			Function.onSet_about(mFragmentManager);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (view.getId()) {
		case R.id.set_buzzer:
			Function.setBuzzerEnable(mContext, value);
			break;
		case R.id.set_lighting_detect:
			SystemProperties.set(Tag.PERSYS_HEADLIGHT_ENABLE, value ? "true" : "false");
			McuMethodManager.getInstance(mContext).setMcuParam_Headlight_detection(value);
			break;
		case R.id.set_assistive_touch:
			SystemProperties.set(Tag.PERSYS_ASSISTIVE_TOUCH[0], value ? "true" : "false");
			/*intent = new Intent(Tag.ACTION_ASSISTIVETOUCH);
			mContext.sendBroadcast(intent);*/
			Settings.System.putInt(getActivity().getContentResolver(),  Settings.System.SHOW_TOUCHES, value ? 1 : 0);
			break;
		case R.id.set_voice_button:
			SystemProperties.set(Tag.PERSYS_VOICE_TOUCH, value ? "true" : "false");
			intent = new Intent(Tag.ACTION_VOICE_BTN_ENABLE);
			mContext.sendBroadcast(intent);
			break;
		case R.id.set_voice_wakeup_button:
			SystemProperties.set(Tag.PERSYS_VOICE_WAKEUP, value ? "true" : "false");
			intent = new Intent(Tag.ACTION_VOICE_WAKEUP_ENABLE);
			mContext.sendBroadcast(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		if (id == ID_OneButton[0]) {
			sendMessage(value);
		}
	}

	private void sendMessage(int arg1) {
		Message message = new Message();
		message.what = 0;
		message.arg1 = arg1;
		mHandler.sendMessage(message);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (0 == msg.what) {
				index_language = msg.arg1;
				String str = specialLocaleNames[index_language];
				mLayout_OneButton[0].setPromptTitle(str);
				str = specialLocaleCodes[index_language];
				String language = str.substring(0, 2);
				String country = str.substring(3, 5);
				final Locale l = new Locale(language, country);
				LocalePicker.updateLocale(l);
			}
		}
	};

}
