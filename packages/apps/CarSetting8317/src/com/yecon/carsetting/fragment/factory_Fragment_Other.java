package com.yecon.carsetting.fragment;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoRadioButtonListener;

public class factory_Fragment_Other extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, onTwoRadioButtonListener, OnProgressDlgListener, OnListDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	int ID_OneButton[] = { R.id.factory_car_type, R.id.factory_mic_volume, R.id.factory_tv_type,
			R.id.factory_tv_area, R.id.factory_dvr_type, R.id.factory_dvd_area_code, R.id.factory_tyre_com,
			R.id.factory_crv_audio_input, R.id.factory_drive_record, R.id.factory_front_camera,
			R.id.factory_qicaideng, R.id.style_logo_setting, R.id.factory_dvd_load_pic,
			R.id.factory_assistive_touch, R.id.factory_sleep_about, };
	int ID_OneCheck[] = { R.id.factory_headlight_detection, R.id.factory_brake_enable,
			R.id.factory_outside_temp, R.id.factory_lcd_enable, R.id.factory_antenna_ctrl,
			R.id.factory_power_amplifier_ctrl, R.id.factory_assistive_touch,
			R.id.factory_voice_touch, R.id.factory_voice_wakeup_touch};
	int ID_TwoRadio[] = { R.id.factory_aux_change, R.id.factory_video_format,
			R.id.factory_air_temp_ctrl, };
	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	private HeaderLayout mLayout_OneCheck[] = new HeaderLayout[ID_OneCheck.length];
	private HeaderLayout mLayout_TwoRadio[] = new HeaderLayout[ID_TwoRadio.length];

	ArrayList<String> list_tv_type;
	ArrayList<String> list_tv_area;
	ArrayList<String> list_dvr_type;

	private String[] stringarray_dvd_area_code;
	private String[] stringarray_tyre_com;
	private String[] stringarray_tv_type;
	private String[] stringarray_tv_area;
	private String[] stringarray_dvr_type;
	private String[] StringArray_crv_audio_input;
	private String[] stringarray_drive_record;
	private String[] stringarray_front_camera;

	private int index_tv_type;
	private int index_tv_area;
	private int index_dvr_type;
	private int index_sleep_type;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("")) {
			}
		}
	};

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		IntentFilter filter = new IntentFilter();
		mContext.registerReceiver(mBroadcastReceiver, filter);

		stringarray_tv_type = getResources().getStringArray(R.array.tv_type);
		list_tv_type = XmlParse.getStringArray(XmlParse.support_tv_type);
		for (int i = 0; i < list_tv_type.size(); i++) {
			if (XmlParse.default_tv_type.equalsIgnoreCase(list_tv_type.get(i))) {
				index_tv_type = i;
				break;
			}
		}
		stringarray_tv_area = getResources().getStringArray(R.array.tv_area);
		list_tv_area = XmlParse.getStringArray(XmlParse.support_tv_area);
		for (int i = 0; i < list_tv_area.size(); i++) {
			if (XmlParse.default_tv_area.equalsIgnoreCase(list_tv_area.get(i))) {
				index_tv_area = i;
				break;
			}
		}
		stringarray_dvr_type = getResources().getStringArray(R.array.dvr_type);
		list_dvr_type = XmlParse.getStringArray(XmlParse.support_dvr_type);
		for (int i = 0; i < list_dvr_type.size(); i++) {
			if (XmlParse.default_dvr_type.equalsIgnoreCase(list_dvr_type.get(i))) {
				index_dvr_type = i;
				break;
			}
		}
		stringarray_dvd_area_code = tzutil.getStringArray(6);
		stringarray_tyre_com = tzutil.getStringArray(9);
		StringArray_crv_audio_input = tzutil.getStringArray(5);
		stringarray_drive_record = getResources().getStringArray(R.array.aux_port);
		stringarray_front_camera = getResources().getStringArray(R.array.aux_port);
		index_sleep_type = XmlParse.sleep_time;
	}

	public factory_Fragment_Other() {

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
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.factory_fragment_other, container, false);
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

	private void initView(View rootView) {

		int i = 0;
		for (HeaderLayout layout : mLayout_OneButton) {
			layout = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			layout.setOneButtonListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_OneCheck) {
			layout = (HeaderLayout) rootView.findViewById(ID_OneCheck[i]);
			layout.setOneCheckBoxListener(this);
			i++;
		}

		i = 0;
		for (HeaderLayout layout : mLayout_TwoRadio) {
			layout = (HeaderLayout) rootView.findViewById(ID_TwoRadio[i]);
			layout.setTwoRadioButtonListener(this);
			i++;
		}

		((HeaderLayout) rootView.findViewById(R.id.factory_mic_volume))
				.setPromptTitle(XmlParse.mic_volume + "");
		((HeaderLayout) rootView.findViewById(R.id.factory_tv_type))
				.setPromptTitle(stringarray_tv_type[index_tv_type]);
		((HeaderLayout) rootView.findViewById(R.id.factory_tv_area))
				.setPromptTitle(stringarray_tv_area[index_tv_area]);
		((HeaderLayout) rootView.findViewById(R.id.factory_dvd_area_code))
				.setPromptTitle(stringarray_dvd_area_code[XmlParse.dvd_area_code]);
		
		((HeaderLayout) rootView.findViewById(R.id.factory_tyre_com))
		.setPromptTitle(stringarray_tyre_com[XmlParse.tyre_com]);
		
		((HeaderLayout) rootView.findViewById(R.id.factory_dvr_type))
				.setPromptTitle(stringarray_dvr_type[index_dvr_type]);
		((HeaderLayout) rootView.findViewById(R.id.factory_crv_audio_input))
				.setPromptTitle(StringArray_crv_audio_input[XmlParse.audioport_crv_audio_input]);
		((HeaderLayout) rootView.findViewById(R.id.factory_drive_record))
				.setPromptTitle(stringarray_drive_record[XmlParse.videoport_drive_record]);
		((HeaderLayout) rootView.findViewById(R.id.factory_front_camera))
				.setPromptTitle(stringarray_front_camera[XmlParse.videoport_front_camera]);

		// //////////////////////////////////////////////////////////////////////////////////////////
		((HeaderLayout) rootView.findViewById(R.id.factory_headlight_detection)).getOneCheckBox()
				.setChecked(XmlParse.headlight_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_brake_enable)).getOneCheckBox()
				.setChecked(XmlParse.brake_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_outside_temp)).getOneCheckBox()
				.setChecked(XmlParse.outside_temp_test == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_lcd_enable)).getOneCheckBox()
				.setChecked(XmlParse.lcd_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_antenna_ctrl)).getOneCheckBox()
				.setChecked(XmlParse.antenna_ctrl_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_power_amplifier_ctrl)).getOneCheckBox()
				.setChecked(XmlParse.power_amplifier_ctrl_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_assistive_touch)).getOneCheckBox()
				.setChecked(XmlParse.assistive_touch_enable[0] == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_voice_touch)).getOneCheckBox()
				.setChecked(XmlParse.voice_touch_enable == 1 ? true : false);
		((HeaderLayout) rootView.findViewById(R.id.factory_voice_wakeup_touch)).getOneCheckBox()
		.setChecked(XmlParse.voice_wakeup_enable == 1 ? true : false);

		// //////////////////////////////////////////////////////////////////////////////////////////
		((HeaderLayout) rootView.findViewById(R.id.factory_aux_change))
				.setRadioCheck(XmlParse.aux_port == 2 ? 2 : 1);
		((HeaderLayout) rootView.findViewById(R.id.factory_video_format))
				.setRadioCheck(XmlParse.default_video_out_format.equalsIgnoreCase("PAL") ? 2 : 1);
		((HeaderLayout) rootView.findViewById(R.id.factory_air_temp_ctrl))
				.setRadioCheck(XmlParse.default_air_temp_ctrl.equalsIgnoreCase("Alone") ? 2 : 1);
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_car_type:
			Function.onSet_car_type(mContext);
			break;

		case R.id.factory_mic_volume:
			buildProgressDialog(view.getId(), getString(R.string.factory_mic_volume), 64,
					XmlParse.mic_volume);
			break;

		case R.id.factory_tv_type:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_tv_type,
					index_tv_type);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_tv_area:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager, stringarray_tv_area,
					index_tv_area);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_dvd_area_code:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_dvd_area_code, XmlParse.dvd_area_code);
			mDialog.setOnItemClickListener(this);
			break;
		
		case R.id.factory_tyre_com:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_tyre_com, XmlParse.tyre_com);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_dvr_type:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_dvr_type, index_dvr_type);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_crv_audio_input:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					StringArray_crv_audio_input, XmlParse.audioport_crv_audio_input);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_drive_record:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_drive_record, XmlParse.videoport_drive_record);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.factory_front_camera:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_front_camera, XmlParse.videoport_front_camera);
			mDialog.setOnItemClickListener(this);
			break;

		case R.id.style_logo_setting:
			Function.onSet_Bootlogo(mFragmentManager);
			break;
		case R.id.factory_dvd_load_pic:
			Function.onSet_Dvdlogo(mFragmentManager);
			break;

		case R.id.factory_qicaideng:
			Function.onSet_Qucaideng(mFragmentManager);
			break;

		case R.id.factory_assistive_touch:
			Function.onSet_AssistiveTouch(mFragmentManager);
			break;

		case R.id.factory_sleep_about:
			Function.onSet_factory_Fragment_Sleep(mFragmentManager);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_headlight_detection:
			XmlParse.headlight_enable = (value == true ? 1 : 0);
			break;
		case R.id.factory_brake_enable:
			XmlParse.brake_enable = (value == true ? 1 : 0);
			break;
		case R.id.factory_outside_temp:
			XmlParse.outside_temp_test = (value == true ? 1 : 0);
			break;
		case R.id.factory_lcd_enable:
			XmlParse.lcd_enable = (value == true ? 1 : 0);
			break;
		case R.id.factory_antenna_ctrl:
			XmlParse.antenna_ctrl_enable = (value == true ? 1 : 0);
			break;
		case R.id.factory_power_amplifier_ctrl:
			XmlParse.power_amplifier_ctrl_enable = (value == true ? 1 : 0);
			break;

		case R.id.factory_assistive_touch:
			XmlParse.assistive_touch_enable[0] = value == true ? 1 : 0;
			break;
		case R.id.factory_voice_touch:
			XmlParse.voice_touch_enable = value == true ? 1 : 0;
			break;
		case R.id.factory_voice_wakeup_touch:
			XmlParse.voice_wakeup_enable = value == true ? 1 : 0;
			break;
		default:
			break;
		}
	}

	@Override
	public void onLeftRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_aux_change:
			XmlParse.aux_port = 1;
			break;
		case R.id.factory_video_format:
			XmlParse.default_video_out_format = "NTSC";
			break;
		case R.id.factory_air_temp_ctrl:
			XmlParse.default_air_temp_ctrl = "Together";
			break;
		default:
			break;
		}
	}

	@Override
	public void onRightRadioButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {

		case R.id.factory_aux_change:
			XmlParse.aux_port = 2;
			break;
		case R.id.factory_video_format:
			XmlParse.default_video_out_format = "PAL";
			break;
		case R.id.factory_air_temp_ctrl:
			XmlParse.default_air_temp_ctrl = "Alone";
			break;
		default:
			break;
		}
	}

	@Override
	public void onProgessDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_mic_volume:
			XmlParse.mic_volume = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_mic_volume)).setPromptTitle(value
					+ "");
			break;
		default:
			break;
		}

	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {

		/*
		 * case R.id.factory_car_type: ((HeaderLayout)
		 * mRootView.findViewById(R.id.factory_car_type))
		 * .setPromptTitle(stringarray_car_type[value]);
		 * XmlParse.default_car_type = list_car_type.get(value); index_car_type
		 * = value;
		 * McuMethodManager.getInstance(mContext).setMcuParam_cantype(index_car_type
		 * ); break;
		 */

		case R.id.factory_tv_type:
			((HeaderLayout) mRootView.findViewById(R.id.factory_tv_type))
					.setPromptTitle(stringarray_tv_type[value]);
			XmlParse.default_tv_type = list_tv_type.get(value);
			index_tv_type = value;
			break;

		case R.id.factory_tv_area:
			((HeaderLayout) mRootView.findViewById(R.id.factory_tv_area))
					.setPromptTitle(stringarray_tv_area[value]);
			XmlParse.default_tv_area = list_tv_area.get(value);
			index_tv_area = value;
			break;

		case R.id.factory_dvd_area_code:
			((HeaderLayout) mRootView.findViewById(R.id.factory_dvd_area_code))
					.setPromptTitle(stringarray_dvd_area_code[value]);
			XmlParse.dvd_area_code = value;
			break;
		case R.id.factory_tyre_com:
			((HeaderLayout) mRootView.findViewById(R.id.factory_tyre_com))
					.setPromptTitle(stringarray_tyre_com[value]);
			XmlParse.tyre_com = value;
			break;

		case R.id.factory_dvr_type:
			((HeaderLayout) mRootView.findViewById(R.id.factory_dvr_type))
					.setPromptTitle(stringarray_dvr_type[value]);
			XmlParse.default_dvr_type = list_dvr_type.get(value);
			index_dvr_type = value;
			break;

		case R.id.factory_crv_audio_input:
			((HeaderLayout) mRootView.findViewById(R.id.factory_crv_audio_input))
					.setPromptTitle(StringArray_crv_audio_input[value]);
			XmlParse.audioport_crv_audio_input = value;
			break;
		case R.id.factory_front_camera:
			((HeaderLayout) mRootView.findViewById(R.id.factory_front_camera))
					.setPromptTitle(stringarray_front_camera[value]);
			XmlParse.videoport_front_camera = value;
			break;

		case R.id.factory_drive_record:
			((HeaderLayout) mRootView.findViewById(R.id.factory_drive_record))
					.setPromptTitle(stringarray_drive_record[value]);
			XmlParse.videoport_drive_record = value;
			break;

		default:
			break;
		}
	}

	private void buildProgressDialog(int id, String title, int progress, int pos) {
		Fragment_Progress Dialog = new Fragment_Progress();
		Dialog.initData(id, title, progress, pos);
		Dialog.setOnProgressDlgListener(this);
		Dialog.show(mFragmentManager, title);
	}

}