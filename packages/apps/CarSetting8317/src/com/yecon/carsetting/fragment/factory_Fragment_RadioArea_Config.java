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
import android.view.Window;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.L;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_RadioArea_Config extends DialogFragment implements
		onOneCheckBoxListener {

	private Context mContext;
	FragmentManager mFragmentManager;

	final int ID_CheckBox[] = { R.id.item_EUROPE, R.id.item_USA, R.id.item_OIRT, R.id.item_JAPAN,
			R.id.item_M_East, R.id.item_Latin, R.id.item_Australia, R.id.item_Asia,
			R.id.item_SAmer1, R.id.item_Arabia, R.id.item_China, R.id.item_SAmer2, R.id.item_Korea,
			R.id.item_Russian, R.id.item_FM, };
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	ArrayList<String> list_radio_area;
	private String[] stringarray_radio_area;

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
		stringarray_radio_area = getResources().getStringArray(R.array.radio_locale_names);
	}

	public factory_Fragment_RadioArea_Config() {

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
		View mRootView = inflater.inflate(R.layout.factory_fragment_radio_area_config, container,
				false);
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
		super.onDestroy();
	}

	private void initView(View rootView) {

		int i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			layout = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			layout.setSubTitle(stringarray_radio_area[i]);
			layout.getOneCheckBox().setChecked(XmlParse.radio_area_enable[i] == 1 ? true : false);
			layout.setOneCheckBoxListener(this);
			i++;
		}
	}

	@Override
	public void onCheckout(View view, boolean arg1) {
		// TODO Auto-generated method stub
		int i = 0;
		for (int id : ID_CheckBox) {
			if (view.getId() == ID_CheckBox[i]) {
				XmlParse.radio_area_enable[i] = (arg1 == true ? 1 : 0);

				if (!arg1
						&& XmlParse.list_radio_area_all.get(i).equalsIgnoreCase(
								XmlParse.default_radio_area))
					XmlParse.default_radio_area = "";

				return;
			}
			i++;
		}
	}

}