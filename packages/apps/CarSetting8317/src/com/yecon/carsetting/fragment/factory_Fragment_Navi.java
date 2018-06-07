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
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;

public class factory_Fragment_Navi extends DialogFragment implements onOneButtonListener,
		onOneCheckBoxListener, OnProgressDlgListener, OnListDlgListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	int ID_OneButton[] = { R.id.factory_navi_path, R.id.factory_navi_remix_ratio, };
	int ID_OneCheck[] = { R.id.factory_volume_remix, R.id.factory_volume_listen, };
	private HeaderLayout mLayout_OneButton[] = new HeaderLayout[ID_OneButton.length];
	private HeaderLayout mLayout_OneCheck[] = new HeaderLayout[ID_OneCheck.length];

	private String[] stringarray_navi_path;
	ArrayList<String> list_navi_path;
	private int index_navi_path;
	private boolean mVolumeRemixEnable;
	private boolean mVolumeListenEnable;

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

		mVolumeRemixEnable = XmlParse.remix_enable == 1 ? true : false;
		mVolumeListenEnable = XmlParse.listen_enable == 1 ? true : false;
		stringarray_navi_path = getResources().getStringArray(R.array.navi_path);
		list_navi_path = XmlParse.getStringArray(XmlParse.support_navi_path);
		for (int i = 0; i < list_navi_path.size(); i++) {
			if (XmlParse.default_navi_path.equalsIgnoreCase(list_navi_path.get(i))) {
				index_navi_path = i;
				break;
			}
		}
	}

	public factory_Fragment_Navi() {

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
		mRootView = inflater.inflate(R.layout.factory_fragment_navi, container, false);
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

		i = 0;
		for (HeaderLayout layout : mLayout_OneCheck) {
			mLayout_OneCheck[i] = (HeaderLayout) rootView.findViewById(ID_OneCheck[i]);
			mLayout_OneCheck[i].setOneCheckBoxListener(this);
			i++;
		}

		((HeaderLayout) rootView.findViewById(R.id.factory_navi_path))
				.setPromptTitle(stringarray_navi_path[index_navi_path]);
		((HeaderLayout) rootView.findViewById(R.id.factory_navi_remix_ratio))
				.setPromptTitle(XmlParse.remix_ratio + "");

		((HeaderLayout) rootView.findViewById(R.id.factory_volume_remix)).getOneCheckBox()
				.setChecked(mVolumeRemixEnable);
		((HeaderLayout) rootView.findViewById(R.id.factory_volume_listen)).getOneCheckBox()
				.setChecked(mVolumeListenEnable);
	}

	@Override
	public void onOneButtonClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_navi_path:
			mDialog = Function.buildListDialog(view.getId(), mFragmentManager,
					stringarray_navi_path, index_navi_path);
			mDialog.setOnItemClickListener(this);
			break;
		case R.id.factory_navi_remix_ratio:
			buildProgressDialog(view.getId(), getString(R.string.factory_navi_remix_ratio), 100,
					XmlParse.remix_ratio);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckout(View view, boolean value) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.factory_volume_remix:
			nav_volume_remix(value);
			break;
		case R.id.factory_volume_listen:
			nav_volume_listen(value);
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgessDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_navi_remix_ratio:
			XmlParse.remix_ratio = value;
			((HeaderLayout) mRootView.findViewById(R.id.factory_navi_remix_ratio))
					.setPromptTitle(value + "");
			break;
			
		default:
			break;
		}

	}

	@Override
	public void onListDlgSetValue(int id, int value) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.factory_navi_path:
			((HeaderLayout) mRootView.findViewById(R.id.factory_navi_path))
					.setPromptTitle(stringarray_navi_path[value]);
			XmlParse.default_navi_path = list_navi_path.get(value);
			index_navi_path = value;
			break;

		default:
			break;
		}
	}

	private void nav_volume_remix(boolean value) {
		mVolumeRemixEnable = value;
		XmlParse.remix_enable = value ? 1 : 0;
		if (value && mVolumeListenEnable) {
			mVolumeListenEnable = false;
			XmlParse.listen_enable = 0;
			((HeaderLayout) mRootView.findViewById(R.id.factory_volume_listen)).getOneCheckBox()
					.setChecked(mVolumeListenEnable);
		}
	}

	private void nav_volume_listen(boolean value) {
		mVolumeListenEnable = value;
		XmlParse.listen_enable = value ? 1 : 0;
		if (value && mVolumeRemixEnable) {
			mVolumeRemixEnable = false;
			XmlParse.remix_enable = 0;
			((HeaderLayout) mRootView.findViewById(R.id.factory_volume_remix)).getOneCheckBox()
					.setChecked(mVolumeRemixEnable);
		}
	}

	private void buildProgressDialog(int id, String title, int progress, int pos) {
		Fragment_Progress Dialog = new Fragment_Progress();
		Dialog.initData(id, title, progress, pos);
		Dialog.setOnProgressDlgListener(this);
		Dialog.show(mFragmentManager, title);
	}

}