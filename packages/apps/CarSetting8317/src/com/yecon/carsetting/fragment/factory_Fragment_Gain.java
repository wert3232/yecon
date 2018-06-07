package com.yecon.carsetting.fragment;

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
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;

public class factory_Fragment_Gain extends DialogFragment implements onOneButtonListener,
		OnProgressDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;

	int ID_OneButton[] = { R.id.factory_bt_call_gain, R.id.factory_navi_gain,
			R.id.factory_other_gain, R.id.factory_aux_db, R.id.factory_a2dp_db, R.id.factory_fm_db,
			R.id.factory_am_db, R.id.factory_tv_db, };

	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];

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

	}

	public factory_Fragment_Gain() {

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
		mRootView = inflater.inflate(R.layout.factory_fragment_gain, container, false);
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

		((HeaderLayout) rootView.findViewById(R.id.factory_bt_call_gain))
				.setPromptTitle(XmlParse.gain[0] + "");
		((HeaderLayout) rootView.findViewById(R.id.factory_navi_gain))
				.setPromptTitle(XmlParse.gain[1] + "");
		((HeaderLayout) rootView.findViewById(R.id.factory_other_gain))
				.setPromptTitle(XmlParse.gain[2] + "");
		((HeaderLayout) rootView.findViewById(R.id.factory_aux_db)).setPromptTitle(XmlParse.db[0]
				+ "");
		((HeaderLayout) rootView.findViewById(R.id.factory_a2dp_db)).setPromptTitle(XmlParse.db[1]
				+ "");
		((HeaderLayout) rootView.findViewById(R.id.factory_fm_db)).setPromptTitle(XmlParse.db[2]
				+ "");
		((HeaderLayout) rootView.findViewById(R.id.factory_am_db)).setPromptTitle(XmlParse.db[3]
				+ "");
		((HeaderLayout) rootView.findViewById(R.id.factory_tv_db)).setPromptTitle(XmlParse.db[4]
				+ "");
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_bt_call_gain:
			buildProgressDialog(view.getId(), getString(R.string.factory_bt_call_gain), 255,
					XmlParse.gain[0]);
			break;

		case R.id.factory_navi_gain:
			buildProgressDialog(view.getId(), getString(R.string.factory_navi_gain), 255,
					XmlParse.gain[1]);
			break;

		case R.id.factory_other_gain:
			buildProgressDialog(view.getId(), getString(R.string.factory_other_gain), 255,
					XmlParse.gain[2]);
			break;

		case R.id.factory_aux_db:
			buildProgressDialog(view.getId(), getString(R.string.factory_aux_db), 255,
					XmlParse.db[0]);
			break;

		case R.id.factory_a2dp_db:
			buildProgressDialog(view.getId(), getString(R.string.factory_a2dp_db), 255,
					XmlParse.db[1]);
			break;

		case R.id.factory_fm_db:
			buildProgressDialog(view.getId(), getString(R.string.factory_fm_db), 255,
					XmlParse.db[2]);
			break;

		case R.id.factory_am_db:
			buildProgressDialog(view.getId(), getString(R.string.factory_am_db), 255,
					XmlParse.db[3]);
			break;

		case R.id.factory_tv_db:
			buildProgressDialog(view.getId(), getString(R.string.factory_tv_db), 255,
					XmlParse.db[4]);
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgessDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_bt_call_gain:
			XmlParse.gain[0] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_bt_call_gain)).setPromptTitle(value
					+ "");
			break;
		case R.id.factory_navi_gain:
			XmlParse.gain[1] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_navi_gain)).setPromptTitle(value
					+ "");
			break;
		case R.id.factory_other_gain:
			XmlParse.gain[2] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_other_gain)).setPromptTitle(value
					+ "");
			break;
		case R.id.factory_aux_db:
			XmlParse.db[0] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_aux_db)).setPromptTitle(value + "");
			break;
		case R.id.factory_a2dp_db:
			XmlParse.db[1] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_a2dp_db))
					.setPromptTitle(value + "");
			break;
		case R.id.factory_fm_db:
			XmlParse.db[2] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_fm_db)).setPromptTitle(value + "");
			break;
		case R.id.factory_am_db:
			XmlParse.db[3] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_am_db)).setPromptTitle(value + "");
			break;
		case R.id.factory_tv_db:
			XmlParse.db[4] = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_tv_db)).setPromptTitle(value + "");
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