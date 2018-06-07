package com.yecon.carsetting.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;

public class factory_Fragment_Radio extends DialogFragment implements onOneButtonListener,
		OnProgressDlgListener, OnListDlgListener {

	private Context mContext;
	FragmentManager mFragmentManager;

	int ID_OneButton[] = { R.id.factory_radio_area_config, R.id.factory_radio_area,
			R.id.factory_radio_type, R.id.factory_fm_dx, R.id.factory_fm_loc, R.id.factory_am_dx,
			R.id.factory_am_loc, R.id.factory_oirt_dx, R.id.factory_oirt_loc, };

	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	Fragment_List mDialog;

	ArrayList<String> list_radio_area;
	private String[] stringarray_radio_area;
	List<Integer> intarray_radio_area;
	private int index_radio_area;

	ArrayList<String> list_radio_type;
	private String[] stringarray_radio_type;
	private int index_radio_type;

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

		stringarray_radio_type = getResources().getStringArray(R.array.factory_radio_type);
		list_radio_type = XmlParse.getStringArray(XmlParse.support_radio_type);
		for (int i = 0; i < list_radio_type.size(); i++) {
			if (XmlParse.default_radio_type.equalsIgnoreCase(list_radio_type.get(i))) {
				index_radio_type = i;
				break;
			}
		}
		init_RadioPara();
	}

	void init_RadioPara() {

		index_radio_area = -1;

		ArrayList<String> list_radio_area_all;
		String[] stringarray_radio_area_all;
		int[] intarray_radio_area_all;

		stringarray_radio_area_all = getResources().getStringArray(R.array.radio_locale_names);
		list_radio_area_all = XmlParse.getStringArray(XmlParse.support_radio_area);
		intarray_radio_area_all = getResources().getIntArray(R.array.radio_locale_values);

		List<String> list = new ArrayList<String>();
		intarray_radio_area = new ArrayList<Integer>();

		list_radio_area = new ArrayList<String>();
		int i = 0;
		for (String s : stringarray_radio_area_all) {
			if (XmlParse.radio_area_enable[i] == 1) {
				list.add(stringarray_radio_area_all[i]);
				list_radio_area.add(list_radio_area_all.get(i));
				intarray_radio_area.add((Integer) intarray_radio_area_all[i]);
			}
			i++;
		}

		int count = list.size();
		stringarray_radio_area = new String[list.size()];
		for (int j = 0; j < count; j++) {
			stringarray_radio_area[j] = list.get(j);
		}

		for (int k = 0; k < list_radio_area.size(); k++) {
			if (XmlParse.default_radio_area.equalsIgnoreCase(list_radio_area.get(k))) {
				index_radio_area = k;
				break;
			}
		}
	}

	public factory_Fragment_Radio() {

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
		View mRootView = inflater.inflate(R.layout.factory_fragment_radio, container, false);
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
			mLayout_OneButton[i] = (HeaderLayout) rootView.findViewById(ID_OneButton[i]);
			mLayout_OneButton[i].setOneButtonListener(this);
			i++;
		}
		i = 1;
		if (index_radio_area == -1) {
			mLayout_OneButton[i++].setPromptTitle("");
		} else {
			mLayout_OneButton[i++].setPromptTitle(stringarray_radio_area[index_radio_area]);
		}

		mLayout_OneButton[i++].setPromptTitle(stringarray_radio_type[index_radio_type]);
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[0] + "");
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[1] + "");
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[2] + "");
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[3] + "");
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[4] + "");
		mLayout_OneButton[i++].setPromptTitle(XmlParse.DX_LOC[5] + "");
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_radio_area_config:
			Function.onSet_factory_Fragment_RadioArea_Config(mFragmentManager);
			break;
		case R.id.factory_radio_area:
			init_RadioPara();
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_radio_area, index_radio_area);
			mDialog.setOnItemClickListener(this);
			break;
		case R.id.factory_radio_type:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_radio_type, index_radio_type);
			mDialog.setOnItemClickListener(this);
			break;
		case R.id.factory_fm_dx:
			buildProgressDialog(view.getId(), getString(R.string.factory_fm_dx), 255,
					XmlParse.DX_LOC[0]);
			break;

		case R.id.factory_fm_loc:
			buildProgressDialog(view.getId(), getString(R.string.factory_fm_loc), 255,
					XmlParse.DX_LOC[1]);
			break;

		case R.id.factory_am_dx:
			buildProgressDialog(view.getId(), getString(R.string.factory_am_dx), 255,
					XmlParse.DX_LOC[2]);
			break;

		case R.id.factory_am_loc:
			buildProgressDialog(view.getId(), getString(R.string.factory_am_loc), 255,
					XmlParse.DX_LOC[3]);
			break;

		case R.id.factory_oirt_dx:
			buildProgressDialog(view.getId(), getString(R.string.factory_oirt_dx), 255,
					XmlParse.DX_LOC[4]);
			break;
		case R.id.factory_oirt_loc:
			buildProgressDialog(view.getId(), getString(R.string.factory_oirt_loc), 255,
					XmlParse.DX_LOC[5]);
			break;

		default:
			break;
		}
	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_radio_area:
			mLayout_OneButton[1].setPromptTitle(stringarray_radio_area[value]);
			XmlParse.default_radio_area = list_radio_area.get(value);
			Function.setRadioCountry(mContext, intarray_radio_area.get(value));
			Log.i("lzy", "........................" + intarray_radio_area.get(value));
			index_radio_area = value;
			break;

		case R.id.factory_radio_type:
			mLayout_OneButton[2].setPromptTitle(stringarray_radio_type[value]);
			XmlParse.default_radio_type = list_radio_type.get(value);
			index_radio_type = value;
			// setMcuParam_radiotype();
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgessDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_fm_dx:
			XmlParse.DX_LOC[0] = value;
			mLayout_OneButton[2].setPromptTitle(value + "");
			break;

		case R.id.factory_fm_loc:
			XmlParse.DX_LOC[1] = value;
			mLayout_OneButton[3].setPromptTitle(value + "");
			break;

		case R.id.factory_am_dx:
			XmlParse.DX_LOC[2] = value;
			mLayout_OneButton[4].setPromptTitle(value + "");
			break;

		case R.id.factory_am_loc:
			XmlParse.DX_LOC[3] = value;
			mLayout_OneButton[5].setPromptTitle(value + "");
			break;

		case R.id.factory_oirt_dx:
			XmlParse.DX_LOC[4] = value;
			mLayout_OneButton[6].setPromptTitle(value + "");
			break;

		case R.id.factory_oirt_loc:
			XmlParse.DX_LOC[5] = value;
			mLayout_OneButton[7].setPromptTitle(value + "");
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