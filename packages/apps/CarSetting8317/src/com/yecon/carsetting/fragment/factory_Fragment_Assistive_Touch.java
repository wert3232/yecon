package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_Assistive_Touch extends DialogFragment implements
		onOneCheckBoxListener {

	private Context mContext;

	final int ID_CheckBox[] = { R.id.item_home, R.id.item_return, R.id.item_power,
			R.id.item_screen, R.id.item_mode, R.id.item_navi, R.id.item_band, R.id.item_play,
			R.id.item_preview, R.id.item_next, R.id.item_mute, R.id.item_voladd,
			R.id.item_volminus, R.id.item_answer, R.id.item_hungup, };
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	private static String[] stringArray;

	public factory_Fragment_Assistive_Touch() {
		super();
	}

	public void initData() {
		mContext = getActivity();
		stringArray = mContext.getResources().getStringArray(R.array.assistive_item);
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
		View mRootView = inflater.inflate(R.layout.factory_fragment_assistive_touch, container);
		initView(mRootView);
		return mRootView;
	}

	private void initView(View rootView) {
		int i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			layout = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			layout.setSubTitle(stringArray[i]);
			layout.getOneCheckBox().setChecked(
					XmlParse.assistive_touch_enable[i + 1] == 1 ? true : false);
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
				XmlParse.assistive_touch_enable[i + 1] = (arg1 == true ? 1 : 0);
				return;
			}
			i++;
		}
	}
}
