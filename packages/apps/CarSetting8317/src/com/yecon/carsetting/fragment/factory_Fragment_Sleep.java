package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_Sleep extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, OnListDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;
	
	private String mTimeUnitSecond;
	private String mTimeUnitMinute;
	private String mTimeUnitHour;

	private String[] stringarray_power_off_time;
    private String[] stringarray_sleep_read_time;

	int ID_OneButton[] = { R.id.factory_sleep_ready_time, R.id.factory_power_off_time };
	final int ID_CheckBox[] = { R.id.factory_boot_logo, R.id.factory_sleep_power_enable, };
	HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	public factory_Fragment_Sleep() {
		super();
	}

	public void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		
		mTimeUnitSecond = getResources().getString(R.string.factory_time_unit_second);
		mTimeUnitMinute = getResources().getString(R.string.factory_time_unit_minute);
		mTimeUnitHour = getResources().getString(R.string.factory_time_unit_hour);
				
		stringarray_sleep_read_time = new String[XmlParse.intarray_sleep_time.length];
		StringBuffer timeBuffer = new StringBuffer();
		for (int i = 0; i < XmlParse.intarray_sleep_time.length; i++) {
		    timeBuffer.append(XmlParse.intarray_sleep_time[i]);
	        timeBuffer.append(" ");
	        timeBuffer.append(mTimeUnitSecond);
	        stringarray_sleep_read_time[i] = timeBuffer.toString();
	        timeBuffer.setLength(0);
		}
		
		stringarray_power_off_time = new String[XmlParse.intarray_power_off_time.length];
		for (int i = 0; i < XmlParse.intarray_power_off_time.length; i++) {
		    stringarray_power_off_time[i] = getPowerOffTime(XmlParse.intarray_power_off_time[i]);
		}
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
		mRootView = inflater.inflate(R.layout.factory_fragment_sleep, container);
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

		((HeaderLayout) rootView.findViewById(R.id.factory_sleep_ready_time))
				.setPromptTitle(stringarray_sleep_read_time[XmlParse.sleep_ready_time]);

		((HeaderLayout) rootView.findViewById(R.id.factory_power_off_time))
				.setPromptTitle(stringarray_power_off_time[XmlParse.sleep_time]);

		i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			layout = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			layout.setOneCheckBoxListener(this);
			i++;
		}

		((HeaderLayout) rootView.findViewById(R.id.factory_boot_logo)).getOneCheckBox().setChecked(
				XmlParse.boot_logo_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_sleep_power_enable)).getOneCheckBox()
				.setChecked(XmlParse.sleep_power_enable == 1 ? true : false);
	}
	
	private String getPowerOffTime(int powerOffTime) {
	    StringBuffer timeBuffer = new StringBuffer();
	    if (powerOffTime < 0) {
	        return "";
	    } else if (powerOffTime < 60) {
	        timeBuffer.append(powerOffTime);
	        timeBuffer.append(" ");
	        timeBuffer.append(mTimeUnitSecond);
	    } else if (powerOffTime >= 60 && powerOffTime < 60 * 60) {
            timeBuffer.append(powerOffTime / 60);
            timeBuffer.append(" ");
            timeBuffer.append(mTimeUnitMinute);
        } else if (powerOffTime >= 1 * 60 * 60) {
            timeBuffer.append(powerOffTime / (60 * 60));
            timeBuffer.append(" ");
            timeBuffer.append(mTimeUnitHour);
        }
	    
	    return timeBuffer.toString();
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.factory_sleep_ready_time:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_sleep_read_time, XmlParse.sleep_ready_time);
			mDialog.setOnItemClickListener(this);
			break;
		case R.id.factory_power_off_time:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
			        stringarray_power_off_time, XmlParse.sleep_time);
			mDialog.setOnItemClickListener(this);
			break;
		default:
			break;
		}

	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		switch (id) {
		case R.id.factory_sleep_ready_time: {
			((HeaderLayout) mRootView.findViewById(R.id.factory_sleep_ready_time))
					.setPromptTitle(stringarray_sleep_read_time[value]);
			XmlParse.sleep_ready_time = value;
			//int flag = XmlParse.sleep_power_enable == 1 ? 0 : 1;
			int sleepTime = XmlParse.intarray_sleep_time[XmlParse.sleep_ready_time];
			int powerOffTime = XmlParse.intarray_power_off_time[XmlParse.sleep_time];
			//McuMethodManager.getInstance(mContext).sendKeyPowerOffCMD(flag, time);
			McuMethodManager.getInstance(mContext).setMcuSystemParam_sleep(sleepTime, powerOffTime);
		}
			break;
		case R.id.factory_power_off_time: {
			((HeaderLayout) mRootView.findViewById(R.id.factory_power_off_time))
					.setPromptTitle(stringarray_power_off_time[value]);
			XmlParse.sleep_time = value;
			int sleepTime = XmlParse.intarray_sleep_time[XmlParse.sleep_ready_time];
            int powerOffTime = XmlParse.intarray_power_off_time[XmlParse.sleep_time];
            //McuMethodManager.getInstance(mContext).sendKeyPowerOffCMD(flag, sleepTime);
            McuMethodManager.getInstance(mContext).setMcuSystemParam_sleep(sleepTime, powerOffTime);
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_boot_logo:
			XmlParse.boot_logo_enable = value == true ? 1 : 0;
			break;
		case R.id.factory_sleep_power_enable:
			XmlParse.sleep_power_enable = value == true ? 1 : 0;
			int flag = XmlParse.sleep_power_enable == 1 ? 0 : 1;
			int sleepTime = XmlParse.intarray_sleep_time[XmlParse.sleep_ready_time];
			McuMethodManager.getInstance(mContext).sendKeyPowerOffCMD(flag, sleepTime);
			break;

		default:
			break;
		}
	}

}
