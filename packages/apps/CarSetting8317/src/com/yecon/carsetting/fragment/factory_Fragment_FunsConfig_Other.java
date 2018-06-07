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

public class factory_Fragment_FunsConfig_Other extends DialogFragment implements
		onOneCheckBoxListener {

	private Context mContext;

	final int ID_CheckBox[] = { R.id.item_set_lighting_detect, R.id.item_set_assistive_touch,
			R.id.item_set_voice_touch, R.id.item_set_swc, R.id.item_set_wallpaper,
			R.id.item_set_wifi, R.id.item_set_alto, R.id.item_set_subwoofer, R.id.item_set_clock, };
	HeaderLayout mLayout_CheckBox[] = new HeaderLayout[ID_CheckBox.length];

	public factory_Fragment_FunsConfig_Other() {
		super();
	}

	public void initData() {
		mContext = getActivity();

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
		View mRootView = inflater.inflate(R.layout.factory_fragment_funconfig_other, container);
		initView(mRootView);
		return mRootView;
	}

	private void initView(View rootView) {
		int i = 0;
		for (HeaderLayout layout : mLayout_CheckBox) {
			mLayout_CheckBox[i] = (HeaderLayout) rootView.findViewById(ID_CheckBox[i]);
			mLayout_CheckBox[i].getOneCheckBox().setChecked(
					XmlParse.funs_other[i] == 1 ? true : false);
			mLayout_CheckBox[i].setOneCheckBoxListener(this);
			i++;
		}
	}

	@Override
	public void onCheckout(View view, boolean arg1) {
		// TODO Auto-generated method stub
		int i = 0;
		for (int id : ID_CheckBox) {
			if (view.getId() == ID_CheckBox[i]) {
				XmlParse.funs_other[i] = (arg1 == true ? 1 : 0);
				return;
			}
			i++;
		}
	}
}
